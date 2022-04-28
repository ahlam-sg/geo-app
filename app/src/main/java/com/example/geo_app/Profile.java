package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference userDatabase;
    private CircleImageView profileCIV;
    private TextView usernameTV, highScoreTV, totalScoreTV;
    private String username = "", imageURL = "", highScore = "0", totalScore = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeObjects();
        connectToDatabase();
        readUser();
        setProfile();
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
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()){
                    if (data.child(Constants.USERNAME_REFERENCE).getValue() != null){
                        username = data.child(Constants.USERNAME_REFERENCE).getValue().toString();
                    }
                    if (data.child(Constants.IMAGE_URL_REFERENCE).getValue() != null){
                        imageURL = data.child(Constants.IMAGE_URL_REFERENCE).getValue().toString();
                    }
                    if (data.child(Constants.HIGH_SCORE_REFERENCE).getValue() != null){
                        highScore = data.child(Constants.HIGH_SCORE_REFERENCE).getValue().toString();
                    }
                    if (data.child(Constants.TOTAL_SCORE_REFERENCE).getValue() != null){
                        totalScore = data.child(Constants.TOTAL_SCORE_REFERENCE).getValue().toString();
                    }
                    Log.d("TAG", "readUser: data exist");
                }
                Log.d("TAG", "readUser: onDataChange");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "readUser: onCancelled", databaseError.toException());
            }
        });
    }

    private void initializeObjects(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileCIV = findViewById(R.id.profile_CIV);
        usernameTV = findViewById(R.id.username_tv);
        highScoreTV = findViewById(R.id.high_score_tv);
        totalScoreTV = findViewById(R.id.total_score_tv);
    }

    private void connectToDatabase(){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        userDatabase = database.getReference().child(Constants.USERS_REFERENCE).child(firebaseUser.getUid());
        Log.d("TAG", "connectToDatabase: uid: " + firebaseUser.getUid());
    }
}