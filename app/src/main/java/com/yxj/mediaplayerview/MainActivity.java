package com.yxj.mediaplayerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yxj.audioplayerview.MediaPlayerView;

public class MainActivity extends AppCompatActivity {

    private MediaPlayerView mediaPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayerView = findViewById(R.id.media_player_view);
        mediaPlayerView.setDataUri("https://yuantu-hz-img.oss-cn-hangzhou.aliyuncs.com/d16c7785a3d03c1f30b4dd3d51e9377b.mp3");

    }

}
