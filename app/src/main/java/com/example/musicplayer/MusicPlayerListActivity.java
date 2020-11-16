package com.example.musicplayer;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.fragments.MusicPlayerListFragment;

public class MusicPlayerListActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MusicPlayerListActivity.class);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return MusicPlayerListFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}