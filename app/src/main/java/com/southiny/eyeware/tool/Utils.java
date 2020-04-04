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

    public static void clockwiseLeftRightFade(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_left_right_fade);
        view.startAnimation(animation);
    }

    public static void clockwiseLeftRight(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_left_right);
        view.startAnimation(animation);
    }


    public static void clockwiseRoundFast(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_round_fast);
        view.startAnimation(animation);
    }

    public static void zoom(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.zoom);
        view.startAnimation(animation);
    }

    public static void fadeClick(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.fade_click);
        view.startAnimation(animation);
    }

    public static void blinkBlink(View view, Context context) {
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

    public static void moveRightLeft(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_right_left);
        view.startAnimation(animation);
    }

    public static void moveUp(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_up);
        view.startAnimation(animation);
    }

    public static void moveDown(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_down);
        view.startAnimation(animation);
    }

    public static void slide(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.slide);
        view.startAnimation(animation);
    }
}
