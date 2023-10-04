package com.facuu16.hp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facuu16.hp.R;

public class SplashFragment extends Fragment {

    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}