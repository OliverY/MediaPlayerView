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

import com.yxj.audioplayerview.listener.Listener;
import com.yxj.audioplayerview.util.AnimationUtils;
import com.yxj.audioplayerview.util.KeyUtils;


/**
 * Author:  Yxj
 * Time:    2019/4/25 上午10:57
 * -----------------------------------------
 * Description: 这个view可以修改或者自定义一个自己的view
 */
public class MediaPlayerView extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,Listener {

    private ImageView btnPlay;
    private ImageView imgLoading;
    private TextView tvCurrent;
    private TextView tvDuration;
    private SeekBar progressBar;
    private MediaPlayerManager mediaPlayerManager;
    private Uri dataUri;
    private int duration;
    private boolean isUserDragSeekBar;
    private AnimationUtils animationUtils;

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
        imgLoading = rootView.findViewById(R.id.image_loading);

        mediaPlayerManager = MediaPlayerManager.getInstance();

        btnPlay.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        setProgressBarEnable(false);

        setBtnPlayDisplay(true);
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
    public void onPreparedListener() {
        setBtnPlayDisplay(false);
        setProgressBarEnable(true);
        isPreparing = false;
        dismissLoading();
    }

    boolean isPreparing;

    @Override
    public void onClick(View v) {

        if(isPreparing){
            return ;
        }
        /*
        1.未播放
            调用play、展示暂停按钮
        2.进行中
            调用paused、展示播放按钮
        3.等待中
            不处理
         */
        if(isPaused){
            isPaused = false;
            if(mediaPlayerManager.isComplete(getKey())){
                isPreparing = true;
                showLoading();
                mediaPlayerManager.releaseLastPlayer(getKey());
                mediaPlayerManager.play(getContext(),dataUri,getKey());
            }else{
                mediaPlayerManager.start();
                setBtnPlayDisplay(false);
                setProgressBarEnable(true);
            }
        }else{
            setProgressBarEnable(false);
            isPaused = true;
            setBtnPlayDisplay(true);
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
        btnPlay.setImageResource(isPaused?R.mipmap.icon_audio_play_pause:R.mipmap.icon_audio_play_start);
    }

    public void initUI(){
        setBtnPlayDisplay(true);
        isPaused = true;
        tvCurrent.setText("00:00");
        setProgressBarEnable(false);
        progressBar.setSecondaryProgress(0);
        progressBar.setProgress(0);
        btnPlay.setVisibility(View.VISIBLE);
        imgLoading.setVisibility(View.GONE);
    }

    private void setProgressBarEnable(boolean enable){
        progressBar.setEnabled(enable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mediaPlayerManager.setListener(KeyUtils.getHash(dataUri,this),this);
        initUI();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mediaPlayerManager.removeListener(KeyUtils.getHash(dataUri,this));
        // 当前控件正在播放，需要release
        if(mediaPlayerManager.getDataSource() == dataUri){
            mediaPlayerManager.releasePlayer();
        }
        if(animationUtils!=null){
            animationUtils.cancel();
        }
    }

    private int getKey(){
        return KeyUtils.getHash(dataUri,this);
    }

    private void showLoading() {
        btnPlay.setVisibility(View.GONE);
        imgLoading.setVisibility(VISIBLE);
        animationUtils = new AnimationUtils(imgLoading);
        animationUtils.active(btnPlay.getWidth()/2,btnPlay.getHeight()/2);
    }

    private void dismissLoading(){
        btnPlay.setVisibility(View.VISIBLE);
        imgLoading.setVisibility(View.GONE);
        animationUtils.cancel();
    }

}
