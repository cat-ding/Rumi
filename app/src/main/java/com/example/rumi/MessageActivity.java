package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.rumi.adapters.MessagesAdapter;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.models.Chat;
import com.example.rumi.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = "MessageActivity";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;

    private Chat chat;
    private boolean changed = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference messagesRef = db.collection(Message.KEY_MESSAGES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        rvMessages = findViewById(R.id.rvMessages);

        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(MessageActivity.this, allMessages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
        rvMessages.setLayoutManager(layoutManager);

        chat = (Chat) Parcels.unwrap(getIntent().getParcelableExtra(Chat.class.getSimpleName()));

        getMessages();
    }

    private void getMessages() {
        // TODO
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