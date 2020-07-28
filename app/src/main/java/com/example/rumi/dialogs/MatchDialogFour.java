package com.example.rumi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class MatchDialogFour extends DialogFragment {

    public static final String TAG = "MatchDialogThree";
    private PageFourListener mListener;

    private ChipGroup chipGroupEntertainment;
    private Button btnAddEntertainment;
    private EditText etEntertainment;

    public interface PageFourListener {
        void sendPageFourInputs(int nextPage);
    }

    public static MatchDialogFour newInstance() {

        Bundle args = new Bundle();
        MatchDialogFour fragment = new MatchDialogFour();

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagefour, null, false);

        findViews(view);
        setPreviousValues();

        builder.setView(view).setTitle("Entertainment").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.sendPageFourInputs(MatchConstants.PAGE_THREE);
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void findViews(View view) {
        chipGroupEntertainment = view.findViewById(R.id.chipGroupEntertainment);
        btnAddEntertainment = view.findViewById(R.id.btnAddEntertainment);
        etEntertainment = view.findViewById(R.id.etEntertainment);

        btnAddEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etEntertainment.getText().toString().trim();
                if (tag.isEmpty())
                    return;

                LayoutInflater inflater = LayoutInflater.from(getContext());
                Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
                chip.setText(tag);
                chipGroupEntertainment.addView(chip);
                etEntertainment.getText().clear();

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipGroupEntertainment.removeView(view);
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
                    if (true) { // CHANGE
                        Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mListener.sendPageFourInputs(MatchConstants.PAGE_FIVE);
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
            mListener = (PageFourListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogFour");
        }
    }
}
