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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = "MessageActivity";
    public static final String KEY_OTHER_NAME = "otherName";
    public static final String KEY_OTHER_PROFILE_IMAGE = "otherProfileImage";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;

    private Chat chat;
    private boolean readChanged = false;
    private boolean messageChanged = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference chatsRef = db.collection(Chat.KEY_CHATS);
    private CollectionReference messagesRef;
    private String currId = firebaseAuth.getCurrentUser().getUid();
    private String otherId;

    private ImageView ivOtherProfileImage;
    private TextView tvOtherName;
    private EditText etMessage;
    private Button btnSend;

    private boolean otherReadStatus;
    private String otherReadKey;

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

        // setting read booleans to toggle blue unread icon later
        Log.d(TAG, "onCreate: " + chat.getMembers().get(0));
        Log.d(TAG, "onCreate: " + chat.getMembers().get(1));
        if (chat.getMembers().get(0).equals(currId)) {
            if (!chat.isMemberOneRead()) {
                readChanged = true; // to know if we need to pass a chat back to update chat recyclerview (onBackPressed)
                chat.setMemberOneRead(true); // set your own read status to true
                chatsRef.document(chat.getChatId()).update(Chat.KEY_MEMBER_ONE_READ, true); // update your own
            }
            otherReadStatus = chat.isMemberTwoRead(); // needed to decide whether or not to update this later
            Log.d(TAG, "memberTwoRead: " + otherReadStatus);
            otherId = chat.getMembers().get(1); // get other's id
            otherReadKey = Chat.KEY_MEMBER_TWO_READ; // needed to know which member's read bool to change later
        } else {
            if (!chat.isMemberTwoRead()) {
                readChanged = true;
                chat.setMemberTwoRead(true);
                chatsRef.document(chat.getChatId()).update(Chat.KEY_MEMBER_TWO_READ, true);
            }
            otherReadStatus = chat.isMemberOneRead();
            Log.d(TAG, "memberOneRead: " + otherReadStatus);
            otherId = chat.getMembers().get(0);
            otherReadKey = Chat.KEY_MEMBER_ONE_READ;
        }

        // setting other user's profile picture and name
        String url = getIntent().getStringExtra(KEY_OTHER_PROFILE_IMAGE);
        tvOtherName.setText(getIntent().getStringExtra(KEY_OTHER_NAME));
        if (!url.isEmpty())
            Glide.with(this).load(url).circleCrop().into(ivOtherProfileImage);

        // onClick to go to other user's profile
        ivOtherProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = new ProfileFragment(otherId);
                fragmentManager.beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        chatsRef.document(chat.getChatId()).collection(Message.KEY_MESSAGES)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // TODO: listen to changes + update adapter
                    }
                });

        getMessages();
    }

    private void getMessages() {
        messagesRef = chatsRef.document(chat.getChatId()).collection(Message.KEY_MESSAGES);
        messagesRef.orderBy(Message.KEY_CREATED_AT, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        adapter.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Message message = documentSnapshot.toObject(Message.class);

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
        }

        if (otherReadStatus) {
            Log.d(TAG, "sendMessage: HEREE");
            chatsRef.document(chat.getChatId()).update(otherReadKey, false); // update
        }

        etMessage.getText().clear();
        messageChanged = true;
        Message message = new Message(new java.util.Date(), messageBody, currId);
        allMessages.add(0, message);
        adapter.notifyItemInserted(0);

        chatsRef.document(chat.getChatId()).collection(Message.KEY_MESSAGES).add(message);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {

        if (readChanged || messageChanged) {
            if (messageChanged) { // update last message if changed
                // transmitting updated chat object back
                String lastMessage = allMessages.get(0).getBody();
                Date lastMessageDate = allMessages.get(0).getCreatedAt();
                chatsRef.document(chat.getChatId()).update(Chat.KEY_LAST_MESSAGE, lastMessage);
                chatsRef.document(chat.getChatId()).update(Chat.KEY_LAST_MESSAGE_DATE, lastMessageDate);
                chat.setLastMessage(lastMessage);
                chat.setLastMessageDate(lastMessageDate);
            }
            // if only read status changed or message new message was sent, sent updated chat
            Intent intent = new Intent();
            intent.putExtra("updatedChat", Parcels.wrap(chat));
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}