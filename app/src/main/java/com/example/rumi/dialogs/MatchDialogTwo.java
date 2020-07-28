package com.example.rumi.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;

public class MatchDialogTwo extends DialogFragment {

    public static final String TAG = "MatchDialogOne";
    private PageTwoListener mListener;

    public interface PageTwoListener {
        void sendPageTwoInputs(int nextPage);
    }

    public static MatchDialogTwo newInstance() {

        Bundle args = new Bundle();

        MatchDialogTwo fragment = new MatchDialogTwo();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_pagetwo, null, false);
        builder.setView(view).setTitle("Room Care").setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.sendPageTwoInputs(MatchConstants.PAGE_ONE);
                dismiss();
            }
        });

        return builder.create();
    }

    // called when fragment is attached to a host fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (PageTwoListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogOne");
        }
    }
}
