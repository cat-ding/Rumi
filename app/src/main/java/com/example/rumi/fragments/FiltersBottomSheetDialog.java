package com.example.rumi.fragments;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;

import com.example.rumi.R;
import com.example.rumi.RegisterActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FiltersBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "FiltersBottomSheetDialog";
    private BottomSheetListener mListener;

    private TextView tvDone, tvCancel;
    private Spinner spinnerSort;
    private String sortType = "Recent (Default)";

    public interface BottomSheetListener {
        void sendFilterSelections(String sortType);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_filters, container, false);

        tvDone = v.findViewById(R.id.tvDone);
        tvCancel = v.findViewById(R.id.tvCancel);
        spinnerSort = v.findViewById(R.id.spinnerSort);

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

        return v;
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
}
