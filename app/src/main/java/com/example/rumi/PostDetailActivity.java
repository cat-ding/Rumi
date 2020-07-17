package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";
    private static final String LOOKING_FOR_HOUSE_STRING = "Looking for: ";
    private static final String LOOKING_FOR_PERSON_STRING = "Offering: ";
    Post post;
    private TextView tvUserName, tvTitle, tvDescription, tvRelativeTime, tvStatus, tvMajorYear,
                    tvNumRooms, tvRent, tvStartDate, tvEndDate, tvFurnished;
    private ImageView ivProfileImage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvUserName = findViewById(R.id.tvUserName);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvRelativeTime = findViewById(R.id.tvRelativeTime);
        tvStatus = findViewById(R.id.tvStatus);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvMajorYear = findViewById(R.id.tvMajorYear);
        tvNumRooms = findViewById(R.id.tvNumRooms);
        tvRent = findViewById(R.id.tvRent);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvFurnished = findViewById(R.id.tvFurnished);

        setFields(post);
    }

    private void setFields(Post post) {
        tvTitle.setText(post.getTitle());
        tvDescription.setText(post.getDescription());
        tvRelativeTime.setText(post.getRelativeTime());

        tvNumRooms.setText("Number of Rooms: " + post.getNumRooms());
        tvRent.setText("Monthly Rent per Room: $" + post.getRent());
        tvStartDate.setText("Start Date: " + post.getStartDate());
        tvEndDate.setText("End Date: " + post.getEndDate());

        if (post.isFurnished()) {
            tvFurnished.setText("Furnished: YES");
        } else {
            tvFurnished.setText("Furnished: NO");
        }

        if (post.isLookingForHouse()) {
            tvStatus.setText(LOOKING_FOR_HOUSE_STRING);
        } else {
            tvStatus.setText(LOOKING_FOR_PERSON_STRING);
        }

        usersRef.document(post.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    tvUserName.setText(task.getResult().getString("name"));
                    tvMajorYear.setText(task.getResult().getString("major") + ", Class of "
                            + task.getResult().getString("year"));
                    if (task.getResult().getString("profileUrl") != null) {
                        Glide.with(PostDetailActivity.this).load(task.getResult().getString("profileUrl")).circleCrop().into(ivProfileImage);
                    }
                } else {
                    Log.e(TAG, "Error retrieving user data! ", task.getException());
                }
            }
        });
    }
}