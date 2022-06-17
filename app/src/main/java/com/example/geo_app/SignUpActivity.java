package com.example.geo_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, usernameET, passwordET;
    private TextInputLayout emailTIL, usernameTIL, passwordTIL;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_sign_up);

        initializeObjects();
        buildGoogleRequest();
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

    public void signUpWithEmail(View view) {
        isInputValid = true;
        checkUserInput();
        if (isInputValid){
            firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString().trim(), passwordET.getText().toString().trim())
                    .addOnCompleteListener(this, task -> {
                        try {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "createUserWithEmail:success");
                                writeNewUser();
                                EmailVerification.sendEmailVerification(SignUpActivity.this);
//                                Dialogs.showSuccessMessageDialog(SignUpActivity.this, getResources().getString(R.string.sign_up_success));
                                Handler handler = new Handler();
                                handler.postDelayed(this::startSignInActivity, 2000);
                            }
                            else{
                                throw Objects.requireNonNull(task.getException());
                            }
                        }
                        catch(FirebaseAuthUserCollisionException e) {
                            Log.w("TAG", "createUserWithEmail:failure", e);
                            Dialogs.showFailureMessageDialog(SignUpActivity.this, getResources().getString(R.string.user_exist_error));
                        } catch (Exception e) {
                            Log.w("TAG", "createUserWithEmail:failure", e);
                            Dialogs.showFailureMessageDialog(SignUpActivity.this, getResources().getString(R.string.sign_up_fail));
                        }
                    });
        }
    }


    public void signUpWithGoogle(View view){
        oneTapClient.beginSignIn(signUpRequest)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ONE_TAP_REQ) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    Log.d("TAG", "Got ID token.");
                    firebaseAuthGoogleAccount(idToken);
                }
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d("TAG", "One-tap dialog was closed.");
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        Log.d("TAG", "One-tap encountered a network error.");
                        break;
                    default:
                        Log.d("TAG", "Couldn't get credential from result." + e.getLocalizedMessage());
                        break;
                }
            }
        }
    }

    private void buildGoogleRequest(){
        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }

    private void firebaseAuthGoogleAccount(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()){
                            writeNewUser();
                            Dialogs.showSuccessMessageDialog(SignUpActivity.this, getResources().getString(R.string.sign_up_success));
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(this::startMainActivity, Constants.START_ACTIVITY_DELAY);
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
        else {
            emailTIL.setError(null);
        }
        if (TextUtils.isEmpty(usernameET.getText().toString())){
            usernameTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else {
            usernameTIL.setError(null);
        }
        if (TextUtils.isEmpty(passwordET.getText().toString())){
            passwordTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if(passwordET.getText().toString().trim().length() < Constants.PASSWORD_LENGTH_MINIMUM){
            passwordTIL.setError(String.format(locale, getResources().getString(R.string.password_length_error), Constants.PASSWORD_LENGTH_MINIMUM));
            isInputValid = false;
        }
        else {
            passwordTIL.setError(null);
        }
    }

    private void writeNewUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        DatabaseReference databaseReference = database.getReference().child(Constants.USERS_REFERENCE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        List<? extends UserInfo> providerData = Objects.requireNonNull(currentUser).getProviderData();
        String provider = providerData.get(1).getProviderId();
        UserModel newUser = new UserModel(provider);
        if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
            newUser.setUsername(currentUser.getDisplayName());
            newUser.setImageURL(Objects.requireNonNull(currentUser.getPhotoUrl()).toString());
        }
        else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)){
            newUser.setUsername(usernameET.getText().toString().trim());
        }
        databaseReference.child(currentUser.getUid()).setValue(newUser);
        Log.d("TAG", "writeNewUser:success");
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void initializeObjects(){
        emailET = findViewById(R.id.email_et);
        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
        emailTIL = findViewById(R.id.email_til);
        usernameTIL = findViewById(R.id.username_til);
        passwordTIL = findViewById(R.id.password_til);
        firebaseAuth = FirebaseAuth.getInstance();
        locale = Locale.forLanguageTag(Preferences.getLanguagePreference(this));
        passwordET.setHint(String.format(locale, getResources().getString(R.string.password_criteria), Constants.PASSWORD_LENGTH_MINIMUM));
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void startSignInActivity(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}