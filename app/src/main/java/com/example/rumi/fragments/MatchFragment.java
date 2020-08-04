package com.example.rumi.fragments;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.example.rumi.adapters.MatchAdapter;
import com.example.rumi.dialogs.MatchDialogFive;
import com.example.rumi.dialogs.MatchDialogFour;
import com.example.rumi.dialogs.MatchDialogOne;
import com.example.rumi.dialogs.MatchDialogSix;
import com.example.rumi.dialogs.MatchDialogThree;
import com.example.rumi.dialogs.MatchDialogTwo;
import com.example.rumi.models.SurveyResponse;
import com.example.rumi.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MatchFragment extends Fragment implements MatchDialogOne.PageOneListener,
        MatchDialogTwo.PageTwoListener, MatchDialogThree.PageThreeListener,
        MatchDialogFour.PageFourListener, MatchDialogFive.PageFiveListener, MatchDialogSix.PageSixListener {

    public static final String TAG = "MatchFragment";
    private static final int MATCH_REQUEST_CODE = 11;

    // shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SURVEY_STATUS = "surveyStatus";

    // firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference surveysRef = db.collection(SurveyResponse.KEY_SURVEY_RESPONSE);
    private CollectionReference usersRef = db.collection(User.KEY_USERS);

    // responses to survey questions
    private MatchConstants.Week currHousePref = null;
    private MatchConstants.Weekend currWeekendPref = null;
    private MatchConstants.Guests currGuestsPref = null;
    private MatchConstants.Clean currCleanPref = null;
    private MatchConstants.Temperature currTempPref = null;
    private ArrayList<String> currActivities = new ArrayList<>(), currHobbies = new ArrayList<>();
    private ArrayList<String> currEntertainment = new ArrayList<>(), currMusic = new ArrayList<>();
    private MatchConstants.Gender currGender = null;
    private MatchConstants.GenderPref currGenderPref = null;
    private MatchConstants.Smoke currSmoke = null;
    private String currSelfIdentifyGender = "", currDescription = "";
    private boolean currGeneralVisible = true, currPrefVisible = true, currActivityVisible = true,
            currHobbyVisible = true, currEntertainmentVisible = true, currMusicVisible = true,
            currPersonalVisible = true;

    // the survey response for the current user
    private SurveyResponse myResponse;

    // the elements of this fragment to toggle visibility with
    private RelativeLayout relativeLayoutIntroPage, relativeLayoutRecommendations;
    private Button btnMatch;

    // recyclerview elements
    private RecyclerView rvMatches;
    private MatchAdapter adapter;
    private List<SurveyResponse> allResponses;

    // weights and other things needed to calculate compatibility score
    HashMap<String, Float> levels;
    public static final int MAX_SCORE = 45; // does not include activities, hobbies, entertainment, or music (bonus)
    public static final int WEEK_WEIGHT = 10;
    public static final int WEEKEND_WEIGHT = 10;
    public static final int GUEST_WEIGHT = 6;
    public static final int CLEAN_WEIGHT = 9;
    public static final int TEMP_WEIGHT = 4;
    public static final float ACTIVITIES_WEIGHT = 1.5f;
    public static final float HOBBIES_WEIGHT = 1.5f;
    public static final float ENTERTAINMENT_WEIGHT = 1.5f;
    public static final float MUSIC_WEIGHT = 1.5f;
    public static final int MAJOR_WEIGHT = 3;
    public static final int YEAR_WEIGHT = 2;
    public static final Float RANGE = 3f;

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

        // init hashmap that is used to calculate compatibility scores with ranges
        levels = new HashMap<>();
        levels.put(MatchConstants.Clean.ALWAYS_CLEAN.toString(), 1f);
        levels.put(MatchConstants.Clean.FAIRLY_CLEAN.toString(), 2f);
        levels.put(MatchConstants.Clean.FAIRLY_MESSY.toString(), 3f);
        levels.put(MatchConstants.Clean.VERY_MESSY.toString(), 4f);

        levels.put(MatchConstants.Temperature.COLD.toString(), 1f);
        levels.put(MatchConstants.Temperature.FAIRLY_COLD.toString(), 2f);
        levels.put(MatchConstants.Temperature.FAIRLY_WARM.toString(), 3f);
        levels.put(MatchConstants.Temperature.WARM.toString(), 4f);

        // recyclerview init
        allResponses = new ArrayList<>();
        adapter = new MatchAdapter(getContext(), allResponses, this);
        rvMatches.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMatches.setLayoutManager(layoutManager);

        // set layout according to what the survey status is from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        boolean surveyCompleted = sharedPreferences.getBoolean(SURVEY_STATUS, false);
        if (surveyCompleted) {
            relativeLayoutIntroPage.setVisibility(View.GONE);
            relativeLayoutRecommendations.setVisibility(View.VISIBLE);

            surveysRef.document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myResponse = documentSnapshot.toObject(SurveyResponse.class);
                    getSurveyResponses();
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

    private void getSurveyResponses() {
        // get all responses
        surveysRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                relativeLayoutIntroPage.setVisibility(View.GONE);
                relativeLayoutRecommendations.setVisibility(View.VISIBLE);
                addToList(queryDocumentSnapshots);
            }
        });
    }

    private void addToList(QuerySnapshot queryDocumentSnapshots) {

        adapter.clear();
        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            SurveyResponse surveyResponse = documentSnapshot.toObject(SurveyResponse.class);
            // don't want to add your own survey response
            if (surveyResponse.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                continue;
            }
            // if our gender pref != no pref, then we need to check if the other person's gender is
            // what we're looking for
            if (!myResponse.getGenderPref().equals(MatchConstants.GenderPref.NO_PREFERENCE.toString())) {
                if (!myResponse.getGenderPref().equals(surveyResponse.getGender()))
                    continue;
            }
            // if the other survey's gender preference != no pref, then we need to check if our gender
            // matches what the other person is looking for
            if (!surveyResponse.getGenderPref().equals(MatchConstants.GenderPref.NO_PREFERENCE.toString())) {
                if (!surveyResponse.getGenderPref().equals(myResponse.getGender()))
                    continue;
            }
            // if smokingPref == non smoker okay with smoking then we don't need to filter by smoking
            if (!myResponse.getSmoking().equals(MatchConstants.Smoke.NON_SMOKER_YES.toString())) {
                if ((myResponse.getSmoking().equals(MatchConstants.Smoke.NON_SMOKER_NO.toString())
                        && surveyResponse.getSmoking().equals(MatchConstants.Smoke.SMOKER.toString())))
                    continue;
                if ((myResponse.getSmoking().equals(MatchConstants.Smoke.SMOKER.toString())
                        && surveyResponse.getSmoking().equals(MatchConstants.Smoke.NON_SMOKER_NO.toString())))
                    continue;
            }

            surveyResponse.setCompatibilityScore(calculateCompatibilityScore(surveyResponse));
            surveyResponse.setSurveyId(documentSnapshot.getId());
            allResponses.add(surveyResponse);
        }

        // TODO: maybe set a flag if size = 0, to let user know matches are not good
        if (allResponses.size() == 0) { // get all if there are none
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                SurveyResponse surveyResponse = documentSnapshot.toObject(SurveyResponse.class);
                if (surveyResponse.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                    continue;
                }
                surveyResponse.setCompatibilityScore(calculateCompatibilityScore(surveyResponse));
                surveyResponse.setSurveyId(documentSnapshot.getId());
                allResponses.add(surveyResponse);
            }
        }

        // only need to get top 5 if there are more than 5 responses in the list, otherwise just sort
        if (allResponses.size() > 5) {
            quickSelect(0, allResponses.size()-1, 5); // rank = 5
            allResponses.subList(5, allResponses.size()).clear();
        }

        Collections.sort(allResponses, new Comparator<SurveyResponse>() {
            public int compare(SurveyResponse one, SurveyResponse two) {
                return Float.compare(two.getCompatibilityScore(), one.getCompatibilityScore());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private float quickSelect(int low, int high, int rank) {
        int partitionIndex = partition(low, high); // partition the array

        if (partitionIndex == rank)
            return allResponses.get(partitionIndex).getCompatibilityScore();
        if (partitionIndex < rank) // recurse on lower half
            return  quickSelect(partitionIndex + 1, high, rank);

        // recurse on upper half
        return quickSelect(low, partitionIndex - 1, rank);
    }

    // partition function for quickSelect(), returns the index of the element after partitioning
    private int partition(int low, int high) {
        // standard pick last element's value to partition around
        float pivotVal = allResponses.get(high).getCompatibilityScore();
        int pivotIndex = low;
        for (int i = low; i <= high; i++) {
            if (allResponses.get(i).getCompatibilityScore() > pivotVal) {
                Collections.swap(allResponses, i, pivotIndex);
                pivotIndex++;
            }
        }

        // swap pivot to the final pivot location
        Collections.swap(allResponses, high, pivotIndex);
        return pivotIndex;
    }

    private float calculateCompatibilityScore(SurveyResponse currRes) {
        float score = 0;
        if (!currRes.getWeek().equals(myResponse.getWeek()))
            score += WEEK_WEIGHT;
        if (!currRes.getWeekend().equals(myResponse.getWeekend()))
            score += WEEKEND_WEIGHT;
        if (!currRes.getGuests().equals(myResponse.getGuests()))
            score += GUEST_WEIGHT;
        if (!currRes.getMajor().equals(myResponse.getMajor()))
            score += MAJOR_WEIGHT;
        if (!currRes.getYear().equals(myResponse.getYear()))
            score += YEAR_WEIGHT;

        // calculation of similarity by dividing difference by range (3) to normalize between 0 and 1
        score += (CLEAN_WEIGHT * Math.abs(levels.get(currRes.getCleanliness()) - levels.get(myResponse.getCleanliness()))
                / RANGE);
        score += (TEMP_WEIGHT * Math.abs(levels.get(currRes.getTemperature()) - levels.get(myResponse.getTemperature()))
                / RANGE);

        // calculation of array similarity
        score +=  (ACTIVITIES_WEIGHT *
                calculateArraySimilarity(new ArrayList<>(currRes.getActivities()), myResponse.getActivities()));

        score +=  (HOBBIES_WEIGHT *
                calculateArraySimilarity(new ArrayList<>(currRes.getHobbies()), myResponse.getHobbies()));

        score +=  (ENTERTAINMENT_WEIGHT *
                calculateArraySimilarity(new ArrayList<>(currRes.getEntertainment()), myResponse.getEntertainment()));

        score +=  (MUSIC_WEIGHT *
                calculateArraySimilarity(new ArrayList<>(currRes.getMusic()), myResponse.getMusic()));

        score = ((MAX_SCORE - score) / MAX_SCORE) * 100; // get a percentage from 0-100 scale

        return score;
    }

    // calculation of similarity by getting how many common elements there are and
    // dividing by the size of the smaller list
    private float calculateArraySimilarity(ArrayList<String> one, ArrayList<String> two) {
        float min = (float) Math.min(one.size(), two.size());
        if (min == 0)
            return 0; // nothing in common - no effect
        one.retainAll(two);
        float diff =  0 - (min - one.size()); // get negative for bonus points
        return (float) diff / min; // normalize
    }

    private void addToDatabase() {

        usersRef.document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String userId = firebaseAuth.getCurrentUser().getUid();
                String url = user.getProfileUrl();
                String name = user.getName();
                String major = user.getMajor();
                String year = user.getYear();

                myResponse = new SurveyResponse(currHousePref.toString(),
                        currWeekendPref.toString(), currGuestsPref.toString(), currCleanPref.toString(),
                        currTempPref.toString(), currGender.toString(), currSelfIdentifyGender,
                        currGenderPref.toString(), currSmoke.toString(), currDescription,
                        currActivities, currHobbies, currEntertainment, currMusic, userId, url,
                        name, major, year, currGeneralVisible, currPrefVisible, currActivityVisible,
                        currHobbyVisible, currEntertainmentVisible, currMusicVisible, currPersonalVisible);

                surveysRef.document(userId).set(myResponse).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        usersRef.document(firebaseAuth.getCurrentUser().getUid()).update(User.KEY_SURVEY_STATUS, true);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SURVEY_STATUS, true);
                        editor.apply();

                        getSurveyResponses();
                    }
                });
            }
        });
    }

    /* FUNCTIONS FOR DIALOG PAGE LAUNCHING AND INFO HANDLING BELOW */

    private void launchMatchingDialog() {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_baseline_emoji_emotions_24)
                .setTitle("Welcome to the Roommate Matching Feature!")
                .setMessage("You'll be asked a series of questions about your preferences and" +
                        " we'll do our best to suggest other users with similar qualities!")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MatchDialogOne matchDialogOne = MatchDialogOne.newInstance(currHousePref, currWeekendPref, currGuestsPref, currGeneralVisible);
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
                        currGeneralVisible = true;
                        currPrefVisible = true;
                        currActivityVisible = true;
                        currHobbyVisible = true;
                        currEntertainmentVisible = true;
                        currMusicVisible = true;
                        currPersonalVisible = true;
                    }
                }).show();
    }

    // obtaining info on house preference, weekend preference, and guests preference
    @Override
    public void sendPageOneInputs(int nextPage, MatchConstants.Week housePref,
                                  MatchConstants.Weekend weekendPref, MatchConstants.Guests guestsPref,
                                  boolean generalVisible) {
        currHousePref = housePref;
        currWeekendPref = weekendPref;
        currGuestsPref = guestsPref;
        currGeneralVisible = generalVisible;
        if (nextPage == MatchConstants.PAGE_ZERO) {
            launchMatchingDialog();
        } else if (nextPage == MatchConstants.PAGE_TWO) {
            openMatchDialogTwo();
        }
    }

    // obtaining info on level of cleanliness and temperature preference
    @Override
    public void sendPageTwoInputs(int nextPage, MatchConstants.Clean cleanPref, MatchConstants.Temperature tempPref,
                                  boolean preferencesVisible) {
        currCleanPref = cleanPref;
        currTempPref = tempPref;
        currPrefVisible = preferencesVisible;
        if (nextPage == MatchConstants.PAGE_ONE) {
            openMatchDialogOne();
        } else if (nextPage == MatchConstants.PAGE_THREE) {
            openMatchDialogThree();
        }
    }

    // obtaining info on activities and hobbies
    @Override
    public void sendPageThreeInputs(int nextPage, ArrayList<String> activities, ArrayList<String> hobbies,
                                    boolean activityVisible, boolean hobbyVisible) {
        currActivities = activities;
        currHobbies = hobbies;
        currActivityVisible = activityVisible;
        currHobbyVisible = hobbyVisible;
        if (nextPage == MatchConstants.PAGE_TWO) {
            openMatchDialogTwo();
        } else if (nextPage == MatchConstants.PAGE_FOUR) {
            openMatchDialogFour();
        }
    }

    // obtaining info on entertainment and music
    @Override
    public void sendPageFourInputs(int nextPage, ArrayList<String> entertainment, ArrayList<String> music,
                                   boolean entertainmentVisible, boolean musicVisible) {
        currEntertainment = entertainment;
        currMusic = music;
        currEntertainmentVisible = entertainmentVisible;
        currMusicVisible = musicVisible;
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
                                   MatchConstants.Smoke smoke, boolean personalVisible) {
        currGender = gender;
        currGenderPref = genderPref;
        currSmoke = smoke;
        currSelfIdentifyGender = selfIdentifyGender;
        currPersonalVisible = personalVisible;
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
        } else {
            addToDatabase();
        }
    }

    private void openMatchDialogOne() {
        MatchDialogOne dialog = MatchDialogOne.newInstance(currHousePref, currWeekendPref, currGuestsPref, currGeneralVisible);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogOne");
    }

    private void openMatchDialogTwo() {
        MatchDialogTwo dialog = MatchDialogTwo.newInstance(currCleanPref, currTempPref, currPrefVisible);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogTwo");
    }

    private void openMatchDialogThree() {
        MatchDialogThree dialog = MatchDialogThree.newInstance(currActivities, currHobbies, currActivityVisible, currHobbyVisible);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogThree");
    }

    private void openMatchDialogFour() {
        MatchDialogFour dialog = MatchDialogFour.newInstance(currEntertainment, currMusic, currEntertainmentVisible, currMusicVisible);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogFour");
    }

    private void openMatchDialogFive() {
        MatchDialogFive dialog = MatchDialogFive.newInstance(currGender, currSelfIdentifyGender,
                currGenderPref, currSmoke, currPersonalVisible);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogFive");
    }

    private void openMatchDialogSix() {
        MatchDialogSix dialog = MatchDialogSix.newInstance(currDescription);
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogSix");
    }
}
