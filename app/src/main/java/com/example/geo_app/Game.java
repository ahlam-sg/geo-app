package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Game extends AppCompatActivity {

    TextView timer;
    TextView points;
    TextView hint_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        timer = findViewById(R.id.timer);
        points = findViewById(R.id.points);
        hint_text = findViewById(R.id.hint_text);

    }

    public void hintBtn(View view) {
    }

    public void exitBtn(View view) {
        finish();
    }
}