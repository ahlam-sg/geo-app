package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, passwordET;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setUIObjects();
        buildGoogleSignInRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Toast.makeText(SignIn.this, "ALREADY SIGNED IN!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void signInWithEmail(View view) {
        isInputValid = true;
        checkEditTextFields();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        if (isInputValid){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmailAndPassword:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignIn.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                            //redirect user to main page
                            //(pass the user object and username variable)
                        } else {
                            Log.w("TAG", "signInWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(SignIn.this, getResources().getString(R.string.sign_in_fail), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void signInWithGoogle(View view) {
        displayGoogleSignInRequest();
    }

    public void forgotPassword(View view) {
    }

    public void register(View view) {
    }

    private void buildGoogleSignInRequest(){
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
    }

    private void displayGoogleSignInRequest(){
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), Constants.REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("TAG", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("TAG", e.getLocalizedMessage());
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Log.d("TAG", "Got ID token.");
                        firebaseAuthGoogleAccount(idToken);
                    } else if (password != null) {
                        // Got a saved username and password. Use them to authenticate
                        // with your backend.
                        Log.d("TAG", "Got password.");
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d("TAG", "One-tap dialog was closed.");
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d("TAG", "One-tap encountered a network error.");
                            // Try again or just ignore.
                            break;
                        default:
                            Log.d("TAG", "Couldn't get credential from result."
                                    + e.getLocalizedMessage());
                            break;
                    }
                }
            break;
        }
    }

    private void firebaseAuthGoogleAccount(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d("TAG", user.getDisplayName());
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }

    private void checkEditTextFields(){
        if (isFieldEmpty(emailET)){
            emailET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if (!isEmailValid(emailET)){
            emailET.setError(getResources().getString(R.string.email_error));
            isInputValid = false;
        }
        if (isFieldEmpty(passwordET)){
            passwordET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
    }

    private boolean isEmailValid(EditText email) {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    private boolean isFieldEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private void setUIObjects(){
        emailET = findViewById(R.id.email_et);
        passwordET = findViewById(R.id.password_et);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Log.w("TAG", "Signed out!");
    }
}