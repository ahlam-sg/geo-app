package com.example.geo_app;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;

public abstract class Score {

    public static int getTotalScore(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return prefs.getInt(Constants.TOTAL_SCORE, 0);
    }

    public static void addScoreToTotalScore(int score, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        int totalScore = getTotalScore(context) + score;
        editor.putInt(Constants.TOTAL_SCORE, totalScore);
        editor.apply();
    }

    public static int getHighestScore(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return prefs.getInt(Constants.HIGHEST_SCORE, 0);
    }

    public static void setHighestScore(int score, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        int highestScore = getHighestScore(context);
        if (score > highestScore){
            editor.putInt(Constants.HIGHEST_SCORE, score);
            editor.apply();
        }
    }


}
