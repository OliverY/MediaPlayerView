package com.yxj.audioplayerview;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yxj.audioplayerview.listener.BufferingUpdateListener;
import com.yxj.audioplayerview.listener.GetDurationListener;
import com.yxj.audioplayerview.listener.MediaPlayerStatusListener;
import com.yxj.audioplayerview.listener.TimeProgressListener;

/**
 * Author:  Yxj
 * Time:    2019/4/25 上午10:57
 * -----------------------------------------
 * Description: 这个view可以修改或者自定义一个自己的view
 */
public class MediaPlayerView extends FrameLayout implements TimeProgressListener,GetDurationListener,View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayerStatusListener, BufferingUpdateListener {

    private ImageView btnPlay;
    private TextView tvCurrent;
    private TextView tvDuration;
    private SeekBar progressBar;
    private MediaPlayerManager mediaPlayerManager;
    private Uri dataUri;
    private int duration;
    private boolean isUserDragSeekBar;

    // 代表现在是否正在播放，播放完毕，release
    private boolean isPaused = true;

    public MediaPlayerView(Context context) {
        this(context,null);
    }

    public MediaPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MediaPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_media_player_standed,this,true);
        btnPlay = rootView.findViewById(R.id.btn_play);
        tvCurrent = rootView.findViewById(R.id.tv_current);
        tvDuration = rootView.findViewById(R.id.tv_duration);
        progressBar = rootView.findViewById(R.id.seek_bar);

        mediaPlayerManager = new MediaPlayerManager();

        mediaPlayerManager.setTimeProgressListener(this);
        mediaPlayerManager.setGetDurationListener(this);
        mediaPlayerManager.setMediaPlayerStatusListener(this);
        mediaPlayerManager.setBufferingUpdateListener(this);

        btnPlay.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        setProgressBarEnable(false);

        setBtnPlayDisplay(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mediaPlayerManager.release();
    }

    public void setDataUri(String url){
        if(TextUtils.isEmpty(url)){
            throw new RuntimeException("Data url can't be null");
        }
        this.dataUri = Uri.parse(url);
    }

    @Override
    public void onDurationListener(int seconds) {
        duration = seconds;
        tvDuration.setText(String.format("%02d:%02d",seconds/60,seconds%60));
    }

    @Override
    public void onTimeProgressListener(int seconds) {
        tvCurrent.setText(String.format("%02d:%02d",seconds/60,seconds%60));
        if(!isUserDragSeekBar){// 没有在拖拽是才可以设置
            progressBar.setProgress((int) (seconds*1.0f / duration * 100));
        }
    }

    @Override
    public void onClick(View v) {
        /*
        1.未播放
            调用play、展示暂停按钮
        2.进行中
            调用paused、展示播放按钮
         */
        if(isPaused){
            isPaused = false;
            setProgressBarEnable(true);
            setBtnPlayDisplay(isPaused);
            if(mediaPlayerManager.isComplete()){
                mediaPlayerManager.play(getContext(),dataUri);
            }else{
                mediaPlayerManager.start();
            }
        }else{
            setProgressBarEnable(false);
            isPaused = true;
            setBtnPlayDisplay(isPaused);
            mediaPlayerManager.pause();
        }

    }

    //SeekBar.OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserDragSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isUserDragSeekBar = false;
        mediaPlayerManager.seekTo(seekBar.getProgress());
    }


    // MediaPlayerStatusListener
    @Override
    public void onCompleteListener() {
        setBtnPlayDisplay(true);
        isPaused = true;
        tvCurrent.setText("00:00");
        setProgressBarEnable(false);
        progressBar.setProgress(0);
    }

    @Override
    public void onReleaseListener() {
        setBtnPlayDisplay(true);
        isPaused = true;
        tvCurrent.setText("00:00");
        setProgressBarEnable(false);
        progressBar.setSecondaryProgress(0);
        progressBar.setProgress(0);
    }

    // BufferingUpdateListener
    @Override
    public void onBufferingUpdate(int percent) {
        progressBar.setSecondaryProgress((int) ((percent *1.0f/100) * duration));
    }

    private void setBtnPlayDisplay(boolean isPaused){
        btnPlay.setImageResource(isPaused?R.mipmap.icon_play_normal:R.mipmap.icon_pause_normal);
    }

    public void release(){
        mediaPlayerManager.release();
    }

    private void setProgressBarEnable(boolean enable){
        progressBar.setEnabled(enable);
    }

}
