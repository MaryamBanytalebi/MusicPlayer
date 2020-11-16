package com.example.musicplayer.activity;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;

import com.example.musicplayer.fragments.MusicPlayerDetailFragment;

import java.util.UUID;

public class MusicPlayerDetailActivity extends SingleFragmentActivity {

    private static UUID mSoundId;
    private static String mState;

    public static Intent newIntent(Context context, UUID uuid, String state) {
        mSoundId = uuid;
        mState = state;
        Intent intent = new Intent(context, MusicPlayerDetailActivity.class);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return MusicPlayerDetailFragment.newInstance(mSoundId,mState);
    }
}