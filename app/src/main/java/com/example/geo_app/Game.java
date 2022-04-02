package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game extends AppCompatActivity {

    TextView timer;
    TextView points;

    ImageView questionImage;
    TextView questionText, hintText;
    Button option1Btn, option2Btn, option3Btn, option4Btn;

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


        option1Btn = findViewById(R.id.option1_btn);
        option2Btn = findViewById(R.id.option2_btn);
        option3Btn = findViewById(R.id.option3_btn);
        option4Btn = findViewById(R.id.option4_btn);
        questionText = findViewById(R.id.question_text);
        questionImage = findViewById(R.id.question_image);
        hintText = findViewById(R.id.hint_text);

        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        countries = (ArrayList<Country>)intent.getSerializableExtra(Constants.COUNTRIES_ARRAYLIST);

        prepareRound();
        setOptionsButtons();
        setQuestionView();
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
        option1Btn.setText(options.get(0));
        option2Btn.setText(options.get(1));
        option3Btn.setText(options.get(2));
        option4Btn.setText(options.get(3));
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

    public void setQuestionView(){
        hintText.setVisibility(View.INVISIBLE);
        switch (category){
            //capital
            case Constants.CATEGORY_CAPITAL:
                questionText.setText(question);
                questionText.setVisibility(View.VISIBLE);
                questionImage.setVisibility(View.INVISIBLE);
                break;
            //map or flag
            case Constants.CATEGORY_MAP:
            case Constants.CATEGORY_FLAG:
                // set image for questionImage
                questionImage.setVisibility(View.VISIBLE);
                questionText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void hintBtn(View view) {
        hintText.setVisibility(View.VISIBLE);
        String hint = String.format(getResources().getString(R.string.hint_text).toString(), continent);
        hintText.setText(hint);
    }

    public void exitBtn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}