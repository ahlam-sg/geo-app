package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference userDatabase;
    private CircleImageView profileCIV;
    private TextView usernameTV, highScoreTV, totalScoreTV;
    private TextView usernameLabelTV, highScoreLabelTV, totalScoreLabelTV;
    private String username = "", imageURL = "", highScore = "0", totalScore = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeObjects();
        connectToDatabase();
        showProgressbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    private void setProfile(){
        usernameTV.setText(username);
        highScoreTV.setText(highScore);
        totalScoreTV.setText(totalScore);
        if(!imageURL.isEmpty()){
            Glide.with(this)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileCIV);
        }
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
        profileCIV = findViewById(R.id.profile_CIV);
        usernameTV = findViewById(R.id.username_tv);
        highScoreTV = findViewById(R.id.high_score_tv);
        totalScoreTV = findViewById(R.id.total_score_tv);
        usernameLabelTV = findViewById(R.id.username_label_tv);
        highScoreLabelTV = findViewById(R.id.high_score_label_tv);
        totalScoreLabelTV = findViewById(R.id.total_score_label_tv);
    }

    private void connectToDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        userDatabase = database.getReference().child(Constants.USERS_REFERENCE).child(firebaseUser.getUid());
    }

    private void hideProfile(){
        profileCIV.setVisibility(View.GONE);
        usernameTV.setVisibility(View.GONE);
        highScoreTV.setVisibility(View.GONE);
        totalScoreTV.setVisibility(View.GONE);
        usernameLabelTV.setVisibility(View.GONE);
        highScoreLabelTV.setVisibility(View.GONE);
        totalScoreLabelTV.setVisibility(View.GONE);
    }

    private void showProfile(){
        profileCIV.setVisibility(View.VISIBLE);
        usernameTV.setVisibility(View.VISIBLE);
        highScoreTV.setVisibility(View.VISIBLE);
        totalScoreTV.setVisibility(View.VISIBLE);
        usernameLabelTV.setVisibility(View.VISIBLE);
        highScoreLabelTV.setVisibility(View.VISIBLE);
        totalScoreLabelTV.setVisibility(View.VISIBLE);
    }

    private void showProgressbar(){
        hideProfile();
        findViewById(R.id.progress_bar_lav).setVisibility(View.VISIBLE);
        readUser();
    }

    private void hideProgressbar(){
        findViewById(R.id.progress_bar_lav).setVisibility(View.GONE);
    }

}