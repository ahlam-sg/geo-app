package com.example.geo_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class SettingsActivity extends CustomToolbar implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_frame_layout, new SettingsFragment())
                    .commit();
        }

        setToolbar();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_frame_layout, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.custom_toolbar);
        setToolbar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0){
                finish();
            }
            getSupportFragmentManager().popBackStack();
        });
    }
}