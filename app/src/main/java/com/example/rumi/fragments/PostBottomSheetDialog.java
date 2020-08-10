package com.example.rumi.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.rumi.CommentsActivity;
import com.example.rumi.FilterConstants;
import com.example.rumi.PostDetailActivity;
import com.example.rumi.R;
import com.example.rumi.models.Post;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class PostBottomSheetDialog extends BottomSheetDialogFragment {

    public static final String TAG = "PostBottomSheetDialog";
    private static final String KEY_POST = "post";
    public static final int RADIUS = 30;
    private static final String LOOKING_FOR_HOUSE_STRING = "Looking for: ";
    private static final String LOOKING_FOR_PERSON_STRING = "Offering: ";

    private TextView tvName, tvDone;

    private Post post;
    private TextView tvUserName, tvTitle, tvDescription, tvRelativeTime, tvStatus, tvMajorYear,
            tvNumRooms, tvRent, tvDuration, tvFurnished, tvAddress, tvNumLikes;
    private ImageView ivProfileImage, ivImage, ivComment, ivLike, ivHeartAnim;
    private ArrayList<String> likeList;
    private int numLikes, postPopularity;
    private RelativeLayout relativeLayout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);

    private FrameLayout flContainer;
    private AnimatedVectorDrawable avdHeart;

    public static PostBottomSheetDialog newInstance(Post post) {
        Bundle args = new Bundle();

        PostBottomSheetDialog fragment = new PostBottomSheetDialog();
        args.putSerializable(KEY_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        tvDone = view.findViewById(R.id.tvDone);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvRelativeTime = view.findViewById(R.id.tvRelativeTime);
        tvStatus = view.findViewById(R.id.tvStatus);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvMajorYear = view.findViewById(R.id.tvMajorYear);
        tvNumRooms = view.findViewById(R.id.tvNumRooms);
        tvRent = view.findViewById(R.id.tvRent);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvFurnished = view.findViewById(R.id.tvFurnished);
        ivImage = view.findViewById(R.id.ivImage);
        tvAddress = view.findViewById(R.id.tvAddress);
        ivComment = view.findViewById(R.id.ivComment);
        ivLike = view.findViewById(R.id.ivLike);
        tvNumLikes = view.findViewById(R.id.tvNumLikes);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        ivHeartAnim = view.findViewById(R.id.ivHeartAnim);

        post = (Post) getArguments().getSerializable(KEY_POST);
        postPopularity = post.getPopularity();

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

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra(Post.KEY_POST_ID, post.getPostId());
                startActivity(intent);
                // TODO: slide up/down animation for comments
            }
        });

        setFields();
    }

    private void openProfileFragment(String userId) {
        FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
        Fragment fragment = new ProfileFragment(userId);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    private void setHeartAnimation() {
        final Drawable drawable = ivHeartAnim.getDrawable();
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
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

    private void updatePopularity(String postId, int postPopularity) {
        postsRef.document(postId).update(Post.KEY_POPULARITY, postPopularity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: unable to update popularity", e.getCause());
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

    private void setFields() {
        tvName.setText(post.getName());

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvTitle.setText(post.getTitle());
        tvDescription.setText(post.getDescription());
        tvRelativeTime.setText(post.getRelativeTime());

        tvNumRooms.setText("" + post.getNumRooms());
        tvRent.setText("$" + post.getRent());
        tvDuration.setText(post.getStartDate() + " to " + post.getEndDate());

        tvAddress.setText(post.getAddress());

        if (!post.getPhotoUrl().isEmpty() && post.getPhotoUrl() != null) {
            Glide.with(getContext()).load(post.getPhotoUrl())
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
                        Glide.with(getContext()).load(task.getResult().getString(User.KEY_PROFILE_URL)).circleCrop().into(ivProfileImage);
                    }
                } else {
                    Log.e(TAG, "Error retrieving user data! ", task.getException());
                }
            }
        });
    }
}
