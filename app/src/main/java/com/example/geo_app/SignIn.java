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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, passwordET;
    private TextInputLayout emailTIL, passwordTIL;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String localeLanguage = Language.getLocaleLanguage(getApplicationContext());
        Language.setLocaleLanguage(localeLanguage, getBaseContext());
        setContentView(R.layout.activity_sign_in);

        initializeObjects();
        buildGoogleSignInRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            redirectToMain();
            Log.w("TAG", "Already signed in");
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
                            Toast.makeText(SignIn.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                            redirectToMain();
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
                .addOnFailureListener(this, e -> Log.d("TAG", e.getLocalizedMessage()));
    }

    public void forgotPassword(View view) {
        ResetPassword.showResetPasswordDialog(SignIn.this);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
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
        if (requestCode == Constants.REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    firebaseAuthGoogleAccount(idToken);
                }
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d("TAG", "One-tap dialog was closed.");
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
        }
    }

    private void firebaseAuthGoogleAccount(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        Toast.makeText(SignIn.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                        redirectToMain();
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void checkUserInput(){
        if (TextUtils.isEmpty(emailET.getText().toString())){
            emailTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if (!PatternsCompat.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()){
            emailTIL.setError(getResources().getString(R.string.email_error));
            isInputValid = false;
        }
        else{
            emailTIL.setError(null);
        }
        if (TextUtils.isEmpty(passwordET.getText().toString())){
            passwordTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else{
            passwordTIL.setError(null);
        }
    }

    private void initializeObjects(){
        emailET = findViewById(R.id.email_et);
        passwordET = findViewById(R.id.password_et);
        emailTIL = findViewById(R.id.email_til);
        passwordTIL = findViewById(R.id.password_til);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void redirectToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void setLanguage(View view) {
        TextView languageTV = findViewById(view.getId());
        if (languageTV.getText().equals(getString(R.string.english))) {
            Language.setLocaleLanguage("en", getBaseContext());
        }
        else{
            Language.setLocaleLanguage("ar", getBaseContext());
        }
        recreate();
    }

}