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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, usernameET, passwordET;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeObjects();
        buildGoogleRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Toast.makeText(SignUp.this, "ALREADY SIGNED IN!!!", Toast.LENGTH_SHORT).show();
        }
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
                                Toast.makeText(SignUp.this, getResources().getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                                writeNewUser();
                                redirectToMain();
                            }
                            else{
                                throw Objects.requireNonNull(task.getException());
                            }
                        }
                        catch(FirebaseAuthUserCollisionException e) {
                            Log.w("TAG", "createUserWithEmail:failure", e);
                            Toast.makeText(SignUp.this, getResources().getString(R.string.user_exist_error), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.w("TAG", "createUserWithEmail:failure", e);
                            Toast.makeText(SignUp.this, getResources().getString(R.string.sign_up_fail), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void signUpWithGoogle(View view){
        oneTapClient.beginSignIn(signUpRequest)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        Log.d("TAG", "Got ID token.");
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
                            break;
                        default:
                            Log.d("TAG", "Couldn't get credential from result." + e.getLocalizedMessage());
                            break;
                    }
                }
                break;
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
                        Toast.makeText(SignUp.this, getResources().getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                        writeNewUser();
                        redirectToMain();
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void redirectToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        if (TextUtils.isEmpty(usernameET.getText().toString())){
            usernameET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        if (TextUtils.isEmpty(passwordET.getText().toString())){
            passwordET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if(passwordET.getText().toString().trim().length() < 8){
            passwordET.setError(getResources().getString(R.string.password_length_error));
            isInputValid = false;
        }
    }

    private void writeNewUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        DatabaseReference databaseReference = database.getReference().child(Constants.USERS_REFERENCE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        List<? extends UserInfo> providerData = currentUser.getProviderData();
        String provider = providerData.get(1).getProviderId();
        User user = new User(provider);
        if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
            user.setUsername(currentUser.getDisplayName());
            user.setImageURL(currentUser.getPhotoUrl().toString());
        }
        else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)){
            user.setUsername(usernameET.getText().toString().trim());
        }
        databaseReference.child(currentUser.getUid()).setValue(user);
        Log.d("TAG", "writeNewUser:success");
    }

    private void initializeObjects(){
        emailET = findViewById(R.id.email_et);
        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Log.w("TAG", "Signed out!");
    }
}