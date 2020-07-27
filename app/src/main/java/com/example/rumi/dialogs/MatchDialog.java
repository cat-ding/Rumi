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
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.rumi.R;

public class MatchDialog extends AppCompatDialogFragment {

    public static final String TAG = "MatchDialog";
    public static final String KEY_PAGE_NUM = "pageNum";
    private PageOneListener mListener;

    public static final int PAGE_ZERO = 0;
    public static final int PAGE_ONE = 1;
    public static final int PAGE_TWO = 2;

    private int pageNum = 1;

    public interface PageOneListener {
        void sendPageOneInputs(int nextPage);
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
            builder.setView(view).setTitle("Page One").setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.sendPageOneInputs(PAGE_TWO);
                    dismiss();
                }
            }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.sendPageOneInputs(PAGE_ZERO);
                    dismiss();
                }
            });
        } else {
            View view = inflater.inflate(R.layout.dialog_pagetwo, null, false);
            builder.setView(view).setTitle("Page Two").setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.sendPageOneInputs(PAGE_ONE);
                    dismiss();
                }
            });
        }
        return builder.create();
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
