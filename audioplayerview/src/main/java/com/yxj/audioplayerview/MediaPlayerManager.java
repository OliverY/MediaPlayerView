package com.yxj.audioplayerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.yxj.audioplayerview.listener.Listener;

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

    private static SparseArray<Audio> urlCurrentPositionMap = new SparseArray<>();

    private MediaPlayer mPlayer;
    private boolean hasPrepared;
    private Handler handler;
    private Timer timer;
    private Audio audio;

    private int lastHash;
//    private Uri dataSource;

    private static MediaPlayerManager mInstance;
    private MediaPlayerManager(){
    }

    public static MediaPlayerManager getInstance(){
        if(mInstance == null){
            synchronized (MediaPlayerManager.class){
                mInstance = new MediaPlayerManager();
            }
        }
        return mInstance;
    }

    private void initIfNecessary() {
        if (null == mPlayer) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
        }
    }

    public void play(Context context, Uri dataSource,int currentHash) {
        // 停止上一个
        if(lastHash != 0){
            // 上一个和自己
            if(lastHash != currentHash){
                MediaListenerCenter.getInstance().sendReleaseEvent(lastHash);
            }
        }
        lastHash = currentHash;

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
            storeCurrentPosition();
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

    private void innerSeekTo(int seconds){
        if (null != mPlayer && hasPrepared) {
            mPlayer.seekTo(seconds*1000);
        }
    }

    /**
     * 释放 上一个播放的
     * view 开始播放时
     *
     */
    public void releaseLastPlayer(int currentHash) {
        // 如果当前播放不是上一个，则release掉
        if(currentHash != lastHash){
            MediaListenerCenter.getInstance().sendReleaseEvent(lastHash);
        }
        releasePlayer();
    }

    public void releaseLastPlayer(){
        MediaListenerCenter.getInstance().sendReleaseEvent(lastHash);
        releasePlayer();
    }

    /**
     * 仅 释放player，不用关心是释放哪个
     */
    public void releasePlayer(){
        hasPrepared = false;
        if(mPlayer!=null){
            storeCurrentPosition();
            mPlayer.release();
            mPlayer = null;
        }
        if(timer!=null)
            timer.cancel();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        hasPrepared = true; // 准备完成后回调到这里

        MediaListenerCenter.getInstance().sendPreparedEvent(lastHash);

        if(urlCurrentPositionMap.get(lastHash)!=null){
            audio = urlCurrentPositionMap.get(lastHash);
            if(audio.duration - audio.currentPosition<=1){
                audio.currentPosition = 0;
            }
            innerSeekTo(audio.currentPosition);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                MediaListenerCenter.getInstance().sendDurationUpdateEvent(lastHash,getDuration());
                start();
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        hasPrepared = false;
        removeAudioPosition();

        stopScheduler();
        // 修改UI
        MediaListenerCenter.getInstance().sendonCompleteEvent(lastHash);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        hasPrepared = false;
        stopScheduler();
        return false;
    }

    // 缓存进度
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        MediaListenerCenter.getInstance().sendBufferingUpdateEvent(lastHash,percent);
    }

    /**
     * 秒
     * @return
     */
    private int getDuration(){
        int duration = mPlayer.getDuration()/1000;
        return duration;
    }

    private int getCurrentPosition(){
        if(mPlayer!=null){
            return mPlayer.getCurrentPosition()/1000;
        }
        return 0;
    }

    class TimeAndProgressTask extends TimerTask {

        @Override
        public void run(){
            if(hasPrepared){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(hasPrepared){
                            int seconds = getCurrentPosition();
                            MediaListenerCenter.getInstance().sendTimeProgressEvent(lastHash,seconds);
                        }
                    }
                });
            }
        }
    }

    public boolean isComplete(int currentHash){
        if(currentHash != lastHash){
            return true;
        }
        return !hasPrepared;
    }

    /**
     * 设置缓冲条监听
     * @param listener
     */
    public void setListener(int hashKey,Listener listener) {
        MediaListenerCenter.getInstance().addListener(hashKey,listener);
    }

    public void removeListener(int hashKey){
        MediaListenerCenter.getInstance().removeListener(hashKey);
    }

    /**
     * 存储当前的播放位置
     */
    private void storeCurrentPosition(){
        audio = new Audio();
        audio.duration = getDuration();
        audio.currentPosition = getCurrentPosition();
        urlCurrentPositionMap.put(lastHash,audio);
    }

    private void removeAudioPosition(){
        urlCurrentPositionMap.remove(lastHash);
    }

    private void startScheduler(){
        timer = new Timer();
        timer.schedule(new TimeAndProgressTask(),0,500);
    }

    private void stopScheduler(){
        if(timer!=null){
            timer.cancel();
        }
    }

    class Audio{
        int currentPosition;
        int duration;

        public Audio() {
        }

        @Override
        public String toString() {
            return "Audio{" +
                    "currentPosition=" + currentPosition +
                    ", duration=" + duration +
                    '}';
        }
    }

    private int getHash(Object object){
        if(object == null){
            return -1;
        }
        return object.hashCode();
    }

    public int getHashKey() {
        return lastHash;
    }

    /**
     * 销毁
     */
    public void destroy(){
        releasePlayer();
        MediaListenerCenter.getInstance().destroy();
        mInstance = null;
    }

}
