package com.example.rumi.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.rumi.ComposeActivity;
import com.example.rumi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PhotoBottomSheetDialog extends BottomSheetDialogFragment {

    public static final String TAG = "PhotoBottomSheetDialog";
    private static final int CAPTURE_IMAGE_CODE = 35;
    public static final int PICK_IMAGE_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final String KEY_FOLDER_NAME = "folderName";
    private static final String KEY_PHOTO_ID = "photoId";

    PhotoBottomSheetListener mListener;

    private TextView tvChooseGallery, tvTakePhoto, tvName;
    private String photoUrl, photoId, folderName;
    private ProgressBar progressBar;

    public interface PhotoBottomSheetListener {
        void sendPhotoUri(Uri photoUri);
    }

    public static PhotoBottomSheetDialog newInstance(String folderName, String photoId) {

        Bundle args = new Bundle();
        PhotoBottomSheetDialog fragment = new PhotoBottomSheetDialog();
        args.putString(KEY_FOLDER_NAME, folderName);
        args.putString(KEY_PHOTO_ID, photoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvChooseGallery = view.findViewById(R.id.tvChooseGallery);
        tvTakePhoto = view.findViewById(R.id.tvTakePhoto);
        progressBar = view.findViewById(R.id.progressBar);
        tvName = view.findViewById(R.id.tvName);

        photoId = getArguments().getString(KEY_PHOTO_ID);
        folderName = getArguments().getString(KEY_FOLDER_NAME);

        if (folderName.equals("postImages")) {
            tvName.setText("Add an Image");
        } else {
            tvName.setText("Change Profile Image");
        }

        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                launchCamera();
            }
        });

        tvChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        // permission hasn't been granted so need to request
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        // permission already granted
                        progressBar.setVisibility(View.VISIBLE);
                        pickImageFromGallery();
                    }
                } else {
                    // system os is less than marshmallow
                    progressBar.setVisibility(View.VISIBLE);
                    pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    progressBar.setVisibility(View.VISIBLE);
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getContext(), "Gallery access was denied!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                handleUpload(bitmap);
            } else {
                Toast.makeText(getContext(), "Photo wasn't taken!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                dismiss();
            }
        }

        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    handleUpload(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "No photo selected!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                dismiss();
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child(folderName)
                .child(photoId + ".jpeg");

        //this is an UploadTask
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error uploading image!", e);
                    }
                });

    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mListener.sendPhotoUri(uri);
                        progressBar.setVisibility(View.GONE);
                        dismiss();
                    }
                });
    }

    // called when fragment is attached to a host fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComposeActivity) {
            try {
                mListener = (PhotoBottomSheetListener) getActivity();
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement PhotoBottomSheetListener");
            }
        } else {
            try {
                mListener = (PhotoBottomSheetListener) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement PhotoBottomSheetListener");
            }
        }
    }
}
