package com.example.geo_app;

import androidx.annotation.NonNull;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;

public class LoadingActivity extends SoundEffectsManager {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<CountryModel> countries = new ArrayList<>();
    private TextView countDownTV;
    private NumberFormat numFormat;
    private boolean isExiting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        setSoundPool();
        countDownTV = findViewById(R.id.count_down_tv);
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);

        connectToDatabase();
        readCountries();
        Log.d("LoadingActivity", "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayerService.sendMusicStatusBroadcast(this, Constants.PAUSE_MUSIC);
    }

    public void connectToDatabase(){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        String language = Preferences.getLanguagePreference(getApplicationContext());
        if (language.equalsIgnoreCase("ar")) {
            databaseReference = database.getReference().child(Constants.COUNTRIES_AR_REFERENCE);
        }
        else{
            databaseReference = database.getReference().child(Constants.COUNTRIES_EN_REFERENCE);
        }
    }

    public void readCountries(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    CountryModel country = data.getValue(CountryModel.class);
                    if (country != null) {
                        country.setCode(data.getKey());
                        countries.add(country);
                    }
                }

                startCountDown();

//                Handler handler = new Handler();
//                handler.postDelayed(() -> startGameActivity(), 1500);
                Log.d("LoadingActivity", "readCountries: onDataChange");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoadingActivity", "readCountries: onCancelled", databaseError.toException());
            }
        });
    }

    public void startGameActivity(){
        Intent intentLoading = new Intent(this, GameActivity.class);
        intentLoading.putExtra(Constants.CATEGORY_KEY, getIntent().getStringExtra(Constants.CATEGORY_KEY));
        intentLoading.putExtra(Constants.COUNTRIES_ARRAYLIST, countries);
        startActivity(intentLoading);
        finish();
    }

    public void playCountDownAnimation(){
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(countDownTV, "scaleX", 1f, 1.5f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(countDownTV, "scaleY", 1f, 1.5f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(scaleDownX,scaleDownY);
        animatorSet.start();
    }

    private void startCountDown(){
        playCountDownSoundEffect();
        Log.d("LoadingActivity", "startCountDown: after playCountDownSoundEffect");
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000 == 1 && !isExiting){
                    cancel();
                    stopCountDownSoundEffect();
                    startGameActivity();
                }
                countDownTV.setText((numFormat.format(millisUntilFinished/1000)));
                playCountDownAnimation();
            }
            public void onFinish() {
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        isExiting = true;
        stopCountDownSoundEffect();
        super.onBackPressed();
    }

}