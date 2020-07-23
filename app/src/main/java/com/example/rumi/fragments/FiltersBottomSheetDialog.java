package com.example.rumi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rumi.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;

public class FiltersBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "FiltersBottomSheetDialog";
    private static final String KEY_CURR_SORT = "currSort";
    private static final String KEY_CURR_LOOKING_FOR = "currLookingFor";
    private static final String KEY_CURR_NUM_ROOMS = "currNumRooms";
    private static final String KEY_CURR_FURNISHED = "currFurnished";

    public static final String SORT_DEFAULT = "Recent (Default)";
    public static final int LOOKING_FOR_DEFAULT = -1; // "Any"
    public static final int LOOKING_FOR_PLACE = 0; // "A Place"
    public static final int LOOKING_FOR_TENANT = 1; // "A Tenant"
    public static final int FILTER_ROOMS_DEFAULT = 0;
    public static final int FURNISHED_DEFAULT = -1; // include both furnished and unfurnished
    public static final int FURNISHED_YES = 0;
    public static final int FURNISHED_NO = 1;
    private BottomSheetListener mListener;

    private TextView tvDone, tvCancel, tvNumRooms;
    private Spinner spinnerSort;
    private String sortType = SORT_DEFAULT;
    private RadioGroup radioGroupLookingFor, radioGroupFurnished;
    private RadioButton radioAll, radioPlace, radioTenant, radioBoth, radioYes, radioNo;
    private SeekBar seekBarNumRooms;
    private int numRooms = FILTER_ROOMS_DEFAULT, lookingFor = LOOKING_FOR_DEFAULT, furnished = FURNISHED_DEFAULT;

    public interface BottomSheetListener {
        void sendFilterSelections(String sortType, int lookingFor, int filterNumRooms, int furnished);
    }

    public static FiltersBottomSheetDialog newInstance(String currSort, int currLookingFor,
                                                       int currNumRooms, int currFurnished) {
        FiltersBottomSheetDialog fragment = new FiltersBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(KEY_CURR_SORT, currSort);
        args.putInt(KEY_CURR_LOOKING_FOR, currLookingFor);
        args.putInt(KEY_CURR_NUM_ROOMS, currNumRooms);
        args.putInt(KEY_CURR_FURNISHED, currFurnished);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDone = view.findViewById(R.id.tvDone);
        tvCancel = view.findViewById(R.id.tvCancel);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        radioGroupLookingFor = view.findViewById(R.id.radioGroupLookingFor);
        radioAll = view.findViewById(R.id.radioAll);
        radioPlace = view.findViewById(R.id.radioPlace);
        radioTenant = view.findViewById(R.id.radioTenant);
        seekBarNumRooms = view.findViewById(R.id.seekBarNumRooms);
        tvNumRooms = view.findViewById(R.id.tvNumRooms);
        radioGroupFurnished = view.findViewById(R.id.radioGroupFurnished);
        radioBoth = view.findViewById(R.id.radioBoth);
        radioYes = view.findViewById(R.id.radioYes);
        radioNo = view.findViewById(R.id.radioNo);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.sendFilterSelections(sortType, lookingFor, numRooms, furnished);
                dismiss();
            }
        });

        // get furnished filter selection from radio group
        radioGroupFurnished.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioBoth:
                        furnished = -1;
                        break;
                    case R.id.radioYes:
                        furnished = 0;
                        break;
                    case R.id.radioNo:
                        furnished = 1;
                        break;
                }
            }
        });

        // get num rooms filter selection from seekbar
        seekBarNumRooms.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress == FILTER_ROOMS_DEFAULT) {
                    tvNumRooms.setText("Any");
                } else if (progress == 1) {
                    tvNumRooms.setText(progress + " room");
                } else {
                    tvNumRooms.setText(progress + " rooms");
                }
                numRooms = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // get looking for filter selection from radio group
        radioGroupLookingFor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radioAll:
                        lookingFor = -1;
                        break;
                    case R.id.radioPlace:
                        lookingFor = 0;
                        break;
                    case R.id.radioTenant:
                        lookingFor = 1;
                        break;
                }
            }
        });

        // getting sort type from spinner
        final ArrayAdapter<CharSequence> adapterMajor = ArrayAdapter.createFromResource(getContext(), R.array.sort, android.R.layout.simple_spinner_item);
        adapterMajor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapterMajor);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { return; }
        });

        setPreviousSelections();
    }

    // called when fragment is attached to a host fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    // setting all the filters to what was set previously
    private void setPreviousSelections() {
        // setting sort
        String currSort = getArguments().getString(KEY_CURR_SORT);
        int spinnerIndex = Arrays.asList((getResources().getStringArray(R.array.sort))).indexOf(currSort);
        spinnerSort.setSelection(spinnerIndex);

        // setting looking for radio buttons (radioAll is selected on default)
        int currLookingFor = getArguments().getInt(KEY_CURR_LOOKING_FOR);
        if (currLookingFor == LOOKING_FOR_PLACE) {
            radioAll.setChecked(false);
            radioPlace.setChecked(true);
            radioTenant.setChecked(false);
        } else if (currLookingFor == LOOKING_FOR_TENANT) {
            radioAll.setChecked(false);
            radioPlace.setChecked(false);
            radioTenant.setChecked(true);
        }

        // setting seekbar num rooms
        seekBarNumRooms.setProgress(getArguments().getInt(KEY_CURR_NUM_ROOMS));

        // setting furnished radio buttons (radioBoth is selected on default)
        int currFurnished = getArguments().getInt(KEY_CURR_FURNISHED);
        if (currFurnished == FURNISHED_YES) {
            radioBoth.setChecked(false);
            radioYes.setChecked(true);
            radioNo.setChecked(false);
        } else if (currFurnished == FURNISHED_NO) {
            radioBoth.setChecked(false);
            radioYes.setChecked(false);
            radioNo.setChecked(true);
        }
    }
}
