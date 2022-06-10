package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        disableUpdatePasswordAndEmailIfSignedWithGoogle();

        getPreferenceManager().findPreference("sign_out").setOnPreferenceClickListener(preference -> {
            signOut();
            return false;
        });
    }

    private void disableUpdatePasswordAndEmailIfSignedWithGoogle(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        List<? extends UserInfo> providerData = currentUser.getProviderData();
        String provider = providerData.get(1).getProviderId();
        if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
            getPreferenceManager().findPreference("update_password").setVisible(false);
            getPreferenceManager().findPreference("update_email").setVisible(false);
        }
        else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)){
            getPreferenceManager().findPreference("update_password").setVisible(true);
            getPreferenceManager().findPreference("update_email").setVisible(true);
        }
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), SignIn.class);
        startActivity(intent);
        getActivity().finishAffinity();
        Log.w("SettingsFragment", "onCreatePreferences: signOut");
    }
}