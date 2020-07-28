package com.example.rumi.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;

public class MatchDialog extends AppCompatDialogFragment {

    public static final String TAG = "MatchDialog";
    public static final String KEY_PAGE_NUM = "pageNum";
    private PageOneListener mListener;

    public static final int PAGE_ZERO = 0;
    public static final int PAGE_ONE = 1;
    public static final int PAGE_TWO = 2;

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
        void sendPageOneInputs(int nextPage, MatchConstants.House housePref, MatchConstants.Weekend weekendPref, MatchConstants.Guests guestsPref);
    }

    public static MatchDialog newInstance(int pageNum) {
        
        Bundle args = new Bundle();
        MatchDialog fragment = new MatchDialog();

        args.putInt(KEY_PAGE_NUM, pageNum);

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        pageNum = getArguments().getInt(KEY_PAGE_NUM);

        if (pageNum == PAGE_ONE) {
            View view = inflater.inflate(R.layout.dialog_pageone, null, false);
            findPageOneViews(view);
            builder.setView(view).setTitle("Room Use").setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!getPageOneInputs())
                        return;
                    mListener.sendPageOneInputs(PAGE_TWO, housePref, weekendPref, guestsPref);
                    dismiss();
                }
            }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.sendPageOneInputs(PAGE_ZERO, housePref, weekendPref, guestsPref);
                    dismiss();
                }
            });
        } else { // PAGE TWO
            View view = inflater.inflate(R.layout.dialog_pagetwo, null, false);
            builder.setView(view).setTitle("Room Care").setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.sendPageOneInputs(PAGE_ONE, housePref, weekendPref, guestsPref);
                    dismiss();
                }
            });
        }
        return builder.create();
    }

    private void findPageOneViews(View view) {
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
                        Toast.makeText(getContext(), "QUIET", Toast.LENGTH_SHORT).show();
                        housePref = MatchConstants.House.QUIET;
                        break;
                    case R.id.radioSocial:
                        Toast.makeText(getContext(), "SOCIAL", Toast.LENGTH_SHORT).show();
                        housePref = MatchConstants.House.SOCIAL;
                        break;
                    case R.id.radioWeekCombo:
                        Toast.makeText(getContext(), "COMBO", Toast.LENGTH_SHORT).show();
                        housePref = MatchConstants.House.COMBO;
                        break;
                    case R.id.radioWeekNoPreference:
                        Toast.makeText(getContext(), "NOPREF", Toast.LENGTH_SHORT).show();
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

    // returns false if at least one field is blank
    private boolean getPageOneInputs() {
        if (housePref == null || weekendPref == null || guestsPref == null) {
            Toast.makeText(getContext(), "Please answer all the questions!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true; // nothing left blank
    }

    // called when fragment is attached to a host fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (PageOneListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialog");
        }
    }
}
