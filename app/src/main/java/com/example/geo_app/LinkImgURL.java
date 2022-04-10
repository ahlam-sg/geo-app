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

//    private ArrayList<String> codes = new ArrayList<>();
//    private ArrayList<String> paths = new ArrayList<>();
//    private ArrayList<String> flagsUri = new ArrayList<>();
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
                    storageRef.child("flags/" + data.getKey().toLowerCase() + "_flag.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> map = new HashMap<String,Object>();
                            map.put("flagURL", uri.toString());
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

//    public void addURL(View view){
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int counter = -1;
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    counter++;
//                    Map<String, Object> map = new HashMap<String,Object>();
//                    map.put("flagURL", flagsUri.get(counter));
//                    data.getRef().updateChildren(map);
//                }
//                Log.d("LINK", "addURL: updated successfully!!");
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("LINK", "addURL: onCancelled", databaseError.toException());
//            }
//        });
//    }

//    public void getURL(View view){
//        flagsUri.clear();
//        for (String path: paths){
//            storageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    flagsUri.add(uri.toString());
//                    Log.d("LINK", "getURL: onSuccess");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                    flagsUri.add(null);
//                    Log.d("LINK", "getURL: onFailure");
//                }
//            });
//        }
////        urlTV.setText(flagsUri.get(0).toString());
//    }

    public void connectToDatabase(View view){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.COUNTRIES_AR_REFERENCE);
    }

//    public void readCodes(View view){
//        codes.clear();
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    String key = data.getKey();
//                    codes.add(key);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("LINK", "readCodes: onCancelled", databaseError.toException());
//            }
//        });
//    }

//    public void setPaths(View view){
//        paths.clear();
//        for (String code: codes){
//            String s = "flags/" + code.toLowerCase() + "_flag.png";
//            paths.add(s);
//        }
//        urlTV.setText(paths.get(0));
//    }

//    public void display(View view){
//        codeTV.setText(codes.get(111));
//        pathTV.setText(paths.get(111));
//        urlTV.setText(flagsUri.get(111));
//        Log.d("LINK", "display: " + flagsUri.get(111).toString());
//    }

    public void showImg(View view) {
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/geo-app-f2b0d.appspot.com/o/flags%2Fae_flag.png?alt=media&token=3409b1d3-177c-433a-87a4-2a5e1d8818f4")
                .into(imgIV);
    }
}