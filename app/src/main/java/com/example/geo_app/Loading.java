package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.CATEGORY_KEY) == Constants.CATEGORY_CAPITAL){

        }

    }


}