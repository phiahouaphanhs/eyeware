package com.southiny.eyeware.tool;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.southiny.eyeware.Main2Activity;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.Scoring;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String ACTION_ALARM_RECEIVER = "my_alarm_no_more_eye_strain";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Logger.log(TAG, "onReceive alarm");

        // reset today scoring and checkout value
        Logger.log(TAG, "set scoring to new day");
        SQLRequest.getRun().getScoring().newDay();

        if (Main2Activity.isActivityRunning) {
            Toast.makeText(context, "Start new day", Toast.LENGTH_SHORT).show();

        }
    }
}
