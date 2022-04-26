package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private boolean isInputValid;
    private EditText emailET, usernameET, passwordET;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUIObjects();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Toast.makeText(Register.this, "ALREADY SIGNED IN!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view) {
        isInputValid = true;
        checkEditTextFields();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String username = usernameET.getText().toString().trim();
        if (isInputValid){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                            //redirect user to main page
                            //(pass the user object and username variable)
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, getResources().getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void registerWithGoogle(View view) {
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
        mAuth = FirebaseAuth.getInstance();
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Log.w("TAG", "Signed out!");
    }
}