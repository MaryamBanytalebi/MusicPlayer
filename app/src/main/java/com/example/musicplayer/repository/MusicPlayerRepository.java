package com.example.musicplayer.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MusicPlayerRepository {

    public static final String TAG = "Music Player";
    private static String ASSET_FOLDER = "musics";
    private static MusicPlayerRepository sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Sound> mSounds = new ArrayList<>();
    private int mIndex;
    private Boolean mFlagPlay;
    private MutableLiveData<Sound> mLiveDataPlayingSound;
    private MutableLiveData<Boolean> mLiveDataIsPlaying;
    private Sound mPlayingSound;
    private boolean isMusicPlaying;
    private boolean isRepeatOne;
    private boolean isRepeatAll;
    private boolean isRepeat;
    private Uri mUri;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static MusicPlayerRepository getInstance(Context context) {
        if (sInstance == null)
            sInstance = new MusicPlayerRepository(context);
        return sInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private MusicPlayerRepository(Context context) {
        mContext = context.getApplicationContext();
        getSongFromExternal();
        loadSounds();
        mFlagPlay = false;
        mIndex = 0;
        mLiveDataPlayingSound = new MutableLiveData<>();
        mLiveDataIsPlaying = new MutableLiveData<>();
        isMusicPlaying = false;
        isRepeatOne = false;
        isRepeatAll = false;
        isRepeat = false;
        isShuffle = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSongFromExternal() {
        ContentResolver cr = mContext.getContentResolver();
        AssetManager assetManager = mContext.getAssets();
        Sound sound = new Sound();
        mSounds = new ArrayList<>();

        mUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        /*String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
*/
        Cursor cursor = cr.query(mUri, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            int musicTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int musicArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int musicAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            while (cursor.moveToNext()) {
                String currentTitle = cursor.getString(musicTitle);
                String currentArtist = cursor.getString(musicArtist);
                String currentAlbum = cursor.getString(musicAlbum);
                sound.setTitle(currentTitle);
                sound.setArtist(currentArtist);
                sound.setAlbum(currentAlbum);
//                sound = new Sound(currentTitle,currentArtist,currentAlbum);
                try {
                    mSounds.add(sound);
                    loadInMediaPlayer(assetManager, sound);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
//                loadSounds();
            }
            ;
            cursor.close();
        }
    }

    public static Sound getSongForCursor(Cursor cursor) {
        Sound sound = new Sound();
        if ((cursor != null) && (cursor.moveToFirst())) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String album = cursor.getString(3);
            int duration = cursor.getInt(4);
            int trackNumber = cursor.getInt(5);
            long artistId = cursor.getInt(6);
            long albumId = cursor.getLong(7);

            sound = new Sound(id, albumId, artistId, title, artist, album, duration, trackNumber);
        }

        if (cursor != null)
            cursor.close();
        return sound;
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    private boolean isShuffle;

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public boolean isRepeatOne() {
        return isRepeatOne;
    }


    public MutableLiveData<Boolean> getLiveDataIsPlaying() {
        return mLiveDataIsPlaying;
    }

    public boolean isRepeatAll() {
        return isRepeatAll;
    }

    public void setRepeatAll(boolean repeatAll) {
        isRepeatAll = repeatAll;
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public Sound getPlayingSound() {
        return mPlayingSound;
    }

    public MutableLiveData<Sound> getLiveDataPlayingSound() {
        return mLiveDataPlayingSound;
    }

    //it runs on constructor at the start of repository
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadSounds() {
        AssetManager assetManager = mContext.getAssets();
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        Bitmap mBitmap;
        try {
            String[] fileNames = assetManager.list(ASSET_FOLDER);
            for (int i = 0; i < fileNames.length; i++) {
                String assetPath = ASSET_FOLDER + File.separator + fileNames[i];
                AssetFileDescriptor afd = mContext.getAssets().openFd(assetPath);
                metaRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                byte[] data = metaRetriever.getEmbeddedPicture();
                if (data != null) {
                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                } else {
                    mBitmap = null;
                }

                afd.close();
                Sound sound = new Sound(assetPath);
                /*sound.setTitle(title);
                sound.setArtist(artist);
                sound.setAlbum(album);*/
                sound.setBitmap(mBitmap);
                loadInMediaPlayer(assetManager, sound);
                mSounds.add(sound);
            }

            metaRetriever.release();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadMusic(UUID uuid) {
        mFlagPlay = true;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        AssetManager assetManager = mContext.getAssets();
        try {
            for (Sound sound : mSounds) {
                if (sound.getSoundId().equals(uuid)) {
                    loadInMediaPlayer(assetManager, sound);
                    play(sound);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public Sound getSound(UUID uuid) {
        Sound result = null;
        for (Sound sound : mSounds) {
            if (sound.getSoundId().equals(uuid))
                result = sound;
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadInMediaPlayer(AssetManager assetManager, Sound sound) throws IOException {
       /* AssetFileDescriptor afd = assetManager.openFd(sound.getAssetPath());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mMediaPlayer.prepare();*/

        String path = getPathFromURI(mContext,mUri);
        Log.d("Main","Path :" + path);
        File file = new File(path);
        Log.d("Main" , "Music exists : " + file.exists() + ", can read : " + file.canRead());
        mMediaPlayer = MediaPlayer.create(mContext,Uri.parse(path));

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void play(Sound sound) {

        if (sound == null || sound.getSoundId() == null)
            return;

        mMediaPlayer.start();
        mLiveDataPlayingSound.postValue(sound);
        mLiveDataIsPlaying.postValue(true);
        mPlayingSound = sound;
        isMusicPlaying = true;
    }

    public int getSoundIndex(UUID uuid) {
        int index = -1;
        for (int i = 0; i < mSounds.size(); i++) {
            if (mSounds.get(i).getSoundId().equals(uuid))
                index = i;
        }
        return index;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void nextSound(Sound sound) {
        int index = getSoundIndex(sound.getSoundId());
        if (index == (mSounds.size() - 1)) {
            loadMusic(mSounds.get(0).getSoundId());
            mPlayingSound = mSounds.get(0);
        } else {
            loadMusic(mSounds.get((index + 1)).getSoundId());
            mPlayingSound = mSounds.get((index + 1));
        }
        isMusicPlaying = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void previousSound(Sound sound) {
        int index = getSoundIndex(sound.getSoundId());
        if (index == 0) {
            loadMusic(mSounds.get((mSounds.size() - 1)).getSoundId());
            mPlayingSound = mSounds.get((mSounds.size() - 1));
        } else {
            loadMusic(mSounds.get((index - 1)).getSoundId());
            mPlayingSound = mSounds.get((index - 1));
        }
        isMusicPlaying = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void repeatOne(Sound sound) {
        if (!isRepeatOne) {
            if (!mMediaPlayer.isPlaying())
                loadMusic(sound.getSoundId());
            mMediaPlayer.setLooping(true);
        } else {
            mMediaPlayer.setLooping(false);
        }
        isRepeatOne = !isRepeatOne;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void repeatAll(Sound sound) {
        int index = getSoundIndex(sound.getSoundId());
        if (index == mSounds.size() - 1)
            index = 0;
        while (index < mSounds.size() && isRepeatAll) {
            loadMusic(mSounds.get(index).getSoundId());
            if (index == mSounds.size() - 1)
                index = 0;
            else
                index += 1;
        }


    }

    public List<Integer> shuffle() {
        Random random = new Random();
        List<Integer> soundIndex = new ArrayList<>();
        soundIndex.add(random.nextInt(mSounds.size() - 0) + 0);
        // you have also handle min to max index
        while (soundIndex.size() != mSounds.size()) {
            int index = random.nextInt(mSounds.size() - 0) + 0;
            for (int i = 0; i < soundIndex.size(); i++) {
                if (index == soundIndex.get(i))
                    break;
            }
            soundIndex.add(index);
        }
        return soundIndex;
    }



    public void release() {
        mMediaPlayer.release();
    }

    public void pause() {
        mMediaPlayer.pause();
        isMusicPlaying = false;
    }

    public void playAgain() {
        if (mFlagPlay)
            mMediaPlayer.start();
        isMusicPlaying = true;
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
