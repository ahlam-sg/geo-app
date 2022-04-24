package com.example.geo_app;

import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

import java.util.Objects;


public class Credits extends SecondaryToolbar {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        setToolbar();
    }

    public void setToolbar(){
        toolbar = findViewById(R.id.secondary_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}