package com.example.rumi.fragments;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.example.rumi.RegisterActivity;
import com.example.rumi.adapters.MatchAdapter;
import com.example.rumi.adapters.PostsAdapter;
import com.example.rumi.dialogs.MatchDialogFive;
import com.example.rumi.dialogs.MatchDialogFour;
import com.example.rumi.dialogs.MatchDialogOne;
import com.example.rumi.dialogs.MatchDialogSix;
import com.example.rumi.dialogs.MatchDialogThree;
import com.example.rumi.dialogs.MatchDialogTwo;
import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MatchFragment extends Fragment implements MatchDialogOne.PageOneListener,
        MatchDialogTwo.PageTwoListener, MatchDialogThree.PageThreeListener,
        MatchDialogFour.PageFourListener, MatchDialogFive.PageFiveListener, MatchDialogSix.PageSixListener {

    public static final String TAG = "MatchFragment";
    private static final int MATCH_REQUEST_CODE = 11;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SURVEY_STATUS = "surveyStatus";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference surveysRef = db.collection(SurveyResponse.KEY_SURVEY_RESPONSE);
    private CollectionReference usersRef = db.collection(User.KEY_USERS);

    private MatchConstants.House currHousePref = null;
    private MatchConstants.Weekend currWeekendPref = null;
    private MatchConstants.Guests currGuestsPref = null;
    private MatchConstants.Clean currCleanPref = null;
    private MatchConstants.Temperature currTempPref = null;
    private ArrayList<String> currActivities = new ArrayList<>();
    private ArrayList<String> currHobbies = new ArrayList<>();
    private ArrayList<String> currEntertainment = new ArrayList<>();
    private ArrayList<String> currMusic = new ArrayList<>();
    private MatchConstants.Gender currGender = null;
    private MatchConstants.GenderPref currGenderPref = null;
    private MatchConstants.Smoke currSmoke = null;
    private String currSelfIdentifyGender = "";
    private String currDescription = "";

    private SurveyResponse currSurveyResponse;

    private RelativeLayout relativeLayoutIntroPage;
    private RelativeLayout relativeLayoutRecommendations;
    private Button btnMatch;

    private RecyclerView rvMatches;
    private MatchAdapter adapter;
    private List<SurveyResponse> allResponses;

    public MatchFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relativeLayoutIntroPage = view.findViewById(R.id.relativeLayoutIntroPage);
        relativeLayoutRecommendations = view.findViewById(R.id.relativeLayoutRecommendations);
        rvMatches = view.findViewById(R.id.rvMatches);

        allResponses = new ArrayList<>();
        adapter = new MatchAdapter(getContext(), allResponses, this);
        rvMatches.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMatches.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        boolean surveyCompleted = sharedPreferences.getBoolean(SURVEY_STATUS, false);
        if (surveyCompleted) {
            relativeLayoutIntroPage.setVisibility(View.GONE);
            relativeLayoutRecommendations.setVisibility(View.VISIBLE);

            surveysRef.document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currSurveyResponse = documentSnapshot.toObject(SurveyResponse.class);
                    calculateCompatibility();
                }
            });
        } else {
            btnMatch = view.findViewById(R.id.btnMatch);

            relativeLayoutIntroPage.setVisibility(View.VISIBLE);
            relativeLayoutRecommendations.setVisibility(View.GONE);

            btnMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchMatchingDialog();
                }
            });
        }
    }

    private void calculateCompatibility() {
        // get all responses
        surveysRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                relativeLayoutIntroPage.setVisibility(View.GONE);
                relativeLayoutRecommendations.setVisibility(View.VISIBLE);
                addToAdapter(queryDocumentSnapshots);
                // TODO: call algorithm here
            }
        });
    }

    private void addToAdapter(QuerySnapshot queryDocumentSnapshots) {

        adapter.clear();

        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            SurveyResponse surveyResponse = documentSnapshot.toObject(SurveyResponse.class);
            // don't want to add your own survey response
            if (surveyResponse.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                continue;
            }
            // if our gender pref != no pref, then we need to check if the other person's gender is
            // what we're looking for
            if (!currSurveyResponse.getGenderPref().equals(MatchConstants.GenderPref.NO_PREFERENCE.toString())) {
                if (!currSurveyResponse.getGenderPref().equals(surveyResponse.getGender()))
                    continue;
            }
            // if the other survey's gender preference != no pref, then we need to check if our gender
            // matches what the other person is looking for
            if (!surveyResponse.getGenderPref().equals(MatchConstants.GenderPref.NO_PREFERENCE.toString())) {
                if (!surveyResponse.getGenderPref().equals(currSurveyResponse.getGender()))
                    continue;
            }
            // if smokingPref == non smoker okay with smoking then we don't need to filter by smoking
            if (!currSurveyResponse.getSmoking().equals(MatchConstants.Smoke.NON_SMOKER_YES.toString())) {
                if ((currSurveyResponse.getSmoking().equals(MatchConstants.Smoke.NON_SMOKER_NO.toString())
                        && surveyResponse.getSmoking().equals(MatchConstants.Smoke.SMOKER.toString())))
                    continue;
                if ((currSurveyResponse.getSmoking().equals(MatchConstants.Smoke.SMOKER.toString())
                        && surveyResponse.getSmoking().equals(MatchConstants.Smoke.NON_SMOKER_NO.toString())))
                    continue;
            }

            surveyResponse.setSurveyId(documentSnapshot.getId());
            allResponses.add(surveyResponse);
        }

        adapter.notifyDataSetChanged();
        // TODO: add if statement for when allResponses is empty even after querying
    }

    private void addToDatabase() {

        usersRef.document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String userId = firebaseAuth.getCurrentUser().getUid();
                String url = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
                String name = user.getName();
                String major = user.getMajor();
                String year = user.getYear();

                currSurveyResponse = new SurveyResponse(currHousePref.toString(),
                        currWeekendPref.toString(), currGuestsPref.toString(), currCleanPref.toString(),
                        currTempPref.toString(), currGender.toString(), currSelfIdentifyGender,
                        currGenderPref.toString(), currSmoke.toString(), currDescription,
                        currActivities, currHobbies, currEntertainment, currMusic, userId, url,
                        name, major, year);

                surveysRef.document(userId).set(currSurveyResponse).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        usersRef.document(firebaseAuth.getCurrentUser().getUid()).update(User.KEY_SURVEY_STATUS, true);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SURVEY_STATUS, true);
                        editor.apply();

                        calculateCompatibility();
                    }
                });
            }
        });

//        currSurveyResponse = new SurveyResponse(currHousePref.toString(),
//                currWeekendPref.toString(), currGuestsPref.toString(), currCleanPref.toString(),
//                currTempPref.toString(), currGender.toString(), currSelfIdentifyGender,
//                currGenderPref.toString(), currSmoke.toString(), currDescription,
//                currActivities, currHobbies, currEntertainment, currMusic, userId, url);

    }

    private void launchMatchingDialog() {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_baseline_emoji_emotions_24)
                .setTitle("Welcome to the Roommate Matching Feature!")
                .setMessage("You'll be asked a series of questions about your preferences and" +
                        " we'll do our best to suggest other users with similar qualities!")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MatchDialogOne matchDialogOne = MatchDialogOne.newInstance(currHousePref, currWeekendPref, currGuestsPref);
                        matchDialogOne.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
                        matchDialogOne.show(getFragmentManager(), "MatchDialogOne");
                    }})
                .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currHousePref = null;
                        currWeekendPref = null;
                        currGuestsPref = null;
                        currCleanPref = null;
                        currTempPref = null;
                        currActivities.clear();
                        currHobbies.clear();
                        currEntertainment.clear();
                        currMusic.clear();
                        currGender = null;
                        currGenderPref = null;
                        currSmoke = null;
                        currSelfIdentifyGender = "";
                        currDescription = "";
                    }
                }).show();
    }

    // obtaining info on house preference, weekend preference, and guests preference
    @Override
    public void sendPageOneInputs(int nextPage, MatchConstants.House housePref, MatchConstants.Weekend weekendPref, MatchConstants.Guests guestsPref) {
        currHousePref = housePref;
        currWeekendPref = weekendPref;
        currGuestsPref = guestsPref;
        if (nextPage == MatchConstants.PAGE_ZERO) {
            launchMatchingDialog();
        } else if (nextPage == MatchConstants.PAGE_TWO) {
            openMatchDialogTwo();
        }
    }

    // obtaining info on level of cleanliness and temperature preference
    @Override
    public void sendPageTwoInputs(int nextPage, MatchConstants.Clean cleanPref, MatchConstants.Temperature tempPref) {
        currCleanPref = cleanPref;
        currTempPref = tempPref;
        if (nextPage == MatchConstants.PAGE_ONE) {
            openMatchDialogOne();
        } else if (nextPage == MatchConstants.PAGE_THREE) {
            openMatchDialogThree();
        }
    }

    // obtaining info on activities and hobbies
    @Override
    public void sendPageThreeInputs(int nextPage, ArrayList<String> activities, ArrayList<String> hobbies) {
        currActivities = activities;
        currHobbies = hobbies;
        if (nextPage == MatchConstants.PAGE_TWO) {
            openMatchDialogTwo();
        } else if (nextPage == MatchConstants.PAGE_FOUR) {
            openMatchDialogFour();
        }
    }

    // obtaining info on entertainment and music
    @Override
    public void sendPageFourInputs(int nextPage, ArrayList<String> entertainment, ArrayList<String> music) {
        currEntertainment = entertainment;
        currMusic = music;
        if (nextPage == MatchConstants.PAGE_THREE) {
            openMatchDialogThree();
        } else if (nextPage == MatchConstants.PAGE_FIVE) {
            openMatchDialogFive();
        }
    }

    // obtaining personal info on gender identify and preference for housemates gender and smoking habits
    @Override
    public void sendPageFiveInputs(int nextPage, MatchConstants.Gender gender,
                                   String selfIdentifyGender, MatchConstants.GenderPref genderPref,
                                   MatchConstants.Smoke smoke) {
        currGender = gender;
        currGenderPref = genderPref;
        currSmoke = smoke;
        currSelfIdentifyGender = selfIdentifyGender;
        if (nextPage == MatchConstants.PAGE_FOUR) {
            openMatchDialogFour();
        } else if (nextPage == MatchConstants.PAGE_SIX) {
            openMatchDialogSix();
        }
    }

    @Override
    public void sendPageSixInputs(int nextPage, String description) {
        currDescription = description;
        if (nextPage == MatchConstants.PAGE_FIVE) {
            openMatchDialogFive();
        } else { // end
            addToDatabase();
        }
    }

    private void openMatchDialogOne() {
        MatchDialogOne dialog = MatchDialogOne.newInstance(currHousePref, currWeekendPref, currGuestsPref);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogOne");
    }

    private void openMatchDialogTwo() {
        MatchDialogTwo dialog = MatchDialogTwo.newInstance(currCleanPref, currTempPref);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogTwo");
    }

    private void openMatchDialogThree() {
        MatchDialogThree dialog = MatchDialogThree.newInstance(currActivities, currHobbies);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogThree");
    }

    private void openMatchDialogFour() {
        MatchDialogFour dialog = MatchDialogFour.newInstance(currEntertainment, currMusic);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogFour");
    }

    private void openMatchDialogFive() {
        MatchDialogFive dialog = MatchDialogFive.newInstance(currGender, currSelfIdentifyGender,
                currGenderPref, currSmoke);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogFive");
    }

    private void openMatchDialogSix() {
        MatchDialogSix dialog = MatchDialogSix.newInstance(currDescription);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogSix");
    }
}
