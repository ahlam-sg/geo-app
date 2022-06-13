package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import java.util.List;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ListPreference languageListPref;
    private Preference updatePasswordPref, updateEmailPref, signOutPref;
    private SwitchPreferenceCompat musicSwitchPref;
    private SwitchPreferenceCompat darkModeSwitchPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        initializeObjects();
        disableUpdatePasswordAndEmailIfSignedWithGoogle();

        signOutPref.setOnPreferenceClickListener(preference -> {
            signOut();
            return false;
        });

        languageListPref.setOnPreferenceChangeListener((preference, newValue) -> {
            int selectedIndex = languageListPref.findIndexOfValue(newValue.toString());
            languageListPref.setValueIndex(selectedIndex);
            String language = newValue.toString();
            Preferences.setLocaleLanguage(language, requireContext());
            startMainActivity();
            return false;
        });

        musicSwitchPref.setOnPreferenceClickListener(preference -> {
            if (musicSwitchPref.isChecked()){
                MusicPlayerService.startMusicPlayerService(requireActivity());
            }
            else{
                MusicPlayerService.stopMusicPlayerService(requireActivity());
            }
            return false;
        });

        darkModeSwitchPref.setOnPreferenceClickListener(preference -> {
            if (darkModeSwitchPref.isChecked()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return false;
        });
    }

    private void disableUpdatePasswordAndEmailIfSignedWithGoogle(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        List<? extends UserInfo> providerData = Objects.requireNonNull(currentUser).getProviderData();
        String provider = providerData.get(1).getProviderId();
        if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
            updatePasswordPref.setVisible(false);
            updateEmailPref.setVisible(false);
        }
        else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)){
            updatePasswordPref.setVisible(true);
            updateEmailPref.setVisible(true);
        }
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), SignInActivity.class);
        startActivity(intent);
        requireActivity().finishAffinity();
        Log.d("SettingsFragment", "onCreatePreferences: signOut");
    }

    private void startMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finishAffinity();
        Log.d("SettingsFragment", "redirectToMainActivity");
    }

    private void initializeObjects(){
        languageListPref = getPreferenceManager().findPreference(getResources().getString(R.string.language_key));
        updatePasswordPref = getPreferenceManager().findPreference(getResources().getString(R.string.update_password_key));
        updateEmailPref = getPreferenceManager().findPreference(getResources().getString(R.string.update_email_key));
        signOutPref = getPreferenceManager().findPreference(getResources().getString(R.string.sign_out_key));
        musicSwitchPref = getPreferenceManager().findPreference(getResources().getString(R.string.music_key));
        darkModeSwitchPref = getPreferenceManager().findPreference(getResources().getString(R.string.dark_mode_key));
    }
}