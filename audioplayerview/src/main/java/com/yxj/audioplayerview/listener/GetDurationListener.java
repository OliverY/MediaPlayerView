package com.yxj.audioplayerview.listener;

/**
 * Author:  Yxj
 * Time:    2019/4/25 上午9:26
 * -----------------------------------------
 * Description:
 */
public interface GetDurationListener {

    /**
     * 获取到总时长监听，只有在prepare（一个耗时过程）完成时才能获取到总时长
     * @param seconds
     */
    void onDurationListener(int seconds);
}
