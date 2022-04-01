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

import java.util.ArrayList;
import java.util.List;

public class Loading extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> keys = new ArrayList<>();
    ArrayList<Country> countries = new ArrayList<>();
    ArrayList<Question> questions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.CATEGORY_KEY) == Constants.CATEGORY_CAPITAL){

        }

    }

}