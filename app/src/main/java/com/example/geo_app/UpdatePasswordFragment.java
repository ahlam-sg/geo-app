package com.example.geo_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

public class UpdatePasswordFragment extends Fragment {
    private EditText currentPasswordET, newPasswordET;
    private TextInputLayout currentPasswordTIL, newPasswordTIL;
    private Button updatePasswordBtn;
    private FirebaseUser user;
    private boolean isInputValid;
    private Locale locale;

    public UpdatePasswordFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update_password, container, false);
        initializeObjects(rootView);

        updatePasswordBtn.setOnClickListener(view -> {
            isInputValid = true;
            checkUserInput();
            if(isInputValid){
                authenticateUser();
            }
        });

        return rootView;
    }

    private void initializeObjects(View rootView){
        currentPasswordET = rootView.findViewById(R.id.current_password_et);
        newPasswordET = rootView.findViewById(R.id.new_password_et);
        currentPasswordTIL = rootView.findViewById(R.id.current_password_til);
        newPasswordTIL = rootView.findViewById(R.id.new_password_til);
        updatePasswordBtn = rootView.findViewById(R.id.update_password_btn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        locale = Locale.forLanguageTag(Preferences.getLanguagePreference(getContext()));
        newPasswordET.setHint(String.format(locale, getResources().getString(R.string.password_criteria), Constants.PASSWORD_LENGTH_MINIMUM));
    }

    private void authenticateUser(){
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPasswordET.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("UpdatePasswordFragment", "User re-authenticated.");
                updateUserPassword();
            }
            else {
                currentPasswordTIL.setError(getResources().getString(R.string.current_password_incorrect));
            }
        });
    }

    private void updateUserPassword(){
        user.updatePassword(newPasswordET.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Dialogs.showSuccessMessageDialog(getContext(), getResources().getString(R.string.update_password_success));
                        Log.d("UpdatePasswordFragment", "updateUserPassword: successful");
                    }
                    else {
                        Log.d("UpdatePasswordFragment", "updateUserPassword: failed");
                    }
                });
    }

    private void checkUserInput(){
        if (TextUtils.isEmpty(currentPasswordET.getText().toString())){
            currentPasswordTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else{
            currentPasswordTIL.setError(null);
        }
        if (TextUtils.isEmpty(newPasswordET.getText().toString())){
            newPasswordTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if(newPasswordET.getText().toString().trim().length() < Constants.PASSWORD_LENGTH_MINIMUM){
            newPasswordTIL.setError(String.format(locale, getResources().getString(R.string.password_length_error), Constants.PASSWORD_LENGTH_MINIMUM));
            isInputValid = false;
        }
        else{
            newPasswordTIL.setError(null);
        }
    }
}