package com.yxj.audioplayerview.util;

import android.net.Uri;
import android.view.View;

/**
 * Author:  Yxj
 * Time:    2019/5/8 上午10:27
 * -----------------------------------------
 * Description: 对 view + 音频的结果做定位播放缓存
 */
public class KeyUtils {

    public static int getHash(Uri uri, View view){
        return uri.hashCode() + view.hashCode();
    }
}
