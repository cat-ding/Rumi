package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static final Integer SIGN_UP_BEGIN = 23;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterHere;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private CollectionReference usersRef = firestore.collection(User.KEY_USERS);

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SURVEY_STATUS = "surveyStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterHere = findViewById(R.id.tvRegister);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                     goMainActivity();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString();

                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your email and password!", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your email!", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login error! Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                                goMainActivity();
                            }
                        }
                    });
                }
            }
        });

        setClickableSpan();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // sets clickable portion of sign up here message
    private void setClickableSpan() {
        String text = tvRegisterHere.getText().toString();
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        };
        ss.setSpan(clickableSpan, SIGN_UP_BEGIN, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegisterHere.setText(ss);
        tvRegisterHere.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void goMainActivity() {
        // set shared preferences for survey status upon login
        usersRef.document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // initiate shared preference for match feature to false
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SURVEY_STATUS, documentSnapshot.getBoolean(User.KEY_SURVEY_STATUS));
                        editor.apply();
                    }
                });

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}