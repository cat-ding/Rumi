package com.example.rumi.dialogs;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class MatchDialogThree extends DialogFragment {

    public static final String TAG = "MatchDialogThree";
    private PageThreeListener mListener;

    private ChipGroup chipGroupActivities, chipGroupHobbies, chipGroupEntertainment;
    private Button btnAddActivity, btnAddHobby, btnAddEntertainment;
    private EditText etActivity, etHobby, etEntertainment;

    public interface PageThreeListener {
        void sendPageThreeInputs(int nextPage);
    }

    public static MatchDialogThree newInstance() {

        Bundle args = new Bundle();
        MatchDialogThree fragment = new MatchDialogThree();

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagethree, null, false);

        findViews(view);
        setPreviousValues();

        builder.setView(view).setTitle("Interests").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.sendPageThreeInputs(MatchConstants.PAGE_TWO);
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void findViews(View view) {

        chipGroupActivities = view.findViewById(R.id.chipGroupActivities);
        chipGroupHobbies = view.findViewById(R.id.chipGroupHobbies);
        btnAddActivity = view.findViewById(R.id.btnAddActivity);
        btnAddHobby = view.findViewById(R.id.btnAddHobby);
        etActivity = view.findViewById(R.id.etActivity);
        etHobby = view.findViewById(R.id.etHobby);

        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etActivity.getText().toString().trim();
                if (tag.isEmpty())
                    return;

                LayoutInflater inflater = LayoutInflater.from(getContext());
                Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
                chip.setText(tag);
                chipGroupActivities.addView(chip);
                etActivity.getText().clear();

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipGroupActivities.removeView(view);
                    }
                });
            }
        });

        btnAddHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etHobby.getText().toString().trim();
                if (tag.isEmpty())
                    return;

                LayoutInflater inflater = LayoutInflater.from(getContext());
                Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
                chip.setText(tag);
                chipGroupHobbies.addView(chip);
                etHobby.getText().clear();

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipGroupHobbies.removeView(view);
                    }
                });
            }
        });
    }

    private void setPreviousValues() {
        // TODO
    }

    // overrides the next button so that dialog doesn't dismiss if all fields are not answered
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button postiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            postiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (true) { // CHANGE
//                        Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    mListener.sendPageThreeInputs(MatchConstants.PAGE_FOUR);
                    dismiss();
                }
            });
        }
    }

    // called when fragment is attached to a host fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (PageThreeListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogThree");
        }
    }
}
