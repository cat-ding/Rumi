package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;

import org.parceler.Parcels;

public class MatchDetailActivity extends AppCompatActivity {

    public static final String TAG = "MatchDetailActivity";

    private SurveyResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        response = Parcels.unwrap(getIntent().getParcelableExtra(SurveyResponse.class.getSimpleName()));

        // TODO: profile photo, name, major; personal info, week + weekend + guest prefs,
        //  cleanliness + temp prefs, activities + hobbies, entertainment + music
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}