package com.example.rumi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    private static final int NUM_PICKER_MIN = 1;
    private static final int NUM_PICKER_MAX = 10;
    private static final float DAYS_IN_MONTH = 30;
    private EditText etTitle, etDescription, etRent;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private NumberPicker numRoomPicker;
    private Spinner spinnerFurnished;
    private ImageView ivCalendarStart, ivCalendarEnd;
    private TextView tvStartDate, tvEndDate;
    private Button btnPost;

    private String title, description, startMonth, startDate, endDate;
    private Date start, end;
    private int numRooms, rent, numMonths;
    private boolean lookingForHouse, furnished;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


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
        etRent = findViewById(R.id.etRent);
        spinnerFurnished = findViewById(R.id.spinnerFurnished);
        ivCalendarStart = findViewById(R.id.ivCalendarStart);
        ivCalendarEnd = findViewById(R.id.ivCalendarEnd);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnPost = findViewById(R.id.btnPost);

        numRoomPicker.setMinValue(NUM_PICKER_MIN);
        numRoomPicker.setMaxValue(NUM_PICKER_MAX);

        numRoomPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numRooms = numRoomPicker.getValue();
            }
        });

        final ArrayAdapter<CharSequence> adapterFurnished = ArrayAdapter.createFromResource(ComposeActivity.this, R.array.furnished, android.R.layout.simple_spinner_item);
        adapterFurnished.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFurnished.setAdapter(adapterFurnished);

        spinnerFurnished.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals("Yes")) {
                    furnished = true;
                } else {
                    furnished = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
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
        } else if (numRooms == 0) {
            Toast.makeText(ComposeActivity.this, "Number of rooms is required!", Toast.LENGTH_SHORT).show();
            return;
        } else if (etRent.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Rent is required!", Toast.LENGTH_SHORT).show();
            etRent.requestFocus();
            return;
        } else if (tvStartDate.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Start date is required!", Toast.LENGTH_SHORT).show();
            return;
        } else if (tvEndDate.getText().toString().isEmpty()) {
            Toast.makeText(ComposeActivity.this, "End date is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        rent = Integer.parseInt(etRent.getText().toString());

        // calculate number of months between two dates, rounded up to the nearest whole month
        float daysBetween = ((end.getTime() - start.getTime()) / (1000*60*60*24));
        numMonths = (int) Math.ceil(daysBetween / DAYS_IN_MONTH);

        // TODO: startMonth/duration, rent, furnished
        final Post post = new Post(title, description, startMonth, firebaseAuth.getCurrentUser().getUid(),
                numRooms, numMonths, rent, furnished, lookingForHouse, startDate, endDate);

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

    // onClick method for calendar icon for choosing start date
    public void chooseStartDate(View view) {
        DatePickerDialog startDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvStartDate.setText(month + "/" + day + "/" + year);
                tvStartDate.setVisibility(View.VISIBLE);
                startMonth = new DateFormatSymbols().getMonths()[month];
                startDate = month + "/" + day + "/" + year;
                try {
                    start = dateFormat.parse(startDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        startDialog.show();
    }

    // onClick method for calendar icon for choosing end date
    public void chooseEndDate(View view) {
        DatePickerDialog endDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvEndDate.setText(month + "/" + day + "/" + year);
                tvEndDate.setVisibility(View.VISIBLE);
                endDate = month + "/" + day + "/" + year;
                try {
                    end = dateFormat.parse(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        endDialog.show();    }

}