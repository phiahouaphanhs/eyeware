package com.southiny.eyeware.tool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.southiny.eyeware.Constants;
import com.southiny.eyeware.R;

import java.util.Calendar;
import java.util.Random;

public final class Utils {

    public static String zbs(int value) {
        if (value >= 0 & value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    public static String getStringDatetimeFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR) +
                " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
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

    public static long getBreakingPoints(BreakingMode bm, int breakEvery_sec, int breakFor_sec) {
        int unitScore = bm.getUnitScore();
        int percentDifference = breakFor_sec - (breakEvery_sec / 60);

        if (percentDifference < Constants.MAX_MINUS_SCORE_PERCENT) {
            percentDifference = Constants.MAX_MINUS_SCORE_PERCENT;
        }

        int plusOrMinusScore = (percentDifference * unitScore) / 100;

        return unitScore + plusOrMinusScore;
    }

    public static long getBluelightFilteringPoints(float dim, float alpha) {
        float percent = (dim + alpha) / 2F;

        long points = (long) (percent * Constants.DEFAULT_UNIT_SCORE_SCREEN_FILTER);

        return (points > 0) ? points : 1;
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

    public static void clockwiseRoundLeft(View view, Context context){
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.clockwise_left);
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

    public static void moveUpDownInfinite(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_up_down_infinite);
        view.startAnimation(animation);
    }

    public static void moveLeftRightInfinite(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_left_right_infinite);
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

    public static void moveUpLong(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.move_up_long_fast);
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

    public static double getColorTemperatureFromRGB(int[] rgb) {
        int R = rgb[0];
        int G = rgb[1];
        int B = rgb[2];

        double nominator = (0.23881* R) + (0.25499 * G) + (-0.58291 * B);

        double denominator = (0.11109 * R) + (-0.85406 * G) + (0.52289 * B);

        double n = nominator/ denominator;

        double CCT = (449 * Math.pow(n, 3)) + (3525 * Math.pow(n, 2)) + (6823.3 * n) + 5520.33;

        if (CCT < 1000) CCT = 1000;
        else if (CCT > 30000) CCT = 30000;

        return CCT;
    }

    public static String getColorTemperatureExplanation(int CCT) {

        //  1700 K	Match flame, low pressure sodium lamps (LPS/SOX)
        if (CCT >= 1600 && CCT <= 1750) {
            return "Match flame, low pressure sodium lamps (LPS/SOX)";
        }

        // 1850 K	Candle flame, sunset/sunrise
        if (CCT > 1750 && CCT <= 2000) {
            return "Candle flame, sunset/sunrise";
        }

//        2400 K	Standard incandescent lamps
        if (CCT > 2000 && CCT <= 2450) {
            return "Standard incandescent lamps";
        }

//        2550 K	Soft white incandescent lamps
        if (CCT > 2450 && CCT <= 2600) {
            return "Soft white incandescent lamps";
        }
//        2700 K	"Soft white" compact fluorescent and LED lamps
        if (CCT > 2600 && CCT <= 2800) {
            return "\"Soft white\" compact fluorescent and LED lamps";
        }
//        3000 K	Warm white compact fluorescent and LED lamps
        if (CCT > 2800 && CCT <= 3100) {
            return "Warm white compact fluorescent and LED lamps";
        }
//        3200 K	Studio lamps, photofloods, etc.
        if (CCT > 3100 && CCT <= 3270) {
            return "Studio lamps, photofloods, etc.";
        }
//        3350 K	Studio "CP" light
        if (CCT > 3270 && CCT <= 4000) {
            return "Studio \"CP\" light";
        }
//        5000 K	Horizon daylight
        if (CCT > 4000 && CCT <= 5200) {
            return "Horizon daylight";
        }
//        5500 – 6000 K	Vertical daylight, electronic flash
        if (CCT > 5200 && CCT <= 6100) {
            return "Vertical daylight, electronic flash";
        }
//        6200 K	Xenon short-arc lamp [2]
        if (CCT > 6100 && CCT <= 6350) {
            return "Xenon short-arc lamp";
        }
//        6500 K	Daylight, overcast
        if (CCT > 6350 && CCT <= 10000) {
            return "Daylight, overcast or LCD / CRT screen";
        }

//        15,000 – 27,000 K	Clear blue poleward sky
        if (CCT > 14000 && CCT <= 28000) {
            return "Clear blue poleward sky";
        }

        return "Mystery of nature";
    }

    public static long getDefaultMinScoreOfLevel(int level) {
        assert level > 1;

        if (level == 2) return Constants.DEFAULT_MIN_SCORE_OF_LEVEL_2;

        if (level == 3) return Constants.DEFAULT_MIN_SCORE_OF_LEVEL_3;

        if (level == 4) return Constants.DEFAULT_MIN_SCORE_OF_LEVEL_4;

        if (level == 5) return Constants.DEFAULT_MIN_SCORE_OF_LEVEL_5;

        if (level == 6) return Constants.DEFAULT_MIN_SCORE_OF_LEVEL_6;

        if (level == 7) return Constants.DEFAULT_MIN_SCORE_OF_LEVEL_7;

        return getDefaultMinScoreOfLevelAbove7(level);
    }

    // level > 7
    private static long getDefaultMinScoreOfLevelAbove7(int level) {
        return (int) Math.pow(Constants.LEVEL_SCORE_INCREASE_RATE, (level-7)) * Constants.DEFAULT_MIN_SCORE_OF_LEVEL_7;
    }

    public static long getReachLevelPoints(int level) {
        return Constants.AWARD_SCORE_LEVEL_UP_BASE * (level - 1);
    }

    public static long getRandomSurprisePoints() {
        Random random = new Random();
        return random.nextInt
                ((int) (Constants.AWARD_SCORE_SURPRISE_MAX + 1 - Constants.AWARD_SCORE_SURPRISE_MIN))
                + Constants.AWARD_SCORE_SURPRISE_MIN;
    }

    public static void setMidnightAlarmIfNotExist(Context context, int hour, int minute) {

        if (hasAlarm(context)) {
            Logger.log("Utils", "already has alarm, don't set it");
        } else {
            Logger.log("Utils", "no alarm yet, set it");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(AlarmReceiver.ACTION_ALARM_RECEIVER);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1001, intent, 0);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.

            assert alarmMgr != null;

            alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    public static void unSetMidNightAlarmIfExist(Context context) {
        if (hasAlarm(context)) {

            //and stopping
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);//the same as up
            intent.setAction(AlarmReceiver.ACTION_ALARM_RECEIVER);//the same as up
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);//the same as up
            assert alarmMgr != null;
            alarmMgr.cancel(pendingIntent);//important
            pendingIntent.cancel();//important

        } else {
            Logger.log("Utils", "alarm has never been set");
        }
    }


   /* public static void stopAlarm() {
        //starting
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), MyReceiver.class);
        intent.setAction(MyReceiver.ACTION_ALARM_RECEIVER);//my custom string action name
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);//used unique ID as 1001
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), aroundInterval, pendingIntent);//first start will start asap

//and stopping
        Intent intent = new Intent(getActivity(), MyReceiver.class);//the same as up
        intent.setAction(MyReceiver.ACTION_ALARM_RECEIVER);//the same as up
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);//the same as up
        alarmManager.cancel(pendingIntent);//important
        pendingIntent.cancel();//important

//checking if alarm is working with pendingIntent
        Intent intent = new Intent(getActivity(), MyReceiver.class);//the same as up
        intent.setAction(MyReceiver.ACTION_ALARM_RECEIVER);//the same as up
        boolean isWorking = (PendingIntent.getBroadcast(getActivity(), 1001, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.d(TAG, "alarm is " + (isWorking ? "" : "not") + " working...");
    }*/

    private static boolean hasAlarm(Context context) {
        //checking if alarm is working with pendingIntent
        Intent intent = new Intent(context, AlarmReceiver.class);//the same as up
        intent.setAction(AlarmReceiver.ACTION_ALARM_RECEIVER);//the same as up
        return (PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
    }

    public static void playClickSound(AudioManager audioManager) {
        audioManager.playSoundEffect(SoundEffectConstants.CLICK,1.0f);
    }

    public static void clickAnimate(View view, Context context) {
        Utils.fadeClick(view, context);
    }
}
