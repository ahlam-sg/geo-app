package com.example.geo_app;

import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class Category extends MainToolbar {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = findViewById(R.id.main_toolbar);
        setToolbar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    public void flagsCategory(View view) {
        Intent intent = new Intent(this, Loading.class);
        intent.putExtra(Constants.CATEGORY_KEY, Constants.CATEGORY_FLAG);
        startActivity(intent);
    }

    public void capitalsCategory(View view) {
        Intent intent = new Intent(this, Loading.class);
        intent.putExtra(Constants.CATEGORY_KEY, Constants.CATEGORY_CAPITAL);
        startActivity(intent);
    }

    public void mapsCategory(View view) {
        Intent intent = new Intent(this, Loading.class);
        intent.putExtra(Constants.CATEGORY_KEY, Constants.CATEGORY_MAP);
        startActivity(intent);
    }
}