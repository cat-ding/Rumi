package com.example.rumi.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.rumi.CommentsActivity;
import com.example.rumi.LikesActivity;
import com.example.rumi.MainActivity;
import com.example.rumi.models.Comment;
import com.example.rumi.models.Post;
import com.example.rumi.PostDetailActivity;
import com.example.rumi.R;
import com.example.rumi.models.User;
import com.example.rumi.fragments.PostsFragment;
import com.example.rumi.fragments.ProfileFragment;
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
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    private static final int REQUEST_CODE = 25;
    public static final int COMMENTS_REQUEST_CODE = 88;
    private static final String LOOKING_FOR_HOUSE_STRING = "Looking for: ";
    private static final String LOOKING_FOR_PERSON_STRING = "Offering: ";
    public static final int RADIUS = 30;
    private Context context;
    private List<Post> posts;
    private Fragment fragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);
    private CollectionReference commentsRef = db.collection(Comment.KEY_COMMENTS);

    public PostsAdapter(Context context, List<Post> posts, Fragment fragment) {
        this.context = context;
        this.posts = posts;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAllPosts(List<Post> postsList) {
        posts.addAll(postsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUserName, tvTitle, tvDescription, tvRelativeTime, tvStatus, tvValues,
                tvNumLikes, tvNumComments;
        private ImageView ivProfileImage, ivImage, ivLike, ivComment, ivHeartAnim;
        private ArrayList<String> likeList;
        private int numLikes;
        private AnimatedVectorDrawable avdHeart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvValues = itemView.findViewById(R.id.tvValues);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            tvNumComments = itemView.findViewById(R.id.tvNumComments);
            ivHeartAnim = itemView.findViewById(R.id.ivHeartAnim);

            final Drawable drawable = ivHeartAnim.getDrawable();

            // setting single tap and double tap listeners
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        ivHeartAnim.setAlpha(0.7f);
                        avdHeart = (AnimatedVectorDrawable) drawable;
                        avdHeart.start();
                        if ((int)ivLike.getTag() == R.drawable.ic_baseline_favorite_border_24) {
                            likePost(posts.get(getAdapterPosition()));
                        }
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            Post detailPost = posts.get(position);
                            Intent intent = new Intent(context, PostDetailActivity.class);
                            intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(detailPost));
                            if (fragment != null) {
                                fragment.startActivityForResult(intent, REQUEST_CODE);
                                ((MainActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                            else {
                                ((LikesActivity) context).startActivityForResult(intent, REQUEST_CODE);
                                ((LikesActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        public void bind(final Post post) {

            commentsRef.whereEqualTo(Comment.KEY_POST_ID, post.getPostId()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int numComments = queryDocumentSnapshots.size();
                            if (numComments == 1) {
                                tvNumComments.setText(numComments + " comment");
                            } else {
                                tvNumComments.setText(numComments + " comments");
                            }
                        }
                    });

            likeList = post.getLikes();
            numLikes = post.getLikes().size();
            updateNumLikes(post.getLikes().size());
            // set like icon filled or not
            if (likeList.contains(firebaseAuth.getCurrentUser().getUid())) {
                ivLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                ivLike.setTag(R.drawable.ic_baseline_favorite_24);
            } else {
                ivLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                ivLike.setTag(R.drawable.ic_baseline_favorite_border_24);
            }

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likePost(post);
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
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra(Post.KEY_POPULARITY, post.getPopularity());
                    intent.putExtra(Post.KEY_POST_ID, post.getPostId());

                    if (fragment != null) {
                        fragment.startActivityForResult(intent, COMMENTS_REQUEST_CODE);
                    }
                    else {
                        ((LikesActivity) context).startActivityForResult(intent, COMMENTS_REQUEST_CODE);
                    }
                }
            });

            // bind values
            tvTitle.setText(post.getTitle());
            tvDescription.setText(post.getDescription());
            tvRelativeTime.setText(post.getRelativeTime());

            if (!post.getPhotoUrl().isEmpty() && post.getPhotoUrl() != null) {
                Glide.with(context).load(post.getPhotoUrl())
                        .transform(new RoundedCorners(RADIUS)).into(ivImage);
                ivImage.setVisibility(View.VISIBLE);
            } else {
                ivImage.setVisibility(View.GONE);
            }

            if (post.isLookingForHouse()) {
                tvStatus.setText(LOOKING_FOR_HOUSE_STRING);
            } else {
                tvStatus.setText(LOOKING_FOR_PERSON_STRING);
            }

            tvValues.setText(post.getNumRooms() + " room(s) | $" + post.getRent() + " /mo | "
                    + post.getStartDate() + " to " + post.getEndDate());

            bindUserFields(post);
        }

        private void likePost(Post post) {
            int postPopularity = post.getPopularity();
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

        private void updateNumLikes(int numLikes) {
            if (numLikes == 1) {
                tvNumLikes.setText(numLikes + " like");
            } else {
                tvNumLikes.setText(numLikes + " likes");
            }
        }

        private void updatePopularity(String postId, int postPopularity) {
            postsRef.document(postId).update(Post.KEY_POPULARITY, postPopularity).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: unable to update popularity", e.getCause());
                }
            });
        }

        private void openProfileFragment(String userId) {
            FragmentManager fragmentManager;
            if (fragment != null) {
                fragmentManager = ((MainActivity)context).getSupportFragmentManager();
            } else {
                fragmentManager = ((LikesActivity)context).getSupportFragmentManager();
            }
            Fragment newFragment = new ProfileFragment(userId);
            fragmentManager.beginTransaction().replace(R.id.flContainer, newFragment).addToBackStack(null).commit();
        }

        private void bindUserFields(Post post) {
            usersRef.document(post.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        tvUserName.setText(task.getResult().getString(Post.KEY_NAME));

                        String profileUrl = task.getResult().getString(User.KEY_PROFILE_URL);
                        if (!profileUrl.isEmpty() && profileUrl != null) {
                            Glide.with(context).load(task.getResult().getString(User.KEY_PROFILE_URL)).circleCrop().into(ivProfileImage);
                        }
                    } else {
                        Log.e(TAG, "Error retrieving user data! ", task.getException());
                    }
                }
            });
        }
    }
}

