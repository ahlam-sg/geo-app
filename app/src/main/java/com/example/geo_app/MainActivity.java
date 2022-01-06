package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickPlayBtn(View view) {
        Intent intent = new Intent(this, Selection.class);
        startActivity(intent);
    }

    public void onClickLeaderBoardBtn(View view) {
    }

    public void onClickSettingsBtn(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void onClickCreditsBtn(View view) {
    }

    public void onClickLoginBtn(View view) {
    }
}