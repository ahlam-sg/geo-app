package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    Question questionObj = new Question();
    TextView code_tv, continent_tv, answer_tv, question_tv, option1_tv, option2_tv, option3_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        code_tv = findViewById(R.id.code_tv);
        continent_tv = findViewById(R.id.continent_tv);
        answer_tv = findViewById(R.id.answer_tv);
        question_tv = findViewById(R.id.question_tv);
        option1_tv = findViewById(R.id.option1_tv);
        option2_tv = findViewById(R.id.option2_tv);
        option3_tv = findViewById(R.id.option3_tv);

    }


    public void connectToDBBtn(View view) {
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.DB_REFERENCE);
    }

    public void loadData(View view) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //load four countries
                int count = 0;
                while (count < 4) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String key = data.getKey();
                        Country country = data.getValue(Country.class);
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

                questionObj.setCode(code);
                questionObj.setContinent(continent);
                questionObj.setQuestion(question);
                questionObj.setAnswer(answer);
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

    public void displayData(View view){
        code_tv.setText("code: "+ questionObj.getCode());
        continent_tv.setText("continent: "+questionObj.getContinent());
        question_tv.setText("question: "+questionObj.getQuestion());
        answer_tv.setText("answer: "+questionObj.getAnswer());
        option1_tv.setText("op1: "+questionObj.getOption1());
        option2_tv.setText("op2: "+questionObj.getOption2());
        option3_tv.setText("op3: "+questionObj.getOption3());
    }


}