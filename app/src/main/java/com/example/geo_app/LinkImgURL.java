package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private Uri flagURI;
    private String suffix="_flag.png";
    private ArrayList<String> codes = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Uri> flagsUri = new ArrayList<>();
    TextView urlTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_img_url);

        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.STORAGE_URL);
        StorageReference storageRef = storage.getReference();
        flagsRef = storageRef.child(Constants.FLAGS_REFERENCE);
        urlTV = findViewById(R.id.url_tv);

//        connectToDatabase();
//        readCodes();
//        setPaths();


    }

    public void addURL(View view){
        for (int i = 0; i < codes.size(); i++) {
            DatabaseReference ref = database.getReference().child(Constants.COUNTRIES_EN_REFERENCE).child(codes.get(i));
            Map<String, Object> updates = new HashMap<String,Object>();
            updates.put("flagURL", flagsUri.get(i));
            ref.updateChildren(updates);
        }
    }

    public void getURL(View view){
        flagsUri.clear();
        for (String path: paths){
            flagsRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    flagsUri.add(uri);
                    Log.d("LINK", "getURL: onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("LINK", "getURL: onFailure");
                }
            });
        }
        urlTV.setText(flagsUri.get(0).toString());
    }

    public void connectToDatabase(View view){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.COUNTRIES_EN_REFERENCE);
    }

    public void readCodes(View view){
        codes.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    codes.add(key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LINK", "readCodes: onCancelled", databaseError.toException());
            }
        });
    }

    public void setPaths(View view){
        paths.clear();
        for (String code: codes){
            paths.add( code + suffix );
        }
    }
}