package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends MainToolbar {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        setToolbarInMain(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
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

//    public void profilePage(View view) {
//        Intent intent = new Intent(this, Profile.class);
//        startActivity(intent);
//    }

//    public void signOut(View view) {
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(this, SignIn.class);
//        startActivity(intent);
//        finishAffinity();
//        Log.w("TAG", "Signed out");
//    }
}