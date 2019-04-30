package com.yxj.mediaplayerview.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yxj.mediaplayerview.adapter.VedioAdapter;


/**
 * Author:  Yxj
 * Time:    2019/4/24 上午9:49
 * -----------------------------------------
 * Description:
 */
public class AudioBean implements MultiItemEntity {

    String url;

    public AudioBean(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getItemType() {
        return VedioAdapter.ITEM_TYPE_AUDIO;
    }
}
