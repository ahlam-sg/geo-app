package com.example.geo_app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class MainActivity extends CustomToolbar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setToolbarInMain(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    public void startCategoryActivity(View view) {
        Intent intent = new Intent(this, CategoryActivity.class);
        startActivity(intent);
    }

    public void startLeaderBoardActivity(View view) {
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void startSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}