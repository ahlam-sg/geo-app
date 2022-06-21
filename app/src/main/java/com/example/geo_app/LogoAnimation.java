package com.example.geo_app;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.widget.ImageView;

public abstract class LogoAnimation {

    public static void infiniteUpscaling(Context context, ImageView logoIV){
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.infinite_upscale_animation);
        animatorSet.setTarget(logoIV);
        animatorSet.playSequentially();
        animatorSet.start();
    }
}
