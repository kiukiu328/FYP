package com.fyp.evhelper.stream;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.R;


public class AlertRecordVideo extends AppCompatActivity {
    VideoView videoView;
    ImageButton leave_btn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_record_video);
        videoView = findViewById(R.id.alert_video_record);
        leave_btn=findViewById(R.id.leave_btn);

        leave_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AlertRecordVideo.this,page2.class);
                finish();
            }
        });


        Log.w("VideoPath",getIntent().getExtras().getString("videoPatn").toString());
        Uri videoUri = Uri.parse(getIntent().getExtras().getString("videoPatn"));

        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });


        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

    }
}
