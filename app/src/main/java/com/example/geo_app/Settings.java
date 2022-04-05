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
        getLocale();
        setContentView(R.layout.activity_settings);

        setToolbar();

        radioGroup = findViewById(R.id.language_radio_group);

        //language
        setLanguageRadioButton();
        languageRadioGroup();
    }

    private void languageRadioGroup(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case ENGLISH:
                        setLocale("en");
                        setLanguagePreference("en", checkedId);
                        recreate();
                        break;
                    case ARABIC:
                        setLocale("ar");
                        setLanguagePreference("ar", checkedId);
                        recreate();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void getLocale(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String language = prefs.getString(Constants.LANGUAGE, "en");
        setLocale(language);
    }

    private void setLanguagePreference(String lang, int checkedId){
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        editor.putString(Constants.LANGUAGE, lang);
        editor.putInt(Constants.LANGUAGE_CHECKED_ID, checkedId);
        editor.apply();
    }

    public void setLanguageRadioButton(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        int btn_id = prefs.getInt(Constants.LANGUAGE_CHECKED_ID, 1);
        radioGroup.check(btn_id);
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.secondary_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}