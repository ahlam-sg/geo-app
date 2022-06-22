package com.example.geo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class EmailVerification {

    public static void sendEmailVerification(Context context){
        String language = Preferences.getLanguagePreference(context);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode(language);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Dialogs.showSuccessMessageDialog(context, context.getResources().getString(R.string.email_verification));
                Log.d("TAG", "Verification email sent.");
                FirebaseAuth.getInstance().signOut();
            }
            else{
                Dialogs.showFailureMessageDialog(context, context.getResources().getString(R.string.email_verification_fail));
                Log.d("TAG", "Verification email was not sent.");
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    public static void checkIfEmailVerified(Activity activity){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finishAffinity();
        }
        else {
            Dialogs.showFailureMessageDialog(activity, activity.getResources().getString(R.string.email_not_verified));
            FirebaseAuth.getInstance().signOut();
        }
    }

}
