package com.yxj.audioplayerview.listener;

/**
 * Author:  Yxj
 * Time:    2019/5/7 上午11:19
 * -----------------------------------------
 * Description: 控件的监听
 */
public interface Listener {

    /**
     * 缓冲数据监听
     * @param percent
     */
    void onBufferingUpdate(int percent);

    /**
     * 获取到总时长监听，只有在prepare（一个耗时过程）完成时才能获取到总时长
     * @param seconds
     */
    void onDurationListener(int seconds);

    /**
     * 音频播放结束监听
     */
    void onCompleteListener();

    /**
     * MediaPlayer.release()添加的监听
     */
    void onReleaseListener();

    /**
     * 时间过程监听
     * @param seconds
     */
    void onTimeProgressListener(int seconds);

    /**
     * 准备完成，立即可以播放了
     */
    void onPreparedListener();
}
