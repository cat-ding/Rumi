package com.example.rumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rumi.fragments.ProfileFragment;
import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;

import org.parceler.Parcels;

public class MatchDetailActivity extends AppCompatActivity {

    public static final String TAG = "MatchDetailActivity";

    private SurveyResponse response;

    private ImageView ivProfileImage;
    private TextView tvUserName, tvMajorYear, tvDescription;
    private RelativeLayout layoutBasicInfo, layoutGeneralInfo, layoutPreferences;

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
        tvDescription = findViewById(R.id.tvDescription);

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

        if (!response.getImageUrl().equals("")) {
            Glide.with(MatchDetailActivity.this).load(response.getImageUrl()).circleCrop().into(ivProfileImage);
        }
        tvUserName.setText(response.getName());
        tvMajorYear.setText(response.getMajor() + ", " + response.getYear());
        tvDescription.setText(response.getDescription());

        if (response.isPersonalVisible()) {
            layoutBasicInfo.setVisibility(View.VISIBLE);
        }

        if (response.isGeneralVisible()) {
            layoutGeneralInfo.setVisibility(View.VISIBLE);
        }

        if (response.isPreferencesVisible()) {
            layoutPreferences.setVisibility(View.VISIBLE);

        }
    }

    private void openProfileFragment(String userId) {
        FragmentManager fragmentManager = (MatchDetailActivity.this).getSupportFragmentManager();
        Fragment fragment = new ProfileFragment(userId);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}