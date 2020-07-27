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

import com.example.rumi.R;
import com.example.rumi.dialogs.MatchDialog;

public class MatchFragment extends Fragment implements MatchDialog.PageOneListener {

    public static final String TAG = "MatchFragment";
    private static final int PAGE_ONE_REQUEST_CODE = 11;

    public static final int PAGE_ONE = 1;
    public static final int PAGE_TWO = 2;
    public static final int PAGE_THREE = 3;

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
                        MatchDialog matchDialog = MatchDialog.newInstance(PAGE_ONE);
                        matchDialog.setTargetFragment(MatchFragment.this, PAGE_ONE_REQUEST_CODE);
                        matchDialog.show(getFragmentManager(), "MatchDialog");
                    }}).setNegativeButton("No thanks", null).show();
    }

    @Override
    public void sendPageOneInputs(int nextPage) {
        if (nextPage == 0)
            launchMatchingDialog();
        else if (nextPage == 1) {
            openSpecifiedDialog(PAGE_ONE);
        } else if (nextPage == 2) {
            openSpecifiedDialog(PAGE_TWO);
        }
    }

    private void openSpecifiedDialog(int pageNum) {
        MatchDialog matchDialog = MatchDialog.newInstance(pageNum);
        matchDialog.setTargetFragment(MatchFragment.this, PAGE_ONE_REQUEST_CODE);
        matchDialog.show(getFragmentManager(), "MatchDialog");
    }
}
