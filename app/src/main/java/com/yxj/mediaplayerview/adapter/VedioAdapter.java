package com.yxj.mediaplayerview.adapter;

import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yxj.audioplayerview.MediaPlayerView;
import com.yxj.mediaplayerview.R;
import com.yxj.mediaplayerview.VedioListActivity;
import com.yxj.mediaplayerview.bean.AudioBean;
import com.yxj.mediaplayerview.bean.TextBean;

import java.util.List;

/**
 * Author:  Yxj
 * Time:    2019/4/23 下午3:30
 * -----------------------------------------
 * Description:
 */
public class VedioAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {

    public static int ITEM_TYPE_TEXT = 1;
    public static int ITEM_TYPE_AUDIO = 2;

    public VedioAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(ITEM_TYPE_TEXT, R.layout.item_view_text);
        addItemType(ITEM_TYPE_AUDIO,R.layout.item_view_audio);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        if(item instanceof TextBean){
            renderText(helper, (TextBean) item);
        }else if(item instanceof AudioBean){
            renderAudio(helper, (AudioBean) item);
        }

    }

    private void renderText(BaseViewHolder helper, TextBean item) {
        helper.setText(R.id.text,item.getContent());
    }

    private void renderAudio(BaseViewHolder helper, AudioBean item) {
        final MediaPlayerView mediaPlayerView = helper.getView(R.id.audio_view);
        mediaPlayerView.setDataUri(item.getUrl());

        helper.getView(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, VedioListActivity.class));
            }
        });
    }

}
