package com.example.geo_app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.airbnb.lottie.LottieAnimationView;

public abstract class Dialogs {

    public static void showFailureMessageDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(message);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.cross_mark);
        builder.setView(dialogView);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void showSuccessMessageDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(message);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.check_mark);
        builder.setView(dialogView);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}
