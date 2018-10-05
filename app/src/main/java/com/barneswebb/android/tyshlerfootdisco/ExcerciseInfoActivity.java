package com.barneswebb.android.tyshlerfootdisco;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ExcerciseInfoActivity extends AppCompatActivity {

    private static final String TAG = "footdiscoInfo";
    private WebView footwork_blurb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excerciseinfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadHtml();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Contact coach Dr Tyshler for tips: tyshler@footdisco.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void loadHtml() {
        footwork_blurb = (WebView) findViewById(R.id.footwork_blurb);

        footwork_blurb.getSettings().setJavaScriptEnabled(true);
        //rbw20161114 - commented out cause of google request: footwork_blurb.getSettings().setPluginState(WebSettings.PluginState.ON); //http://stackoverflow.com/a/17784472
        footwork_blurb.setWebViewClient(new WebViewClient() {
            @Override
            //http://inducesmile.com/android/embed-and-play-youtube-video-in-android-webview/
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        //rbw20161114 - commented out cause of google request: String htmlSrc= (DetectConnection.checkInternetConnection(this))?"footworkmp3":"footworkmp3_nointernet";
        String htmlSrc="footworkmp3_nointernet";
        footwork_blurb.loadDataWithBaseURL("", readRawHtmlFile(htmlSrc), "text/html", "UTF-8", ""); //http://stackoverflow.com/a/13741394

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

}
