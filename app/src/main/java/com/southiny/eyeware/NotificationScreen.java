package com.southiny.eyeware;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.Logger;

import static com.southiny.eyeware.tool.Utils.zbs;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NotificationScreen extends AppCompatActivity {

    public static final String TAG = NotificationScreen.class.getSimpleName();

    private TextView notificationSecondTextView;

    ClockService mService;
    boolean mBound = false;

    long currentBreakingTime_sec;

    Handler timerHandler = new Handler();

    private Runnable timerRunnable = new Runnable() {
        private final String TAG = NotificationScreen.TAG + "_UI_TimerCountdown";

        @Override
        public void run() {

            // set timer
            notificationSecondTextView.setText(zbs((int) currentBreakingTime_sec));
            Logger.log(this.TAG, "tick ! " + currentBreakingTime_sec);

            if (currentBreakingTime_sec > 0) {
                // re-post runnable
                currentBreakingTime_sec -= 1;
                timerHandler.postDelayed(this, 1000);
            } else {
                // stop
                stopUpdateTimer();
            }
        }
    };

    /*****/

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(TAG, "onCreate()");

        Logger.log(TAG, "set content view...");
        setContentView(R.layout.activity_notification_screen);

        // Bind to ClockService
        Logger.log(TAG, "bind to " + ClockService.TAG);
        Intent intent = new Intent(this, ClockService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        ProtectionMode pm = SQLRequest.getRun().getCurrentProtectionMode();
        currentBreakingTime_sec = pm.getBreakingFor_sec();

        Logger.log(TAG, "find view by id...");
        notificationSecondTextView = findViewById(R.id.notification_second);

        /*****/

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.wait_text);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        Button dummyButton = findViewById(R.id.dummy_button);
        dummyButton.setOnTouchListener(mDelayHideTouchListener);
        dummyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopUpdateTimer();
                timerHandler.removeCallbacks(timerRunnable);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /****/


    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.log(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.log(TAG, "onDestroy()");
        Logger.log(TAG, "unbind " + ClockService.TAG);
        unbindService(connection);
        mBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Logger.log(TAG, "onServiceConnected()");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ClockService.LocalBinder binder = (ClockService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Logger.log(TAG, ClockService.TAG + " bound.");

            updateTimerRealTime();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.log(TAG, "onServiceDisconnected()");
            mBound = false;
            Logger.log(TAG, ClockService.TAG + " unbound.");
        }
    };

    private void updateTimerRealTime() {
        Logger.log(TAG, "updateTimerRealTime()");
        // vibrate
        Logger.log(TAG, "vibrate the start");
        vibrate(Constants.DEFAULT_VIBRATION_DURATION_MILLIS);

        // post runnable
        Logger.log(TAG, "post UI timer runnable...");
        //currentBreakingTime_sec = mCService.getStartBreakingIn_sec();  // v2
        timerHandler.post(timerRunnable);
    }

    private void stopUpdateTimer() {
        Logger.log(TAG, "stopUpdateTimer()");
        // vibrate
        Logger.log(TAG, "vibrate the stop");
        vibrate(Constants.DEFAULT_VIBRATION_DURATION_MILLIS);

        // stop breaking
        mService.setFinishedBreaking();
        Logger.log(TAG, "finished.");
        finish();
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
