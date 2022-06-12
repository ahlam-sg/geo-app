package com.example.geo_app;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.Objects;

public class MusicPlayerService extends Service {

    private static MediaPlayer mediaPlayer;
    private MusicStatusReceiver receiver = new MusicStatusReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
        this.registerReceiver(receiver, new IntentFilter(Constants.MUSIC_STATUS_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        this.unregisterReceiver(receiver);
    }

    private class MusicStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (Objects.equals(intentAction, Constants.MUSIC_STATUS_ACTION)) {
                String musicStatus = intent.getStringExtra(Constants.MUSIC_STATUS);
                if (Objects.equals(musicStatus, Constants.PAUSE_MUSIC)) {
                    mediaPlayer.pause();
                }
                else if (Objects.equals(musicStatus, Constants.RESUME_MUSIC)){
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);
                        mediaPlayer.setLooping(true);
                    }
                    mediaPlayer.start();
                }
                else if (mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        }
    }

    public static void startMusicPlayerService(Activity activity){
        activity.startService(new Intent(activity.getApplicationContext(), MusicPlayerService.class));
    }

    public static void stopMusicPlayerService(Activity activity){
        activity.stopService(new Intent(activity.getApplicationContext(), MusicPlayerService.class));
    }

    public static void sendMusicStatusBroadcast(Context context, String status) {
        Intent intent = new Intent(Constants.MUSIC_STATUS_ACTION);
        intent.putExtra(Constants.MUSIC_STATUS, status);
        context.sendBroadcast(intent);
    }
}
