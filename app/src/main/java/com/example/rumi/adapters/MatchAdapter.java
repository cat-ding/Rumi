package com.example.rumi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rumi.MainActivity;
import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.example.rumi.fragments.ProfileFragment;
import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;

import org.w3c.dom.Text;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    public static final String TAG = "MatchAdapter";
    private Context context;
    private List<SurveyResponse> responses;
    private Fragment fragment;

    public MatchAdapter(Context context, List<SurveyResponse> responses, Fragment fragment) {
        this.context = context;
        this.responses = responses;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SurveyResponse response = responses.get(position);
        holder.bind(response);
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        responses.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAllReponses(List<SurveyResponse> responseList) {
        responses.addAll(responseList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvUserName, tvMajorYear, tvDescription, tvCompatibilityScore;
        TextView tvGenderIdentity, tvGenderPreference, tvSmokingPreference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMajorYear = itemView.findViewById(R.id.tvMajorYear);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCompatibilityScore = itemView.findViewById(R.id.tvCompatibilityScore);
            tvGenderIdentity = itemView.findViewById(R.id.tvGenderIdentity);
            tvGenderPreference = itemView.findViewById(R.id.tvGenderPreference);
            tvSmokingPreference = itemView.findViewById(R.id.tvSmokingPreference);
        }

        public void bind(final SurveyResponse response) {
            if (!response.getImageUrl().equals("")) {
                Glide.with(context).load(response.getImageUrl()).circleCrop().into(ivProfileImage);
            }
            tvUserName.setText(response.getName());
            tvMajorYear.setText(response.getMajor() + ", " + response.getYear());
            tvDescription.setText(response.getDescription());
            String score = String.format("%.2f", response.getCompatibilityScore());
            tvCompatibilityScore.setText(score + "% match");

            if (response.isPersonalVisible()) {
                bindPersonalInfo(response);
            } else {
                tvGenderIdentity.setVisibility(View.GONE);
                tvGenderPreference.setVisibility(View.GONE);
                tvSmokingPreference.setVisibility(View.GONE);
            }

            // onClickListeners to open ProfileFragment
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
        }

        private void bindPersonalInfo(SurveyResponse response) {
            Log.d(TAG, "bindPersonalInfo: -----------------------------------");
            tvGenderIdentity.setVisibility(View.VISIBLE);
            tvGenderPreference.setVisibility(View.VISIBLE);
            tvSmokingPreference.setVisibility(View.VISIBLE);

            String gender = response.getGender();
            String genderPref = response.getGenderPref();
            String smoking = response.getSmoking();

            if (gender.equals(MatchConstants.Gender.SELF_IDENTIFY.toString())) {
                tvGenderIdentity.setText("I identify as: " + response.getSelfIdentifyGender());
            } else if (gender.equals(MatchConstants.Gender.NO_ANSWER.toString())){
                tvGenderIdentity.setText("I identify as: No answer");
            } else if (gender.equals(MatchConstants.Gender.FEMALE.toString())) {
                Log.d(TAG, "FEMALE");
                tvGenderIdentity.setText("I identify as: Female");
            } else if (gender.equals(MatchConstants.Gender.MALE.toString())) {
                tvGenderIdentity.setText("I identify as: Male");
            }

            if (genderPref.equals(MatchConstants.GenderPref.NO_PREFERENCE.toString())) {
                tvGenderPreference.setText("Gender preference: No preference");
            } else if (genderPref.equals(MatchConstants.GenderPref.FEMALE.toString())) {
                tvGenderPreference.setText("Gender preference: Female");
            } else if (genderPref.equals(MatchConstants.GenderPref.MALE.toString())) {
                tvGenderPreference.setText("Gender preference: Male");
            }

            if (smoking.equals(MatchConstants.Smoke.NON_SMOKER_NO.toString())) {
                tvSmokingPreference.setText("Smoking: Non-smoking, not okay with smokers");
            } else if (smoking.equals(MatchConstants.Smoke.NON_SMOKER_YES.toString())) {
                tvSmokingPreference.setText("Smoking: Non-smoking, okay with smokers");
            } else if (smoking.equals(MatchConstants.Smoke.SMOKER.toString())) {
                tvSmokingPreference.setText("Smoking: Smoker");
            }
        }

        private void openProfileFragment(String userId) {
            FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
            Fragment fragment = new ProfileFragment(userId);
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        }
    }

}
