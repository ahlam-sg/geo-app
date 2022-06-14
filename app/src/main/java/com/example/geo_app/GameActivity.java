package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends SoundEffectsManager {

    private int score = 0, countCorrect = 0;
    private boolean isExiting = false;
    private ImageView questionImage;
    private TextView questionTV, hintTV, timerTV, pointsTV;
    private final Random rand = new Random();
    private String category, correctAnswer = "", question = "", continent = "", code = "";
    private ArrayList<CountryModel> countries = new ArrayList<>();
    private final ArrayList<String> optionLabels = new ArrayList<>();
    private ArrayList<Button> optionButtons = new ArrayList<>();
    private final ArrayList<ReviewModel> reviewModel = new ArrayList<>();
    private NumberFormat numFormat;
    private boolean soundEffectStatus;
    private long millisRemaining;
    private CountDownTimer timer;
    private AlertDialog pauseGameAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setSoundPool();
        getIntentData();
        initializeObjects();
        startRound();
        startTimer(Constants.TIMER_VALUE);
        reviewModel.clear();
        setOnClickListenerForPauseGameFAB();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayerService.sendMusicStatusBroadcast(this, Constants.PAUSE_MUSIC);
    }

    private void startRound(){
        OptionButtons.setClickableButtons(optionButtons, true);
        OptionButtons.resetButtonsColors(optionButtons, getApplicationContext());
        setQuestionInfo();
        setQuestionView();
        setOptionLabelsArrayList();
        OptionButtons.setTextForButtons(optionButtons, optionLabels);
    }

    public void checkSelectedOption(View view){
        OptionButtons.setClickableButtons(optionButtons, false);
        Button selectedButton = findViewById(view.getId());
        String selectedButtonLabel = selectedButton.getText().toString();

        if (selectedButtonLabel.equalsIgnoreCase(correctAnswer)){
            OptionButtons.setBlinkingButton(selectedButton, R.color.green, getApplicationContext());
            if (soundEffectStatus){
                playCorrectSoundEffect();
            }
            score+=150;
            pointsTV.setText(numFormat.format(score));
            countCorrect++;
        }
        else{
            OptionButtons.setBlinkingButton(selectedButton, R.color.red, getApplicationContext());
            OptionButtons.setBlinkingButton(OptionButtons.getCorrectButton(optionButtons, correctAnswer), R.color.green, getApplicationContext());
            if (soundEffectStatus){
                playIncorrectSoundEffect();
            }
        }

        setReviewModel(selectedButton);
        Handler handler = new Handler();
        handler.postDelayed(this::startRound, Constants.START_ROUND_DELAY);
    }

    private void setQuestionInfo(){
        int index = getRandomIndex();
        setQuestionBasedOnCategory(index);
        code = countries.get(index).getCode();
        continent = countries.get(index).getContinent();
        correctAnswer = countries.get(index).getCountry();
    }

    private void setReviewModel(Button button){
        ReviewModel rev = new ReviewModel();
        rev.setQuestion(question);
        rev.setCode(code);
        rev.setContinent(continent);
        rev.setOption1(optionLabels.get(0));
        rev.setOption2(optionLabels.get(1));
        rev.setOption3(optionLabels.get(2));
        rev.setOption4(optionLabels.get(3));
        rev.setSelectedOptionIndex(OptionButtons.getButtonIndex(button, getApplicationContext()));
        rev.setCorrectOptionIndex(OptionButtons.getButtonIndex(OptionButtons.getCorrectButton(optionButtons, correctAnswer), getApplicationContext()));
        reviewModel.add(rev);
    }

    private void setQuestionBasedOnCategory(int index){
        switch (category){
            case Constants.CATEGORY_CAPITAL:
                question = countries.get(index).getCapital();
                break;
            case Constants.CATEGORY_MAP:
                question = countries.get(index).getMapURL();
                break;
            case Constants.CATEGORY_FLAG:
                question = countries.get(index).getFlagURL();
                break;
        }
        while(question == null){
            index = getRandomIndex();
            setQuestionBasedOnCategory(index);
        }
    }

    private void setOptionLabelsArrayList(){
        optionLabels.clear();
        optionLabels.add(correctAnswer);

        while (optionLabels.size() < 4){
            int index = rand.nextInt(countries.size()-1);
            if (!optionLabels.contains(countries.get(index).getCountry())){
                optionLabels.add(countries.get(index).getCountry());
            }
        }
        Collections.shuffle(optionLabels);
    }

    private void setQuestionView(){
        hintTV.setVisibility(View.INVISIBLE);
        switch (category){
            case Constants.CATEGORY_CAPITAL:
                questionTV.setText(question);
                questionTV.setVisibility(View.VISIBLE);
                questionImage.setVisibility(View.INVISIBLE);
                break;
            case Constants.CATEGORY_MAP:
            case Constants.CATEGORY_FLAG:
                questionImage.setVisibility(View.VISIBLE);
                questionTV.setVisibility(View.INVISIBLE);
                Glide.with(this)
                        .load(question)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(questionImage);
                break;
        }
    }

    private void startTimer(long timerValue){
        timer = new CountDownTimer(timerValue, Constants.TIMER_COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                timerTV.setText(numFormat.format(millisUntilFinished/1000));
            }
            @Override
            public void onFinish() {
                if (!isExiting) {
                    if (soundEffectStatus){
                        playResultSoundEffect();
                    }
                    startResultActivity();
                }
            }
        }.start();
    }

    public void pauseGame(View view) {
        timer.cancel();
        pauseGameAlertDialog.show();
    }

    private void setOnClickListenerForPauseGameFAB(){
        PauseGame.resumeFAB.setOnClickListener(view -> {
            startTimer(millisRemaining);
            pauseGameAlertDialog.dismiss();
        });
        PauseGame.replayFAB.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra(Constants.CATEGORY_KEY, category);
            startActivity(intent);
            finish();
            pauseGameAlertDialog.dismiss();
        });
        PauseGame.exitFAB.setOnClickListener(view -> {
            isExiting = true;
            finish();
            pauseGameAlertDialog.dismiss();
        });
    }

    private int getRandomIndex(){
        return rand.nextInt(countries.size()-1);
    }

    private void initializeObjects(){
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);
        soundEffectStatus = Preferences.getSoundEffectPreference(getApplicationContext());
        Button option1Btn = findViewById(R.id.option1_btn);
        Button option2Btn = findViewById(R.id.option2_btn);
        Button option3Btn = findViewById(R.id.option3_btn);
        Button option4Btn = findViewById(R.id.option4_btn);
        optionButtons = new ArrayList<>(Arrays.asList(option1Btn, option2Btn, option3Btn, option4Btn));
        questionTV = findViewById(R.id.question_text);
        questionImage = findViewById(R.id.question_iv);
        hintTV = findViewById(R.id.hint_text);
        timerTV = findViewById(R.id.timer);
        pointsTV = findViewById(R.id.points);
        pointsTV.setText(numFormat.format(score));
        pauseGameAlertDialog = PauseGame.getPauseGameDialog(GameActivity.this);
    }

    private void getIntentData(){
        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.CATEGORY_KEY);
        countries = (ArrayList<CountryModel>)intent.getSerializableExtra(Constants.COUNTRIES_ARRAYLIST);
    }

    private void startResultActivity(){
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.CATEGORY_KEY, category);
        intent.putExtra(Constants.REVIEW_MODEL_ARRAYLIST, reviewModel);
        intent.putExtra(Constants.COUNT_CORRECT, countCorrect);
        intent.putExtra(Constants.SCORE, score);
        startActivity(intent);
        finish();
    }

    public void showHint(View view) {
        if (soundEffectStatus){
            playHintSoundEffect();
        }
        hintTV.setVisibility(View.VISIBLE);
        String hint = String.format(getResources().getString(R.string.hint_text), continent);
        hintTV.setText(hint);
    }

    @Override
    public void onBackPressed() {
        isExiting = true;
        super.onBackPressed();
    }
}