package com.example.geo_app;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    ArrayList<Country> countries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        connectToDatabase();
        readCountries();

        Intent intentCategory = getIntent();
        Intent intentLoading = new Intent(this, Game.class);
        intentLoading.putExtra(Constants.CATEGORY_KEY, intentCategory.getStringExtra(Constants.CATEGORY_KEY));
        intentLoading.putExtra(Constants.COUNTRIES_ARRAYLIST, countries);
        startActivity(intentLoading);
    }

    public void connectToDatabase(){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        if (getLocaleLanguage() == "ar") {
            databaseReference = database.getReference().child(Constants.AR_DB_REFERENCE);
        }
        else{
            databaseReference = database.getReference().child(Constants.EN_DB_REFERENCE);
        }
    }

    public void readCountries(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    Country country = data.getValue(Country.class);
                    country.setCode(key);
                    countries.add(country);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "readCountries: onCancelled", databaseError.toException());
            }
        });
    }

    public String getLocaleLanguage(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String language = prefs.getString(Constants.LANGUAGE, "");
        return language;
    }

}