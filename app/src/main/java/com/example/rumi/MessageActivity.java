package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rumi.adapters.MessagesAdapter;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.models.Chat;
import com.example.rumi.models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = "MessageActivity";
    public static final String KEY_OTHER_NAME = "otherName";
    public static final String KEY_OTHER_PROFILE_IMAGE = "otherProfileImage";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;

    private Chat chat;
    private boolean changed = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference chatsRef = db.collection(Chat.KEY_CHATS);
    private CollectionReference messagesRef;

    private ImageView ivOtherProfileImage;
    private TextView tvOtherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        rvMessages = findViewById(R.id.rvMessages);
        ivOtherProfileImage = findViewById(R.id.ivOtherProfileImage);
        tvOtherName = findViewById(R.id.tvOtherName);

        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(MessageActivity.this, allMessages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
        rvMessages.setLayoutManager(layoutManager);

        chat = (Chat) Parcels.unwrap(getIntent().getParcelableExtra(Chat.class.getSimpleName()));
        String url = getIntent().getStringExtra(KEY_OTHER_PROFILE_IMAGE);
        tvOtherName.setText(getIntent().getStringExtra(KEY_OTHER_NAME));
        if (!url.isEmpty())
            Glide.with(this).load(url).circleCrop().into(ivOtherProfileImage);

        getMessages();
    }

    private void getMessages() {
        messagesRef = chatsRef.document(chat.getChatId()).collection(Message.KEY_MESSAGES);
        messagesRef.orderBy(Message.KEY_CREATED_AT, Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        adapter.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Message message = documentSnapshot.toObject(Message.class);

                            allMessages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (changed) {
            // transmitting updated chat object back
            Intent intent = new Intent();
            intent.putExtra("updatedChat", Parcels.wrap(chat));
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}