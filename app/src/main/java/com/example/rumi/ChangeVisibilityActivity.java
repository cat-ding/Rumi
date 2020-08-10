package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

public class ChangeVisibilityActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "ChangeVisibilityActivity";

    private SurveyResponse response;

    private SwitchMaterial switchPersonal, switchGeneral, switchPreferences, switchActivities,
            switchHobbies, switchEntertainment, switchMusic;
    private boolean newPersonal, newGeneral, newPref, newActivity, newHobby, newEntertainment, newMusic;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference surveyRef = db.collection(SurveyResponse.KEY_SURVEY_RESPONSE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_visibility);

        switchPersonal = findViewById(R.id.switchPersonal);
        switchGeneral = findViewById(R.id.switchGeneral);
        switchPreferences = findViewById(R.id.switchPreferences);
        switchActivities = findViewById(R.id.switchActivities);
        switchHobbies = findViewById(R.id.switchHobbies);
        switchEntertainment = findViewById(R.id.switchEntertainment);
        switchMusic = findViewById(R.id.switchMusic);

        response = Parcels.unwrap(getIntent().getParcelableExtra(SurveyResponse.class.getSimpleName()));
        Log.d(TAG, "onCreate: " + response.getSurveyId());

        setPreviousSettings();
        switchPersonal.setOnCheckedChangeListener(this);
        switchGeneral.setOnCheckedChangeListener(this);
        switchPreferences.setOnCheckedChangeListener(this);
        switchActivities.setOnCheckedChangeListener(this);
        switchHobbies.setOnCheckedChangeListener(this);
        switchEntertainment.setOnCheckedChangeListener(this);
        switchMusic.setOnCheckedChangeListener(this);
    }

    private void setPreviousSettings() {
        newPersonal = response.isPersonalVisible();
        newGeneral = response.isGeneralVisible();
        newPref = response.isPreferencesVisible();
        newActivity = response.isActivityVisible();
        newHobby = response.isHobbyVisible();
        newEntertainment = response.isEntertainmentVisible();
        newMusic = response.isMusicVisible();

        switchPersonal.setChecked(newPersonal);
        switchGeneral.setChecked(newGeneral);
        switchPreferences.setChecked(newPref);
        switchActivities.setChecked(newActivity);
        switchHobbies.setChecked(newHobby);
        switchEntertainment.setChecked(newEntertainment);
        switchMusic.setChecked(newMusic);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switchPersonal:
                newPersonal = b;
                break;
            case R.id.switchGeneral:
                newGeneral = b;
                break;
            case R.id.switchPreferences:
                newPref = b;
                break;
            case R.id.switchActivities:
                newActivity = b;
                break;
            case R.id.switchHobbies:
                newHobby = b;
                break;
            case R.id.switchEntertainment:
                newEntertainment = b;
                break;
            case R.id.switchMusic:
                newMusic = b;
                break;
        }
    }

    public void cancelClicked(View view) {
        finish();
    }

    public void applyClicked(View view) {
        if (newPersonal != response.isPersonalVisible()
                || newGeneral != response.isGeneralVisible()
                || newPref != response.isPreferencesVisible()
                || newActivity != response.isActivityVisible()
                || newHobby != response.isHobbyVisible()
                || newEntertainment != response.isEntertainmentVisible()
                || newMusic != response.isMusicVisible()) {

            Map<String, Object> updatedValues = new HashMap<>();
            updatedValues.put(SurveyResponse.KEY_PERSONAL_VISIBLE, newPersonal);
            updatedValues.put(SurveyResponse.KEY_GENERAL_VISIBLE, newGeneral);
            updatedValues.put(SurveyResponse.KEY_PREFERENCES_VISIBLE, newPref);
            updatedValues.put(SurveyResponse.KEY_ACTIVITY_VISIBLE, newActivity);
            updatedValues.put(SurveyResponse.KEY_HOBBY_VISIBLE, newHobby);
            updatedValues.put(SurveyResponse.KEY_ENTERTAINMENT_VISIBLE, newEntertainment);
            updatedValues.put(SurveyResponse.KEY_MUSIC_VISIBLE, newMusic);
            surveyRef.document(response.getSurveyId()).update(updatedValues)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            response.setPersonalVisible(newPersonal);
                            response.setGeneralVisible(newGeneral);
                            response.setPreferencesVisible(newPref);
                            response.setActivityVisible(newActivity);
                            response.setHobbyVisible(newHobby);
                            response.setEntertainmentVisible(newEntertainment);
                            response.setMusicVisible(newMusic);
                            Intent intent = new Intent();
                            intent.putExtra("updatedResponse", Parcels.wrap(response));
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}