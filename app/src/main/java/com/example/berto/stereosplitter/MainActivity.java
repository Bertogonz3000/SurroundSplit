package com.example.berto.stereosplitter;

import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;

    long curTime;

    boolean playPause = false;


    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference timeRef = db.collection("session1").document("time");
    DocumentReference playPauseRef = db.collection("session1").document("playPause");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Test song
        player = MediaPlayer.create(this, R.raw.starwarsfull);


        setupPlayPauseListener();

//        setupTimeListener();
//
//        while(player.isPlaying()){
//            timeRef.update("time", player.getCurrentPosition());
//        }

    }

    //Play the song
    public void playPause(View view){
        if(!playPause){

            playPauseRef.update("playPause", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Play Success", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Pause Success", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Pause Fail", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    //Play the right channel
    public void playRight(View view){
        player.setVolume(0, 1);
    }

    //Play the left channel
    public void playLeft(View view){
        player.setVolume(1,0);
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

                Toast.makeText(getApplicationContext(), "CHECK1", Toast.LENGTH_SHORT).show();


                if(documentSnapshot != null && documentSnapshot.exists()){
                    curTime = (long) documentSnapshot.getData().get("time");
                    Toast.makeText(getApplicationContext(), "CHECK2", Toast.LENGTH_SHORT).show();
                    if(player.getCurrentPosition() > curTime + 200 || player.getCurrentPosition() < curTime - 200){
                        player.seekTo((int)curTime + 100);
                    }
                }

            }
        });
    }

    private void setupPlayPauseListener(){
        final DocumentReference docRef = db.collection("session1").document("playPause");

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(getApplicationContext(),"Failed to listen", Toast.LENGTH_SHORT).show();
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    playPause = (boolean) documentSnapshot.getData().get("playPause");
//                    Toast.makeText(getApplicationContext(), "CHECK", Toast.LENGTH_SHORT).show();
                        if(playPause){
                            player.start();
                        } else {
                            player.pause();
                        }
                }
            }
        });
    }
}
