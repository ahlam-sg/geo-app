package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Game extends AppCompatActivity {

    int score = 0;
    ImageView questionImage;
    TextView questionTV, hintTV, timerTV, pointsTV;
    Button option1Btn, option2Btn, option3Btn, option4Btn;

    Random rand = new Random();
    String category, correctAnswer, question, continent, code;
    ArrayList<Country> countries = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    ArrayList<String> optionsCodes = new ArrayList<>();
    ArrayList<Button> optionButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setUIObject();
        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        countries = (ArrayList<Country>)intent.getSerializableExtra(Constants.COUNTRIES_ARRAYLIST);
        startRound();
        startTimer();
    }

    public void startRound(){
        resetButtonsColors();
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
        setOptionsButtons();
        setQuestionView();
    }

    public void checkSelectedOption(View view){
        Button selectedButton = (Button)findViewById(view.getId());
        String selectedOption = selectedButton.getText().toString();
        //correct
        if (selectedOption == correctAnswer){
            blinkButton(selectedButton, R.color.green);
            score+=150;
            pointsTV.setText(score + "");
        }
        //incorrect
        else{
            blinkButton(selectedButton, R.color.red);
            blinkButton(getCorrectAnswerBtn(), R.color.green);
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> startRound(), 2000);
    }

    public void setOptionsButtons(){
        int i = 0;
        for (Button btn: optionButtons) {
            btn.setText(options.get(i));
            i++;
        }
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
        hintTV.setVisibility(View.INVISIBLE);
        switch (category){
            //capital
            case Constants.CATEGORY_CAPITAL:
                questionTV.setText(question);
                questionTV.setVisibility(View.VISIBLE);
                questionImage.setVisibility(View.INVISIBLE);
                break;
            //map or flag
            case Constants.CATEGORY_MAP:
            case Constants.CATEGORY_FLAG:
                // set image for questionImage
                questionImage.setVisibility(View.VISIBLE);
                questionTV.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void startTimer(){
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTV.setText(millisUntilFinished/1000 + "");
            }
            public void onFinish() {
                //end game
                //show score
            }
        }.start();
    }

    public void blinkButton(Button btn, int color){
        btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        btn.startAnimation(anim);
    }

    public Button getCorrectAnswerBtn(){
        Button correctBtn = optionButtons.get(0);
        for (Button btn: optionButtons) {
            if(btn.getText().equals(correctAnswer));
                correctBtn = findViewById(btn.getId());
        }
        return correctBtn;
    }

    public void resetButtonsColors(){
        for (Button btn: optionButtons) {
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        }
    }

    public void setUIObject(){
        option1Btn = findViewById(R.id.option1_btn);
        option2Btn = findViewById(R.id.option2_btn);
        option3Btn = findViewById(R.id.option3_btn);
        option4Btn = findViewById(R.id.option4_btn);
        optionButtons = new ArrayList<>(
                Arrays.asList(option1Btn, option2Btn, option3Btn, option4Btn));
        questionTV = findViewById(R.id.question_text);
        questionImage = findViewById(R.id.question_image);
        hintTV = findViewById(R.id.hint_text);
        timerTV = findViewById(R.id.timer);
        pointsTV = findViewById(R.id.points);
        pointsTV.setText(score + "");
    }

    public void hintBtn(View view) {
        hintTV.setVisibility(View.VISIBLE);
        String hint = String.format(getResources().getString(R.string.hint_text).toString(), continent);
        hintTV.setText(hint);
    }

    public void exitBtn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}