package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onStart() {
        super.onStart();
        User.signInIfNotAuthenticated(getApplicationContext());
    }

    public void startBtn(View view) {
        Intent intent = new Intent(this, Category.class);
        startActivity(intent);
    }

    public void leaderBoardBtn(View view) {
        Intent intent = new Intent(this, LeaderBoard.class);
        startActivity(intent);
    }

    public void settingsBtn(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void creditsBtn(View view) {
        Intent intent = new Intent(this, Credits.class);
        startActivity(intent);
    }

    public void logoutBtn(View view) {
    }

    public void signInPage(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void signUpPage(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void profilePage(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
        finishAffinity();
        Log.w("TAG", "Signed out");
    }
}