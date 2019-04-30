package com.yxj.mediaplayerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yxj.audioplayerview.MediaPlayerView;
import com.yxj.mediaplayerview.adapter.VedioAdapter;
import com.yxj.mediaplayerview.bean.AudioBean;
import com.yxj.mediaplayerview.bean.TextBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        List<MultiItemEntity> vedioList = new ArrayList<>();
        vedioList.add(new TextBean("1"));
        vedioList.add(new TextBean("2"));
        vedioList.add(new AudioBean("http://matt.chinauui.com/day_150404/B9_4C_rBBGdVQnrmWAASavAARRUMQFqU0175.mp3"));
        vedioList.add(new TextBean("3"));
        vedioList.add(new TextBean("4"));
        vedioList.add(new AudioBean("http://matt.chinauui.com/day_150404/B9_4C_rBBGdVQnrmWAASavAARRUMQFqU0175.mp3"));
        vedioList.add(new TextBean("5"));
        vedioList.add(new TextBean("6"));
        vedioList.add(new TextBean("7"));
        vedioList.add(new TextBean("8"));
        vedioList.add(new TextBean("9"));
        vedioList.add(new TextBean("10"));
        vedioList.add(new TextBean("11"));
        vedioList.add(new TextBean("12"));

        VedioAdapter adapter = new VedioAdapter(vedioList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                int type = linearLayoutManager.getItemViewType(view);

                int position = linearLayoutManager.getPosition(view);

                if(type == VedioAdapter.ITEM_TYPE_AUDIO){
                    Log.e("yxj","position:"+position);
                }

            }
        });

    }

}
