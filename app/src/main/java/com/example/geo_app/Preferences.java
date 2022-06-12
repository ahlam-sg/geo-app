package com.example.geo_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

    public static boolean getSoundEffectPreference(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getResources().getString(R.string.sound_effects_key), true);
    }
}
