package com.yxj.audioplayerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
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
    private Uri lastSource;
    private Uri dataSource;

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

    public void play(Context context, Uri dataSource) {
        // 停止上一个
        if(lastSource != null){
            // 上一个和自己
            if(lastSource != dataSource){
                MediaEventCenter.getInstance().sendReleaseEvent(getHash(lastSource));
            }
        }
        this.dataSource = dataSource;
        lastSource = dataSource;

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
    public void releaseLastPlayer(Uri currentDataSource) {
        // 如果当前播放不是上一个，则release掉
        if(getHash(currentDataSource) != getHash(lastSource)){
            MediaEventCenter.getInstance().sendReleaseEvent(getHash(lastSource));
        }
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

        if(urlCurrentPositionMap.get(getHash(dataSource))!=null){
            audio = urlCurrentPositionMap.get(getHash(dataSource));
            if(audio.duration - audio.currentPosition<=1){
                audio.currentPosition = 0;
            }
            innerSeekTo(audio.currentPosition);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                MediaEventCenter.getInstance().sendDurationUpdateEvent(getHash(dataSource),getDuration());
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
        MediaEventCenter.getInstance().sendonCompleteEvent(getHash(dataSource));
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
        MediaEventCenter.getInstance().sendBufferingUpdateEvent(getHash(dataSource),percent);
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
        int current = mPlayer.getCurrentPosition()/1000;
        return current;
    }

    class TimeAndProgressTask extends TimerTask {

        @Override
        public void run(){
            if(hasPrepared){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int seconds = getCurrentPosition();
                        MediaEventCenter.getInstance().sendTimeProgressEvent(getHash(dataSource),seconds);
                    }
                });
            }
        }
    }

    public boolean isComplete(Uri uri){
        if(getHash(uri) != getHash(lastSource)){
            return true;
        }
        return !hasPrepared;
    }

    /**
     * 设置缓冲条监听
     * @param listener
     */
    public void setListener(Uri uri,Listener listener) {
        MediaEventCenter.getInstance().addListener(uri.hashCode(),listener);
    }

    public void removeListener(Uri uri){
        MediaEventCenter.getInstance().removeListener(uri.hashCode());
    }

    /**
     * 存储当前的播放位置
     */
    private void storeCurrentPosition(){
        if(audio == null){
            audio = new Audio(0,getDuration());
        }
        audio.currentPosition = getCurrentPosition();
        urlCurrentPositionMap.put(getHash(dataSource),audio);
    }

    private void removeAudioPosition(){
        urlCurrentPositionMap.remove(getHash(dataSource));
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

        public Audio(int currentPosition, int duration) {
            this.currentPosition = currentPosition;
            this.duration = duration;
        }
    }

    private int getHash(Object object){
        if(object == null){
            return -1;
        }
        return object.hashCode();
    }

    public Uri getDataSource() {
        return dataSource;
    }

    /**
     * 销毁
     */
    public void destroy(){
        releasePlayer();
        MediaEventCenter.getInstance().destroy();
        mInstance = null;
    }
}
