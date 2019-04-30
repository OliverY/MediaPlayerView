package com.yxj.audioplayerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.ArrayMap;
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

    public static ArrayMap<Uri,Audio> urlCurrentPositionMap = new ArrayMap<>();

    private MediaPlayer mPlayer;
    private boolean hasPrepared;
    private Handler handler;
    private TimeProgressListener timeProgressListener;
    private GetDurationListener getDurationListener;
    private MediaPlayerStatusListener mediaPlayerStatusListener;
    private BufferingUpdateListener bufferingUpdateListener;
    private Timer timer;
    private Audio audio;
    private Uri dataSource;

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
        this.dataSource = dataSource;

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
            // 开启定时器
            startScheduler();
            mPlayer.start();
        }
    }

    public void pause() {
        if (null != mPlayer && hasPrepared) {
            stopScheduler();
            mPlayer.pause();
            storeCurrentPosition(audio);
        }
    }

    /**
     * 设置百分比
     * @param percent
     */
    public void seekTo(int percent) {
        if (null != mPlayer && hasPrepared) {
            mPlayer.seekTo((int) ((percent*1.0f/100) *getDuration()*1000));
        }
    }

    public void innerSeekTo(int seconds){
        if (null != mPlayer && hasPrepared) {
            mPlayer.seekTo(seconds*1000);
        }
    }

    public void release() {
        if(mediaPlayerStatusListener!=null)
            mediaPlayerStatusListener.onReleaseListener();
        hasPrepared = false;
        if(mPlayer!=null){
            storeCurrentPosition(audio);
//            mPlayer.stop();  这里不能调用stop，否则在perpare过程中，调用stop会报错
            mPlayer.release();
            mPlayer = null;
        }
        if(timer!=null)
            timer.cancel();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        hasPrepared = true; // 准备完成后回调到这里

        if(urlCurrentPositionMap.get(dataSource)!=null){
            audio = urlCurrentPositionMap.get(dataSource);
        }else{
            audio = new Audio(0,getDuration());
            storeCurrentPosition(audio);
        }

        innerSeekTo(audio.currentPosition);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(getDurationListener != null) {
                    getDurationListener.onDurationListener(getDuration());
                }
                start();
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        hasPrepared = false;
        audio.currentPosition = 0;
        storeCurrentPosition(audio);

        stopScheduler();
        // 通知调用处，调用play()方法进行下一个曲目的播放
        if(mediaPlayerStatusListener!=null)
            mediaPlayerStatusListener.onCompleteListener();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("error","error:what"+what+" extra:"+extra);
        hasPrepared = false;
        stopScheduler();
        return false;
    }

    // 缓存进度
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if(bufferingUpdateListener!=null)
            bufferingUpdateListener.onBufferingUpdate(percent);
    }

    /**
     * 秒
     * @return
     */
    private int getDuration(){
        return mPlayer.getDuration()/1000;
    }

    private int getCurrentPosition(){
        return mPlayer.getCurrentPosition()/1000+1;
    }

    class TimeAndProgressTask extends TimerTask {

        @Override
        public void run(){
            if(hasPrepared){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int seconds = getCurrentPosition();
                        Log.e("yxj","getCurrentPosition:"+seconds);
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

    /**
     * 进度监听
     * @param timeProgressListener
     */
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

    private void storeCurrentPosition(Audio audio){
        audio.currentPosition = getCurrentPosition();
        if(audio.currentPosition == audio.duration){
            audio.currentPosition = 0;
        }
        urlCurrentPositionMap.put(dataSource,audio);
    }

    private void startScheduler(){
        timer = new Timer();
        timer.schedule(new TimeAndProgressTask(),0,300);
    }

    private void stopScheduler(){
        if(timer!=null){
            timer.cancel();
        }
    }

    class Audio{
        int currentPosition;
        int duration;

        public Audio(int currentPosition, int duration) {
            this.currentPosition = currentPosition;
            this.duration = duration;
        }
    }
}
