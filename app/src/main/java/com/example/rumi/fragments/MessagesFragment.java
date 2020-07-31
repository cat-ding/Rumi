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

import com.example.rumi.R;
import com.example.rumi.adapters.ChatsAdapter;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.models.Chat;
import com.example.rumi.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    public static final String TAG = "MessagesFragment";

    private RecyclerView rvChats;
    private ChatsAdapter adapter;
    private List<Chat> allChats;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference chatsRef = db.collection(Chat.KEY_CHATS);

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChats = view.findViewById(R.id.rvChats);

        allChats = new ArrayList<>();
        adapter = new ChatsAdapter(getContext(), allChats, this);
        rvChats.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(layoutManager);

        getChats();
    }

    private void getChats() {
        chatsRef.whereArrayContains(Chat.KEY_MEMBERS, firebaseAuth.getCurrentUser().getUid())
                .orderBy(Chat.KEY_LAST_MESSAGE_DATE, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        adapter.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            chat.setChatId(documentSnapshot.getId());

                            allChats.add(chat);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }


}
