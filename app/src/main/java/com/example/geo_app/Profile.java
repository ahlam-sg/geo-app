package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends MainToolbar {

    private FirebaseUser firebaseUser;
    private DatabaseReference userDatabase;
    private CircleImageView profileCIV;
    private TextView usernameTV, highScoreTV, totalScoreTV;
    private String username = "", imageURL = "", highScore = "0", totalScore = "0";
    private Toolbar toolbar;
    private LinearProgressIndicator levelProgressIndicator;
    private TextView levelTV, nextLevelTV;
    private RelativeLayout userInfoLayout, levelInfoLayout;
    private NumberFormat numFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.main_toolbar);
        setToolbar(toolbar);

        initializeObjects();
        connectToDatabase();
        showProgressbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    private void setLevel(){
        double levelDouble = ((double) Integer.parseInt(totalScore) / 10000);
        int levelInt = (int)Math.floor(levelDouble);
        if (levelInt < 1){
            levelInt = 1;
            levelDouble++;
        }
        String level = String.format(getResources().getString(R.string.level), String.valueOf(numFormat.format(levelInt)));
        levelTV.setText(level);
        double levelProgressStatus = (levelDouble - levelInt) * 100;
        levelProgressIndicator.setProgress((int)levelProgressStatus);
        int remainingPoints = (int)(((double)((100 - levelProgressStatus) / 100)) * 10000);
        String nextLevel = String.format(getResources().getString(R.string.next_level),
                String.valueOf(numFormat.format(remainingPoints)),
                String.valueOf(numFormat.format(levelInt+1)));
        nextLevelTV.setText(nextLevel);
    }

    private void setProfile(){
        usernameTV.setText(username);
        highScoreTV.setText(numFormat.format(Integer.parseInt(highScore)));
        totalScoreTV.setText(numFormat.format(Integer.parseInt(totalScore)));
        if(!imageURL.isEmpty()){
            Glide.with(this)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileCIV);
        }
        setLevel();
    }

    public void readUser(){
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()){
                    if (data.child(Constants.USERNAME_REFERENCE).getValue() != null){
                        username = Objects.requireNonNull(data.child(Constants.USERNAME_REFERENCE).getValue()).toString();
                    }
                    if (data.child(Constants.IMAGE_URL_REFERENCE).getValue() != null){
                        imageURL = Objects.requireNonNull(data.child(Constants.IMAGE_URL_REFERENCE).getValue()).toString();
                    }
                    if (data.child(Constants.HIGH_SCORE_REFERENCE).getValue() != null){
                        highScore = Objects.requireNonNull(data.child(Constants.HIGH_SCORE_REFERENCE).getValue()).toString();
                    }
                    if (data.child(Constants.TOTAL_SCORE_REFERENCE).getValue() != null){
                        totalScore = Objects.requireNonNull(data.child(Constants.TOTAL_SCORE_REFERENCE).getValue()).toString();
                    }
                    setProfile();
                    hideProgressbar();
                    showProfile();
                }
                Log.d("Profile", "readUser: onDataChange");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Profile", "readUser: onCancelled", databaseError.toException());
            }
        });
    }

    private void initializeObjects(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);
        profileCIV = findViewById(R.id.profile_CIV);
        usernameTV = findViewById(R.id.username_tv);
        highScoreTV = findViewById(R.id.high_score_tv);
        totalScoreTV = findViewById(R.id.total_score_tv);
        levelProgressIndicator = findViewById(R.id.level_lpi);
        levelTV = findViewById(R.id.level_tv);
        nextLevelTV = findViewById(R.id.next_level_tv);
        userInfoLayout = findViewById(R.id.user_info_layout);
        levelInfoLayout = findViewById(R.id.level_info_layout);
    }

    private void connectToDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        userDatabase = database.getReference().child(Constants.USERS_REFERENCE).child(firebaseUser.getUid());
    }

    private void hideProfile(){
        userInfoLayout.setVisibility(View.GONE);
        levelInfoLayout.setVisibility(View.GONE);
    }

    private void showProfile(){
        userInfoLayout.setVisibility(View.VISIBLE);
        levelInfoLayout.setVisibility(View.VISIBLE);
    }

    private void showProgressbar(){
        hideProfile();
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        readUser();
    }

    private void hideProgressbar(){
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

}