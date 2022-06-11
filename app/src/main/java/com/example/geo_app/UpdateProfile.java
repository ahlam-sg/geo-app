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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference profileStorageRef = storage.getReference().child(Constants.PROFILE_REFERENCE + currentUser.getUid());
        UploadTask uploadTask = profileStorageRef.putStream(inputStream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("ProfileActivity","uploadImageToStorage: onSuccess");
                updateUserImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ProfileActivity","uploadImageToStorage: onFailure");
            }
        });
    }

    private static void updateUserImage(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference profileStorageRef = storage.getReference().child(Constants.PROFILE_REFERENCE + currentUser.getUid());
        profileStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                databaseReference.child(currentUser.getUid()).child(Constants.IMAGE_URL_REFERENCE).setValue(uri.toString());
                Log.d("ProfileActivity","updateUserImage: onSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ProfileActivity","updateUserImage: onFailure");
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
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.w("TAG", "NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void deleteImageFromStorage(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference profileStorageRef = storage.getReference().child(Constants.PROFILE_REFERENCE + currentUser.getUid());
        profileStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("ProfileActivity","deleteImageFromStorage: onSuccess");
                deleteUserImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ProfileActivity","deleteImageFromStorage: onFailure");
            }
        });
    }

    private static void deleteUserImage(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        databaseReference.child(currentUser.getUid()).child(Constants.IMAGE_URL_REFERENCE).setValue("");
        Log.d("ProfileActivity","deleteUserImage");
    }


    public static void showUpdateUsernameDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.update_username_view, null);
        EditText newUsernameEt = dialogView.findViewById(R.id.new_username_et);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
            Log.w("TAG", "PositiveButton");
            String new_username = newUsernameEt.getText().toString();
            updateUsername(new_username);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.w("TAG", "NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void updateUsername(String new_username){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        databaseReference.child(currentUser.getUid()).child(Constants.USERNAME_REFERENCE).setValue(new_username);
    }

}
