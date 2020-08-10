package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rumi.fragments.ProfileFragment;
import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.parceler.Parcels;

import java.util.ArrayList;

public class MatchDetailActivity extends AppCompatActivity {

    public static final String TAG = "MatchDetailActivity";

    // basic info fields
    public static final String GENDER_NO_ANSWER = "No answer";
    public static final String GENDER_FEMALE = "Female";
    public static final String GENDER_MALE = "Male";
    public static final String GENDER_PREF_FEMALE = "Female";
    public static final String GENDER_PREF_MALE = "Male";
    public static final String SMOKING_NON_SMOKER_NOT_OKAY = "Non-smoker, not okay with smokers";
    public static final String SMOKING_NON_SMOKER_OKAY = "Non-smoker, okay with smokers";
    public static final String SMOKING_SMOKER = "Smoker";

    // general info fields
    public static final String WEEKDAY_QUIET = "Quiet";
    public static final String WEEKDAY_COMBO = "Combo of social and quiet";
    public static final String WEEKDAY_SOCIAL = "Social place";
    public static final String WEEKEND_QUIET = "Quiet";
    public static final String WEEKEND_HANG = "Social hangout place";
    public static final String WEEKEND_PARTY = "Party place";
    public static final String GUESTS_OCCASIONAL = "Occasional guests";
    public static final String GUESTS_NONE = "No guests";

    // preferences fields
    public static final String ALWAYS_CLEAN = "Always clean";
    public static final String FAIRLY_CLEAN = "Fairly clean";
    public static final String FAIRLY_MESSY = "Fairly messy";
    public static final String MESSY = "Messy";
    public static final String COLD = "Cold";
    public static final String FAIRLY_COLD = "Fairly cold";
    public static final String FAIRLY_WARM = "Fairly warm";
    public static final String WARM = "Warm";

    public static final String NO_PREFERENCE = "No Preference";

    private SurveyResponse response;

    private ImageView ivProfileImage;
    private TextView tvUserName, tvMajorYear, tvDescription;
    private RelativeLayout layoutBasicInfo, layoutGeneralInfo, layoutPreferences,
            layoutActivities, layoutHobbies, layoutEntertainment, layoutMusic;

    private TextView tvGender, tvGenderPref, tvSmoking;
    private TextView tvWeekday, tvWeekend, tvGuests;
    private TextView tvClean, tvTemp;

    private ChipGroup chipGroupActivities, chipGroupHobbies, chipGroupEntertainment, chipGroupMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvMajorYear = findViewById(R.id.tvMajorYear);
        layoutBasicInfo = findViewById(R.id.layoutBasicInfo);
        layoutGeneralInfo = findViewById(R.id.layoutGeneralInfo);
        layoutPreferences = findViewById(R.id.layoutPreferences);
        layoutActivities = findViewById(R.id.layoutActivities);
        layoutHobbies = findViewById(R.id.layoutHobbies);
        layoutEntertainment = findViewById(R.id.layoutEntertainment);
        layoutMusic = findViewById(R.id.layoutMusic);
        tvDescription = findViewById(R.id.tvDescription);
        tvGender = findViewById(R.id.tvGender);
        tvGenderPref = findViewById(R.id.tvGenderPref);
        tvSmoking = findViewById(R.id.tvSmoking);
        tvWeekday = findViewById(R.id.tvWeekday);
        tvWeekend = findViewById(R.id.tvWeekend);
        tvGuests = findViewById(R.id.tvGuests);
        tvClean = findViewById(R.id.tvClean);
        tvTemp = findViewById(R.id.tvTemp);
        chipGroupActivities = findViewById(R.id.chipGroupActivities);
        chipGroupHobbies = findViewById(R.id.chipGroupHobbies);
        chipGroupEntertainment = findViewById(R.id.chipGroupEntertainment);
        chipGroupMusic = findViewById(R.id.chipGroupMusic);

        response = Parcels.unwrap(getIntent().getParcelableExtra(SurveyResponse.class.getSimpleName()));

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileFragment(response.getUserId());
            }
        });
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileFragment(response.getUserId());
            }
        });

        setInfoFields();
    }

    private void setInfoFields() {

        if (!response.getImageUrl().isEmpty() && response.getImageUrl() != null) {
            Glide.with(MatchDetailActivity.this).load(response.getImageUrl()).circleCrop().into(ivProfileImage);
        }
        tvUserName.setText(response.getName());
        tvMajorYear.setText(response.getMajor() + ", " + response.getYear());
        tvDescription.setText(response.getDescription());

        if (response.isPersonalVisible()) {
            layoutBasicInfo.setVisibility(View.VISIBLE);
            bindPersonalInfo();
        } else {
            layoutBasicInfo.setVisibility(View.GONE);
        }

        if (response.isGeneralVisible()) {
            layoutGeneralInfo.setVisibility(View.VISIBLE);
            bindGeneralInfo();
        } else {
            layoutGeneralInfo.setVisibility(View.GONE);
        }

        if (response.isPreferencesVisible()) {
            layoutPreferences.setVisibility(View.VISIBLE);
            bindPreferences();
        } else {
            layoutPreferences.setVisibility(View.GONE);
        }

        if (response.isActivityVisible() && !response.getActivities().isEmpty()) {
            layoutActivities.setVisibility(View.VISIBLE);
            addChips(response.getActivities(), chipGroupActivities);
        } else {
            layoutActivities.setVisibility(View.GONE);
        }

        if (response.isHobbyVisible() && !response.getHobbies().isEmpty()) {
            layoutHobbies.setVisibility(View.VISIBLE);
            addChips(response.getHobbies(), chipGroupHobbies);
        } else {
            layoutHobbies.setVisibility(View.GONE);
        }

        if (response.isEntertainmentVisible() && !response.getEntertainment().isEmpty()) {
            layoutEntertainment.setVisibility(View.VISIBLE);
            addChips(response.getEntertainment(), chipGroupEntertainment);
        } else {
            layoutEntertainment.setVisibility(View.GONE);
        }

        if (response.isMusicVisible() && !response.getMusic().isEmpty()) {
            layoutMusic.setVisibility(View.VISIBLE);
            addChips(response.getMusic(), chipGroupMusic);
        } else {
            layoutMusic.setVisibility(View.GONE);
        }
    }

    private void bindPersonalInfo() {
        String gender = response.getGender();
        String genderPref = response.getGenderPref();
        String smoking = response.getSmoking();

        if (gender.equals(MatchConstants.Gender.SELF_IDENTIFY.toString())) {
            tvGender.setText(response.getSelfIdentifyGender());
        } else if (gender.equals(MatchConstants.Gender.NO_ANSWER.toString())) {
            tvGender.setText(GENDER_NO_ANSWER);
        } else if (gender.equals(MatchConstants.Gender.FEMALE.toString())) {
            tvGender.setText(GENDER_FEMALE);
        } else { // gender = male
            tvGender.setText(GENDER_MALE);
        }

        if (genderPref.equals(MatchConstants.GenderPref.NO_PREFERENCE.toString())) {
            tvGenderPref.setText(NO_PREFERENCE);
        } else if (genderPref.equals(MatchConstants.GenderPref.FEMALE.toString())) {
            tvGenderPref.setText(GENDER_PREF_FEMALE);
        } else { // gender pref = male
            tvGenderPref.setText(GENDER_PREF_MALE);
        }

        if (smoking.equals(MatchConstants.Smoke.NON_SMOKER_NO.toString())) {
            tvSmoking.setText(SMOKING_NON_SMOKER_NOT_OKAY);
        } else if (smoking.equals(MatchConstants.Smoke.NON_SMOKER_YES.toString())) {
            tvSmoking.setText(SMOKING_NON_SMOKER_OKAY);
        } else { // smoker
            tvSmoking.setText(SMOKING_SMOKER);
        }
    }

    private void bindGeneralInfo() {
        String weekday = response.getWeek();
        String weekend = response.getWeekend();
        String guests = response.getGuests();

        if (weekday.equals(MatchConstants.Week.QUIET.toString())) {
            tvWeekday.setText(WEEKDAY_QUIET);
        } else if (weekday.equals(MatchConstants.Week.COMBO.toString())) {
            tvWeekday.setText(WEEKDAY_COMBO);
        } else if (weekday.equals(MatchConstants.Week.SOCIAL.toString())) {
            tvWeekday.setText(WEEKDAY_SOCIAL);
        } else { // no pref
            tvWeekday.setText(NO_PREFERENCE);
        }

        if (weekend.equals(MatchConstants.Weekend.QUIET.toString())) {
            tvWeekend.setText(WEEKEND_QUIET);
        } else if (weekend.equals(MatchConstants.Weekend.HANG.toString())) {
            tvWeekend.setText(WEEKEND_HANG);
        } else if (weekend.equals(MatchConstants.Weekend.PARTY.toString())) {
            tvWeekend.setText(WEEKEND_PARTY);
        } else { // no pref
            tvWeekend.setText(NO_PREFERENCE);
        }

        if (guests.equals(MatchConstants.Guests.OCCASIONAL.toString())) {
            tvGuests.setText(GUESTS_OCCASIONAL);
        } else if (guests.equals(MatchConstants.Guests.NONE.toString())) {
            tvGuests.setText(GUESTS_NONE);
        } else { // no pref
            tvGuests.setText(NO_PREFERENCE);
        }
    }

    private void bindPreferences() {
        String clean = response.getCleanliness();
        String temperature = response.getTemperature();

        if (clean.equals(MatchConstants.Clean.ALWAYS_CLEAN.toString())) {
            tvClean.setText(ALWAYS_CLEAN);
        } else if (clean.equals(MatchConstants.Clean.FAIRLY_CLEAN.toString())) {
            tvClean.setText(FAIRLY_CLEAN);
        } else if (clean.equals(MatchConstants.Clean.FAIRLY_MESSY.toString())) {
            tvClean.setText(FAIRLY_MESSY);
        } else {
            tvClean.setText(MESSY);
        }

        if (temperature.equals(MatchConstants.Temperature.COLD.toString())) {
            tvTemp.setText(COLD);
        } else if (temperature.equals(MatchConstants.Temperature.FAIRLY_COLD.toString())) {
            tvTemp.setText(FAIRLY_COLD);
        } else if (temperature.equals(MatchConstants.Temperature.FAIRLY_WARM.toString())) {
            tvTemp.setText(FAIRLY_WARM);
        } else {
            tvTemp.setText(WARM);
        }
    }

    private void openProfileFragment(String userId) {
        FragmentManager fragmentManager = (MatchDetailActivity.this).getSupportFragmentManager();
        Fragment fragment = new ProfileFragment(userId);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    private void addChips(ArrayList<String> list, ChipGroup chipGroup) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String item : list) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
            chip.setText(item);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setCloseIcon(null);
            chipGroup.addView(chip);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}