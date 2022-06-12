package com.example.geo_app;

import androidx.annotation.NonNull;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
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
    private boolean soundEffectStatus;
    private static MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        setSoundPool();
        soundEffectStatus = Preferences.getSoundEffectPreference(getApplicationContext());
        countDownTV = findViewById(R.id.count_down_tv);
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);

        connectToDatabase();
        readCountries();
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
        setMediaPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundEffectStatus){
            stopMediaPlayer();
        }
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
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(countDownTV, "scaleX", 1f, 2f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(countDownTV, "scaleY", 1f, 2f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(scaleDownX,scaleDownY);
        animatorSet.start();
    }

    private void startCountDown(){
        if (soundEffectStatus){
            mediaPlayer.start();
        }
        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000 == 1 && !isExiting){
                    cancel();
                    startGameActivity();
                }
                countDownTV.setText((numFormat.format(millisUntilFinished/1000)));
                playCountDownAnimation();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        isExiting = true;
        super.onBackPressed();
    }

    private void setMediaPlayer(){
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.count_down_sound_effect);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    private void stopMediaPlayer(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}