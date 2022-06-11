package com.example.geo_app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        sendMusicStatusBroadcast(Constants.RESUME_MUSIC);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        sendMusicStatusBroadcast(Constants.PAUSE_MUSIC);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void sendMusicStatusBroadcast(String status) {
        Intent intent = new Intent(Constants.MUSIC_STATUS_ACTION);
        intent.putExtra(Constants.MUSIC_STATUS, status);
        sendBroadcast(intent);
    }
}