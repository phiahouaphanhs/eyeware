package com.southiny.eyeware.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.southiny.eyeware.Constants;
import com.southiny.eyeware.Main2Activity;
import com.southiny.eyeware.R;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.tool.Logger;

import java.util.ArrayList;

public class BlueLightFilterService extends Service {
    public static final String TAG = BlueLightFilterService.class.getSimpleName();

    private View mOverlayView = null;

    public static final String INTENT_EXTRA_CODE = "extra_code";
    public static final int ADD_CODE = 1;
    public static final int REMOVE_AND_EXIT_CODE = 0;
    public static final int ADD_NOTIF_CODE = 2;

    private int currentFilterIndex = 0;

    private String currentFilterColorCode = "#000000"; // pas necessaire mais pour optimiser la performance
    private float currentFilterDimAmount = 0;
    private float currentFilterScreenAlpha = 0;

    public ArrayList<ScreenFilter> sfs;

    private boolean isOnNotification = false;
    private boolean hasFilterOn = false;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public BlueLightFilterService() {
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public BlueLightFilterService getService() {
            Logger.log(TAG, "localbinder getservice");
            // Return this instance of service so clients can call public methods
            return BlueLightFilterService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.log(TAG, "onBind()");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log(TAG, "onCreate()");

        ProtectionMode pm = SQLRequest.getRun().getCurrentProtectionMode();
        sfs = pm.getActivatedScreenFiltersByOrder();
        currentFilterIndex = sfs.size() - 1;

        /****** FOREGROUND ************/


        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle("Blue light filter on screen")
                        // .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_miracle_head2)
                        .setContentIntent(pendingIntent)
                        .setShowWhen(false) // to hide timestamp
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .build();

        startForeground(2, notification);


        /****** FOREGROUND ************/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(TAG, "onStartCommand()");

        int code = intent.getIntExtra(INTENT_EXTRA_CODE, -1);
        //int layoutIndex = intent.getIntExtra(INTENT_EXTRA_LAYOUT_INDEX, -1);


        Logger.log(TAG, "received code = " + code);

        switch (code) {
            case ADD_CODE:
                // change blue light filter color
                if (!isOnNotification && sfs.size() > 0) {
                    currentFilterIndex = (currentFilterIndex + 1) % sfs.size();
                    Logger.log(TAG, "current filter index " + currentFilterIndex);
                    removeFilter();
                    addFilter(sfs.get(currentFilterIndex));

                } else {
                    Logger.log(TAG, "is on notification");
                    if (isOnNotification) {
                        int sec = SQLRequest.getRun().getCurrentProtectionMode().getBreakingFor_sec();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            private int attempt = 0;
                            @Override
                            public void run() {
                                if (!isOnNotification) {
                                    currentFilterIndex = (currentFilterIndex + 1) % sfs.size();
                                    Logger.log(TAG, "current filter index " + currentFilterIndex);
                                    removeFilter();
                                    addFilter(sfs.get(currentFilterIndex));
                                } else {
                                    if (attempt < 10) {
                                        attempt++;
                                        handler.postDelayed(this, 1000);
                                    }
                                }
                            }
                        }, sec * 1000);
                    }

                }

                break;

            case ADD_NOTIF_CODE:
                removeFilter();
                addNotif();

                int breakingForSec = SQLRequest.getRun().getCurrentProtectionMode().getBreakingFor_sec();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Logger.log(TAG, "run() in runnable");
                        vibrate(Constants.DEFAULT_VIBRATION_DURATION_MILLIS);
                        removeFilter();
                        isOnNotification = false;
                        addFilter(sfs.get(currentFilterIndex));

                        Logger.log(TAG, "send intent to " + ClockService.TAG);
                        Intent intent = new Intent(getApplicationContext(), ClockService.class);
                        intent.putExtra(ClockService.INTENT_SET_FINISH_NOTIF, true);
                        startForegroundService(intent);

                    }
                }, breakingForSec * 1000);

                break;

            case REMOVE_AND_EXIT_CODE:
                removeFilter();
                stopSelf();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Logger.log(TAG, "onDestroy()");
        super.onDestroy();
    }

    /*** METHODS *****/

    private void overlay(final String colorCode, final float alpha, final float dim) {
        Logger.log(TAG, "overlay() change blue light filter " + colorCode);

        // create a handler to run on the main thread (otherwise error)
        /* Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {*/
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,

                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,

                PixelFormat.TRANSLUCENT);

        // An alpha value to apply to this entire window.
        // An alpha of 1.0 means fully opaque and 0.0 means fully transparent
        params.alpha = alpha;

        // When FLAG_DIM_BEHIND is set, this is the amount of dimming to apply.
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        params.dimAmount = dim;

        //params.buttonBrightness = 0.8F;

        // params.screenBrightness = 0.2F;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        mOverlayView = inflater.inflate(R.layout.screen_overlay, null);

        mOverlayView.setBackgroundColor(Color.parseColor(colorCode));
        currentFilterColorCode = colorCode;
        currentFilterDimAmount = dim;
        currentFilterScreenAlpha = alpha;

        mOverlayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        assert wm != null;
        try {
            wm.addView(mOverlayView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addNotif() {
        Logger.log(TAG, "addNotif()");

        if (Settings.canDrawOverlays(this)) {

            if (!hasFilterOn)  {
                vibrate(Constants.DEFAULT_VIBRATION_DURATION_MILLIS);
                overlay("#000000", 1.0F, 1.0F); // 0.6F = normal; 1.0F = block the screen
                isOnNotification = true;
                hasFilterOn = true;
            } else {
                Logger.warn(TAG, "already has filter on");
            }

        } else {
            Logger.err(TAG, "cannot add notif filter, no overlay permission.");
        }
    }

    private void addFilter(ScreenFilter sc) {
        addFilter(sc.getColorCode(), sc.getScreenAlpha(), sc.getDimAmount());
    }

    private void addFilter(String colorCode, float alpha, float dim) {
        Logger.log(TAG, "addFilter()");

        if (Settings.canDrawOverlays(this)) {

            if (!hasFilterOn) {
                overlay(colorCode, alpha, dim); // 0.6F = normal; 1.0F = block the screen
                hasFilterOn = true;
            } else {
                Logger.warn(TAG, "already has filter on");
            }


        } else {
            Logger.err(TAG, "cannot add filter, no overlay permission.");
        }
    }

    private void removeFilter() {
        Logger.log(TAG, "removeFilter()");

        if (Settings.canDrawOverlays(this)) {

            if (hasFilterOn) {
                if (mOverlayView != null) {
                    Logger.log(TAG, "mOverlayView is not null");
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

                    assert wm != null;
                    Logger.log(TAG, "removing...");
                    wm.removeViewImmediate(mOverlayView);
                    hasFilterOn = false;
                } else {
                    Logger.warn(TAG, "overlay view is null");
                }
            } else {
                Logger.log(TAG, "no filter on");
            }

        } else {
            Logger.err(TAG, "cannot remove filter, no overlay permission.");
        }
    }

    // save
    public void updateCurrentFilterDim(float dim) {
        Logger.log(TAG, "updateCurrentFilterDim()");
        removeFilter();
        addFilter(currentFilterColorCode, currentFilterScreenAlpha, dim);
        ScreenFilter sc = sfs.get(currentFilterIndex);
        sc.setDimAmount(dim);
        sc.save();
    }

    public void updateCurrentFilterAlpha(float alpha) {
        Logger.log(TAG, "updateCurrentFilterAlpha()");
        removeFilter();
        addFilter(currentFilterColorCode, alpha, currentFilterDimAmount);
        ScreenFilter sc = sfs.get(currentFilterIndex);
        sc.setScreenAlpha(alpha);
        sc.save();
    }

    public void updateCurrentFilter(int index) {
        Logger.log(TAG, "updateCurrentFilter()");
        removeFilter();
        addFilter(sfs.get(index));
        currentFilterIndex = index;
    }

    public float getCurrentFilterDim() {
        return currentFilterDimAmount;
    }

    public float getCurrentFilterAlpha() {
        return currentFilterScreenAlpha;
    }

    public String getCurrentFilterColor() {
        return currentFilterColorCode;
    }

    public int getCurrentFilterIndex() {
        return currentFilterIndex;
    }

    private void vibrate(long duration) {
        Logger.log(TAG, "vibrate()");
        Run run = SQLRequest.getRun();

        if (run.isVibrationActivated()) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duration);
            }
        } else {
            Logger.log(TAG, "vibration is off");
        }
    }
}
