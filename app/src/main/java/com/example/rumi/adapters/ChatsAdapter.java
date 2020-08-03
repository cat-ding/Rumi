package com.example.rumi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rumi.MainActivity;
import com.example.rumi.MessageActivity;
import com.example.rumi.R;
import com.example.rumi.models.Chat;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    public static final String TAG = "ChatsAdapter";
    private static final int REQUEST_CODE_MESSAGE = 77;
    public static final String KEY_OTHER_NAME = "otherName";
    public static final String KEY_OTHER_PROFILE_IMAGE = "otherProfileImage";

    private Context context;
    private List<Chat> chats;
    private Fragment fragment;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection(User.KEY_USERS);


    public ChatsAdapter(Context context, List<Chat> chats, Fragment fragment) {
        this.context = context;
        this.chats = chats;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void clear() {
        chats.clear();
        notifyDataSetChanged();
    }

    public void addAllChats(List<Chat> chatsList) {
        chats.addAll(chatsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage, ivUnread;
        TextView tvUserName, tvLastMessage;
        String otherName, otherProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            ivUnread = itemView.findViewById(R.id.ivUnread);

            itemView.setOnClickListener(this);
        }

        public void bind(Chat chat) {
            String currUserId = firebaseAuth.getCurrentUser().getUid();
            ArrayList<String> members = chat.getMembers();
            if (members.get(0).equals(currUserId)) {
                if (!chat.isMemberOneRead()) {
                    ivUnread.setVisibility(View.VISIBLE);
                } else {
                    ivUnread.setVisibility(View.GONE);
                }
                getUserInfo(members.get(1));
            } else {
                if (!chat.isMemberTwoRead()) {
                    ivUnread.setVisibility(View.VISIBLE);
                } else {
                    ivUnread.setVisibility(View.GONE);
                }
                getUserInfo(members.get(0));
            }

            tvLastMessage.setText(chat.getLastMessage());
        }

        private void getUserInfo(String userId) {
            usersRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    otherName = user.getName();
                    tvUserName.setText(user.getName());
                    if (!user.getProfileUrl().isEmpty()) {
                        Glide.with(context).load(user.getProfileUrl()).circleCrop().into(ivProfileImage);
                        otherProfileImage = user.getProfileUrl();
                    } else {
                        Glide.with(context).load("").circleCrop().into(ivProfileImage);
                        otherProfileImage = "";
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION
                    && otherName != null
                    && otherProfileImage != null) {
                Chat chat = chats.get(position);
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(Chat.class.getSimpleName(), Parcels.wrap(chat));
                intent.putExtra(KEY_OTHER_NAME, otherName);
                intent.putExtra(KEY_OTHER_PROFILE_IMAGE, otherProfileImage);
                fragment.startActivityForResult(intent, REQUEST_CODE_MESSAGE);
                ((MainActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }
}
