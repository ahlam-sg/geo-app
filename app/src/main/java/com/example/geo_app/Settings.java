package com.example.geo_app;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Settings extends MainToolbar {

    private static final int ENGLISH = 1;
    private static final int ARABIC = 2;
    Toolbar toolbar;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar();

        radioGroup = findViewById(R.id.language_radio_group);
//        setLanguageRadioButton();
//        languageRadioGroup();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

//    private void languageRadioGroup(){
//        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            View radioButton = radioGroup.findViewById(checkedId);
//            int index = radioGroup.indexOfChild(radioButton);
//            switch (index) {
//                case ENGLISH:
//                    Language.setLocaleLanguage("en", getBaseContext());
//                    recreate();
//                    break;
//                case ARABIC:
//                    Language.setLocaleLanguage("ar", getBaseContext());
//                    recreate();
//                    break;
//                default:
//                    break;
//            }
//        });
//    }
//
//    private void setLanguageRadioButton(){
//        String localeLanguage = Language.getLocaleLanguage(getBaseContext());
//        if (localeLanguage.equalsIgnoreCase("ar")){
//            radioGroup.check(R.id.arabic_btn);
//        }
//        else {
//            radioGroup.check(R.id.english_btn);
//        }
//    }

    private void setToolbar(){
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
        finishAffinity();
        Log.w("TAG", "Signed out");
    }
}