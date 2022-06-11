package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {

    ListPreference languageListPref;
    Preference updatePasswordPref, updateEmailPref, signOutPref;
    SwitchPreferenceCompat musicSwitchPref, soundEffectsSwitchPref;

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
            Preferences.setLocaleLanguage(language, getContext());
            redirectToMainActivity();
            return false;
        });

        musicSwitchPref.setOnPreferenceClickListener(preference -> {
            if (musicSwitchPref.isChecked()){
                Preferences.startMusicPlayerService(getActivity());
            }
            else{
                Preferences.stopMusicPlayerService(getActivity());
            }
            return false;
        });

//        //(to enable/disable se for all app)(not just custom se)(send broadcast)
//        soundEffectsSwitchPref.setOnPreferenceClickListener(preference -> {
//            if (soundEffectsSwitchPref.isChecked()){
//                //enable se
//            }
//            else{
//                //disable se
//            }
//            return false;
//        });
    }

    private void disableUpdatePasswordAndEmailIfSignedWithGoogle(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        List<? extends UserInfo> providerData = currentUser.getProviderData();
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
        getActivity().finishAffinity();
        Log.d("SettingsFragment", "onCreatePreferences: signOut");
    }

    private void redirectToMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finishAffinity();
        Log.d("SettingsFragment", "redirectToMainActivity");
    }

    private void initializeObjects(){
        languageListPref = getPreferenceManager().findPreference(getResources().getString(R.string.language_key));
        updatePasswordPref = getPreferenceManager().findPreference(getResources().getString(R.string.update_password_key));
        updateEmailPref = getPreferenceManager().findPreference(getResources().getString(R.string.update_email_key));
        signOutPref = getPreferenceManager().findPreference(getResources().getString(R.string.sign_out_key));
        musicSwitchPref = getPreferenceManager().findPreference(getResources().getString(R.string.music_key));
        soundEffectsSwitchPref = getPreferenceManager().findPreference(getResources().getString(R.string.sound_effects_key));
    }
}