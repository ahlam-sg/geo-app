package com.example.geo_app;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.util.PatternsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, passwordET;
    private TextInputLayout emailTIL, passwordTIL;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        String language = Preferences.getLanguagePreference(getApplicationContext());
        Preferences.setLocaleLanguage(language, getBaseContext());
        Preferences.enableLightDarkMode(getApplicationContext());
        setStatusBarColor();
        setContentView(R.layout.activity_sign_in);

        boolean musicStatus = Preferences.getMusicPreference(getApplicationContext());
        if(musicStatus){
            MusicPlayerService.startMusicPlayerService(this);
        }

        initializeObjects();
        buildGoogleSignInRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startMainActivity();
            Log.w("TAG", "Already signed in");
        }
    }

    public void setStatusBarColor(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.background, typedValue, true);
        getWindow().setStatusBarColor(typedValue.data);
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
//                            Toast.makeText(SignInActivity.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                            EmailVerification.checkIfEmailVerified(SignInActivity.this);
//                            startMainActivity();
                        } else {
                            Log.w("TAG", "signInWithEmailAndPassword:failure", task.getException());
                            Dialogs.showFailureMessageDialog(SignInActivity.this, getResources().getString(R.string.sign_in_fail));
                        }
                    });
        }
    }

    public void signInWithGoogle(View view) {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), Constants.ONE_TAP_REQ,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("TAG", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> Log.d("TAG", e.getLocalizedMessage()));
    }

    public void forgotPassword(View view) {
        ResetPassword.showResetPasswordDialog(SignInActivity.this);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
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
        if (requestCode == Constants.ONE_TAP_REQ) {
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
                        Dialogs.showFailureMessageDialog(SignInActivity.this, getResources().getString(R.string.network_error));
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
//                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                        startMainActivity();
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

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void setLanguage(View view) {
        TextView languageTV = findViewById(view.getId());
        if (languageTV.getText().equals(getString(R.string.english))) {
            Preferences.setLocaleLanguage("en", getBaseContext());
            Preferences.setLanguagePreference("en", getBaseContext());
        }
        else{
            Preferences.setLocaleLanguage("ar", getBaseContext());
            Preferences.setLanguagePreference("ar", getBaseContext());
        }
        recreate();
    }
}