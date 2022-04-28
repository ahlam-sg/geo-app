package com.example.geo_app;

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
import com.google.firebase.auth.AuthCredential;
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

        initializeObjects();
        buildGoogleSignInRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(SignIn.this, "ALREADY SIGNED IN!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void signInWithEmail(View view) {
        isInputValid = true;
        checkUserInput();
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
                    Log.d("TAG", e.getLocalizedMessage());
                });
    }

    public void forgotPassword(View view) {
    }

    public void signUp(View view) {
    }

    private void buildGoogleSignInRequest(){
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        firebaseAuthGoogleAccount(idToken);
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d("TAG", "One-tap dialog was closed.");
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d("TAG", "One-tap encountered a network error.");
                            Toast.makeText(SignIn.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.d("TAG", "Couldn't get credential from result." + e.getLocalizedMessage());
                            break;
                    }
                }
            break;
        }
    }

    private void firebaseAuthGoogleAccount(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        Toast.makeText(SignIn.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void checkUserInput(){
        if (TextUtils.isEmpty(emailET.getText().toString())){
            emailET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if (!PatternsCompat.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()){
            emailET.setError(getResources().getString(R.string.email_error));
            isInputValid = false;
        }
        if (TextUtils.isEmpty(passwordET.getText().toString())){
            passwordET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
    }

    private void initializeObjects(){
        emailET = findViewById(R.id.email_et);
        passwordET = findViewById(R.id.password_et);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Log.w("TAG", "Signed out!");
    }
}