package com.example.rumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    private static final int LOG_IN_BEGIN = 25;
    private EditText etEmail, etPassword, etName;
    private Button btnRegister;
    private TextView tvLogin;
    private Spinner spinnerMajor, spinnerYear;
    private String major, year;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        spinnerMajor = findViewById(R.id.spinnerMajor);
        spinnerYear = findViewById(R.id.spinnerYear);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setClickableSpan();

        final ArrayAdapter<CharSequence> adapterMajor = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.majors, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.year, android.R.layout.simple_spinner_item);
        adapterMajor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajor.setAdapter(adapterMajor);
        spinnerYear.setAdapter(adapterYear);

        spinnerMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                major = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RegisterActivity.this, major, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { return; }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                year = adapterView.getItemAtPosition(i).toString();
                if (year.equals("Other")) {
                    year = "";
                } else if (!year.equals("Graduate Student")) {
                    year = "Class of " + year;
                }
                Toast.makeText(RegisterActivity.this, year, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { return; }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                final String name = etName.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Name is required!", Toast.LENGTH_SHORT).show();
                    etName.requestFocus();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email is required!", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password is required!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                } // major and year are optional (might change later)

                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("major", major);
                            user.put("year", year);

                            db.collection("users").document(task.getResult().getUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "onSuccess: new user profile created");
                                }
                            });

                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.e(TAG, "Error creating user! ", task.getException());
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