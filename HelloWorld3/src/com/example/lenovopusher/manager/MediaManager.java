package com.example.lenovopusher.manager;

import android.media.MediaPlayer;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class MediaManager {
    private static MediaPlayer mMediaPlayer;

    private static boolean isPause;

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener){
        if(mMediaPlayer==null){
            mMediaPlayer=new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pause(){
        //将音频进行停止
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPause=true;
        }
    }

    public static void resume(){
        if(mMediaPlayer!=null&&isPause){
            mMediaPlayer.start();
            isPause=false;
        }
    }

    public static void release(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }
}
