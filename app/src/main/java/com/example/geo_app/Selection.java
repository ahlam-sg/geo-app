package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Selection extends SecondaryToolbar {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        //toolbar
        toolbar = findViewById(R.id.secondary_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClickFlagBtn(View view) {
    }

    public void onClickCapitalBtn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.CATEGORY_KEY, Constants.CATEGORY_CAPITAL);
        startActivity(intent);
    }

    public void onClickMapBtn(View view) {
    }
}