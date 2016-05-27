package com.barneswebb.android.tyshlerfootdisco;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @thanks http://examples.javacodegeeks.com/android/android-mediaplayer-example/
 */
public class MainActivity extends AppCompatActivity {

    private static final String  TAG            = "footdiscoMain";

    private static final int FORWARD_TIME = 2000;
    Boolean IS_BUTTON_PLAY = new Boolean(true);
    Boolean IS_BUTTON_PAUSE = new Boolean(false);

    private MediaPlayer mediaPlayer;
    public TextView songName, duration;
    private double timeElapsed = 0, finalTime = 0;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private ImageButton playButton;
    private WebView footwork_blurb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
    }

    public void initializeViews(){
        mediaPlayer = MediaPlayer.create(this, R.raw.footdisco2012mp3);
        finalTime = mediaPlayer.getDuration();
        duration = (TextView) findViewById(R.id.songDuration);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        playButton = (ImageButton) findViewById(R.id.media_play);
        footwork_blurb = (WebView) findViewById(R.id.footwork_blurb);

        footwork_blurb.getSettings().setJavaScriptEnabled(true);
        footwork_blurb.loadDataWithBaseURL("", readRawHtmlFile("footworkmp3"), "text/html", "UTF-8", ""); //http://stackoverflow.com/a/13741394


        (findViewById(R.id.media_play)).setTag(IS_BUTTON_PLAY);

        seekbar.setMax((int) finalTime);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                restart(null);//XXX View not used
            }
        });
    }

    @Override public void onResume() {
        super.onResume();

        //findViewById(R.id.fullscreen_content).setSystemUiVisibility(
                  //View.SYSTEM_UI_FLAG_LOW_PROFILE
              //| View.SYSTEM_UI_FLAG_FULLSCREEN
              //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              //| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    /* Copied from tyshler_training_system\...\MainActivity.java */
    public String readRawHtmlFile(String rawResId) {
        Log.d(TAG, "Loading raw resource - id: " + rawResId);
        try
        { //ta: http://stackoverflow.com/a/16161277
            InputStream is = this.getResources().openRawResource(this.getResources().getIdentifier(rawResId, "raw", this.getPackageName()));
            byte[] buffer = new byte[0];
            buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            return new String(buffer);
        }
        catch (Resources.NotFoundException | IOException e) {
            Log.e(TAG, e.toString());
        }
        return "<err>";
    }


    public void play(View view) {
        if ( view.getTag() == IS_BUTTON_PLAY)
        {
            playButton.setImageResource(android.R.drawable.ic_media_pause);
            playButton.setTag(IS_BUTTON_PAUSE);

            mediaPlayer.start();
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekbar.setProgress((int) timeElapsed);
            durationHandler.postDelayed(updateSeekBarTime, 100);
        } else {
            playButton.setImageResource(android.R.drawable.ic_media_play);
            playButton.setTag(IS_BUTTON_PLAY);

            mediaPlayer.pause();
        }
    }

    public void restart(View view) {
        mediaPlayer.seekTo(0);
    }

    public void pause(View view) {
        mediaPlayer.pause();
    }

    public void forward(View view) {
        if ((timeElapsed + FORWARD_TIME) < finalTime) {
            timeElapsed = timeElapsed + FORWARD_TIME;
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }


    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                timeElapsed = mediaPlayer.getCurrentPosition(); //get current position
                seekbar.setProgress((int) timeElapsed);
                double timeRemaining = finalTime - timeElapsed;
                duration.setText(String.format("-%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            }
            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };
}
