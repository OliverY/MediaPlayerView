package com.yxj.mediaplayerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yxj.mediaplayerview.adapter.VedioAdapter;
import com.yxj.mediaplayerview.bean.AudioBean;
import com.yxj.mediaplayerview.bean.TextBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private VedioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EventBus.getDefault().register(this);

        makeList();
    }

    private void makeList() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        List<MultiItemEntity> vedioList = new ArrayList<>();
        vedioList.add(new TextBean("1"));
        vedioList.add(new TextBean("2"));
        vedioList.add(new AudioBean("http://matt.chinauui.com/day_150404/B9_4C_rBBGdVQnrmWAASavAARRUMQFqU0175.mp3"));
        vedioList.add(new TextBean("3"));
        vedioList.add(new TextBean("4"));
        vedioList.add(new AudioBean("http://att.chinauui.com/day_120107/20120107_83ecfde73b4b0222b46a3yr1103W13y0.mp3"));
        vedioList.add(new TextBean("5"));
        vedioList.add(new TextBean("6"));
        vedioList.add(new TextBean("7"));
        vedioList.add(new AudioBean("http://cdnringbd.shoujiduoduo.com/ringres/userv1/a48/534/72106534.aac"));
        vedioList.add(new AudioBean("http://isure.stream.qqmusic.qq.com/R400002lny3r1zoNO8.m4a?guid=2474607544&vkey=F7ED1808F7AF28869CC428DB90429D36FD2E1D2DB91985D86BF1E878560C18AA13666804F685D0824CAAF65984C815D9543501430D30E621&uin=0&fromtag=66"));
        vedioList.add(new TextBean("8"));
        vedioList.add(new TextBean("9"));
        vedioList.add(new TextBean("10"));
        vedioList.add(new TextBean("11"));
        vedioList.add(new TextBean("12"));

        adapter = new VedioAdapter(vedioList);
        recyclerView.setAdapter(adapter);
    }

//    int lastPosition;

//    @Subscribe
//    public void notifyLastItem(Events events){
//        Log.e("yxj","last:"+lastPosition+" current:"+events.position);
//        if(lastPosition != events.position){
//            adapter.notifyItemChanged(lastPosition);
//            lastPosition = events.position;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(adapter != null){
//            adapter.notifyItemChanged(lastPosition);
//        }
//    }
}
