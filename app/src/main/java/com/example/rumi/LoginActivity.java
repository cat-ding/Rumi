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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    public static final String TAG = "LoginActivity";
    public static final Integer SIGN_UP_BEGIN = 23;
    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText inputEmail, inputPassword;
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

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterHere = findViewById(R.id.tvRegister);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        inputEmail.setOnFocusChangeListener(this);
        inputPassword.setOnFocusChangeListener(this);

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
                final String email = layoutEmail.getEditText().getText().toString().toLowerCase().trim();
                final String password = layoutPassword.getEditText().getText().toString();

                boolean error = false;
                layoutEmail.setError(null);
                layoutPassword.setError(null);
                layoutEmail.clearFocus();
                layoutPassword.clearFocus();

                if (email.isEmpty()) {
                    layoutEmail.setError("Email required");
                    error = true;
                }
                if (password.isEmpty()) {
                    layoutPassword.setError("Password required");
                    error = true;
                }

                if (error)
                    return;

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            layoutEmail.setError("Email may be incorrect");
                            layoutPassword.setError("Password may be incorrect");
                        } else {
                            goMainActivity();
                        }
                    }
                });
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

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.inputEmail:
                    layoutEmail.setError(null);
                    break;
                case R.id.inputPassword:
                    layoutPassword.setError(null);
                    break;
            }
        }
    }
}