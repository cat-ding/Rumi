package com.example.rumi.dialogs;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;

public class MatchDialogTwo extends DialogFragment {

    public static final String TAG = "MatchDialogThree";
    public static final String KEY_CURR_CLEAN = "currCleanPref";
    public static final String KEY_CURR_TEMP = "currTempPref";
    public static final String KEY_CURR_PREF_VISIBLE = "currPrefVisibility";
    private PageTwoListener mListener;

    private RadioGroup radioGroupClean, radioGroupTemp;
    private RadioButton radioAlwaysClean, radioFairlyClean, radioMessy, radioCleanNoPref;
    private RadioButton radioCold, radioFairlyCold, radioFairlyWarm, radioWarm;
    private MatchConstants.Clean cleanPref = null;
    private MatchConstants.Temperature tempPref = null;

    private CheckBox checkVisibility;
    private boolean preferencesVisible;

    private ImageView ivInfo;

    public interface PageTwoListener {
        void sendPageTwoInputs(int nextPage, MatchConstants.Clean cleanPref,
                               MatchConstants.Temperature tempPref, boolean preferencesVisible);
    }

    public static MatchDialogTwo newInstance(MatchConstants.Clean currCleanPref,
                                             MatchConstants.Temperature currTempPref,
                                             boolean currPrefVisibility) {

        Bundle args = new Bundle();
        MatchDialogTwo fragment = new MatchDialogTwo();
        args.putSerializable(KEY_CURR_CLEAN, currCleanPref);
        args.putSerializable(KEY_CURR_TEMP, currTempPref);
        args.putBoolean(KEY_CURR_PREF_VISIBLE, currPrefVisibility);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagetwo, null, false);

        findViews(view);
        setPreviousValues();

        builder.setView(view).setTitle("Preferences (Page 2/6)").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.sendPageTwoInputs(MatchConstants.PAGE_ONE, cleanPref, tempPref, preferencesVisible);
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void findViews(View view) {

        radioGroupClean = view.findViewById(R.id.radioGroupClean);
        radioGroupTemp = view.findViewById(R.id.radioGroupTemp);
        radioAlwaysClean = view.findViewById(R.id.radioAlwaysClean);
        radioFairlyClean = view.findViewById(R.id.radioFairlyClean);
        radioMessy = view.findViewById(R.id.radioMessy);
        radioCleanNoPref = view.findViewById(R.id.radioCleanNoPref);
        radioCold = view.findViewById(R.id.radioCold);
        radioFairlyCold = view.findViewById(R.id.radioFairlyCold);
        radioFairlyWarm = view.findViewById(R.id.radioFairlyWarm);
        radioWarm = view.findViewById(R.id.radioWarm);
        checkVisibility = view.findViewById(R.id.checkVisibility);
        ivInfo = view.findViewById(R.id.ivInfo);

        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_baseline_info_24)
                        .setTitle("Privacy Information")
                        .setMessage("Checking this box will display these responses to other users " +
                                "that have filled out the survey to look for a suitable roommate. " +
                                "\n\nThis can help others better understand your preferences and help " +
                                "you find the most compatible match.")
                        .setPositiveButton("Done", null).show();
            }
        });

        checkVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferencesVisible = b;
            }
        });

        radioGroupClean.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioAlwaysClean:
                        cleanPref = MatchConstants.Clean.ALWAYS_CLEAN;
                        break;
                    case R.id.radioFairlyClean:
                        cleanPref = MatchConstants.Clean.FAIRLY_CLEAN;
                        break;
                    case R.id.radioMessy:
                        cleanPref = MatchConstants.Clean.FAIRLY_MESSY;
                        break;
                    case R.id.radioCleanNoPref:
                        cleanPref = MatchConstants.Clean.VERY_MESSY;
                }
            }
        });

        radioGroupTemp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioCold:
                        tempPref = MatchConstants.Temperature.COLD;
                        break;
                    case R.id.radioFairlyCold:
                        tempPref = MatchConstants.Temperature.FAIRLY_COLD;
                        break;
                    case R.id.radioFairlyWarm:
                        tempPref = MatchConstants.Temperature.FAIRLY_WARM;
                        break;
                    case R.id.radioWarm:
                        tempPref = MatchConstants.Temperature.WARM;
                }
            }
        });
    }

    private void setPreviousValues() {
        cleanPref = (MatchConstants.Clean) getArguments().getSerializable(KEY_CURR_CLEAN);
        tempPref = (MatchConstants.Temperature) getArguments().getSerializable(KEY_CURR_TEMP);
        preferencesVisible = getArguments().getBoolean(KEY_CURR_PREF_VISIBLE);

        checkVisibility.setChecked(preferencesVisible);

        if (cleanPref != null) {
            if (cleanPref == MatchConstants.Clean.ALWAYS_CLEAN) {
                radioAlwaysClean.setChecked(true);
                radioFairlyClean.setChecked(false);
                radioMessy.setChecked(false);
                radioCleanNoPref.setChecked(false);
            } else if (cleanPref == MatchConstants.Clean.FAIRLY_CLEAN) {
                radioAlwaysClean.setChecked(false);
                radioFairlyClean.setChecked(true);
                radioMessy.setChecked(false);
                radioCleanNoPref.setChecked(false);
            } else if (cleanPref == MatchConstants.Clean.FAIRLY_MESSY) {
                radioAlwaysClean.setChecked(false);
                radioFairlyClean.setChecked(false);
                radioMessy.setChecked(true);
                radioCleanNoPref.setChecked(false);
            } else {
                radioAlwaysClean.setChecked(false);
                radioFairlyClean.setChecked(false);
                radioMessy.setChecked(false);
                radioCleanNoPref.setChecked(true);
            }
        }

        if (tempPref != null) {
            if (tempPref == MatchConstants.Temperature.COLD) {
                radioCold.setChecked(true);
                radioFairlyCold.setChecked(false);
                radioFairlyWarm.setChecked(false);
                radioWarm.setChecked(false);
            } else if (tempPref == MatchConstants.Temperature.FAIRLY_COLD) {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(true);
                radioFairlyWarm.setChecked(false);
                radioWarm.setChecked(false);
            } else if (tempPref == MatchConstants.Temperature.FAIRLY_WARM) {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(false);
                radioFairlyWarm.setChecked(true);
                radioWarm.setChecked(false);
            } else {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(false);
                radioFairlyWarm.setChecked(false);
                radioWarm.setChecked(true);
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
                    if (cleanPref == null || tempPref == null) {
                        Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mListener.sendPageTwoInputs(MatchConstants.PAGE_THREE, cleanPref, tempPref, preferencesVisible);
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
            mListener = (PageTwoListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogTwo");
        }
    }
}
