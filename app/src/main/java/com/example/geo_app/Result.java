package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class Result extends AppCompatActivity {

    TextView testTV;
    RecyclerView reviewRV;
    ArrayList<ReviewModel> reviewModel = new ArrayList<>();
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        reviewRV = findViewById(R.id.review_rv);
        testTV = findViewById(R.id.test_tv);

        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        reviewModel = (ArrayList<ReviewModel>)intent.getSerializableExtra(Constants.REVIEW_MODEL_ARRAYLIST);
        testTV.setText(reviewModel.get(0).getQuestion());

        //________ISSUE IS HERE
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviewModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewRV.setLayoutManager(linearLayoutManager);
        reviewRV.setAdapter(reviewAdapter);
    }
}