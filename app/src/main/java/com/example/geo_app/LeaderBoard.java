package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LeaderBoard extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference usersDatabase;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

//        connectToDatabase();
    }

    @Override
    public void onStart() {
        super.onStart();
        User.signInIfNotAuthenticated(getApplicationContext());
    }

//    public void readAllScores(){
//        usersDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String username = "", highScore = "", totalScore = "";
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    if (data.child(data.getKey()).child(Constants.USERNAME_REFERENCE).getValue() != null){
//                        username = Objects.requireNonNull(data.child(Constants.USERNAME_REFERENCE).getValue()).toString();
//                    }
//                    if (data.child(data.getKey()).child(Constants.HIGH_SCORE_REFERENCE).getValue() != null){
//                        highScore = Objects.requireNonNull(data.child(Constants.USERNAME_REFERENCE).getValue()).toString();
//                    }
//                    if (data.child(data.getKey()).child(Constants.TOTAL_SCORE_REFERENCE).getValue() != null){
//                        totalScore = Objects.requireNonNull(data.child(Constants.USERNAME_REFERENCE).getValue()).toString();
//                    }
//                    User user = new User();
//                    user.setUsername(username);
//                    user.setHighScore(Integer.parseInt(highScore));
//                    user.setHighScore(Integer.parseInt(totalScore));
//                    users.add(user);
//                }
//                Log.d("TAG", "readAllScores: onDataChange");
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("TAG", "readAllScores: onCancelled", databaseError.toException());
//            }
//        });
//    }

    private void connectToDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        usersDatabase = database.getReference().child(Constants.USERS_REFERENCE);
    }
}