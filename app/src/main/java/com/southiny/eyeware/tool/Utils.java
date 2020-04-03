package com.southiny.eyeware.tool;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.southiny.eyeware.R;

public final class Utils {

    public static String zbs(int value) {
        if (value >= 0 & value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    public static String getTransparencyCodeByAlpha(float alpha) {
        double a1 = Math.round(alpha * 100) / 100.0d;
        int a2 = (int) Math.round(a1 * 255);
        String hex = Integer.toHexString(a2).toUpperCase();
        if (hex.length() == 1) {
            hex = "0" + hex;
        }

        return hex;
    }

    public static void clockwise(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_round_fade);
        view.startAnimation(animation);
    }

    public static void clockwiseLeftRight(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_left_right);
        view.startAnimation(animation);
    }


    public static void clockwiseFast(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_fast);
        view.startAnimation(animation);
    }

    public static void zoom(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.my_animation);
        view.startAnimation(animation);
    }

    public static void fade(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.fade_click);
        view.startAnimation(animation);
    }

    public static void blinkblink(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.blink_blink);
        view.startAnimation(animation);
    }

    public static void blinkButterfly(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.blink_butterfly);
        view.startAnimation(animation);
    }

    public static void move(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move);
        view.startAnimation(animation);
    }

    public static void moveLeftRight(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_left_right);
        view.startAnimation(animation);
    }

    public static void moveTopDown(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_top_down);
        view.startAnimation(animation);
    }

    public static void slide(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.slide);
        view.startAnimation(animation);
    }
}
