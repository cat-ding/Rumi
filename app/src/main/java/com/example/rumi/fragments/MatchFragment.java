package com.example.rumi.fragments;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.example.rumi.dialogs.MatchDialogFive;
import com.example.rumi.dialogs.MatchDialogFour;
import com.example.rumi.dialogs.MatchDialogOne;
import com.example.rumi.dialogs.MatchDialogSix;
import com.example.rumi.dialogs.MatchDialogThree;
import com.example.rumi.dialogs.MatchDialogTwo;
import com.google.api.LogDescriptor;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MatchFragment extends Fragment implements MatchDialogOne.PageOneListener,
        MatchDialogTwo.PageTwoListener, MatchDialogThree.PageThreeListener,
        MatchDialogFour.PageFourListener, MatchDialogFive.PageFiveListener, MatchDialogSix.PageSixListener {

    public static final String TAG = "MatchFragment";
    private static final int MATCH_REQUEST_CODE = 11;

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

    private Button btnMatch;

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

        btnMatch = view.findViewById(R.id.btnMatch);

        btnMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMatchingDialog();
            }
        });
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
            // TODO: upload responses to firebase + calculate combatibility
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
