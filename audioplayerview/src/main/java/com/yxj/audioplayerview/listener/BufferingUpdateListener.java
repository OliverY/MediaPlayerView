package com.yxj.audioplayerview.listener;

/**
 * Author:  Yxj
 * Time:    2019/4/25 下午3:08
 * -----------------------------------------
 * Description:
 */
public interface BufferingUpdateListener {

    /**
     * 缓冲数据监听
     * @param percent
     */
    void onBufferingUpdate(int percent);
}
