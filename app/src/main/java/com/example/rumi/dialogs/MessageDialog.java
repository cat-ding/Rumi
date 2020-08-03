package com.example.rumi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.google.android.material.textfield.TextInputLayout;

public class MessageDialog extends DialogFragment {

    private TextInputLayout layoutMessage;
    private MessageListener mListener;
    private String message;

    public interface MessageListener {
        void sendMessage(String message);
    }

    public static MessageDialog newInstance() {

        Bundle args = new Bundle();

        MessageDialog fragment = new MessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_message, null, false);
        layoutMessage = view.findViewById(R.id.layoutMessage);
        builder.setView(view).setTitle("Send a message").setPositiveButton("Send", null)
                .setNegativeButton("Cancel", null);
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
                    message = layoutMessage.getEditText().getText().toString().trim();
                    if (message.isEmpty()) {
                        Toast.makeText(getContext(), "Message can't be empty!", Toast.LENGTH_SHORT).show();
                    }
                    mListener.sendMessage(message);
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
            mListener = (MessageListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MessageListener");
        }
    }

}
