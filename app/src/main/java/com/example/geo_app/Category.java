package com.example.geo_app;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Category extends SecondaryToolbar {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        setToolbar();
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.secondary_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void flagsBtn(View view) {
    }

    public void capitalsBtn(View view) {
        Intent intent = new Intent(this, Loading.class);
        intent.putExtra(Constants.CATEGORY_KEY, Constants.CATEGORY_CAPITAL);
        startActivity(intent);
    }

    public void mapsBtn(View view) {
    }
}