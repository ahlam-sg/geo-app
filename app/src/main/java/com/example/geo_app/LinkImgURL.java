package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LinkImgURL extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference flagsRef;
    StorageReference storageRef;
    FirebaseStorage storage;


    TextView urlTV, codeTV, pathTV;
    ImageView imgIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_img_url);

        storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        storageRef = storage.getReference();
        flagsRef = storageRef.child(Constants.FLAGS_REFERENCE);
        urlTV = findViewById(R.id.url_tv);
        codeTV = findViewById(R.id.code_tv);
        pathTV = findViewById(R.id.path_tv);
        imgIV = findViewById(R.id.img_iv);


    }

    public void addImgURL(View view){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    storageRef.child("maps/" + data.getKey().toLowerCase() + "_map.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> map = new HashMap<String,Object>();
                            map.put("mapURL", uri.toString());
                            data.getRef().updateChildren(map);
                            Log.d("LINK", "addImgURL: onSuccess");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.d("LINK", "addImgURL: onFailure");
                        }
                    });
                }
                Log.d("LINK", "addImgURL: DATABASE updated successfully!!");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LINK", "addImgURL: onCancelled", databaseError.toException());
            }
        });
    }

    public void connectToDatabase(View view){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.COUNTRIES_AR_REFERENCE);
    }

    public void showImg(View view) {
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/geo-app-f2b0d.appspot.com/o/maps%2Fad_map.png?alt=media&token=bd053570-92af-4795-9bdd-9f7ceb05005f")
                .into(imgIV);
    }
}