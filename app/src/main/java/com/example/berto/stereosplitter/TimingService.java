package com.example.berto.stereosplitter;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimingService extends IntentService {

    //The Firestore database that we will be sending timing updates to
    FirebaseFirestore db;

    //Reference to the time document so we can post
    DocumentReference timeRef;


    //Mediaplayer so the thread can check current position
    MediaPlayer player;

    //Previous position of the music player
    int oldPos;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TimingService() {
        super("TimingService");
    }

    public TimingService(FirebaseFirestore db, MediaPlayer player){
        super("TimingService");
        this.db = db;
        this.player = player;
        if (player != null) {
//            Log.i("PLAYERCHECK", "We done it");
        }
        //Set up the time document reference
        timeRef = db.collection("session1").document("time");
        //Get the current position of the player
        oldPos = player.getCurrentPosition();

        timingRun();
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.i("TAGCHECK", "Starting timing writes");
        timingRun();

    }

    private void timingRun(){
        while(player == null) {
            Log.i("TAGCHECK", "NULL BITCH!");
        }
            while (true) {
//                Log.i("PLAYERCHECK", "WE IN HERE");
                if(!player.isPlaying()) Log.i("TAGCHECK", "THIS ISN'T IT CHIEF");
                if (player != null && player.isPlaying()) {
                    Log.i("TAGCHECK", "Timing write");
                    if (player.getCurrentPosition() >= oldPos + 200) {
                        timeRef.update("time", player.getCurrentPosition());
                        oldPos = player.getCurrentPosition();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

}
