package com.example.hu.mediaplayerapk.media;

/**
 * Created by Administrator on 2016/11/5.
 */
public interface IMediaPlayer {


    void play(String url, boolean isFD, boolean isLoop);

    void stop();

    void close();

    void pause();

    void unPause();
}
