package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    private static final int LOG_IN_BEGIN = 25;
    private TextInputLayout layoutEmail, layoutPassword, layoutName, layoutMajor, layoutYear;
    private Button btnRegister;
    private TextView tvLogin;
    private String major, year;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutName = findViewById(R.id.layoutName);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        layoutMajor = findViewById(R.id.layoutMajor);
        layoutYear = findViewById(R.id.layoutYear);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setClickableSpan();

        // setting up spinner for majors
        String[] majors = getResources().getStringArray(R.array.majors);
        ArrayAdapter<String> adapterMajor =
                new ArrayAdapter<>(RegisterActivity.this, R.layout.item_dropdown, majors);
        AutoCompleteTextView dropdownMajors = findViewById(R.id.dropdownMajors);
        dropdownMajors.setAdapter(adapterMajor);

        // setting up spinner for year
        String[] years = getResources().getStringArray(R.array.year);
        ArrayAdapter<String> adapterYear =
                new ArrayAdapter<>(RegisterActivity.this, R.layout.item_dropdown, years);
        AutoCompleteTextView dropdownYears = findViewById(R.id.dropdownYears);
        dropdownYears.setAdapter(adapterYear);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = layoutEmail.getEditText().getText().toString().trim();
                final String password = layoutPassword.getEditText().getText().toString().trim();
                final String name = layoutName.getEditText().getText().toString();
                final String major = layoutMajor.getEditText().getText().toString();
                final String year = layoutYear.getEditText().getText().toString();

                boolean error = false;
                layoutName.setError(null);
                layoutEmail.setError(null);
                layoutPassword.setError(null);
                layoutMajor.setError(null);
                layoutYear.setError(null);

                if (name.isEmpty()) {
                    layoutName.setError("Name is required");
                    error = true;
                }
                if (email.isEmpty()) {
                    layoutEmail.setError("Email is required");
                    error = true;
                }
                if (password.isEmpty()) {
                    layoutPassword.setError("Password is required");
                    error = true;
                } else if (password.length() < 6) {
                    layoutPassword.setError("Password must be at least 6 characters");
                    error = true;
                }
                if (major.isEmpty()) {
                    layoutMajor.setError("Major is required");
                    error = true;
                } else if (!Arrays.asList(getResources().getStringArray(R.array.majors)).contains(major)) {
                    layoutMajor.setError("Choose a valid major");
                    error = true;
                }
                if (year.isEmpty()) {
                    layoutYear.setError("Year is required");
                    error = true;
                } else if (!Arrays.asList(getResources().getStringArray(R.array.year)).contains(year)) {
                    layoutYear.setError("Choose a valid year");
                    error = true;
                }

                if (error)
                    return;

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();

                            User user = new User(name, email, major, year);
                            db.collection(User.KEY_USERS).document(task.getResult().getUser().getUid()).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "onSuccess: new user profile created");
                                        }
                                    });

                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.e(TAG, "Error creating user: ", task.getException());
                            Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    // sets clickable portion of login here message
    private void setClickableSpan() {
        String text = tvLogin.getText().toString();
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        };
        ss.setSpan(clickableSpan, LOG_IN_BEGIN, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(ss);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
    }
}