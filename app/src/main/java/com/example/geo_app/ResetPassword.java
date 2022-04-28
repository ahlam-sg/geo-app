package com.example.geo_app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.PatternsCompat;
import com.google.firebase.auth.FirebaseAuth;

public abstract class ResetPassword{

    public static void showResetPasswordDialog(Context context){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        EditText emailET = new EditText(context);
        emailET.setHint(R.string.enter_your_email);
        alert.setMessage(R.string.reset_password);
        alert.setView(emailET);
        alert.setPositiveButton(R.string.send, (dialog, whichButton) -> {
            Log.w("TAG", "PositiveButton");
            String email = emailET.getText().toString();
            if(PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                sendPasswordResetEmail(email, context);
            }
            else{
                Toast.makeText(context, context.getResources().getString(R.string.email_error), Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.w("TAG", "NegativeButton"));
        alert.show();
    }

    private static void sendPasswordResetEmail(String email, Context context){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showDoneDialog(context);
                        Log.d("TAG", "Email sent.");
                    }
                    else{
                        showFailedDialog(context);
                        Log.d("TAG", "Email was not sent.");
                    }
                });
    }

    private static void showDoneDialog(Context context){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.done);
        alert.setMessage(R.string.check_your_email);
        alert.setPositiveButton(R.string.ok, (dialog, whichButton) -> Log.w("TAG", "PositiveButton"));
        alert.show();
    }

    private static void showFailedDialog(Context context){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.failed_to_send);
        alert.setMessage(R.string.no_account_with_email);
        alert.setPositiveButton(R.string.ok, (dialog, whichButton) -> Log.w("TAG", "PositiveButton"));
        alert.show();
    }
}
