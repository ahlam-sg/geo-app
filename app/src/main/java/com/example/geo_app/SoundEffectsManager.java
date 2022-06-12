package com.example.geo_app;

import android.media.AudioAttributes;
import android.media.SoundPool;
import androidx.appcompat.app.AppCompatActivity;

public class SoundEffectsManager extends AppCompatActivity {

    protected static SoundPool soundPool;
    protected static int correctSoundID;
    protected static int incorrectSoundID;
    protected static int resultSoundID;
    protected static int hintSoundID;

    protected void setSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(2)
                .build();
        loadSoundsToSoundPool();
    }

    private void loadSoundsToSoundPool(){
        correctSoundID = soundPool.load(getApplicationContext(), R.raw.correct_sound_effect, 0);
        incorrectSoundID = soundPool.load(getApplicationContext(), R.raw.incorrect_sound_effect, 0);
        resultSoundID = soundPool.load(getApplicationContext(), R.raw.result_sound_effect, 2);
        hintSoundID = soundPool.load(getApplicationContext(), R.raw.hint_sound_effect, 1);
    }

    protected void playCorrectSoundEffect(){
        soundPool.play(correctSoundID, 1, 1, 0, 0, 1);
    }

    protected void playIncorrectSoundEffect(){
        soundPool.play(incorrectSoundID, 1, 1, 0, 0, 1);
    }

    protected void playResultSoundEffect(){
        soundPool.play(resultSoundID, 1, 1, 2, 0, 1);
    }

    protected void playHintSoundEffect(){
        soundPool.play(hintSoundID, 1, 1, 1, 0, 1);
    }

}
