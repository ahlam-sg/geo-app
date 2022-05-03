package com.example.geo_app;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public abstract class Language {

    public static String getLocaleLanguage(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return prefs.getString(Constants.LANGUAGE, "en");
    }

    public static void setLocaleLanguage(String lang, Context context){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void setLanguagePreference(String lang, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        editor.putString(Constants.LANGUAGE, lang);
        editor.apply();
    }

    public static void updateLanguage(String lang, Context context){
        setLocaleLanguage(lang, context);
        setLanguagePreference(lang, context);
    }


}
