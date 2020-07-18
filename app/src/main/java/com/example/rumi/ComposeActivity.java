package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    private static final int NUM_PICKER_MIN = 1;
    private static final int NUM_PICKER_MAX = 10;
    private static final float DAYS_IN_MONTH = 30;
    private static final int CAPTURE_IMAGE_CODE = 10;
    private static final String STRING_YES = "Yes";
    private static final String STRING_LOOKING_FOR_PLACE = "Looking for a place";
    private EditText etTitle, etDescription, etRent, etNumRooms;
    private RadioGroup radioGroupOne, radioGroupFurnished;
    private RadioButton radioButtonHouse, radioButtonFurnished;
    private ImageView ivImagePreview;
    private TextView tvStartDate, tvEndDate;
    private Button btnPost;

    private String title, description, startMonth, startDate, endDate;
    private String photoUrl = "";
    private Date start, end;
    private int numRooms, rent, numMonths;
    private boolean lookingForHouse, furnished;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String postId = UUID.randomUUID().toString();
    private DocumentReference postRef = db.collection(Post.KEY_POSTS).document(postId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        radioGroupOne = findViewById(R.id.radioGroupOne);
        radioGroupFurnished = findViewById(R.id.radioGroupFurnished);
        etRent = findViewById(R.id.etRent);
        etNumRooms = findViewById(R.id.etNumRooms);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnPost = findViewById(R.id.btnPost);
        ivImagePreview = findViewById(R.id.ivImagePreview);
    }

    // onClick method for radio buttons
    public void checkPlaceRadioButton(View view) {
        int radioId = radioGroupOne.getCheckedRadioButtonId();
        radioButtonHouse = findViewById(radioId);
        if (radioButtonHouse.getText().equals(STRING_LOOKING_FOR_PLACE)) {
            lookingForHouse = true;
        } else {
            lookingForHouse = false;
        }
    }

    public void checkFurnishedRadioButton(View view) {
        int radioId = radioGroupFurnished.getCheckedRadioButtonId();
        radioButtonFurnished = findViewById(radioId);
        if (radioButtonFurnished.getText().equals("Yes")) {
            furnished = true;
        } else {
            furnished = false;
        }
    }

    // onClick method for post button - uploads new post to database
    public void makePost(View view) {
        title = etTitle.getText().toString();
        description = etDescription.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Title is required!", Toast.LENGTH_SHORT).show();
            etTitle.requestFocus();
            return;
        } else if (description.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Description is required!", Toast.LENGTH_SHORT).show();
            etDescription.requestFocus();
            return;
        } else if (etRent.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Rent is required!", Toast.LENGTH_SHORT).show();
            etRent.requestFocus();
            return;
        } else if (etNumRooms.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Number of rooms is required!", Toast.LENGTH_SHORT).show();
            etRent.requestFocus();
            return;
        } else if (tvStartDate.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Start date is required!", Toast.LENGTH_SHORT).show();
            return;
        } else if (tvEndDate.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "End date is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        rent = Integer.parseInt(etRent.getText().toString());
        numRooms = Integer.parseInt(etRent.getText().toString());

        // calculate number of months between two dates, rounded up to the nearest whole month
        float daysBetween = ((end.getTime() - start.getTime()) / (1000*60*60*24));
        numMonths = (int) Math.ceil(daysBetween / DAYS_IN_MONTH);

        final Post post = new Post(title, description, startMonth, firebaseAuth.getCurrentUser().getUid(),
                numRooms, numMonths, rent, furnished, lookingForHouse, startDate, endDate, photoUrl,
                postId);

        postRef.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ComposeActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("newPost", Parcels.wrap(post));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // onClick method for calendar icon for choosing start date
    public void chooseStartDate(View view) {
        DatePickerDialog startDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvStartDate.setText(month + "/" + day + "/" + year);
                tvStartDate.setVisibility(View.VISIBLE);
                startMonth = new DateFormatSymbols().getMonths()[month];
                startDate = month + "/" + day + "/" + year;
                try {
                    start = dateFormat.parse(startDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        startDialog.show();
    }

    // onClick method for calendar icon for choosing end date
    public void chooseEndDate(View view) {
        DatePickerDialog endDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvEndDate.setText(month + "/" + day + "/" + year);
                tvEndDate.setVisibility(View.VISIBLE);
                endDate = month + "/" + day + "/" + year;
                try {
                    end = dateFormat.parse(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        endDialog.show();    }

    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(ComposeActivity.this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivImagePreview.setImageBitmap(bitmap);
                ivImagePreview.setVisibility(View.VISIBLE);
                handleUpload(bitmap);
            } else {
                Toast.makeText(ComposeActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String imageId = UUID.randomUUID().toString();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("postImages")
                .child(imageId + ".jpeg");

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
                        Log.e(TAG, "Error uploading profile image!", e);
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoUrl = uri.toString();
                    }
                });
    }
}