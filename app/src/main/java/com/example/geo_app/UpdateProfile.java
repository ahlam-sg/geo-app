package com.example.geo_app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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
                Log.d("Profile","updateImage: onSuccess");
                updateUserImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Profile","updateImage: onFailure");
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
                Log.d("Profile","linkImageToUser: onSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Profile","linkImageToUser: onFailure");
            }
        });
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
