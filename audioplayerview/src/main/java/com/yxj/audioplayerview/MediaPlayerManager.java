package com.yxj.audioplayerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.yxj.audioplayerview.listener.BufferingUpdateListener;
import com.yxj.audioplayerview.listener.GetDurationListener;
import com.yxj.audioplayerview.listener.MediaPlayerStatusListener;
import com.yxj.audioplayerview.listener.TimeProgressListener;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author:  Yxj
 * Time:    2019/4/24 下午4:13
 * -----------------------------------------
 * Description:
 */
public class MediaPlayerManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mPlayer;
    private boolean hasPrepared;
    private Handler handler;
    private TimeProgressListener timeProgressListener;
    private GetDurationListener getDurationListener;
    private MediaPlayerStatusListener mediaPlayerStatusListener;
    private BufferingUpdateListener bufferingUpdateListener;
    private Timer timer;

    private void initIfNecessary() {
        if (null == mPlayer) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
        }
    }

    public void play(Context context, Uri dataSource) {
        hasPrepared = false; // 开始播放前讲Flag置为不可操作
        initIfNecessary(); // 如果是第一次播放/player已经释放了，就会重新创建、初始化
        handler = new Handler();

        try {
            mPlayer.reset();
            mPlayer.setDataSource(context, dataSource); // 设置曲目资源
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        // release()会释放player、将player置空，所以这里需要判断一下
        if (null != mPlayer && hasPrepared) {
            mPlayer.start();
        }
    }

    public void pause() {
        if (null != mPlayer && hasPrepared) {
            mPlayer.pause();
        }
    }

    /**
     * 设置百分比
     * @param position
     */
    public void seekTo(int position) {
        if (null != mPlayer && hasPrepared) {
            mPlayer.seekTo((int) ((position*1.0f/100) *getDuration()*1000));
        }
    }

    public void release() {
        if(mediaPlayerStatusListener!=null)
            mediaPlayerStatusListener.onReleaseListener();
        hasPrepared = false;
        if(mPlayer!=null){
//            mPlayer.stop();  这里不能调用stop，否则在perpare过程中，调用stop会报错
            mPlayer.release();
            mPlayer = null;
        }
        if(timer!=null)
            timer.cancel();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("yxj","onPrepared---->");
        hasPrepared = true; // 准备完成后回调到这里
        start();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(getDurationListener != null)
                    getDurationListener.onDurationListener(getDuration());

                timer = new Timer();
                timer.schedule(new TimeAndProgressTask(),0,300);
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        hasPrepared = false;
        if(timer!=null)
            timer.cancel();
        // 通知调用处，调用play()方法进行下一个曲目的播放
        if(mediaPlayerStatusListener!=null)
            mediaPlayerStatusListener.onCompleteListener();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("error","error:what"+what+" extra:"+extra);
        hasPrepared = false;
        if(timer!=null)
            timer.cancel();
        return false;
    }

    // 缓存进度
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if(bufferingUpdateListener!=null)
            bufferingUpdateListener.onBufferingUpdate(percent);
    }

    private int getDuration(){
        return mPlayer.getDuration()/1000;
    }

    private int getCurrentPosition(){
        return mPlayer.getCurrentPosition()/1000;
    }

    class TimeAndProgressTask extends TimerTask {

        @Override
        public void run(){
            if(hasPrepared){
                final int seconds = getCurrentPosition();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timeProgressListener.onTimeProgressListener(seconds);
                    }
                });
            }
        }
    }

    public boolean isComplete(){
        return !hasPrepared;
    }

    /**
     * 播放状态监听
     * 监听播放完成状态、release状态
     * @param mediaPlayerStatusListener
     */
    public void setMediaPlayerStatusListener(MediaPlayerStatusListener mediaPlayerStatusListener) {
        this.mediaPlayerStatusListener = mediaPlayerStatusListener;
    }

    public void setTimeProgressListener(TimeProgressListener timeProgressListener){
        this.timeProgressListener = timeProgressListener;
    }

    public void setGetDurationListener(GetDurationListener getDurationListener) {
        this.getDurationListener = getDurationListener;
    }

    /**
     * 设置缓冲条监听
     * @param bufferingUpdateListener
     */
    public void setBufferingUpdateListener(BufferingUpdateListener bufferingUpdateListener) {
        this.bufferingUpdateListener = bufferingUpdateListener;
    }
}
