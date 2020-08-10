package com.example.rumi.adapters;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rumi.CommentsActivity;
import com.example.rumi.R;
import com.example.rumi.fragments.ProfileFragment;
import com.example.rumi.models.Comment;
import com.example.rumi.models.Post;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    public static final String TAG = "CommentsAdapter";
    private Context context;
    private List<Comment> comments;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);
    private CollectionReference commentsRef = db.collection(Comment.KEY_COMMENTS);
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAllComments(List<Comment> commentsList) {
        comments.addAll(commentsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView tvBody;
        TextView tvUserName;
        ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBody = itemView.findViewById(R.id.tvBody);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);

            itemView.setOnLongClickListener(this);
        }

        public void bind(final Comment comment) {
            // TODO: set onclick listener for profile picture
            tvBody.setText(comment.getBody());

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProfileFragment(comment.getUserId());
                }
            });
            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProfileFragment(comment.getUserId());
                }
            });

            bindUserFields(comment);
        }

        private void bindUserFields(Comment comment) {
            usersRef.document(comment.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        tvUserName.setText(task.getResult().getString(Post.KEY_NAME));

                        String profileUrl = task.getResult().getString(User.KEY_PROFILE_URL);
                        if (!profileUrl.isEmpty() && profileUrl != null) {
                            Glide.with(context.getApplicationContext()).load(task.getResult().getString(User.KEY_PROFILE_URL)).circleCrop().into(ivProfileImage);
                        }

                    } else {
                        Log.e(TAG, "Error retrieving user data! ", task.getException());
                    }
                }
            });
        }

        private void openProfileFragment(String userId) {
            FragmentManager fragmentManager = ((CommentsActivity)context).getSupportFragmentManager();
            Fragment fragment = new ProfileFragment(userId);
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        }

        @Override
        public boolean onLongClick(View view) {
            final Comment commentToDelete = comments.get(getAdapterPosition());
            if (comments.get(getAdapterPosition()).getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                new AlertDialog.Builder(context)
                        .setMessage("Do you want to delete this comment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                commentsRef.document(commentToDelete.getCommentId()).delete();
                                CommentsActivity.postPopularity-=5;
                                postsRef.document(commentToDelete.getPostId()).update(Post.KEY_POPULARITY,
                                        CommentsActivity.postPopularity).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: unable to update popularity", e.getCause());
                                    }
                                });

                                comments.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            }})
                        .setNegativeButton("No", null).show();
            }

            return true;
        }
    }
}