package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TestDB extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> keys = new ArrayList<>();
    ArrayList<Country> countries = new ArrayList<>();
    ArrayList<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);
    }

    public void connectToDB(){
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.DB_REFERENCE);
    }

    public void loadData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //load four countries
                int count = 0;
                while (count < 4) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String key = dataSnapshot.getKey();
                        Country country = dataSnapshot.getValue(Country.class);
                        Log.i("!!!!!!!!!!Country: ", country.getCountry_en());
                        if (!keys.contains(key)) {
                            keys.add(key);
                            countries.add(country);
                            count++;
                        }
                    }
                }
                //prepare question
                String code = keys.get(0);
                String continent = countries.get(0).getContinent_en();
                String answer = countries.get(0).getCountry_en();
                String question = countries.get(0).getCapital_en();
                String option1 = countries.get(1).getCountry_en();
                String option2 = countries.get(2).getCountry_en();
                String option3 = countries.get(3).getCountry_en();
                Question questionObj = new Question(code, answer, question, continent);
                questionObj.setOption1(option1);
                questionObj.setOption2(option2);
                questionObj.setOption3(option3);

                countries.clear();
                keys.clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("NAY", "loadCountry:onCancelled", databaseError.toException());
            }
        });
    }
}