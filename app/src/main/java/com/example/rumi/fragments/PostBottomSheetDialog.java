package com.example.rumi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rumi.FilterConstants;
import com.example.rumi.R;
import com.example.rumi.models.Post;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.Arrays;

public class PostBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String KEY_POST = "post";

    private TextView tvName, tvDone;

    private Post post;

    public static PostBottomSheetDialog newInstance(Post post) {
        Bundle args = new Bundle();

        PostBottomSheetDialog fragment = new PostBottomSheetDialog();

        args.putSerializable(KEY_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        tvDone = view.findViewById(R.id.tvDone);

        post = (Post) getArguments().getSerializable(KEY_POST);

        setFields();
    }

    private void setFields() {
        tvName.setText(post.getName());

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
