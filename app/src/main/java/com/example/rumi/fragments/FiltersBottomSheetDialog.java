package com.example.rumi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    public static final String SORT_DEFAULT = "Recent (Default)";
    private static final String SORT_POPULARITY = "Popularity";
    private static final String SORT_RENT_HIGH_TO_LOW = "Rent (High to Low)";
    private static final String SORT_RENT_LOW_TO_HIGH = "Rent (Low to High)";
    private BottomSheetListener mListener;

    private TextView tvDone, tvCancel;
    private Spinner spinnerSort;
    private String sortType = "Recent (Default)";

    public interface BottomSheetListener {
        void sendFilterSelections(String sortType);
    }

    public static FiltersBottomSheetDialog newInstance(String currSort) {
        
        FiltersBottomSheetDialog fragment = new FiltersBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString("currSort", currSort);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_filters, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDone = view.findViewById(R.id.tvDone);
        tvCancel = view.findViewById(R.id.tvCancel);
        spinnerSort = view.findViewById(R.id.spinnerSort);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.sendFilterSelections(sortType);
                dismiss();
            }
        });

        // getting sort type from spinner
        final ArrayAdapter<CharSequence> adapterMajor = ArrayAdapter.createFromResource(getContext(), R.array.sort, android.R.layout.simple_spinner_item);
        adapterMajor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapterMajor);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
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

    private void setPreviousSelections() {
        String currSort = getArguments().getString("currSort");
        int spinnerIndex;
        if (currSort.equals(SORT_DEFAULT))
            spinnerIndex = 0;
        else if (currSort.equals(SORT_POPULARITY))
            spinnerIndex = 1;
        else if (currSort.equals(SORT_RENT_HIGH_TO_LOW))
            spinnerIndex = 2;
        else
            spinnerIndex = 3;
        spinnerSort.setSelection(spinnerIndex);
    }
}
