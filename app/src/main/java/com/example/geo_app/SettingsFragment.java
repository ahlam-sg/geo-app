package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.List;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ListPreference languageListPref;
    private Preference updatePasswordPref, updateEmailPref, signOutPref;
    private SwitchPreferenceCompat musicSwitchPref;
    private SwitchPreferenceCompat darkModeSwitchPref;
    private Preference deleteAccountPref;
    private String provider;
    private FirebaseUser user;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        initializeObjects();
        disableUpdatePasswordAndEmailIfSignedWithGoogle();

        signOutPref.setOnPreferenceClickListener(preference -> {
            signOut();
            return false;
        });

        deleteAccountPref.setOnPreferenceClickListener(preference -> {
            if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
                authenticateUserWithGoogleProvider();
            }
            else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)){
                DeleteAccountFragment fragment = new DeleteAccountFragment();
                FragmentTransaction transaction = requireFragmentManager().beginTransaction();
                transaction.replace(R.id.settings_frame_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            return false;
        });

        languageListPref.setOnPreferenceChangeListener((preference, newValue) -> {
            int selectedIndex = languageListPref.findIndexOfValue(newValue.toString());
            languageListPref.setValueIndex(selectedIndex);
            String language = newValue.toString();
            Preferences.setLocaleLanguage(language, requireContext());
            startSignInActivity();
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
            startSignInActivity();
            return false;
        });
    }

    private void disableUpdatePasswordAndEmailIfSignedWithGoogle(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        List<? extends UserInfo> providerData = Objects.requireNonNull(currentUser).getProviderData();
        provider = providerData.get(1).getProviderId();
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
        Log.d("SettingsFragment", "onCreatePreferences: signOut");
        startSignInActivity();
    }

    private void startSignInActivity(){
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        requireActivity().finishAffinity();
        Log.d("SettingsFragment", "redirectToSignInActivity");
    }

    private void initializeObjects(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        languageListPref = getPreferenceManager().findPreference(getResources().getString(R.string.language_key));
        updatePasswordPref = getPreferenceManager().findPreference(getResources().getString(R.string.update_password_key));
        updateEmailPref = getPreferenceManager().findPreference(getResources().getString(R.string.update_email_key));
        signOutPref = getPreferenceManager().findPreference(getResources().getString(R.string.sign_out_key));
        musicSwitchPref = getPreferenceManager().findPreference(getResources().getString(R.string.music_key));
        darkModeSwitchPref = getPreferenceManager().findPreference(getResources().getString(R.string.dark_mode_key));
        deleteAccountPref = getPreferenceManager().findPreference(getResources().getString(R.string.delete_account_key));
    }

    private void authenticateUserWithGoogleProvider(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(requireActivity(), gso).silentSignIn().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                AuthCredential credential = GoogleAuthProvider.getCredential(task.getResult().getIdToken(), null);
                user.reauthenticate(credential).addOnCompleteListener(authenticateTask -> {
                    if (authenticateTask.isSuccessful()) {
                        Log.d("DeleteAccountFragment", "authenticateUserWithGoogleProvider: User re-authenticated.");
                        DeleteAccount.showDeleteAccountDialog(requireActivity(), requireContext(), provider);
                    }
                    else {
                        Log.d("DeleteAccountFragment", "authenticateUserWithGoogleProvider: Failed to re-authenticate user.");
                    }
                });
            }
        });
    }
}