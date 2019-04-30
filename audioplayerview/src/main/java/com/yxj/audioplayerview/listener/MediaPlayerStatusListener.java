package com.yxj.audioplayerview.listener;

/**
 * Author:  Yxj
 * Time:    2019/4/25 上午11:39
 * -----------------------------------------
 * Description:
 */
public interface MediaPlayerStatusListener {

    /**
     * 音频播放结束监听
     */
    void onCompleteListener();

    /**
     * MediaPlayer.release()添加的监听
     */
    void onReleaseListener();
}
