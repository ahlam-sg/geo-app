package com.example.geo_app;

import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import java.util.Locale;

public class Settings extends SecondaryToolbar {

    private static final int ENGLISH = 1;
    private static final int ARABIC = 2;
    Toolbar toolbar;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_settings);

        //toolbar
        toolbar = findViewById(R.id.secondary_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //language
        radioGroup = findViewById(R.id.language_radio_group);
        setLanguageBtn();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case ENGLISH:
                        setLocale("en");
                        saveLangPreference("en", checkedId);
                        recreate();
                        break;
                    case ARABIC:
                        setLocale("ar");
                        saveLangPreference("ar", checkedId);
                        recreate();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    //change locale language
    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    //save language preference
    private void saveLangPreference(String lang, int checkedId){
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        editor.putString(Constants.LANGUAGE, lang);
        editor.putInt(Constants.LANGUAGE_CHECKED_ID, checkedId);
        editor.apply();
    }

    //load language preference
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String language = prefs.getString(Constants.LANGUAGE, "en");
        setLocale(language);
    }

    //restore language preference
    public void setLanguageBtn(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        int btn_id = prefs.getInt(Constants.LANGUAGE_CHECKED_ID, 1);
        radioGroup.check(btn_id);
    }
}