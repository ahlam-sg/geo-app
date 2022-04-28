package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Result extends AppCompatActivity {

    RecyclerView reviewRV;
    ArrayList<ReviewModel> reviewModel = new ArrayList<>();
    String category;
    int score, countCorrect;
    double percentage;
    TextView countTV, scoreTV, percentageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initializeObjects();
        getIntentData();
        setResultTextViews();
        setReviewRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        User.signInIfNotAuthenticated(getApplicationContext());
    }

    private void setResultTextViews(){
        percentage = ((double)countCorrect / reviewModel.size()) * 100;
        countTV.setText("(" + countCorrect + "/" + reviewModel.size() + ")");
        scoreTV.setText(score + " " + getResources().getString(R.string.points));
        percentageTV.setText((int)percentage + "%");
    }

    private void setReviewRecyclerView(){
        reviewRV = findViewById(R.id.review_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new ReviewAdapter(this, reviewModel, category);
        reviewRV.setLayoutManager(layoutManager);
        reviewRV.setAdapter(adapter);
    }

    private void getIntentData(){
        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        reviewModel = (ArrayList<ReviewModel>)intent.getSerializableExtra(Constants.REVIEW_MODEL_ARRAYLIST);
        score = intent.getIntExtra(Constants.SCORE, 0);
        countCorrect = intent.getIntExtra(Constants.COUNT_CORRECT, 0);
    }

    private void initializeObjects(){
        reviewRV = findViewById(R.id.review_rv);
        countTV = findViewById(R.id.count_tv);
        scoreTV = findViewById(R.id.score_tv);
        percentageTV = findViewById(R.id.percentage_tv);
    }

    public void replayFAB(View view) {
        Intent intent = new Intent(this, Loading.class);
        intent.putExtra(Constants.CATEGORY_KEY, category);
        startActivity(intent);
        finish();
    }

    public void leaderboardBtn(View view) {
        Intent intent = new Intent(this, LeaderBoard.class);
        startActivity(intent);
        finish();
    }
}