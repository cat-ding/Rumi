package com.example.rumi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumi.Post;
import com.example.rumi.PostsAdapter;
import com.example.rumi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";

    protected RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postsRef = db.collection("posts");

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, this);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        //TODO: add endless recycler view scroll listener

        loadPosts();
    }

    private void loadPosts() {
        postsRef.get()
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
                    }
                });
    }

}
