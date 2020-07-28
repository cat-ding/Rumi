package com.example.rumi.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.example.rumi.dialogs.MatchDialogOne;
import com.example.rumi.dialogs.MatchDialogThree;
import com.example.rumi.dialogs.MatchDialogTwo;

public class MatchFragment extends Fragment implements MatchDialogOne.PageOneListener, MatchDialogTwo.PageTwoListener, MatchDialogThree.PageThreeListener {

    public static final String TAG = "MatchFragment";
    private static final int MATCH_REQUEST_CODE = 11;

    private MatchConstants.House currHousePref = null;
    private MatchConstants.Weekend currWeekendPref = null;
    private MatchConstants.Guests currGuestsPref = null;
    private MatchConstants.Clean currCleanPref = null;
    private MatchConstants.Temperature currTempPref = null;

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
                    }
                }).show();
    }

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

    @Override
    public void sendPageThreeInputs(int nextPage) {
        if (nextPage == MatchConstants.PAGE_TWO) {
            openMatchDialogTwo();
        } else if (nextPage == MatchConstants.PAGE_THREE) {
            // TODO: open page 4
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
        MatchDialogThree dialog = MatchDialogThree.newInstance();
        dialog.setTargetFragment(MatchFragment.this, MATCH_REQUEST_CODE);
        dialog.show(getFragmentManager(), "MatchDialogThree");
    }
}
