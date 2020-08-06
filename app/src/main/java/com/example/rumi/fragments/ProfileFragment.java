package com.example.rumi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.rumi.ComposeActivity;
import com.example.rumi.LikesActivity;
import com.example.rumi.LoginActivity;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.dialogs.MatchDialogSix;
import com.example.rumi.dialogs.MessageDialog;
import com.example.rumi.models.Chat;
import com.example.rumi.models.Message;
import com.example.rumi.models.Post;
import com.example.rumi.R;
import com.example.rumi.models.SurveyResponse;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment implements MessageDialog.MessageListener {

    public static final String TAG = "ProfileFragment";
    private static final int CAPTURE_IMAGE_CODE = 35;
    public static final int LIKE_POST_REQUEST = 25;
    public static final int MESSAGE_REQUEST_CODE = 99;
    public static final int LIKES_REQUEST = 111;

    private TextView tvName, tvMajorYear;
    private ImageView ivProfileImage, btnChangeProfileImage;
    private Button btnLogout, btnMessage;

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;
    private androidx.appcompat.widget.Toolbar toolbar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);
    private CollectionReference usersRef = db.collection(User.KEY_USERS);
    private CollectionReference surveysRef = db.collection(SurveyResponse.KEY_SURVEY_RESPONSE);
    private CollectionReference chatsRef = db.collection(Chat.KEY_CHATS);
    private String userId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangeProfileImage = view.findViewById(R.id.btnChangeProfileImage);
        btnMessage = view.findViewById(R.id.btnMessage);
        tvName = view.findViewById(R.id.tvName);
        tvMajorYear = view.findViewById(R.id.tvMajorYear);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        rvPosts = view.findViewById(R.id.rvPosts);

        // configure swipe refresh container
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                loadPosts();
            }
        });

        // show logout and change photo buttons, hide message button
        if (firebaseAuth.getCurrentUser().getUid().equals(userId)) {
            btnLogout.setVisibility(View.VISIBLE);
            btnChangeProfileImage.setVisibility(View.VISIBLE);
            btnMessage.setVisibility(View.GONE);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDialog dialog = MessageDialog.newInstance();
                dialog.setTargetFragment(ProfileFragment.this, MESSAGE_REQUEST_CODE);
                dialog.show(getFragmentManager(), "MessageDialog");
            }
        });

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, this);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        getUserInfo();
        loadPosts();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_likes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getContext(), LikesActivity.class);
        startActivityForResult(intent, LIKES_REQUEST);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        return super.onOptionsItemSelected(item);
    }

    private void getUserInfo() {
        usersRef.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user.getProfileUrl() != "") {
                        Glide.with(getContext()).load(user.getProfileUrl()).circleCrop().into(ivProfileImage);
                    }
                    tvName.setText(user.getName());
                    tvMajorYear.setText(user.getMajor() + ", " + user.getYear());
                } else {
                    Log.e(TAG, "Error retrieving user data! ", task.getException());
                    Toast.makeText(getContext(), "Error retrieving user data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPosts() {
        postsRef.whereEqualTo(Post.KEY_USER_ID, userId).orderBy(Post.KEY_CREATED_AT, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        adapter.clear();
                        // QueryDocumentSnapshots are guaranteed to exist
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Post post = documentSnapshot.toObject(Post.class);

                            allPosts.add(post);
                        }
                        adapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                    }
                });
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
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == LIKE_POST_REQUEST) {
            Parcelable updatedPostParcel = data.getParcelableExtra("updatedPost");

            if (updatedPostParcel != null) {
                Post updatedPost = Parcels.unwrap(updatedPostParcel);

                // find adapter position (where the tweet was)
                int position = -1;
                for (int i = 0; i < allPosts.size(); i++) {
                    if (allPosts.get(i).getPostId().equals(updatedPost.getPostId())) {
                        position = i;
                        break;
                    }
                }
                allPosts.remove(position);
                allPosts.add(position, updatedPost);
                adapter.notifyItemChanged(position);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(userId + ".jpeg");

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
                        updateUser(uri);
                    }
                });
    }

    private void updateUser(final Uri uri) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        firebaseAuth.getCurrentUser().updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Profile image updated successfully!", Toast.LENGTH_SHORT).show();
                        Glide.with(getContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl()).circleCrop().into(ivProfileImage);
                        usersRef.document(userId).update(User.KEY_PROFILE_URL, firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                        surveysRef.document(userId).update(SurveyResponse.KEY_IMAGE_URL, firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                        loadPosts();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile image upload failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void sendMessage(final String message) {
        String currUserId = firebaseAuth.getCurrentUser().getUid();
        final String chatDocId, memberOne, memberTwo;
        final ArrayList<String> members = new ArrayList<>();
        final boolean currUserIsOne, oneRead, twoRead;

        if (currUserId.compareTo(userId) > 0) {
            chatDocId = currUserId + userId;
            members.add(currUserId);
            members.add(userId);
            currUserIsOne = true;
            oneRead = true;
            twoRead = false;
        } else {
            chatDocId = userId + currUserId;
            members.add(userId);
            members.add(currUserId);
            currUserIsOne = false;
            oneRead = false;
            twoRead = true;
        }

        chatsRef.document(chatDocId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) { // just update is read value for member one or two
                    Map<String, Object> newValues = new HashMap<>();
                    newValues.put(Chat.KEY_LAST_MESSAGE, message);
                    newValues.put(Chat.KEY_LAST_MESSAGE_DATE, new java.util.Date());

                    if (currUserIsOne) { // update member two's read value to false
                        newValues.put(Chat.KEY_MEMBER_TWO_READ, false);
                    } else { // update member one's read value to false
                        newValues.put(Chat.KEY_MEMBER_ONE_READ, false);
                    }

                    chatsRef.document(chatDocId).update(newValues);
                    updateMessageCollection(message, chatDocId);
                } else { // create new chat
                    Chat newChat = new Chat(message, members, new java.util.Date(), oneRead ,twoRead, chatDocId);
                    chatsRef.document(chatDocId).set(newChat)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updateMessageCollection(message, chatDocId);
                                }
                            });

                }
            }
        });
    }

    private void updateMessageCollection(String message, String chatDocId) {
        Message newMessage = new Message(new java.util.Date(), message, firebaseAuth.getCurrentUser().getUid());

        chatsRef.document(chatDocId).collection(Message.KEY_MESSAGES).add(newMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
