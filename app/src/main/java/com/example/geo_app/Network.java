package com.example.geo_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;

import java.net.InetAddress;

public abstract class Network {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void showNetworkErrorDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(R.string.internet_not_connected);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.cross_mark);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.retry, (dialog, whichButton) -> {
            Log.d("Network", "showNetworkConnectionDialog: PositiveButton");
            if(!Network.isNetworkConnected(context)){
                showNetworkErrorDialog(context);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> {
            Log.w("Network", "showNetworkConnectionDialog: NegativeButton");
            System.exit(0);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
