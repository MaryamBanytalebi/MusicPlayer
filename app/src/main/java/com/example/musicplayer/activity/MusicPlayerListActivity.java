package com.example.musicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.fragments.MusicPlayerListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.View;

import com.example.musicplayer.R;

public class MusicPlayerListActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,MusicPlayerListActivity.class);
        return intent;
    }


    @Override
    public Fragment createFragment() {
        return MusicPlayerListFragment.newInstance();
    }
}