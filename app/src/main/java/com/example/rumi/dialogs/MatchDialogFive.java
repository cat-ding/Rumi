package com.example.rumi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;

public class MatchDialogFive extends DialogFragment {

    public static final String TAG = "MatchDialogFive";
    private static final String KEY_GENDER = "currGender";
    private static final String KEY_SELF_IDENTIFY_GENDER = "currSelfIdentifyGender";
    private static final String KEY_GENDER_PREF = "currGenderPref";
    private static final String KEY_SMOKE = "currSmoke";
    private static final String KEY_CURR_PERSONAL_VISIBLE = "currPersonalVisible";
    private PageFiveListener mListener;

    private RadioGroup radioGroupGenderIdentity, radioGroupGenderPreference, radioGroupSmoking;
    private RadioButton radioMale, radioFemale, radioSelfIdentify, radioNoAnswer;
    private RadioButton radioMalePref, radioFemalePref, radioNoPref;
    private RadioButton radioNonSmokerNo, radioNonSmokerYes, radioSmoker;
    private EditText etSelfIdentify;

    private boolean selfIdentify = false;
    private String selfIdentifyGender = "";
    private MatchConstants.Gender gender = null;
    private MatchConstants.GenderPref genderPref = null;
    private MatchConstants.Smoke smoke = null;

    private CheckBox checkVisibility;
    private boolean personalVisible = true;

    public interface PageFiveListener {
        void sendPageFiveInputs(int nextPage, MatchConstants.Gender gender,
                                String selfIdentifyGender, MatchConstants.GenderPref genderPref,
                                MatchConstants.Smoke smoke, boolean personalVisible);
    }

    public static MatchDialogFive newInstance(MatchConstants.Gender currGender,
                                              String currSelfIdentifyGender,
                                              MatchConstants.GenderPref currGenderPref,
                                              MatchConstants.Smoke currSmoke,
                                              boolean currPersonalVisible) {

        Bundle args = new Bundle();
        MatchDialogFive fragment = new MatchDialogFive();
        args.putSerializable(KEY_GENDER, currGender);
        args.putString(KEY_SELF_IDENTIFY_GENDER, currSelfIdentifyGender);
        args.putSerializable(KEY_GENDER_PREF, currGenderPref);
        args.putSerializable(KEY_SMOKE, currSmoke);
        args.putBoolean(KEY_CURR_PERSONAL_VISIBLE, currPersonalVisible);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagefive, null, false);

        findViews(view);
        setPreviousValues();

        builder.setView(view).setTitle("Personal Information (Page 5/7)").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selfIdentify) {
                            selfIdentifyGender = etSelfIdentify.getText().toString();
                        }
                        mListener.sendPageFiveInputs(MatchConstants.PAGE_FOUR, gender, selfIdentifyGender, genderPref, smoke, personalVisible);
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void findViews(View view) {
        radioGroupGenderIdentity = view.findViewById(R.id.radioGroupGenderIdentity);
        radioGroupGenderPreference = view.findViewById(R.id.radioGroupGenderPreference);
        radioGroupSmoking = view.findViewById(R.id.radioGroupSmoking);
        radioMale = view.findViewById(R.id.radioMale);
        radioFemale = view.findViewById(R.id.radioFemale);
        radioSelfIdentify = view.findViewById(R.id.radioSelfIdentify);
        radioNoAnswer = view.findViewById(R.id.radioNoAnswer);
        radioMalePref = view.findViewById(R.id.radioMalePref);
        radioFemalePref = view.findViewById(R.id.radioFemalePref);
        radioNoPref = view.findViewById(R.id.radioNoPref);
        radioNonSmokerNo = view.findViewById(R.id.radioNonSmokerNo);
        radioNonSmokerYes = view.findViewById(R.id.radioNonSmokerYes);
        radioSmoker = view.findViewById(R.id.radioSmoker);
        etSelfIdentify = view.findViewById(R.id.etSelfIdentify);
        checkVisibility = view.findViewById(R.id.checkVisibility);

        checkVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                personalVisible = b;
            }
        });

        radioGroupGenderIdentity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                etSelfIdentify.clearFocus();
                switch(checkedId) {
                    case R.id.radioMale:
                        selfIdentify = false;
                        gender = MatchConstants.Gender.MALE;
                        break;
                    case R.id.radioFemale:
                        selfIdentify = false;
                        gender = MatchConstants.Gender.FEMALE;
                        break;
                    case R.id.radioSelfIdentify:
                        etSelfIdentify.requestFocus();
                        selfIdentify = true;
                        gender = MatchConstants.Gender.SELF_IDENTIFY;
                        break;
                    case R.id.radioNoAnswer:
                        selfIdentify = false;
                        gender = MatchConstants.Gender.NO_ANSWER;
                }
            }
        });

        radioGroupGenderPreference.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                etSelfIdentify.clearFocus();
                switch(checkedId) {
                    case R.id.radioMalePref:
                        genderPref = MatchConstants.GenderPref.MALE;
                        break;
                    case R.id.radioFemalePref:
                        genderPref = MatchConstants.GenderPref.FEMALE;
                        break;
                    case R.id.radioNoPref:
                        genderPref = MatchConstants.GenderPref.NO_PREFERENCE;
                }
            }
        });

        radioGroupSmoking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                etSelfIdentify.clearFocus();
                switch(checkedId) {
                    case R.id.radioNonSmokerNo:
                        smoke = MatchConstants.Smoke.NON_SMOKER_NO;
                        break;
                    case R.id.radioNonSmokerYes:
                        smoke = MatchConstants.Smoke.NON_SMOKER_YES;
                        break;
                    case R.id.radioSmoker:
                        smoke = MatchConstants.Smoke.SMOKER;
                }
            }
        });
    }

    private void setPreviousValues() {
        gender = (MatchConstants.Gender) getArguments().getSerializable(KEY_GENDER);
        selfIdentifyGender = getArguments().getString(KEY_SELF_IDENTIFY_GENDER);
        genderPref = (MatchConstants.GenderPref) getArguments().getSerializable(KEY_GENDER_PREF);
        smoke = (MatchConstants.Smoke) getArguments().getSerializable(KEY_SMOKE);
        personalVisible = getArguments().getBoolean(KEY_CURR_PERSONAL_VISIBLE);

        checkVisibility.setChecked(personalVisible);

        if (gender != null) {
            if (gender == MatchConstants.Gender.MALE) {
                radioMale.setChecked(true);
                radioFemale.setChecked(false);
                radioSelfIdentify.setChecked(false);
                radioNoAnswer.setChecked(false);
            } else if (gender == MatchConstants.Gender.FEMALE) {
                radioMale.setChecked(false);
                radioFemale.setChecked(true);
                radioSelfIdentify.setChecked(false);
                radioNoAnswer.setChecked(false);
            } else if (gender == MatchConstants.Gender.SELF_IDENTIFY) {
                etSelfIdentify.setText(selfIdentifyGender);
                radioMale.setChecked(false);
                radioFemale.setChecked(false);
                radioSelfIdentify.setChecked(true);
                radioNoAnswer.setChecked(false);
            } else {
                radioMale.setChecked(false);
                radioFemale.setChecked(false);
                radioSelfIdentify.setChecked(false);
                radioNoAnswer.setChecked(true);
            }
        }

        if (genderPref != null) {
            if (genderPref == MatchConstants.GenderPref.MALE) {
                radioMalePref.setChecked(true);
                radioFemalePref.setChecked(false);
                radioNoPref.setChecked(false);
            } else if (genderPref == MatchConstants.GenderPref.FEMALE) {
                radioMalePref.setChecked(false);
                radioFemalePref.setChecked(true);
                radioNoPref.setChecked(false);
            } else {
                radioMalePref.setChecked(false);
                radioFemalePref.setChecked(false);
                radioNoPref.setChecked(true);
            }
        }

        if (smoke != null) {
            if (smoke == MatchConstants.Smoke.NON_SMOKER_NO) {
                radioNonSmokerNo.setChecked(true);
                radioNonSmokerYes.setChecked(false);
                radioSmoker.setChecked(false);
            } else if (smoke == MatchConstants.Smoke.NON_SMOKER_YES) {
                radioNonSmokerNo.setChecked(false);
                radioNonSmokerYes.setChecked(true);
                radioSmoker.setChecked(false);
            } else {
                radioNonSmokerNo.setChecked(false);
                radioNonSmokerYes.setChecked(false);
                radioSmoker.setChecked(true);
            }
        }
    }

    // overrides the next button so that dialog doesn't dismiss if all fields are not answered
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gender == null || genderPref == null || smoke == null) {
                        Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selfIdentify) {
                        selfIdentifyGender = etSelfIdentify.getText().toString();
                    }
                    mListener.sendPageFiveInputs(MatchConstants.PAGE_SIX, gender, selfIdentifyGender, genderPref, smoke, personalVisible);
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
            mListener = (PageFiveListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogFive");
        }
    }
}
