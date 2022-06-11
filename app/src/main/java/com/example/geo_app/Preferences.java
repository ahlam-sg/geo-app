package com.example.geo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.Log;
import androidx.preference.PreferenceManager;
import java.util.Locale;

public abstract class Preferences {

    public static String getLanguagePreference(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getResources().getString(R.string.language_key), "en");
    }

    public static void setLocaleLanguage(String lang, Context context){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        Log.d("Preferences", "setLocaleLanguage");
    }

    public static boolean getMusicPreference(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getResources().getString(R.string.music_key), true);
    }

    public static void startMusicPlayerService(Activity activity){
        activity.startService(new Intent(activity.getApplicationContext(), MusicPlayerService.class));
    }

    public static void stopMusicPlayerService(Activity activity){
        activity.stopService(new Intent(activity.getApplicationContext(), MusicPlayerService.class));
    }

    public static boolean getSoundEffectPreference(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getResources().getString(R.string.sound_effects_key), true);
    }

    public static void playCorrectSoundEffect(Context context){
        MediaPlayer correctMediaPlayer = MediaPlayer.create(context, R.raw.correct_sound_effect);
        correctMediaPlayer.start();
    }

    public static void playIncorrectSoundEffect(Context context){
        MediaPlayer incorrectMediaPlayer = MediaPlayer.create(context, R.raw.incorrect_sound_effect);
        incorrectMediaPlayer.start();
    }

    public static void sendMusicStatusBroadcast(Context context, String status) {
        Intent intent = new Intent(Constants.MUSIC_STATUS_ACTION);
        intent.putExtra(Constants.MUSIC_STATUS, status);
        context.sendBroadcast(intent);
    }

}
