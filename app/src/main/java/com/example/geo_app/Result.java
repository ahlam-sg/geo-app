package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Result extends AppCompatActivity {

    private RecyclerView reviewRV;
    private ArrayList<ReviewModel> reviewModel = new ArrayList<>();
    private String category;
    private int score, countCorrect, countWrong;
    private TextView countTV, scoreTV, percentageTV;
    private DatabaseReference userDatabase;
    private long highScore, totalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initializeObjects();
        getIntentData();
        setResultTextViews();
        setReviewRecyclerView();
        setPieChart();
        updateHighAndTotalScore();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    private void setPieChart(){
        countWrong = reviewModel.size() - countCorrect;
        PieChart pieChart = findViewById(R.id.pie_chart);
        pieChart.addPieSlice(new PieModel("Correct", countCorrect, Color.parseColor("#8AC926")));
        pieChart.addPieSlice(new PieModel("Wrong", countWrong, Color.parseColor("#ff595e")));
        pieChart.startAnimation();
    }

    private void setResultTextViews(){
        double percentage = ((double) countCorrect / reviewModel.size()) * 100;
        countTV.setText("(" + countCorrect + "/" + reviewModel.size() + ")");
        scoreTV.setText(score + " " + getResources().getString(R.string.points));
        percentageTV.setText((int) percentage + "%");
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

    private void connectToDatabase(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        if (firebaseUser != null) {
            userDatabase = database.getReference().child(Constants.USERS_REFERENCE).child(firebaseUser.getUid());
        }
    }

    private void setHighAndTotalScore(int score){
        userDatabase.child(Constants.TOTAL_SCORE_REFERENCE).setValue(ServerValue.increment(score));
        long newHighScore = highScore;
        if (score > highScore) {
            newHighScore = score;
        }
        userDatabase.child(Constants.HIGH_SCORE_REFERENCE).setValue(newHighScore);
    }

    private void updateHighAndTotalScore(){
        connectToDatabase();
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()){
                    if (data.child(Constants.HIGH_SCORE_REFERENCE).getValue() != null){
                        highScore = (long) Objects.requireNonNull(data.child(Constants.HIGH_SCORE_REFERENCE).getValue());
                    }
                    if (data.child(Constants.TOTAL_SCORE_REFERENCE).getValue() != null){
                        totalScore = (long) Objects.requireNonNull(data.child(Constants.TOTAL_SCORE_REFERENCE).getValue());
                    }

                }
                setHighAndTotalScore(score);
                Log.d("Result", "readHighAndTotalScore: onDataChange");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Result", "readHighAndTotalScore: onCancelled", databaseError.toException());
            }
        });
    }
}