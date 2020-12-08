package com.example.musicplayer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.musicplayer.repository.MusicPlayerRepository;

import java.util.UUID;

public class OnClearFromRecentService extends IntentService {

    public static final String BBIS = "BBIS";
    private MusicPlayerRepository mRepository;
    private UUID mPlayingSoundId;

    public OnClearFromRecentService() {
        super(BBIS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mRepository = MusicPlayerRepository.getInstance(getApplicationContext());
        String action = intent.getAction();
        mPlayingSoundId = mRepository.getPlayingSound().getSoundId();

        if ("action_previous".equalsIgnoreCase(action)) {
            previousMethod();
        } else if ("action_play".equalsIgnoreCase(action)) {
            playMethod();
        } else if ("action_next".equalsIgnoreCase(action)) {
            nextMethod();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void nextMethod() {
        mRepository.nextSound(mRepository.getSound(mPlayingSoundId));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void previousMethod() {
        mRepository.previousSound(mRepository.getSound(mPlayingSoundId));
    }

    private void playMethod() {
        if (mRepository.getMediaPlayer().isPlaying()) {
            mRepository.pause();
        } else {
            mRepository.playAgain();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }
}
