package com.example.geo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public abstract class DeleteAccount {

    public static void showDeleteAccountDialog(Activity activity, Context context, String provider){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(R.string.confirm_delete_account);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.question_mark);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
            Log.d("DeleteAccountFragment", "showDeleteAccountDialog: PositiveButton");
            if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
                revokeAccessOfUserSignedInWithGoogle(context);
                signOutUserSignedInWithGoogle(context);
            }
            deleteUserData();
            deleteUserAccount(context);
            FirebaseAuth.getInstance().signOut();
            Handler handler = new Handler();
            handler.postDelayed(() -> startSignInActivity(activity), Constants.START_ACTIVITY_DELAY);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.w("DeleteAccountFragment", "showDeleteAccountDialog: NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void deleteUserData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        databaseReference.child(Objects.requireNonNull(user).getUid()).removeValue();
        Log.d("DeleteAccountFragment","deleteUserData");
    }

    private static void deleteUserAccount(Context context){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(user).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Dialogs.showSuccessMessageDialog(context, context.getResources().getString(R.string.delete_account_success));
                        Log.d("DeleteAccountFragment", "deleteUserAccount: successful");
                    }
                    else {
                        Log.d("DeleteAccountFragment", "deleteUserAccount: failed");
                    }
                });
    }

    private static void revokeAccessOfUserSignedInWithGoogle(Context context){
        GoogleSignIn.getClient(
                context,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).revokeAccess();
    }

    private static void signOutUserSignedInWithGoogle(Context context){
        GoogleSignIn.getClient(
                context,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();
    }

    private static void startSignInActivity(Activity activity){
        Intent intent = new Intent(activity, SignInActivity.class);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

}
