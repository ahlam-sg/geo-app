package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Game extends AppCompatActivity {

    int score = 0, countCorrect = 0;
    boolean isExiting = false;
    ImageView questionImage;
    TextView questionTV, hintTV, timerTV, pointsTV;
    Button option1Btn, option2Btn, option3Btn, option4Btn;

    Random rand = new Random();
    String category, correctAnswer = "", selectedOption = "", question = "", continent = "", code = "";
    ArrayList<Country> countries = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    ArrayList<Button> optionButtons = new ArrayList<>();
    ArrayList<ReviewModel> reviewModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getIntentData();
        setUIObjects();
        startRound();
        startTimer();
        reviewModel.clear();
    }

    public void startRound(){
        setClickableButtons(true);
        resetButtonsColors();
        setQuestionInfo();
        setQuestionView();
        setOptionsArrayList();
        setOptionsButtons();
    }

    public void checkSelectedOption(View view){
        setClickableButtons(false);
        Button selectedButton = (Button)findViewById(view.getId());
        selectedOption = selectedButton.getText().toString();

        //correct
        if (selectedOption == correctAnswer){
            setBlinkColorButton(selectedButton, R.color.green);
            score+=150;
            pointsTV.setText(score + "");
            countCorrect++;
        }
        //incorrect
        else{
            setBlinkColorButton(selectedButton, R.color.red);
            setBlinkColorButton(getCorrectAnswerButton(), R.color.green);
        }

        setReviewModel(selectedButton);
        Handler handler = new Handler();
        handler.postDelayed(() -> startRound(), 1000);
    }

    private void setQuestionInfo(){
        int index = getRandomIndex();
        setQuestionBasedOnCategory(index);
        code = countries.get(index).getCode();
        continent = countries.get(index).getContinent();
        correctAnswer = countries.get(index).getCountry();
    }

    public void setReviewModel(Button button){
        ReviewModel rev = new ReviewModel();
        rev.setQuestion(question);
        rev.setCode(code);
        rev.setContinent(continent);
        rev.setOption1(options.get(0));
        rev.setOption2(options.get(1));
        rev.setOption3(options.get(2));
        rev.setOption4(options.get(3));
        rev.setSelectedOptionIndex(getOptionIndex(button));
        rev.setCorrectOptionIndex(getOptionIndex(getCorrectAnswerButton()));
        reviewModel.add(rev);
    }

    public int getOptionIndex(Button button){
        String optionIDName = getResources().getResourceEntryName(button.getId());
        for(int i=0; i<4; i++){
            if (optionIDName.contains(String.valueOf(i+1))){
                return i;
            }
        }
        return -1;
    }

    public void setOptionsButtons(){
        int index = 0;
        for (Button btn: optionButtons) {
            btn.setText(options.get(index));
            index++;
        }
    }

    public void setQuestionBasedOnCategory(int index){
        switch (category){
            case Constants.CATEGORY_CAPITAL:
                question = countries.get(index).getCapital();
                break;
//            case Constants.CATEGORY_MAP:
//                question = countries.get(index).getMap;
//                break;
            case Constants.CATEGORY_FLAG:
                question = countries.get(index).getFlagURL();
                break;
        }
        while(question.isEmpty()){
            index = getRandomIndex();
            setQuestionBasedOnCategory(index);
        }
    }

    public void setOptionsArrayList(){
        options.clear();
        options.add(correctAnswer);

        while (options.size() < 4){
            int index = rand.nextInt(countries.size()-1);
            if (!options.contains(countries.get(index).getCountry())){
                options.add(countries.get(index).getCountry());
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
                Glide.with(this)
                        .load(question)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(questionImage);
                break;
        }
    }

    public void startTimer(){
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTV.setText(millisUntilFinished/1000 + "");
            }
            public void onFinish() {
                //end game
                //show score
                if (!isExiting) {
                    startResultActivity();
                }
            }
        }.start();
    }

    public void setBlinkColorButton(Button btn, int color){
        btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        btn.startAnimation(anim);
    }

    public Button getCorrectAnswerButton(){
        Button correctBtn = optionButtons.get(0);
        for (Button btn: optionButtons) {
            if(btn.getText() == correctAnswer){
                correctBtn = findViewById(btn.getId());
                return correctBtn;
            }
        }
        return correctBtn;
    }

    public void resetButtonsColors(){
        for (Button btn: optionButtons) {
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        }
    }

    public void setClickableButtons(boolean state){
        for (Button btn: optionButtons) {
            btn.setClickable(state);
        }
    }

    private int getRandomIndex(){
        int index = rand.nextInt(countries.size()-1);
        return index;
    }

    private void setUIObjects(){
        option1Btn = findViewById(R.id.option1_btn);
        option2Btn = findViewById(R.id.option2_btn);
        option3Btn = findViewById(R.id.option3_btn);
        option4Btn = findViewById(R.id.option4_btn);
        optionButtons = new ArrayList<>(
                Arrays.asList(option1Btn, option2Btn, option3Btn, option4Btn));
        questionTV = findViewById(R.id.question_text);
        questionImage = findViewById(R.id.question_iv);
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
        isExiting = true;
        finish();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        countries = (ArrayList<Country>)intent.getSerializableExtra(Constants.COUNTRIES_ARRAYLIST);
    }

    public void startResultActivity(){
        Intent intent = new Intent(this, Result.class);
        intent.putExtra(Constants.CATEGORY_KEY, category);
        intent.putExtra(Constants.REVIEW_MODEL_ARRAYLIST, reviewModel);
        intent.putExtra(Constants.COUNT_CORRECT, countCorrect);
        intent.putExtra(Constants.SCORE, score);
        startActivity(intent);
        finish();
    }


}