package com.example.geo_app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import java.util.ArrayList;


public abstract class OptionButtons {

    public static Button getCorrectButton(ArrayList<Button> buttons, String correctOption){
        for (Button btn: buttons) {
            if(btn.getText() == correctOption){
                return btn;
            }
        }
        return buttons.get(0);
    }

    public static int getButtonIndex(Button button, Context context){
        String optionIDName = context.getResources().getResourceEntryName(button.getId());
        for(int i=0; i<4; i++){
            if (optionIDName.contains(String.valueOf(i+1))){
                return i;
            }
        }
        return -1;
    }

    public static void setTextForButtons(ArrayList<Button> buttons, ArrayList<String> labels){
        int index = 0;
        for (Button btn: buttons) {
            btn.setText(labels.get(index));
            index++;
        }
    }

    public static void setClickableButtons(ArrayList<Button> buttons, boolean state) {
        for (Button btn : buttons) {
            btn.setClickable(state);
        }
    }

    public static void resetButtonsColors(ArrayList<Button> buttons, Context context){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        for (Button btn: buttons) {
            btn.setBackgroundTintList(ColorStateList.valueOf(typedValue.data));
        }
    }

    public static void setBlinkingButton(Button button, int color, Context context){
        button.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(color)));
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        button.startAnimation(anim);
    }

}
