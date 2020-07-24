package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rumi.adapters.CommentsAdapter;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";
    private String postId;
    private int postPopularity;
    private RecyclerView rvComments;
    private CommentsAdapter adapter;
    private List<Comment> allComments;

    private Button btnPost;
    private EditText etComment;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference commentsRef = db.collection(Comment.KEY_COMMENTS);
    private CollectionReference postsRef = db.collection(Post.KEY_POSTS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postId = getIntent().getStringExtra(Post.KEY_POST_ID);;
        postPopularity = getIntent().getIntExtra(Post.KEY_POPULARITY, 0);

        rvComments = findViewById(R.id.rvComments);
        btnPost = findViewById(R.id.btnPost);
        etComment = findViewById(R.id.etComment);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = etComment.getText().toString().trim();
                if (body.isEmpty()) {
                    Toast.makeText(CommentsActivity.this, "Sorry, your comment cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                etComment.getText().clear();
                postComment(body);
            }
        });

        allComments = new ArrayList<>();
        adapter = new CommentsAdapter(CommentsActivity.this, allComments);
        rvComments.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);

        queryComments();
    }

    private void queryComments() {
        commentsRef.whereEqualTo(Post.KEY_POST_ID, postId).orderBy(Post.KEY_CREATED_AT, Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        adapter.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Comment comment = documentSnapshot.toObject(Comment.class);
                            allComments.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void postComment(String body) {
        postPopularity++;
        final Comment comment = new Comment(postId, firebaseAuth.getCurrentUser().getUid(),
                UUID.randomUUID().toString(), body);
        commentsRef.document().set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CommentsActivity.this, "Comment created!", Toast.LENGTH_SHORT).show();
                queryComments();
            }
        });
        postsRef.document(postId).update(Post.KEY_POPULARITY, postPopularity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: unable to update popularity", e.getCause());
            }
        });
    }
}