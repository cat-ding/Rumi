package com.example.rumi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;

public class MatchDialogSix extends DialogFragment {

    public static final String TAG = "MatchDialogSix";
    private static final String KEY_DESCRIPTION = "currDescription";
    private PageSixListener mListener;

    private EditText etDescription;
    private String description = "";

    public interface PageSixListener {
        void sendPageSixInputs(int nextPage, String description);
    }

    public static MatchDialogSix newInstance(String currDescription) {

        Bundle args = new Bundle();
        MatchDialogSix fragment = new MatchDialogSix();
        args.putString(KEY_DESCRIPTION, currDescription);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagesix, null, false);

        etDescription = view.findViewById(R.id.etDescription);
        // set previous value if any:
        description = getArguments().getString(KEY_DESCRIPTION);
        etDescription.setText(description);

        builder.setView(view).setTitle("Description (Page 6/7)").setPositiveButton("Submit", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        description = etDescription.getText().toString();
                        mListener.sendPageSixInputs(MatchConstants.PAGE_FIVE, description);
                        dismiss();
                    }
                });

        return builder.create();
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
                    if (etDescription.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a description!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    description = etDescription.getText().toString();
                    mListener.sendPageSixInputs(MatchConstants.NO_PAGE, description);
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
            mListener = (PageSixListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogSix");
        }
    }
}
