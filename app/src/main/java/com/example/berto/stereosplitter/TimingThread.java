package com.example.berto.stereosplitter;

import android.media.MediaPlayer;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimingThread extends Thread {

    //The Firestore database that we will be sending timing updates to
    FirebaseFirestore db;

    //Reference to the time document so we can post
    DocumentReference timeRef;


    //Mediaplayer so the thread can check current position
    MediaPlayer player;

    //Previous position of the music player
    int oldPos;

    public TimingThread(FirebaseFirestore db, MediaPlayer player){
        super("TimingService");
        this.db = db;
        this.player = player;
        if (player != null) {
//            Log.i("PLAYERCHECK", "We done it");
        }
        //Set up the time document reference
        timeRef = db.collection("session1").document("time");
        timeRef.update("time", 0);
        //Get the current position of the player
        oldPos = player.getCurrentPosition();
    }

    @Override
    public void run() {
        super.run();
        while(player == null) {
            Log.i("TAGCHECK", "NULL BITCH!");
        }
        while (true) {
//                Log.i("PLAYERCHECK", "WE IN HERE");
            if(!player.isPlaying()) Log.i("TAGCHECK", "THIS ISN'T IT CHIEF");
            if (player != null && player.isPlaying()) {
                Log.i("TAGCHECK", "Timing write");
                if (player.getCurrentPosition() >= oldPos + 500) {
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
