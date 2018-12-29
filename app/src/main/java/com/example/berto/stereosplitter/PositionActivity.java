package com.example.berto.stereosplitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PositionActivity extends AppCompatActivity {

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        i = new Intent(PositionActivity.this, MainActivity.class);
    }

    public void leader(View view){
        i.putExtra("position", true);
        startActivity(i);
    }

    public void follower(View view){
        i.putExtra("position", false);
        startActivity(i);
    }
}
