package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
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
    StorageReference storageRef;
    FirebaseStorage storage;

    private Uri flagURI;
    private String suffix="_flag.png";
    private ArrayList<String> codes = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Uri> flagsUri = new ArrayList<>();
    TextView urlTV, codeTV, pathTV;

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

//        connectToDatabase();
//        readCodes();
//        setPaths();


    }

    public void addURL(View view){
////        for (int i = 0; i < codes.size(); i++) {
//            DatabaseReference ref = database.getReference().child(Constants.COUNTRIES_EN_REFERENCE);
//            Map<String, Object> updates = new HashMap<String,Object>();
//            for (int i = 0; i < codes.size(); i++) {
//                updates.put("flag_url", flagsUri.get(i));
//            }
//            ref.updateChildren(updates);
////        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            int counter = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<String,Object>();
                    map.put("flag_url", flagsUri.get(counter).toString());
                    data.getRef().updateChildren(map);
                    counter++;
                }
                Log.d("LINK", "addURL: updated successfully!!");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LINK", "addURL: onCancelled", databaseError.toException());
            }
        });
    }

    public void getURL(View view){
        flagsUri.clear();
        for (String path: paths){
            storageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
//        urlTV.setText(flagsUri.get(0).toString());
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
            String s = "flags/" + code.toLowerCase() + "_flag.png";
            paths.add(s);
        }
        urlTV.setText(paths.get(0));
    }

    public void display(View view){
        codeTV.setText(codes.get(11));
        pathTV.setText(paths.get(11));
        urlTV.setText(flagsUri.get(11).toString());
        Log.d("LINK", "display: " + flagsUri.get(11).toString());
    }
}