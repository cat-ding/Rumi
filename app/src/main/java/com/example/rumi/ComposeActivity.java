package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    private static final int NUM_PICKER_MIN = 1;
    private static final int NUM_PICKER_MAX = 10;
    private EditText etTitle, etDescription;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private NumberPicker numRoomPicker;
    private Button btnPost;
    private String title, description;
    private int numRooms;
    private boolean lookingForHouse;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DocumentReference postRef = db.collection("posts").document();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        radioGroup = findViewById(R.id.radioGroup);
        numRoomPicker = findViewById(R.id.numRoomPicker);
        btnPost = findViewById(R.id.btnPost);

        numRoomPicker.setMinValue(NUM_PICKER_MIN);
        numRoomPicker.setMaxValue(NUM_PICKER_MAX);

        numRoomPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numRooms = numRoomPicker.getValue();
            }
        });
    }

    // onClick method for radio buttons
    public void checkRadioButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        if (radioButton.getText().equals("Looking for a place")) {
            lookingForHouse = true;
        } else {
            lookingForHouse = false;
        }
    }

    // onClick method for post button
    public void makePost(View view) {
        title = etTitle.getText().toString();
        description = etDescription.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Title is required!", Toast.LENGTH_SHORT).show();
            etTitle.requestFocus();
            return;
        } else if (description.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Description is required!", Toast.LENGTH_SHORT).show();
            etDescription.requestFocus();
            return;
        }

        final Post post = new Post(title, description, "DUMMY MONTH", firebaseAuth.getCurrentUser().getUid(),
                numRooms, 99999, 99999, true, lookingForHouse);

        postRef.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ComposeActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("newPost", Parcels.wrap(post));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}