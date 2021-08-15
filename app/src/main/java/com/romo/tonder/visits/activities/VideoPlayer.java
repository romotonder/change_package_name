package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.romo.tonder.visits.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoPlayer extends AppCompatActivity {
    String vUrl="";
    private VideoView videoPlayer;
    YouTubePlayerView youTubePlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        if (getIntent().hasExtra("url")){
            vUrl=getIntent().getStringExtra("url");
            if(vUrl.contains("www.youtube.com")){
                String videoID=getVideoId(vUrl);
                youTubePlayerView = findViewById(R.id.youtube_player_view);
                getLifecycle().addObserver(youTubePlayerView);
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        String videoId = videoID;
                        youTubePlayer.loadVideo(videoId, 0);
                    }
                });
            }else {
                //getVideoDetails();
            }
        }
    }

    private final static String expression = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    public static String getVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(videoUrl);
        try {
            if (matcher.find())
                return matcher.group();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private void getVideoDetails() {
        //Set MediaController  to enable play, pause, forward, etc options.
        MediaController mediaController= new MediaController(VideoPlayer.this);
        mediaController.setAnchorView(videoPlayer);
        //Location of Media File
        Uri uri = Uri.parse(vUrl);
        //Starting VideView By Setting MediaController and URI
        videoPlayer.setMediaController(mediaController);
        videoPlayer.setVideoURI(uri);
        videoPlayer.requestFocus();
        videoPlayer.start();
    }
}