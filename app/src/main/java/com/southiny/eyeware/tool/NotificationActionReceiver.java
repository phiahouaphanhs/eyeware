package com.southiny.eyeware.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.southiny.eyeware.Main2Activity;
import com.southiny.eyeware.R;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.Scoring;
import com.southiny.eyeware.service.BlueLightFilterService;
import com.southiny.eyeware.service.ClockService;

import java.util.Calendar;

// for action button in notification
public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String TAG = NotificationActionReceiver.class.getSimpleName();

    public NotificationActionReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Logger.log(TAG, "stop code = " + String.valueOf(action));

        if (action != null) {
            if (action.equals(ClockService.INTENT_STOP_PROGRAM_ACTION)) {
                Logger.log(TAG, "stop " + ClockService.TAG);

                Intent in = new Intent(Main2Activity.currentInstance, ClockService.class);
                Main2Activity.currentInstance.stopService(in);

                Toast.makeText(Main2Activity.currentInstance, Main2Activity.currentInstance.getString(R.string.home_timer_stop_message), Toast.LENGTH_SHORT).show();

                Logger.log(TAG, "finished.");
                Main2Activity.currentInstance.finish();

            } else if (action.equals(BlueLightFilterService.INTENT_CHANGE_FILTER_ACTION)) {
                Logger.log(TAG, "Strong : Send intent to " + BlueLightFilterService.TAG);
                intent = new Intent(ClockService.currentInstance, BlueLightFilterService.class);
                intent.putExtra(BlueLightFilterService.INTENT_EXTRA_CODE, BlueLightFilterService.NEXT_CODE);
                ClockService.currentInstance.startForegroundService(intent);
            }
        }
    }
}
