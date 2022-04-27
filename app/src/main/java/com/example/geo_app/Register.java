package com.example.geo_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, usernameET, passwordET;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUIObjects();
        buildGoogleRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Toast.makeText(Register.this, "ALREADY SIGNED IN!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void registerWithEmail(View view) {
        isInputValid = true;
        checkEditTextFields();
//        String username = usernameET.getText().toString().trim();
        if (isInputValid){
            firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString().trim(), passwordET.getText().toString().trim())
                    .addOnCompleteListener(this, task -> {
                        try {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "createUserWithEmail:success");
//                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(Register.this, getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                //redirect user to main page
                                //(pass the user object and username variable)
                            }
                            else{
                                throw Objects.requireNonNull(task.getException());
                            }
                        }
                        catch(FirebaseAuthUserCollisionException e) {
                            Log.w("TAG", "createUserWithEmail:failure", e);
                            Toast.makeText(Register.this, getResources().getString(R.string.user_exist_error), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.w("TAG", "createUserWithEmail:failure", e);
                            Toast.makeText(Register.this, getResources().getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void registerWithGoogle(View view) {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, Constants.REQ_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQ_SIGN_IN){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }catch (Exception e){
                Log.w("TAG", "onActivityResult:failure" + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener(authResult -> {
                Log.w("TAG", "onSuccess: Register successful");

//                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                String userID = currentUser.getUid();
//                String userEmail = currentUser.getEmail();

                if (Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()){
                    Log.d("TAG", "onSuccess: New account created");
                    Toast.makeText(Register.this, getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("TAG", "onSuccess: Existing account");
                    Toast.makeText(Register.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> Log.w("TAG", "onFailure: Register failed" + e.getMessage()));
    }

    public void buildGoogleRequest(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
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
        if (isFieldEmpty(usernameET)){
            usernameET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        if (isFieldEmpty(passwordET)){
            passwordET.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else if(!isPasswordValid(passwordET)){
            passwordET.setError(getResources().getString(R.string.password_length_error));
            isInputValid = false;
        }
    }

    private boolean isEmailValid(EditText email) {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    private boolean isFieldEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private boolean isPasswordValid(EditText password){
        return (password.getText().toString().trim().length() >= 8);
    }

    private void setUIObjects(){
        emailET = findViewById(R.id.email_et);
        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Log.w("TAG", "Signed out!");
    }
}