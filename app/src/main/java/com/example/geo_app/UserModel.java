package com.example.geo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class UserModel {
    private String uid;
    private String username;
    private String provider;
    private String imageURL;
    private long highScore;
    private long totalScore;

    public UserModel() {
    }

    public UserModel(String provider) {
        this.provider = provider;
        highScore = 0;
        totalScore = 0;
        username = "";
        imageURL = "";
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getHighScore() {
        return highScore;
    }
    public void setHighScore(long highScore) {
        this.highScore = highScore;
    }

    public long getTotalScore() {
        return totalScore;
    }
    public void setTotalScore(long totalScore) {
        this.totalScore = totalScore;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public static void signInIfNotAuthenticated(Context context){
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(context.getApplicationContext(), SignIn.class);
            context.getApplicationContext().startActivity(intent);
            ((Activity)context.getApplicationContext()).finishAffinity();
            Log.w("TAG", "Must sign in");
        }
    }
}
