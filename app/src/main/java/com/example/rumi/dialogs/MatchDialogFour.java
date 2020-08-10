package com.example.rumi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.rumi.MatchConstants;
import com.example.rumi.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class MatchDialogFour extends DialogFragment {

    public static final String TAG = "MatchDialogFour";
    private static final String KEY_CURR_ENTERTAINMENT = "currEntertainment";
    private static final String KEY_CURR_MUSIC = "currMusic";
    private static final String KEY_CURR_ENTERTAINMENT_VISIBLE = "currEntertainmentVisible";
    private static final String KEY_CURR_MUSIC_VISIBLE = "currMusicVisible";
    private PageFourListener mListener;

    private ChipGroup chipGroupEntertainment, chipGroupMusic;
    private Button btnAddEntertainment;
    private EditText etEntertainment;

    private EditText etMusic;
    private ImageView ivAddMusic, ivInfoEntertainment, ivInfoMusic;

    private ArrayList<String> entertainment = new ArrayList<>();
    private ArrayList<String> music = new ArrayList<>();

    private CheckBox checkEntertainmentVisibility, checkMusicVisibility;
    private boolean entertainmentVisible = true, musicVisible = true;

    public interface PageFourListener {
        void sendPageFourInputs(int nextPage, ArrayList<String> entertainment, ArrayList<String> music,
                                boolean entertainmentVisible, boolean musicVisible);
    }

    public static MatchDialogFour newInstance(ArrayList<String> currEntertainment,
                                              ArrayList<String> currMusic,
                                              boolean currEntertainmentVisible,
                                              boolean currMusicVisible) {

        Bundle args = new Bundle();
        MatchDialogFour fragment = new MatchDialogFour();
        args.putStringArrayList(KEY_CURR_ENTERTAINMENT, currEntertainment);
        args.putStringArrayList(KEY_CURR_MUSIC, currMusic);
        args.putBoolean(KEY_CURR_ENTERTAINMENT_VISIBLE, currEntertainmentVisible);
        args.putBoolean(KEY_CURR_MUSIC_VISIBLE, currMusicVisible);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagefour, null, false);

        findViews(view);
        setPreviousValues();

        builder.setView(view).setTitle("Entertainment (Page 4/6)").setPositiveButton("Next", null)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.sendPageFourInputs(MatchConstants.PAGE_THREE, entertainment, music, entertainmentVisible, musicVisible);
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void findViews(View view) {
        chipGroupEntertainment = view.findViewById(R.id.chipGroupEntertainment);
        btnAddEntertainment = view.findViewById(R.id.btnAddEntertainment);
        etEntertainment = view.findViewById(R.id.etEntertainment);
        chipGroupMusic = view.findViewById(R.id.chipGroupMusic);
        etMusic = view.findViewById(R.id.etMusic);
        chipGroupMusic = view.findViewById(R.id.chipGroupMusic);
        ivAddMusic = view.findViewById(R.id.ivAddMusic);
        checkEntertainmentVisibility = view.findViewById(R.id.checkEntertainmentVisibility);
        checkMusicVisibility = view.findViewById(R.id.checkMusicVisibility);
        ivInfoEntertainment = view.findViewById(R.id.ivInfoEntertainment);
        ivInfoMusic = view.findViewById(R.id.ivInfoMusic);

        ivInfoEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfoDialog();
            }
        });

        ivInfoMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfoDialog();
            }
        });

        checkEntertainmentVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                entertainmentVisible = b;
            }
        });

        checkMusicVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                musicVisible = b;
            }
        });

        btnAddEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etEntertainment.getText().toString().trim().toLowerCase();
                if (tag.isEmpty())
                    return;
                if (entertainment.contains(tag)) {
                    Toast.makeText(getContext(), "Can't add a duplicate!", Toast.LENGTH_SHORT).show();
                    return;
                }

                entertainment.add(tag);
                addEntertainmentChip(tag);
                etEntertainment.getText().clear();
            }
        });

        ivAddMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etMusic.getText().toString().trim().toLowerCase();
                if (tag.isEmpty())
                    return;
                if (music.contains(tag)) { // need to look into the strings resource as well
                    Toast.makeText(getContext(), "Can't add a duplicate!", Toast.LENGTH_SHORT).show();
                    return;
                }

                music.add(tag);
                addMusicChip(tag, true);
                etMusic.getText().clear();
            }
        });
    }

    private void openInfoDialog() {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_baseline_info_24)
                .setTitle("Privacy Information")
                .setMessage("Checking this box will display these responses to other users " +
                        "that have filled out the survey to look for a suitable roommate. " +
                        "\n\nThis can help others better understand your preferences and help " +
                        "you find the most compatible match.")
                .setPositiveButton("Done", null).show();
    }

    private void addEntertainmentChip(final String toAdd) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
        chip.setText(toAdd);
        chipGroupEntertainment.addView(chip);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroupEntertainment.removeView(view);
                entertainment.remove(toAdd);
            }
        });
    }

    private void addMusicChip(final String toAdd, boolean checked) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
        chip.setText(toAdd);
        chip.setCheckable(true);
        chip.setChecked(checked);
        chip.setCloseIcon(null);
        chipGroupMusic.addView(chip);

        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    music.add(toAdd);
                } else {
                    music.remove(toAdd);
                }
            }
        });
    }

    private void setPreviousValues() {
        music = getArguments().getStringArrayList(KEY_CURR_MUSIC);
        entertainment = getArguments().getStringArrayList(KEY_CURR_ENTERTAINMENT);
        entertainmentVisible = getArguments().getBoolean(KEY_CURR_ENTERTAINMENT_VISIBLE);
        musicVisible = getArguments().getBoolean(KEY_CURR_MUSIC_VISIBLE);

        checkEntertainmentVisibility.setChecked(entertainmentVisible);
        checkMusicVisibility.setChecked(musicVisible);

        ArrayList<String> addedMusic = new ArrayList<>();
        // add this first to preserve the order
        for (String genre : getResources().getStringArray(R.array.music)) {
            if (music.contains(genre)) {
                addMusicChip(genre, true);
            } else {
                addMusicChip(genre, false);
            }
            addedMusic.add(genre);
        }

        for (String genre : music) {
            if (!addedMusic.contains(genre)) {
                addMusicChip(genre, true);
            }
        }

        for (String entertain : entertainment) {
            addEntertainmentChip(entertain);
        }
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
                    mListener.sendPageFourInputs(MatchConstants.PAGE_FIVE, entertainment, music, entertainmentVisible, musicVisible);
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
            mListener = (PageFourListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MatchDialogFour");
        }
    }
}
