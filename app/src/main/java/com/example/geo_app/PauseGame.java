package com.example.geo_app;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class PauseGame {

    public static FloatingActionButton resumeFAB;
    public static FloatingActionButton replayFAB;
    public static FloatingActionButton exitFAB;

    public static AlertDialog getPauseGameDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.pause_game_dialog_view, null);
        resumeFAB = dialogView.findViewById(R.id.resume_FAB);
        replayFAB = dialogView.findViewById(R.id.replay_FAB);
        exitFAB = dialogView.findViewById(R.id.exit_FAB);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
