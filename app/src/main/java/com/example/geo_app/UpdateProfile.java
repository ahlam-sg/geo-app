package com.example.geo_app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public abstract class UpdateProfile {

    public static void uploadImageToStorage(InputStream inputStream){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference profileStorageRef = storage.getReference().child(Constants.PROFILE_REFERENCE + user.getUid());
        UploadTask uploadTask = profileStorageRef.putStream(inputStream);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d("UpdateProfile","uploadImageToStorage: onSuccess");
            updateUserImage();
        }).addOnFailureListener(e -> Log.d("UpdateProfile","uploadImageToStorage: onFailure"));
    }

    private static void updateUserImage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference profileStorageRef = storage.getReference().child(Constants.PROFILE_REFERENCE + user.getUid());
        profileStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            databaseReference.child(user.getUid()).child(Constants.IMAGE_URL_REFERENCE).setValue(uri.toString());
            Log.d("UpdateProfile","updateUserImage: onSuccess");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UpdateProfile","updateUserImage: onFailure");
            }
        });
    }

    public static void showDeleteImageDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(R.string.delete_image_message);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.question_mark);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
            deleteImageFromStorage();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.d("UpdateProfile", "NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void deleteImageFromStorage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference profileStorageRef = storage.getReference().child(Constants.PROFILE_REFERENCE + user.getUid());
        profileStorageRef.delete().addOnSuccessListener(unused -> {
            Log.d("UpdateProfile","deleteImageFromStorage: onSuccess");
            deleteUserImage();
        }).addOnFailureListener(e -> Log.d("UpdateProfile","deleteImageFromStorage: onFailure"));
    }

    private static void deleteUserImage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        databaseReference.child(user.getUid()).child(Constants.IMAGE_URL_REFERENCE).setValue("");
        Log.d("UpdateProfile","deleteUserImage");
    }


    public static void showUpdateUsernameDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.update_username_view, null);
        EditText newUsernameEt = dialogView.findViewById(R.id.new_username_et);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
            Log.d("UpdateProfile", "PositiveButton");
            String new_username = newUsernameEt.getText().toString();
            updateUsername(new_username);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.d("UpdateProfile", "NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void updateUsername(String new_username){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        databaseReference.child(user.getUid()).child(Constants.USERNAME_REFERENCE).setValue(new_username);
    }

}
