package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.rumi.fragments.ProfileFragment;
import com.example.rumi.models.Comment;
import com.example.rumi.models.Post;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";
    public static final int COMMENTS_REQUEST_CODE = 88;
    private static final String LOOKING_FOR_HOUSE_STRING = "Looking for: ";
    private static final String LOOKING_FOR_PERSON_STRING = "Offering: ";
    public static final int RADIUS = 30;
    private Post post;
    private TextView tvUserName, tvTitle, tvDescription, tvRelativeTime, tvStatus, tvMajorYear,
                    tvNumRooms, tvRent, tvDuration, tvFurnished, tvAddress, tvNumLikes, tvNumComments;
    private ImageView ivProfileImage, ivImage, ivComment, ivLike, ivHeartAnim;
    private ArrayList<String> likeList;
    private int numLikes, postPopularity;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);
    private CollectionReference commentsRef = db.collection(Comment.KEY_COMMENTS);

    private RelativeLayout layoutAddress;
    private RelativeLayout relativeLayout;

    private AnimatedVectorDrawable avdHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        postPopularity = post.getPopularity();

        tvUserName = findViewById(R.id.tvUserName);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvRelativeTime = findViewById(R.id.tvRelativeTime);
        tvStatus = findViewById(R.id.tvStatus);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvMajorYear = findViewById(R.id.tvMajorYear);
        tvNumRooms = findViewById(R.id.tvNumRooms);
        tvRent = findViewById(R.id.tvRent);
        tvDuration = findViewById(R.id.tvDuration);
        tvFurnished = findViewById(R.id.tvFurnished);
        ivImage = findViewById(R.id.ivImage);
        tvAddress = findViewById(R.id.tvAddress);
        ivComment = findViewById(R.id.ivComment);
        ivLike = findViewById(R.id.ivLike);
        tvNumLikes = findViewById(R.id.tvNumLikes);
        tvNumComments = findViewById(R.id.tvNumComments);
        layoutAddress = findViewById(R.id.layoutAddress);
        relativeLayout = findViewById(R.id.relativeLayout);

        ivHeartAnim = findViewById(R.id.ivHeartAnim);

        setHeartAnimation();

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost();
            }
        });

        // onClickListeners to open ProfileFragment
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileFragment(post.getUserId());
            }
        });
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileFragment(post.getUserId());
            }
        });

        setFields();
    }

    private void setHeartAnimation() {
        final Drawable drawable = ivHeartAnim.getDrawable();
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(PostDetailActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    ivHeartAnim.setAlpha(0.7f);
                    avdHeart = (AnimatedVectorDrawable) drawable;
                    avdHeart.start();
                    if ((int)ivLike.getTag() == R.drawable.ic_baseline_favorite_border_24) {
                        likePost();
                    }
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void likePost() {
        if ((int)ivLike.getTag() == R.drawable.ic_baseline_favorite_border_24) {
            ivLike.setImageResource(R.drawable.ic_baseline_favorite_24);
            ivLike.setTag(R.drawable.ic_baseline_favorite_24);
            db.collection(Post.KEY_POSTS).document(post.getPostId())
                    .update(Post.KEY_LIKES, FieldValue.arrayUnion(firebaseAuth.getCurrentUser().getUid()));
            likeList.add(firebaseAuth.getCurrentUser().getUid());
            numLikes++;
            postPopularity++;
        } else {
            ivLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            ivLike.setTag(R.drawable.ic_baseline_favorite_border_24);
            db.collection(Post.KEY_POSTS).document(post.getPostId())
                    .update(Post.KEY_LIKES, FieldValue.arrayRemove(firebaseAuth.getCurrentUser().getUid()));
            likeList.remove(firebaseAuth.getCurrentUser().getUid());
            numLikes--;
            postPopularity--;
        }
        post.setLikes(likeList);
        post.setPopularity(postPopularity);
        updateNumLikes(numLikes);
        updatePopularity(post.getPostId(), postPopularity);
    }

    private void openProfileFragment(String userId) {
        FragmentManager fragmentManager = (PostDetailActivity.this).getSupportFragmentManager();
        Fragment fragment = new ProfileFragment(userId);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    private void updatePopularity(String postId, int postPopularity) {
        postsRef.document(postId).update(Post.KEY_POPULARITY, postPopularity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: unable to update popularity", e.getCause());
            }
        });
    }

    private void setFields() {

        commentsRef.whereEqualTo(Comment.KEY_POST_ID, post.getPostId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        updateNumComments(queryDocumentSnapshots.size());
                    }
                });

        tvTitle.setText(post.getTitle());
        tvDescription.setText(post.getDescription());
        tvRelativeTime.setText(post.getRelativeTime());

        tvNumRooms.setText("" + post.getNumRooms());
        tvRent.setText("$" + post.getRent());
        tvDuration.setText(post.getStartDate() + " to " + post.getEndDate());

        if (!post.isLookingForHouse() && post.getLatitude() != 0 && post.getLongitude() != 0) { // check this when I have wifi
            layoutAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(post.getAddress());
        } else {
            layoutAddress.setVisibility(View.GONE);
        }

        if (!post.getPhotoUrl().isEmpty() && post.getPhotoUrl() != null) {
            Glide.with(PostDetailActivity.this).load(post.getPhotoUrl())
                    .transform(new RoundedCorners(RADIUS)).into(ivImage);
            ivImage.setVisibility(View.VISIBLE);
        } else {
            ivImage.setVisibility(View.GONE);
        }

        if (post.isFurnished()) {
            tvFurnished.setText("Yes");
        } else {
            tvFurnished.setText("No");
        }

        if (post.isLookingForHouse()) {
            tvStatus.setText(LOOKING_FOR_HOUSE_STRING);
        } else {
            tvStatus.setText(LOOKING_FOR_PERSON_STRING);
        }

        // set like icon filled or not
        likeList = post.getLikes();
        numLikes = likeList.size();
        updateNumLikes(post.getLikes().size());
        // set like icon filled or not
        if (likeList.contains(firebaseAuth.getCurrentUser().getUid())) {
            ivLike.setImageResource(R.drawable.ic_baseline_favorite_24);
            ivLike.setTag(R.drawable.ic_baseline_favorite_24);
        } else {
            ivLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            ivLike.setTag(R.drawable.ic_baseline_favorite_border_24);
        }

        usersRef.document(post.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    tvUserName.setText(task.getResult().getString(User.KEY_NAME));
                    tvMajorYear.setText(task.getResult().getString(User.KEY_MAJOR) + ", "
                            + task.getResult().getString(User.KEY_YEAR));

                    String profileUrl = task.getResult().getString(User.KEY_PROFILE_URL);
                    if (!profileUrl.isEmpty() && profileUrl != null) {
                        Glide.with(PostDetailActivity.this).load(task.getResult().getString(User.KEY_PROFILE_URL)).circleCrop().into(ivProfileImage);
                    }
                } else {
                    Log.e(TAG, "Error retrieving user data! ", task.getException());
                }
            }
        });
    }

    private void updateNumLikes(int numLikes) {
        if (numLikes == 1) {
            tvNumLikes.setText(numLikes + " like");
        } else {
            tvNumLikes.setText(numLikes + " likes");
        }
    }

    private void updateNumComments(int numComments) {
        if (numComments == 1) {
            tvNumComments.setText(numComments + " comment");
        } else {
            tvNumComments.setText(numComments + " comments");
        }
    }

    // onClick method for the comment icon (ivComment)
    public void goToComments(View view) {
        Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
        intent.putExtra(Post.KEY_POST_ID, post.getPostId());
        intent.putExtra(Post.KEY_POPULARITY, post.getPopularity());
        startActivityForResult(intent, COMMENTS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == COMMENTS_REQUEST_CODE) {
            int updatedPopularity = data.getIntExtra("updatedPopularity", 0);
            int updatedNumComments = data.getIntExtra("updatedNumComments", 0);
            post.setPopularity(updatedPopularity);
            postPopularity = updatedPopularity;
            updateNumComments(updatedNumComments);
        }
    }

    @Override
    public void onBackPressed() {
        // transmitting post object back to ProfileFragment
        Intent intent = new Intent();
        intent.putExtra("updatedPost", Parcels.wrap(post));
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}