package com.yxj.mediaplayerview;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yxj.audioplayerview.MediaPlayerManager;
import com.yxj.audioplayerview.MediaPlayerView;

/**
 * Author:  Yxj
 * Time:    2019/5/8 下午3:42
 * -----------------------------------------
 * Description:
 */
public class SingleVedioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_audio);

        MediaPlayerView mediaPlayerView = findViewById(R.id.audio_view);
        mediaPlayerView.setDataUri("http://att.chinauui.com/day_120107/20120107_83ecfde73b4b0222b46a3yr1103W13y0.mp3");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().releaseLastPlayer();
    }

}
