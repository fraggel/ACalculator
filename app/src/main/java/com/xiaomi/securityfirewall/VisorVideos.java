package com.xiaomi.securityfirewall;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VisorVideos extends AppCompatActivity {
    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaPlayer mp=null;
    boolean clickVideo=true;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the main layout of the activity
        setContentView(R.layout.activity_visor_videos);
        getSupportActionBar().hide();
        //set the media controller buttons

        //initialize the VideoView
        myVideoView = (VideoView) findViewById(R.id.videoView);

        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(VisorVideos.this);
        // set a title for the progress bar
        progressDialog.setTitle(" ");
        // set a message for the progress bar
        progressDialog.setMessage("Cargando...");
        //set the progress bar not cancelable on users' touch
        progressDialog.setCancelable(false);
        // show the progress bar
        progressDialog.show();

        try {
            //set the media controller in the VideoView
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("key");
                myVideoView.setVideoURI(Uri.parse(value));
                myVideoView.start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        myVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickVideo) {
                    mp.setVolume(1f, 1f);
                    clickVideo=false;
                }else{
                    mp.setVolume(0f, 0f);
                    clickVideo=true;
                }
            }
        });
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
                progressDialog.dismiss();
                MediaController mediaController = new MediaController(VisorVideos.this){
                    @Override
                    public void show(int timeout) {
                        super.show(0);
                    }

                    @Override
                    public void hide() {

                    }
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                            mp.release();
                            super.hide();//Hide mediaController
                            finish();//Close this activity
                            return true;//If press Back button, finish here
                        }
                        //If not Back button, other button (volume) work as usual.
                        return super.dispatchKeyEvent(event);
                    }
                };
                myVideoView.setMediaController(mediaController);
                mp=mediaPlayer;
                mediaPlayer.setVolume(0f,0f);
                //if we have a position on savedInstanceState, the video playback should start from here
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    myVideoView.pause();
                }
            }
        });

    }
    @Override
    protected void onPause() {
         VideoView myVideoView = (VideoView) findViewById(R.id.videoView);
        myVideoView.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        VideoView myVideoView = (VideoView) findViewById(R.id.videoView);
        myVideoView.setVisibility(View.INVISIBLE);
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        VideoView myVideoView = (VideoView) findViewById(R.id.videoView);
        myVideoView.setVisibility(View.INVISIBLE);
        super.onDestroy();
        finish();
    }
   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);

    }
}

