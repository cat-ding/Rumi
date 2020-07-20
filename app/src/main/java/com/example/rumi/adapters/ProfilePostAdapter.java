package com.example.rumi.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rumi.models.Post;
import com.example.rumi.PostDetailActivity;
import com.example.rumi.R;
import com.example.rumi.models.User;
import com.example.rumi.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder> {

    public static final String TAG = "ProfilePostAdapter";
    private static final int REQUEST_CODE = 35;
    private static final String LOOKING_FOR_HOUSE_STRING = "Looking for: ";
    private static final String LOOKING_FOR_PERSON_STRING = "Offering: ";
    private Context context;
    private List<Post> posts;
    private ProfileFragment fragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);

    public ProfilePostAdapter(Context context, List<Post> posts, ProfileFragment fragment) {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUserName, tvTitle, tvDescription, tvRelativeTime, tvStatus, tvValues;
        private ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvValues = itemView.findViewById(R.id.tvValues);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);

            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            // bind the post data to the view elements

            tvTitle.setText(post.getTitle());
            tvDescription.setText(post.getDescription());
            tvRelativeTime.setText(post.getRelativeTime());

            if (post.isLookingForHouse()) {
                tvStatus.setText(LOOKING_FOR_HOUSE_STRING);
            } else {
                tvStatus.setText(LOOKING_FOR_PERSON_STRING);
            }

            tvValues.setText(post.getNumRooms() + " room(s) | $" + post.getRent() + " /mo | "
                    + post.getDuration() + " months starting " + post.getStartMonth());

            bindUserFields(post);
        }

        private void bindUserFields(Post post) {
            usersRef.document(post.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        tvUserName.setText(task.getResult().getString(User.KEY_NAME));
                        if (task.getResult().getString(User.KEY_PROFILE_URL) != null) {
                            Glide.with(context).load(task.getResult().getString(User.KEY_PROFILE_URL)).circleCrop().into(ivProfileImage);
                        }
                    } else {
                        Log.e(TAG, "Error retrieving user data! ", task.getException());
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                fragment.startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }
}

