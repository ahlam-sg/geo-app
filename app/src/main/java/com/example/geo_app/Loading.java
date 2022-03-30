package com.example.geo_app;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Loading extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.CATEGORY_KEY) == Constants.CATEGORY_CAPITAL){
            connectToDB();
            loadCountry();
        }

    }

    public void connectToDB(){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.DB_REFERENCE);
    }

    public void loadCountry(){
        databaseReference.child("SA").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Country country = dataSnapshot.getValue(Country.class);
                Log.d("YAY", "loadCountry:onDataChanged");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("NAY", "loadCountry:onCancelled", databaseError.toException());
            }
        });
    }



}