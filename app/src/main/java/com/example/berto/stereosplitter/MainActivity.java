package com.example.berto.stereosplitter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Timer;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;

    long curTime;

    boolean playPause = false;


    //Job ID for timing service
    private static final int TIME_JOB_ID = 1000;


    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //References to documents for playPause for the DB
    DocumentReference playPauseRef = db.collection("session1").document("playPause");
    DocumentReference timeRef = db.collection("session1").document("time");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Test song
        player = MediaPlayer.create(this, R.raw.starwarsfull);

        Intent i = getIntent();

        //Setup firebase listener for playpause document
        setupPlayPauseListener();


        //If the user is a follower
        if(!i.getBooleanExtra("position", false)) {
            //Setup firebase listener for time document
            setupTimeListener();
            //Toast.makeText(this, "follower", Toast.LENGTH_SHORT).show();
        }

        if(player == null) Log.i("FAILCHECK", "FAILED");
        else Log.i("FAILCHECK", "WTF?");

        //Setup the time setting service
//        setupTimingService();

        //If the user is a leader
        if(i.getBooleanExtra("position", false)) {
            timeRef.update("time", 0);
            //Setup timing thread
            setUpTimingThread();
            //Toast.makeText(this, "leader", Toast.LENGTH_SHORT).show();

        }

//        //On startup, get play/paused status
//        playPauseRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if(task.isSuccessful()){
//                    DocumentSnapshot doc = task.getResult();
//                    playPause = (boolean) doc.getData().get("playPause");
//                    if(playPause){
//                        player.start();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Failed to get playPause data", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });



    }

    //Play the song
    public void playPause(View view){
        if(!playPause){

            playPauseRef.update("playPause", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   // Toast.makeText(getApplicationContext(), "Play Success", Toast.LENGTH_SHORT).show();
                    playPause = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Play Fail", Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            playPauseRef.update("playPause", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                //    Toast.makeText(getApplicationContext(), "Pause Success", Toast.LENGTH_SHORT).show();
                    playPause = false;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Pause Fail", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    //Restart the audio track
    public void restart(View view) {
        player.seekTo(0);
        timeRef.update("time", 0);
    }

    //Play the right channel
    public void playRight(View view){
        player.setVolume(0, 1);
    }

    //Play the left channel
    public void playLeft(View view){
        player.setVolume(1,0);
    }

    //Play both channels
    public void playBoth(View view){
        player.setVolume(1,1);
    }

    //Setup realtime listener for session1
    private void setupTimeListener() {
        final DocumentReference docRef = db.collection("session1").document("time");

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Toast.makeText(getApplicationContext(), "Failed to listen", Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(getApplicationContext(), "CHECK1", Toast.LENGTH_SHORT).show();


                if(documentSnapshot != null && documentSnapshot.exists()){
                    curTime = (long) documentSnapshot.getData().get("time");
//                    Toast.makeText(getApplicationContext(), "CHECK2", Toast.LENGTH_SHORT).show();
                    if(player.getCurrentPosition() > curTime + 200 || player.getCurrentPosition() < curTime - 200){
                        player.seekTo((int)curTime + 100);
                    }
                }

            }
        });
    }

    private void setupPlayPauseListener(){
        playPauseRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(getApplicationContext(),"Failed to listen", Toast.LENGTH_SHORT).show();
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    playPause = (boolean) documentSnapshot.getData().get("playPause");
//                    Toast.makeText(getApplicationContext(), "CHECK", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "First playpause =" + playPause, Toast.LENGTH_SHORT).show();

                    if(playPause){
                            //Toast.makeText(getApplicationContext(), "Starting", Toast.LENGTH_SHORT).show();
                            player.start();
                            playPause = true;
                            //Toast.makeText(getApplicationContext(), "After start playpause =" + playPause, Toast.LENGTH_SHORT).show();
                        } else {
                           // Toast.makeText(getApplicationContext(), "Pausing", Toast.LENGTH_SHORT).show();
                            player.pause();
                            playPause = false;
                           // Toast.makeText(getApplicationContext(), "After pause playpause =" + playPause, Toast.LENGTH_SHORT).show();

                        }
                }
            }
        });
    }

    //Setup the TimingService to post currentTime for the player to the database every X seconds
//    private void setupTimingService(){
//        //New instance of TimingService
//        TimingService service = new TimingService(db, player);
//
//        //declare the new intent
//        Intent tServiceIntent = new Intent(MainActivity.this, TimingService.class);
//
//
//        //Start the service
//        service.onHandleIntent(tServiceIntent);
//
//    }

    //Setup the TimingThread
    private void setUpTimingThread(){
        TimingThread thread = new TimingThread(db, player);
        thread.start();
    }
}

