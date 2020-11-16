package com.example.musicplayer;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.musicplayer.fragments.MusicPlayerFragment;

public class MusicPlayerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return MusicPlayerFragment.newInstance(" ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }
}