package com.example.hu.mediaplayerapk.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class BGMPlayer implements MediaPlayer.OnPreparedListener, IMediaPlayer, MediaPlayer.OnCompletionListener {

    private static final int STATUS_IDLE = 0;
    private static final int STATUS_PREPARING = 1;
    private static final int STATUS_PREPARED = 2;
    private static final int STATUS_STARTED = 3;
    private static final int STATUS_PAUSED = 4;
    private static final int STATUS_STOPPED = 5;
    private int mStatus = STATUS_IDLE;

    private MediaPlayer mediaPlayer;
    private Context mContext;
    private List<String> fileList;
    private int musicIndex = 0;

    private static BGMPlayer mInstance;

    private BGMPlayer(List<String> fileList, Context mContext) {
        this.fileList = fileList;
        this.mContext = mContext;
        initMediaPlayer();
    }

    public static BGMPlayer getInstance(List<String> fileList, Context mContext) {
        if (mInstance == null) {
            mInstance = new BGMPlayer(fileList, mContext);
        } else {
            mInstance.fileList = fileList;
            mInstance.mContext = mContext;
        }
        return mInstance;
    }

    public void startPlay() {
        if (fileList.size() > 0) {
            play(fileList.get(0), false, false);
        }
    }

    private void initMediaPlayer() {
        mStatus = STATUS_IDLE;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                stop();
            mediaPlayer.reset();
        } else
            mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
    }

    private void nextMusic() {
        musicIndex++;
        if (musicIndex >= fileList.size()) {
            musicIndex = 0;
        }
        if (fileList.size() > 0) {
            play(fileList.get(musicIndex), false, false);
        }
    }

    @Override
    public void play(String url, boolean isFD, boolean isLoop) {
        Log.e("aaa", "---------------playBGM");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                stop();
            mediaPlayer.reset();
            try {
                if (isFD) {
                    AssetFileDescriptor fd = mContext.getAssets().openFd(url);
                    mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                } else {
                    mediaPlayer.setDataSource(url);
                }
                mediaPlayer.prepare();
                mStatus = STATUS_PREPARING;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("aaaa", "----------setURLFailed");
            }
        } else {
            initMediaPlayer();
            mediaPlayer.reset();
            try {
                if (isFD) {
                    AssetFileDescriptor fd = mContext.getAssets().openFd(url);
                    mediaPlayer.setDataSource(fd.getFileDescriptor());
                } else {
                    mediaPlayer.setDataSource(url);
                }
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mStatus = STATUS_PREPARING;
        }
        if (isLoop) {
            mediaPlayer.setLooping(true);
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer == null) return;
        mediaPlayer.stop();
        mStatus = STATUS_STOPPED;
    }

    @Override
    public void close() {
        stop();
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(null);
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

    @Override
    public void pause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mStatus = STATUS_PAUSED;
        }
    }

    @Override
    public void unPause() {
        if (mediaPlayer == null) return;
        if (mStatus == STATUS_PREPARED || mStatus == STATUS_PAUSED) {
            mediaPlayer.start();
            mStatus = STATUS_STARTED;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaPlayer == null) return;
        mStatus = STATUS_PREPARED;
        mediaPlayer.start();
        mStatus = STATUS_STARTED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextMusic();
    }
}
