package com.example.geo_app;

import android.os.Bundle;

import androidx.core.util.PatternsCompat;
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

import java.util.Objects;


public class UpdateEmailFragment extends Fragment {

    private EditText currentEmailET, newEmailET, passwordET;
    private TextInputLayout newEmailTIL, passwordTIL;
    private Button updateEmailBtn;
    private FirebaseUser user;
    private boolean isInputValid;

    public UpdateEmailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update_email, container, false);
        initializeObjects(rootView);

        currentEmailET.setText(user.getEmail());

        updateEmailBtn.setOnClickListener(view -> {
            isInputValid = true;
            checkUserInput();
            if(isInputValid){
                authenticateUser();
            }
        });
        return rootView;
    }

    private void authenticateUser(){
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), passwordET.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("UpdateEmailFragment", "User re-authenticated.");
                updateUserEmail();
            }
            else {
                passwordTIL.setError(getResources().getString(R.string.password_incorrect));
            }
        });
    }

    private void updateUserEmail(){
        user.verifyBeforeUpdateEmail(newEmailET.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Dialogs.showSuccessMessageDialog(getContext(), getResources().getString(R.string.email_verification));
                        Log.d("UpdateEmailFragment", "updateUserEmail: successful");
                    }
                    else {
                        Log.d("UpdateEmailFragment", "updateUserEmail: failed");
                    }
                });
    }

    private void checkUserInput(){
        if (TextUtils.isEmpty(newEmailET.getText().toString())){
            newEmailTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if (!PatternsCompat.EMAIL_ADDRESS.matcher(newEmailET.getText().toString()).matches()){
            newEmailTIL.setError(getResources().getString(R.string.email_error));
            isInputValid = false;
        }
        else {
            newEmailTIL.setError(null);
        }
        if (TextUtils.isEmpty(passwordET.getText().toString())){
            passwordTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else{
            passwordTIL.setError(null);
        }
    }

    private void initializeObjects(View rootView){
        currentEmailET = rootView.findViewById(R.id.current_email_et);
        newEmailET = rootView.findViewById(R.id.new_email_et);
        newEmailTIL = rootView.findViewById(R.id.new_email_til);
        passwordET = rootView.findViewById(R.id.password_et);
        passwordTIL = rootView.findViewById(R.id.password_til);
        updateEmailBtn = rootView.findViewById(R.id.update_email_btn);
        String language = Preferences.getLanguagePreference(requireContext());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode(language);
        user = firebaseAuth.getCurrentUser();
    }
}