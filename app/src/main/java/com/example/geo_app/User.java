package com.example.geo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class User {
    private String username;
    private String provider;
    private String imageURL;
    private String highScore;
    private String totalScore;

    public User() {
    }

    public User(String provider) {
        this.provider = provider;
        highScore = "";
        totalScore = "";
        username = "";
        imageURL = "";
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

    public String getHighScore() {
        return highScore;
    }
    public void setHighScore(String highScore) {
        this.highScore = highScore;
    }

    public String getTotalScore() {
        return totalScore;
    }
    public void setTotalScore(String totalScore) {
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
