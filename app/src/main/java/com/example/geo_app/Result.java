package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class Result extends AppCompatActivity {

    RecyclerView reviewRV;
    ArrayList<ReviewModel> reviewModel = new ArrayList<>();
    String category;
    int score, countCorrect;
    TextView countTV, scoreTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        reviewRV = findViewById(R.id.review_rv);
        countTV = findViewById(R.id.count_tv);
        scoreTV = findViewById(R.id.score_tv);

        getIntentData();

        countTV.setText("(" + countCorrect + "/" + reviewModel.size() + ")");
        scoreTV.setText(score + " " + getResources().getString(R.string.points));

        setReviewRV();
    }

    public void setReviewRV(){
        reviewRV = findViewById(R.id.review_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new ReviewAdapter(this, reviewModel);
        reviewRV.setLayoutManager(layoutManager);
        reviewRV.setAdapter(adapter);
    }

    public void getIntentData(){
        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        reviewModel = (ArrayList<ReviewModel>)intent.getSerializableExtra(Constants.REVIEW_MODEL_ARRAYLIST);
        score = intent.getIntExtra(Constants.SCORE, 0);
        countCorrect = intent.getIntExtra(Constants.COUNT_CORRECT, 0);
    }
}