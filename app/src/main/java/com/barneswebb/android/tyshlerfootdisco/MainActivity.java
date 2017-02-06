package com.barneswebb.android.tyshlerfootdisco;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.barneswebb.android.tyshlerfootdisco.trainingrec.ExerciseDataOpenHelper;
import com.barneswebb.android.tyshlerfootdisco.trainingrec.MyTrainingRecordActivity;

import static com.barneswebb.android.tyshlerfootdisco.trainingrec.ExerciseDataOpenHelper.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
    private ImageButton rewindButton,playButton, saveButton;
    private ImageView bgImageView;


    protected static ExerciseDataOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale("ru"));
        getBaseContext().getResources().updateConfiguration(
                getResources().getConfiguration(),
                getBaseContext().getResources().getDisplayMetrics()
        );

        setContentView(R.layout.activity_main);
        initializeViews();

        db = new ExerciseDataOpenHelper(this);
    }

    public void initializeViews(){
        mediaPlayer = MediaPlayer.create(this, R.raw.footdisco2012mp3);
        finalTime = mediaPlayer.getDuration();
        duration =      (TextView) findViewById(R.id.songDuration);
        seekbar =        (SeekBar) findViewById(R.id.seekBar);
        rewindButton=(ImageButton) findViewById(R.id.media_rew);
        playButton = (ImageButton) findViewById(R.id.media_play);
        saveButton = (ImageButton) findViewById(R.id.media_save);
        bgImageView =  (ImageView) findViewById(R.id.bg_image);

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                restart(v);
            }
        });
        playButton.setTag(IS_BUTTON_PLAY);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Map<String, String> data = new HashMap<String, String>() {{
                    put(FIELD_NAMES[1], PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("username", "not_set")); //userName
                    put(FIELD_NAMES[2], ISO8601Format.format(new Date())); //excerzDate
                    put(FIELD_NAMES[3], getExcerciseDuration()); //excerzDur
                    put(FIELD_NAMES[4], TAG); //program
                }};

                final EditText input = new EditText(v.getContext());
                (new AlertDialog.Builder(v.getContext()))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.savedlg_title)
                    .setMessage(getString(R.string.savedlg_duration)+getExcerciseDuration()+getString(R.string.savedlg_howdiditgo))
                        .setView(input)
                    .setPositiveButton(R.string.savedlg_savebtn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            data.put(FIELD_NAMES[5], input.getText().toString());
                            db.createExcercise(data);
                            Toast.makeText(MainActivity.this, R.string.savedlg_savednotific, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.savedlg_cancelbtn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                        .create()
                        .show();
            }
        });

        seekbar.setMax((int) finalTime);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                restart(null);//XXX View not used
            }
        });
    }



    public String getExcerciseDuration() {
        double actuDurat = (mediaPlayer.getCurrentPosition() > 0)?
                mediaPlayer.getCurrentPosition()
                :      mediaPlayer.getDuration();

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) actuDurat),
                TimeUnit.MILLISECONDS.toSeconds((long) actuDurat));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_trainingrecord:
                startActivity(new Intent(this, MyTrainingRecordActivity.class));
                break;
            case R.id.action_info:
                startActivity(new Intent(this, ExcerciseInfoActivity.class));
                break;
            //case R.id.action_settings:
            //    startActivityForResult(new Intent(this, SettingsActivity.class), 1);
            //    break;
            case R.id.action_about:
                aboutDlg();
                break;

            //case R.id.action_dbedit:
            //    startActivity(new Intent(this, AndroidDatabaseManager.class));
            //    break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void aboutDlg()
    {
        String verString = "Release: "+ BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")";

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ttslogo_icon)
                .setTitle("About")
                .setMessage("(c) 2017 FencingMultimedia.com\n\nDevelopment:\nRichard@Barnes-Webb.com\n"+verString+"\nAndroid API ver: "+android.os.Build.VERSION.SDK_INT)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();  } })
                        .show();
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



    public void play(View view) {
        boolean isPlaying = (Boolean)view.getTag();
        setPlayButton(isPlaying);
        if ( isPlaying ) {
            mediaPlayer.start();
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekbar.setProgress((int) timeElapsed);
            durationHandler.postDelayed(updateSeekBarTime, 100);
        } else {
            mediaPlayer.pause();
        }
    }

    public void setBgImage(Uri imgUri) {
        bgImageView.setImageURI(imgUri);
    }

    public void setPlayButton(Boolean isPlaying) {
        if ( isPlaying ) {
            playButton.setImageResource(R.drawable.blueicons_pause);
            playButton.setTag(IS_BUTTON_PAUSE);
        } else {
            playButton.setImageResource(R.drawable.blueicons_play);
            playButton.setTag(IS_BUTTON_PLAY);
        }
    }

    public void restart(View view) {
        mediaPlayer.seekTo(0);
        seekbar.setProgress(0);
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
