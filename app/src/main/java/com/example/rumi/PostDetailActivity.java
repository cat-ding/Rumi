package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rumi.models.Post;
import com.example.rumi.models.User;
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
                    tvNumRooms, tvRent, tvStartDate, tvEndDate, tvFurnished, tvAddress;
    private ImageView ivProfileImage, ivImage, ivComment, ivLike;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);

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
        ivImage = findViewById(R.id.ivImage);
        tvAddress = findViewById(R.id.tvAddress);
        ivComment = findViewById(R.id.ivComment);
        ivLike = findViewById(R.id.ivLike);

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
        tvAddress.setText("Address: " + post.getAddress());

        if (!post.getPhotoUrl().equals("")) {
            Glide.with(PostDetailActivity.this).load(post.getPhotoUrl()).into(ivImage);
            ivImage.setVisibility(View.VISIBLE);
        }

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
                    tvUserName.setText(task.getResult().getString(User.KEY_NAME));
                    tvMajorYear.setText(task.getResult().getString(User.KEY_MAJOR) + ", "
                            + task.getResult().getString(User.KEY_YEAR));
                    if (task.getResult().getString(User.KEY_PROFILE_URL) != null) {
                        Glide.with(PostDetailActivity.this).load(task.getResult().getString(User.KEY_PROFILE_URL)).circleCrop().into(ivProfileImage);
                    }
                } else {
                    Log.e(TAG, "Error retrieving user data! ", task.getException());
                }
            }
        });
    }

    // onClick method for the comment icon (ivComment)
    public void goToComments(View view) {
        Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
        intent.putExtra(Post.KEY_POST_ID, post.getPostId());
        startActivity(intent);
    }
}