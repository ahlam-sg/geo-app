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
import java.util.Random;

public class testDB2 extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Country> countries = new ArrayList<>();
    ArrayList<Question> questions = new ArrayList<>();

    ArrayList<String> usedCodes = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    Question question = new Question();
    TextView code_tv, continent_tv, answer_tv, question_tv, option1_tv, option2_tv, option3_tv, option4_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db2);

        code_tv = findViewById(R.id.code_tv);
        continent_tv = findViewById(R.id.continent_tv);
        answer_tv = findViewById(R.id.answer_tv);
        question_tv = findViewById(R.id.question_tv);
        option1_tv = findViewById(R.id.option1_tv);
        option2_tv = findViewById(R.id.option2_tv);
        option3_tv = findViewById(R.id.option3_tv);
        option4_tv = findViewById(R.id.option4_tv);
    }


    public void connectToDBBtn(View view) {
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        databaseReference = database.getReference().child(Constants.DB_REFERENCE);
    }

    public void loadData(View view) {
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
                Log.d("NAY", "loadCountry:onCancelled", databaseError.toException());
            }
        });
    }

    public void prepareQuestion(){
        Random rand = new Random();
        int i = rand.nextInt(countries.size()-1);

        question.setCode(countries.get(i).getCode());
        question.setContinent(countries.get(i).getContinent_en());
        question.setAnswer(countries.get(i).getCountry_en());

        //put if to check for map, capital, flag
        question.setQuestion(countries.get(i).getCapital_en());

        while (options.size() < 4){
            i = rand.nextInt(countries.size()-1);
            usedCodes.add(countries.get(i).getCode());
            if (question.getCode() != countries.get(i).getCode() && usedCodes.contains(countries.get(i).getCode())){
                options.add(countries.get(i).getCountry_en());
                usedCodes.add(countries.get(i).getCode());
            }
        }

        question.setOption1(options.get(0));
        question.setOption2(options.get(1));
        question.setOption3(options.get(2));
        question.setOption4(question.getAnswer());

        options.clear();
        usedCodes.clear();
    }

    public void displayData(View view){
        prepareQuestion();
        code_tv.setText("code: "+ question.getCode());
        continent_tv.setText("continent: "+ question.getContinent());
        question_tv.setText("question: "+ question.getQuestion());
        answer_tv.setText("answer: "+ question.getAnswer());
        option1_tv.setText("op1: "+ question.getOption1());
        option2_tv.setText("op2: "+ question.getOption2());
        option3_tv.setText("op3: "+ question.getOption3());
        option4_tv.setText("op4: "+ question.getOption4());
    }


}