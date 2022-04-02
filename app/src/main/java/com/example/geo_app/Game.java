package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Game extends AppCompatActivity {

    TextView timer;
    TextView points;
    TextView hint_text;
    Button option1_btn, option2_btn, option3_btn, option4_btn;

    Random rand = new Random();
    String category, correctAnswer, question, continent, code;
    ArrayList<Country> countries = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    ArrayList<String> optionsCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        timer = findViewById(R.id.timer);
        points = findViewById(R.id.points);
        hint_text = findViewById(R.id.hint_text);

        option1_btn = findViewById(R.id.option1_btn);
        option2_btn = findViewById(R.id.option2_btn);
        option3_btn = findViewById(R.id.option3_btn);
        option4_btn = findViewById(R.id.option4_btn);

        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        countries = (ArrayList<Country>)intent.getSerializableExtra(Constants.COUNTRIES_ARRAYLIST);

        prepareRound();
        setOptionsButtons();
    }


    public void prepareRound(){
        int i = rand.nextInt(countries.size()-1);
        setQuestion(i);
        while(question.isEmpty()){
            i = rand.nextInt(countries.size()-1);
            setQuestion(i);
        }
        code = countries.get(i).getCode();
        continent = countries.get(i).getContinent();
        correctAnswer = countries.get(i).getCountry();
        setOptions(i);
    }

    public void setOptionsButtons(){
        option1_btn.setText(options.get(0));
        option2_btn.setText(options.get(1));
        option3_btn.setText(options.get(2));
        option4_btn.setText(options.get(3));
    }

    public void setQuestion(int i){
        switch (category){
            case Constants.CATEGORY_CAPITAL:
                question = countries.get(i).getCapital();
                break;
//            case Constants.CATEGORY_MAP:
//                question = countries.get(i).getMap;
//                break;
//            case Constants.CATEGORY_FLAG:
//                question = countries.get(i).getFlag();
//                break;
        }
    }

    public void setOptions(int i){
        options.clear();
        optionsCodes.clear();
        options.add(correctAnswer);
        optionsCodes.add(code);

        while (options.size() < 4){
            i = rand.nextInt(countries.size()-1);
            if (!optionsCodes.contains(countries.get(i).getCode())){
                options.add(countries.get(i).getCountry());
                optionsCodes.add(countries.get(i).getCode());
            }
        }
        Collections.shuffle(options);
    }

    public void hintBtn(View view) {
    }

    public void exitBtn(View view) {
        finish();
    }



}