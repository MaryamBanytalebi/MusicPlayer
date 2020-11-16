package com.example.musicplayer.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Sound;
import com.example.musicplayer.repository.MusicPlayerRepository;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MusicPlayerFragment extends Fragment {


    public static final String TAG = "BeatBoxFragment";
    public static final String BUNDLE_KEY_STATE = "Bundle_Key_State";
    private RecyclerView mRecyclerView;
    private MusicPlayerRepository mRepository;
    private List<Sound> mSounds;
    private SeekBar mSeekBar;
    private ImageButton mImageButton_Pause, mImageButton_Play;
    private TextView mTextViewTime;
    private ImageView mImageViewSeekBar;
    private MutableLiveData<String> mLiveDataTime;
    private String mState;
    private Bitmap mBitmap;
    private Drawable mDrawable;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    public static MusicPlayerFragment newInstance(String state) {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_STATE,state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setRetainInstance(true);
        mState = getArguments().getString(BUNDLE_KEY_STATE);
        mRepository = MusicPlayerRepository.getInstance(getContext());
        mSounds = mRepository.getSounds();
        mLiveDataTime = new MutableLiveData<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mRepository.release();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        findViews(view);
        if (mRepository.getPlayingSound()!=null){
            if (mRepository.getPlayingSound().getBitmap()!=null) {
                mImageViewSeekBar.setImageBitmap(mRepository.getPlayingSound().getBitmap());
            }
            else {
                mImageViewSeekBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
            }
        }
        initViews();
        listeners();
        seekBar();
        setLiveDataObservers();
        setupAdapter();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView");
    }


    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_beat_box);
        /*mSeekBar = view.findViewById(R.id.seekBar);
        mImageButton_Play = view.findViewById(R.id.imageBtn_play);
        mImageButton_Pause = view.findViewById(R.id.imageBtn_pause);
        mTextViewTime = view.findViewById(R.id.txtView_Time);*/
        mImageViewSeekBar = view.findViewById(R.id.imageSeekBar);
    }

    private void initViews() {

        int rowNumber = getResources().getInteger(R.integer.row_number);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), rowNumber));
        if (!mRepository.getMediaPlayer().isPlaying())
            mSeekBar.setEnabled(false);
    }

    private void listeners() {

        mImageButton_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.playAgain();
            }
        });
        mImageButton_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.pause();
            }
        });
    }

    private void setLiveDataObservers() {
        mLiveDataTime.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String time) {
                mTextViewTime.setText(time);
            }
        });
    }

    private void seekBar() {
        mSeekBar.setMax(mRepository.getMediaPlayer().getDuration());
        mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getDuration()) - (minutes * 60);

        final String maxTime = "/" + minutes + ":" + seconds;
        mTextViewTime.setText("0" + maxTime);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getCurrentPosition());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getCurrentPosition()) - (minutes * 60);
                String currentTime;
                if (minutes != 0) {
                    currentTime = minutes + ":" + seconds;
                } else {
                    currentTime = "" + seconds;
                }
                String time = currentTime + maxTime;
                mLiveDataTime.postValue(time);
//                mTextViewTime.setText(currentTime + maxTime);
            }
        }, 0, 1000);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    mRepository.getMediaPlayer().seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupAdapter() {
        List<Sound> sounds = mRepository.getSounds();
        SoundAdapter adapter = new SoundAdapter(sounds);
        mRecyclerView.setAdapter(adapter);
    }

    private class SoundHolder extends RecyclerView.ViewHolder {

        private ImageButton mButton;
        private TextView mTextMusicName;
        private Sound mSound;

        public SoundHolder(@NonNull View itemView) {
            super(itemView);

            mButton = itemView.findViewById(R.id.button_beat_box);
            mTextMusicName = itemView.findViewById(R.id.txt_music_name);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRepository.loadMusic(mSound.getName());
                    mSeekBar.setEnabled(true);
                    if (mSound.getBitmap()!=null) {
                        mImageViewSeekBar.setImageBitmap(mSound.getBitmap());
                    }
                    else {
                        mImageViewSeekBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
                    }
                    MediaPlayer mediaPlayer = mRepository.getMediaPlayer();
                   /* mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            int index = -1;
                            int nextMusic = -1;
                            for (int i = 0; i < mSounds.size(); i++) {
                                if (mSounds.get(i).equals(mSound)) {
                                    index = i;
                                    nextMusic = i + 1;
                                }
                            }
                            if (index != mSounds.size() - 1) {
                                mRepository.loadMusic(mSounds.get(nextMusic).getName());
                                mImageViewSeekBar.setImageDrawable(mSounds.get(nextMusic).getDrawable());
                            }
                            else {
                                mRepository.loadMusic(mSounds.get(0).getName());
                                mImageViewSeekBar.setImageDrawable(mSounds.get(0).getDrawable());
                            }
                        }
                    });*/
                }
            });
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            if (mState.equalsIgnoreCase("Tracks"))
                mTextMusicName.setText(mSound.getTitle());
            else if (mState.equalsIgnoreCase("Artists"))
                mTextMusicName.setText(mSound.getArtist());
            else if (mState.equalsIgnoreCase("Albums"))
                mTextMusicName.setText(mSound.getAlbum());

            if (sound.getBitmap()!=null)
                mButton.setImageBitmap(sound.getBitmap());
            else
                mButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public List<Sound> getSounds() {
            return mSounds;
        }

        public void setSounds(List<Sound> sounds) {
            mSounds = sounds;
        }

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_sound, parent, false);

            return new SoundHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
}