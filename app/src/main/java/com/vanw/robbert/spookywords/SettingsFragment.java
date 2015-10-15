package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends android.support.v4.app.Fragment {
    // the fragment initialization parameter
    // this is used to determine if menu was opened in game
    protected static final String ARG_INGAME = "ingame";

    private String inGame;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ingame Parameter 1.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String ingame) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INGAME, ingame);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inGame = getArguments().getString(ARG_INGAME);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layoutId;
        if(inGame != null && Objects.equals(inGame, "ingame")) { // use different layout based on parameter
            layoutId = R.layout.fragment_settings_ingame;
        }
        else {
            layoutId = R.layout.fragment_settings;
        }
        // Inflate the layout for this fragment
        return inflater.inflate(layoutId, container, false);
    }
}
