package com.example.berto.stereosplitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SessionSelectionActivity extends AppCompatActivity {

    EditText etNewSession;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_selection);

        etNewSession = findViewById(R.id.etNewSession);
    }

    public void newSession(View view){
        String sessionName = etNewSession.getText().toString();

        etNewSession.setText("");

        CollectionReference colRef = db.collection(sessionName);

        colRef.document("playPause").update("playPause", false);

        colRef.document("time").update("time", 0);
    }
}
