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
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;

public class MatchDialogOne extends AppCompatDialogFragment {

    public static final String TAG = "MatchDialogOne";
    public static final String KEY_CURR_HOUSE_PREF = "currHousePref";
    public static final String KEY_CURR_WEEKEND_PREF = "currWeekendPref";
    public static final String KEY_CURR_GUESTS_PREF = "currGuestsPref";

    private PageOneListener mListener;

    private int pageNum = 1;

    // for page one - room use
    private RadioGroup radioGroupHousePreference, radioGroupWeekend, radioGroupGuests;
    private RadioButton radioQuiet, radioSocial, radioWeekCombo, radioWeekNoPreference;
    private RadioButton radioParty, radioHang, radioWeekendQuiet, radioNotHome;
    private RadioButton radioOccasionalGuest, radioNoGuests, radioGuestsNoPreference;
    private MatchConstants.House housePref = null;
    private MatchConstants.Weekend weekendPref = null;
    private MatchConstants.Guests guestsPref = null;


    public interface PageOneListener {
        void sendPageOneInputs(int nextPage, MatchConstants.House housePref,
                               MatchConstants.Weekend weekendPref, MatchConstants.Guests guestsPref);
    }

    public static MatchDialogOne newInstance(MatchConstants.House currHousePref,
                                             MatchConstants.Weekend currWeekendPref,
                                             MatchConstants.Guests currGuestsPref) {
        Bundle args = new Bundle();
        MatchDialogOne fragment = new MatchDialogOne();
        args.putSerializable(KEY_CURR_HOUSE_PREF, currHousePref);
        args.putSerializable(KEY_CURR_WEEKEND_PREF, currWeekendPref);
        args.putSerializable(KEY_CURR_GUESTS_PREF, currGuestsPref);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_pageone, null, false);
        findViews(view);
        setPreviousValues();
        builder.setView(view).setTitle("Room Use").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.sendPageOneInputs(MatchConstants.PAGE_ZERO, housePref, weekendPref, guestsPref);
                        dismiss();
                    }
                });
        return builder.create();
    }

    private void findViews(View view) {
        radioGroupHousePreference = view.findViewById(R.id.radioGroupHousePreference);
        radioGroupWeekend = view.findViewById(R.id.radioGroupWeekend);
        radioGroupGuests = view.findViewById(R.id.radioGroupGuests);
        radioQuiet = view.findViewById(R.id.radioQuiet);
        radioSocial = view.findViewById(R.id.radioSocial);
        radioWeekCombo = view.findViewById(R.id.radioWeekCombo);
        radioWeekNoPreference = view.findViewById(R.id.radioWeekNoPreference);
        radioParty = view.findViewById(R.id.radioParty);
        radioHang = view.findViewById(R.id.radioHang);
        radioWeekendQuiet = view.findViewById(R.id.radioWeekendQuiet);
        radioNotHome = view.findViewById(R.id.radioNotHome);
        radioOccasionalGuest = view.findViewById(R.id.radioOccasionalGuest);
        radioNoGuests = view.findViewById(R.id.radioNoGuests);
        radioGuestsNoPreference = view.findViewById(R.id.radioGuestsNoPreference);

        radioGroupHousePreference.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioQuiet:
                        housePref = MatchConstants.House.QUIET;
                        break;
                    case R.id.radioSocial:
                        housePref = MatchConstants.House.SOCIAL;
                        break;
                    case R.id.radioWeekCombo:
                        housePref = MatchConstants.House.COMBO;
                        break;
                    case R.id.radioWeekNoPreference:
                        housePref = MatchConstants.House.NO_PREFERENCE;
                }
            }
        });

        radioGroupWeekend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioParty:
                        weekendPref = MatchConstants.Weekend.PARTY;
                        break;
                    case R.id.radioHang:
                        weekendPref = MatchConstants.Weekend.HANG;
                        break;
                    case R.id.radioWeekendQuiet:
                        weekendPref = MatchConstants.Weekend.QUIET;
                        break;
                    case R.id.radioNotHome:
                        weekendPref = MatchConstants.Weekend.NO_PREFERENCE;
                }
            }
        });

        radioGroupGuests.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioOccasionalGuest:
                        guestsPref = MatchConstants.Guests.OCCASIONAL;
                        break;
                    case R.id.radioNoGuests:
                        guestsPref = MatchConstants.Guests.NO_GUESTS;
                        break;
                    case R.id.radioGuestsNoPreference:
                        guestsPref = MatchConstants.Guests.NO_PREFERENCE;
                }
            }
        });
    }

    private void setPreviousValues() {
        housePref = (MatchConstants.House) getArguments().getSerializable(KEY_CURR_HOUSE_PREF);
        weekendPref = (MatchConstants.Weekend) getArguments().getSerializable(KEY_CURR_WEEKEND_PREF);
        guestsPref = (MatchConstants.Guests) getArguments().getSerializable(KEY_CURR_GUESTS_PREF);

        if (housePref != null) {
            if (housePref == MatchConstants.House.QUIET) {
                radioQuiet.setChecked(true);
                radioSocial.setChecked(false);
                radioWeekCombo.setChecked(false);
                radioWeekNoPreference.setChecked(false);
            } else if (housePref == MatchConstants.House.SOCIAL) {
                radioQuiet.setChecked(false);
                radioSocial.setChecked(true);
                radioWeekCombo.setChecked(false);
                radioWeekNoPreference.setChecked(false);
            } else if (housePref == MatchConstants.House.COMBO) {
                radioQuiet.setChecked(false);
                radioSocial.setChecked(false);
                radioWeekCombo.setChecked(true);
                radioWeekNoPreference.setChecked(false);
            } else {
                radioQuiet.setChecked(false);
                radioSocial.setChecked(false);
                radioWeekCombo.setChecked(false);
                radioWeekNoPreference.setChecked(true);
            }
        }

        if (weekendPref != null) {
            if (weekendPref == MatchConstants.Weekend.PARTY) {
                radioParty.setChecked(true);
                radioHang.setChecked(false);
                radioWeekendQuiet.setChecked(false);
                radioNotHome.setChecked(false);
            } else if (weekendPref == MatchConstants.Weekend.HANG) {
                radioParty.setChecked(false);
                radioHang.setChecked(true);
                radioWeekendQuiet.setChecked(false);
                radioNotHome.setChecked(false);
            } else if (weekendPref == MatchConstants.Weekend.QUIET) {
                radioParty.setChecked(false);
                radioHang.setChecked(false);
                radioWeekendQuiet.setChecked(true);
                radioNotHome.setChecked(false);
            } else {
                radioParty.setChecked(false);
                radioHang.setChecked(false);
                radioWeekendQuiet.setChecked(false);
                radioNotHome.setChecked(true);
            }
        }

        if (guestsPref != null) {
            if (guestsPref == MatchConstants.Guests.OCCASIONAL) {
                radioOccasionalGuest.setChecked(true);
                radioNoGuests.setChecked(false);
                radioGuestsNoPreference.setChecked(false);
            } else if (guestsPref == MatchConstants.Guests.NO_GUESTS) {
                radioOccasionalGuest.setChecked(false);
                radioNoGuests.setChecked(true);
                radioGuestsNoPreference.setChecked(false);
            } else {
                radioOccasionalGuest.setChecked(false);
                radioNoGuests.setChecked(false);
                radioGuestsNoPreference.setChecked(true);
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
                    if (housePref == null || weekendPref == null || guestsPref == null) {
                        Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mListener.sendPageOneInputs(MatchConstants.PAGE_TWO, housePref, weekendPref, guestsPref);
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
            mListener = (PageOneListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogOne");
        }
    }
}
