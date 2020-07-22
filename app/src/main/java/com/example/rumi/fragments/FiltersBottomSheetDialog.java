package com.example.rumi.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rumi.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FiltersBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "FiltersBottomSheetDialog";
    private BottomSheetListener mListener;

    private TextView tvDone, tvCancel;

    public interface BottomSheetListener {
        void sendFilterSelections();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_filters, container, false);

        tvDone = v.findViewById(R.id.tvDone);
        tvCancel = v.findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: interface
                mListener.sendFilterSelections();
            }
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
