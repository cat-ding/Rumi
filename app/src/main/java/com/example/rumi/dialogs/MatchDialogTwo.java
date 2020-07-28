package com.example.rumi.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private PageTwoListener mListener;

    private RadioGroup radioGroupClean, radioGroupTemp;
    private RadioButton radioAlwaysClean, radioFairlyClean, radioMessy, radioCleanNoPref;
    private RadioButton radioCold, radioFairlyCold, radioFairlyWarm, radioWarm, radioTempNoPref;
    private MatchConstants.Clean cleanPref = null;
    private MatchConstants.Temperature tempPref = null;

    public interface PageTwoListener {
        void sendPageTwoInputs(int nextPage, MatchConstants.Clean cleanPref, MatchConstants.Temperature tempPref);
    }

    public static MatchDialogTwo newInstance(MatchConstants.Clean currCleanPref,
                                             MatchConstants.Temperature currTempPref) {

        Bundle args = new Bundle();
        MatchDialogTwo fragment = new MatchDialogTwo();
        args.putSerializable(KEY_CURR_CLEAN, currCleanPref);
        args.putSerializable(KEY_CURR_TEMP, currTempPref);
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

        builder.setView(view).setTitle("Preferences").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.sendPageTwoInputs(MatchConstants.PAGE_ONE, cleanPref, tempPref);
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
        radioTempNoPref = view.findViewById(R.id.radioTempNoPref);

        radioGroupClean.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioAlwaysClean:
                        cleanPref = MatchConstants.Clean.ALWAYS;
                        break;
                    case R.id.radioFairlyClean:
                        cleanPref = MatchConstants.Clean.FAIRLY;
                        break;
                    case R.id.radioMessy:
                        cleanPref = MatchConstants.Clean.MESSY;
                        break;
                    case R.id.radioCleanNoPref:
                        cleanPref = MatchConstants.Clean.NO_PREFERENCE;
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
                        break;
                    case R.id.radioTempNoPref:
                        tempPref = MatchConstants.Temperature.NO_PREFERENCE;
                }
            }
        });
    }

    private void setPreviousValues() {
        cleanPref = (MatchConstants.Clean) getArguments().getSerializable(KEY_CURR_CLEAN);
        tempPref = (MatchConstants.Temperature) getArguments().getSerializable(KEY_CURR_TEMP);

        if (cleanPref != null) {
            if (cleanPref == MatchConstants.Clean.ALWAYS) {
                radioAlwaysClean.setChecked(true);
                radioFairlyClean.setChecked(false);
                radioMessy.setChecked(false);
                radioCleanNoPref.setChecked(false);
            } else if (cleanPref == MatchConstants.Clean.FAIRLY) {
                radioAlwaysClean.setChecked(false);
                radioFairlyClean.setChecked(true);
                radioMessy.setChecked(false);
                radioCleanNoPref.setChecked(false);
            } else if (cleanPref == MatchConstants.Clean.MESSY) {
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
                radioTempNoPref.setChecked(false);
            } else if (tempPref == MatchConstants.Temperature.FAIRLY_COLD) {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(true);
                radioFairlyWarm.setChecked(false);
                radioWarm.setChecked(false);
                radioTempNoPref.setChecked(false);
            } else if (tempPref == MatchConstants.Temperature.FAIRLY_WARM) {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(false);
                radioFairlyWarm.setChecked(true);
                radioWarm.setChecked(false);
                radioTempNoPref.setChecked(false);
            } else if (tempPref == MatchConstants.Temperature.WARM) {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(false);
                radioFairlyWarm.setChecked(false);
                radioWarm.setChecked(true);
                radioTempNoPref.setChecked(false);
            } else {
                radioCold.setChecked(false);
                radioFairlyCold.setChecked(false);
                radioFairlyWarm.setChecked(false);
                radioWarm.setChecked(false);
                radioTempNoPref.setChecked(true);
            }
        }
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
                    if (cleanPref == null || tempPref == null) {
                        Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mListener.sendPageTwoInputs(MatchConstants.PAGE_THREE, cleanPref, tempPref);
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
            throw new ClassCastException(context.toString() + " must implement MatchDialogThree");
        }
    }
}
