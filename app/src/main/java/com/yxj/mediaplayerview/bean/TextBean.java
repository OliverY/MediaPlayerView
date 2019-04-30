package com.yxj.mediaplayerview.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yxj.mediaplayerview.adapter.VedioAdapter;


/**
 * Author:  Yxj
 * Time:    2019/4/24 上午9:49
 * -----------------------------------------
 * Description:
 */
public class TextBean implements MultiItemEntity {

    String content;

    public TextBean(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return VedioAdapter.ITEM_TYPE_TEXT;
    }
}
