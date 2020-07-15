package com.example.rumi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rumi.fragments.PostsFragment;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    private static final int REQUEST_CODE = 25;
    private static final String LOOKING_FOR_HOUSE_STRING = "Looking for: ";
    private static final String LOOKING_FOR_PERSON_STRING = "Offering: ";
    public static final String IS_FURNISHED = ", is furnished";
    public static final String IS_NOT_FURNISHED = ", is not furnished";
    private Context context;
    private List<Post> posts;
    private PostsFragment fragment;

    public PostsAdapter(Context context, List<Post> posts, PostsFragment fragment) {
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

        //TODO
//        private RelativeLayout relativeLayout;
//        private ImageView ivLike;
//        private ImageView ivComment;
//        private TextView tvNumLikes;
//        private Integer numLikes;
//        private ImageView ivImage;

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
            tvUserName.setText(post.getUserName());

            if (post.isLookingForHouse()) {
                tvStatus.setText(LOOKING_FOR_HOUSE_STRING);
            } else {
                tvStatus.setText(LOOKING_FOR_PERSON_STRING);
            }

            String furnished;
            if (post.isFurnished()) {
                furnished = IS_FURNISHED;
            } else {
                furnished = IS_NOT_FURNISHED;
            }
            tvValues.setText(post.getNumRooms() + " room(s), $" + post.getRent() + " per month per room, "
                    + post.getDuration() + " months, starting " + post.getStartMonth() + furnished);

            // TODO: make sure profile image and relative time display correctly
             tvRelativeTime.setText(post.getRelativeTime());
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

