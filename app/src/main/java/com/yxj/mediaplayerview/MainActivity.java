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
        mediaPlayerView.setDataUri("http://zhangmenshiting.qianqian.com/data2/music/39c28e2e7761d344a256e4e215b61c62/599521867/599521867.m4a?xcode=6ad4574be976cb39c9bf3d409e407d1c");

    }

}
