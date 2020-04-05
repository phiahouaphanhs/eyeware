package com.southiny.eyeware.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.tool.Logger;

public class NotificationService extends Service {
    public static final String TAG = NotificationService.class.getSimpleName();

    public static final String INTENT_EXTRA_CODE = "extra_for_notification_service";
    public static final int INTENT_EXTRA_BREAK_NOTIF = 1;
    public static final int INTENT_EXTRA_LOCK_NOTIF = 2;
    public static final int INTENT_EXTRA_BREAK_NOTIF_SHORT = 3;

    private NotificationManager notificationManager;
    private Run run;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public NotificationService() { }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public NotificationService getService() {
            Logger.log(TAG, "localbinder getservice");
            // Return this instance of service so clients can call public methods
            return NotificationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.log(TAG, "onBind()");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(TAG, "onStartCommand()");

        run = SQLRequest.getRun();

        int code = intent.getIntExtra(INTENT_EXTRA_CODE, -1) ;

        if (code == INTENT_EXTRA_BREAK_NOTIF) {

            Logger.log(TAG, "push start notification");
            int breakingForSec = SQLRequest.getRun().getCurrentProtectionMode().getBreakingFor_sec();

            String title = "LOOK AWAY OR CLOSE EYES";
            // String message = getString(R.string.notification_message);
            String message = "If you care";
            createNotification(title, message);

            Logger.log(TAG, "post a runnable delay " + breakingForSec);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Logger.log(TAG, "push stop notification");
                    String title = "Thank !";
                    String message = "Good luck with the work";
                    createNotification(title, message);

                    Logger.log(TAG, "send intent to " + ClockService.TAG);
                    Intent intent = new Intent(getApplicationContext(), ClockService.class);
                    intent.putExtra(ClockService.INTENT_SET_FINISH_NOTIF, true);
                    startForegroundService(intent);

                    removeAllNotificationsIn4Sec();

                }
            }, breakingForSec * 1000);

        } else if (code == INTENT_EXTRA_LOCK_NOTIF) {
            String title = "20 SECONDS LEFT";
            // String message = getString(R.string.notification_message);
            String message = "Before the app lock your phone screen";
            createNotification(title, message);

            removeAllNotificationsIn4Sec();

        } else if (code == INTENT_EXTRA_BREAK_NOTIF_SHORT) {

            Logger.log(TAG, "push start notification");
            int breakingForSec = SQLRequest.getRun().getCurrentProtectionMode().getBreakingFor_sec();

            String title = "LOOK AWAY OR CLOSE EYES";
            String message = "If you care";
            createNotification(title, message);

            Logger.log(TAG, "post a runnable delay " + breakingForSec);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Logger.log(TAG, "push stop notification");
                    String title = "Thank !";
                    String message = "Good luck with the work";
                    createNotification(title, message);

                    removeAllNotificationsIn4Sec();

                }
            }, 4000);

        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void createNotification(String title, String message) {
        Logger.log(TAG, "createNotification()");
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Logger.log(TAG, "SDK >=");
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                if (run.isVibrationActivated()) {
                    mChannel.enableVibration(true);
                    //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mChannel.setVibrationPattern(new long[]{Constants.DEFAULT_VIBRATION_DURATION_MILLIS});
                }
                mChannel.setLightColor(Color.GREEN);

                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent = new Intent(this, NotificationService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(title)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setShowWhen(false);
            if(run.isVibrationActivated()) {
                builder.setVibrate(new long[]{Constants.DEFAULT_VIBRATION_DURATION_MILLIS});
            }
        } else {
            Logger.log(TAG, "SDK <");
            builder = new NotificationCompat.Builder(this);

            intent = new Intent(this, NotificationService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(title)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setShowWhen(false)
                    .setPriority(Notification.PRIORITY_HIGH);
            if (run.isVibrationActivated()) {
                builder.setVibrate(new long[]{Constants.DEFAULT_VIBRATION_DURATION_MILLIS});
            }

        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notificationManager.notify(NOTIFY_ID, notification);
    }

    private void removeAllNotificationsIn4Sec() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancelAll();
            }
        }, 4000);
    }
}
