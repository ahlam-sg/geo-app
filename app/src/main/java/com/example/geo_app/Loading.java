package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    }

    public void connectToDatabase(){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        //arabic
        if (getLocaleLanguage().equalsIgnoreCase("ar")) {
            databaseReference = database.getReference().child(Constants.COUNTRIES_AR_REFERENCE);
        }
        //english
        else{
            databaseReference = database.getReference().child(Constants.COUNTRIES_EN_REFERENCE);
        }
    }

    public void readCountries(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Country country = data.getValue(Country.class);
                    if (country != null) {
                        country.setCode(data.getKey());
                        countries.add(country);
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(() -> startGameActivity(), 1500);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Loading", "readCountries: onCancelled", databaseError.toException());
            }
        });
    }

    public String getLocaleLanguage(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return prefs.getString(Constants.LANGUAGE, "");
    }

    public void startGameActivity(){
        Intent intentLoading = new Intent(this, Game.class);
        intentLoading.putExtra(Constants.CATEGORY_KEY, getIntent().getStringExtra(Constants.CATEGORY_KEY));
        intentLoading.putExtra(Constants.COUNTRIES_ARRAYLIST, countries);
        startActivity(intentLoading);
        finish();
    }

}