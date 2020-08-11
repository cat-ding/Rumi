package com.example.rumi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rumi.adapters.MessagesAdapter;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.fragments.ProfileFragment;
import com.example.rumi.models.Chat;
import com.example.rumi.models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = "MessageActivity";
    public static final String KEY_OTHER_NAME = "otherName";
    public static final String KEY_OTHER_PROFILE_IMAGE = "otherProfileImage";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;

    private Chat chat;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference chatsRef = db.collection(Chat.KEY_CHATS);
    private CollectionReference messagesRef;
    private String currId = firebaseAuth.getCurrentUser().getUid();
    private String otherId;

    private ImageView ivOtherProfileImage;
    private TextView tvOtherName;
    private EditText etMessage;
    private ImageView btnSend;

    private boolean otherReadStatus;
    private String otherReadKey;
    private boolean isCurrUserMemberOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        rvMessages = findViewById(R.id.rvMessages);
        ivOtherProfileImage = findViewById(R.id.ivOtherProfileImage);
        tvOtherName = findViewById(R.id.tvOtherName);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(MessageActivity.this, allMessages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this, LinearLayoutManager.VERTICAL, true);
        rvMessages.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);

        chat = (Chat) Parcels.unwrap(getIntent().getParcelableExtra(Chat.class.getSimpleName()));
        messagesRef = chatsRef.document(chat.getChatId()).collection(Message.KEY_MESSAGES);

        // setting read booleans to toggle blue unread icon later
        if (chat.getMembers().get(0).equals(currId)) {
            if (!chat.isMemberOneRead()) {
                chat.setMemberOneRead(true); // set your own read status to true
                chatsRef.document(chat.getChatId()).update(Chat.KEY_MEMBER_ONE_READ, true); // update your own
            }
            isCurrUserMemberOne = true;
            otherReadStatus = chat.isMemberTwoRead(); // needed to decide whether or not to update this later
            otherId = chat.getMembers().get(1); // get other's id
            otherReadKey = Chat.KEY_MEMBER_TWO_READ; // needed to know which member's read bool to change later
        } else {
            if (!chat.isMemberTwoRead()) {
                chat.setMemberTwoRead(true);
                chatsRef.document(chat.getChatId()).update(Chat.KEY_MEMBER_TWO_READ, true);
            }
            isCurrUserMemberOne = false;
            otherReadStatus = chat.isMemberOneRead();
            otherId = chat.getMembers().get(0);
            otherReadKey = Chat.KEY_MEMBER_ONE_READ;
        }

        // setting other user's profile picture and name
        String url = getIntent().getStringExtra(KEY_OTHER_PROFILE_IMAGE);
        tvOtherName.setText(getIntent().getStringExtra(KEY_OTHER_NAME));
        if (!url.isEmpty())
            Glide.with(getApplicationContext()).load(url).circleCrop().into(ivOtherProfileImage);

        // onClick to go to other user's profile
        ivOtherProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = new ProfileFragment(otherId);
                fragmentManager.beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    // auto populates all messages at beginning and will then update in real time if any messages are sent
    @Override
    protected void onStart() {
        super.onStart();
        messagesRef.orderBy(Message.KEY_CREATED_AT, Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MessageActivity.this, "Error loading messages!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error loading messages: ", error);
                        }

                        adapter.clear();
                        for (DocumentSnapshot doc : snapshots) {
                            Message message = doc.toObject(Message.class);
                            allMessages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        rvMessages.smoothScrollToPosition(0);
                    }
                });
    }

    // onClick method for btnSend
    public void sendMessage(View view) {
        String messageBody = etMessage.getText().toString().trim();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Message can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otherReadStatus) {
            chatsRef.document(chat.getChatId()).update(otherReadKey, false); // update
        }

        etMessage.getText().clear();
        Message message = new Message(new java.util.Date(), messageBody, currId);
        allMessages.add(0, message);
        adapter.notifyItemInserted(0);
        rvMessages.smoothScrollToPosition(0);

        // update lastMessage, lastMessageDate of the chat document, and add a new message document
        Map<String, Object> updatedValues = new HashMap<>();
        updatedValues.put(Chat.KEY_LAST_MESSAGE, messageBody);
        updatedValues.put(Chat.KEY_LAST_MESSAGE_DATE, message.getCreatedAt());
        chatsRef.document(chat.getChatId()).update(updatedValues);
        chatsRef.document(chat.getChatId()).collection(Message.KEY_MESSAGES).add(message);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        updatedChat();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updatedChat();
    }

    private void updatedChat() {
        Map<String, Object> newChat = new HashMap<>();
        newChat.put(Chat.KEY_LAST_MESSAGE, allMessages.get(0).getBody());
        newChat.put(Chat.KEY_LAST_MESSAGE_DATE, allMessages.get(0).getCreatedAt());
        if (isCurrUserMemberOne) {
            newChat.put(Chat.KEY_MEMBER_ONE_READ, true);
        } else {
            newChat.put(Chat.KEY_MEMBER_TWO_READ, true);
        }
        chatsRef.document(chat.getChatId()).update(newChat);
    }
}