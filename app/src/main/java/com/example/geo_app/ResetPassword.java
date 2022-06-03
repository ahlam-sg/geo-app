package com.example.geo_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.PatternsCompat;
import com.google.firebase.auth.FirebaseAuth;

public abstract class ResetPassword{

    public static void showResetPasswordDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.reset_password_view, null);
        EditText resetEmailET = dialogView.findViewById(R.id.reset_email_et);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.send, (dialog, whichButton) -> {
            Log.w("TAG", "PositiveButton");
            String email = resetEmailET.getText().toString();
            if(PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                sendPasswordResetEmail(email, context);
            }
            else{
                Dialogs.showFailureMessageDialog(context, context.getResources().getString(R.string.email_error));
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.w("TAG", "NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void sendPasswordResetEmail(String email, Context context){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Dialogs.showSuccessMessageDialog(context, context.getResources().getString(R.string.check_your_email));
                        Log.d("TAG", "Email sent.");
                    }
                    else{
                        Dialogs.showFailureMessageDialog(context, context.getResources().getString(R.string.no_account_with_email));
                        Log.d("TAG", "Email was not sent.");
                    }
                });
    }

}
