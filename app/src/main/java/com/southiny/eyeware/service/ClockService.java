package com.southiny.eyeware.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.southiny.eyeware.Constants;
import com.southiny.eyeware.Main2Activity;
import com.southiny.eyeware.NotificationScreen;
import com.southiny.eyeware.R;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;

import java.util.Objects;

public class ClockService extends Service {

    public static final String TAG = ClockService.class.getSimpleName();

    private Run run;
    private ProtectionMode pm;
    private ParentalControl pctrl;
    private long cptBreakSec, cptBluelightChangeSec, cptLockScreenSec, cptUnLockScreenSec;
    private boolean breakFinished = true;

    //private RunChangeListener runChangeListener;
    private UserInteractionReceiver userInteractionReceiver;
    private final Handler breakEveryHandler = new Handler();
    private final Handler changeBlueLightEveryHandler = new Handler();
    private final Handler nonInteractHandler = new Handler();
    private final Handler reInteractHandler = new Handler();

    private final Handler lockAndUnlockScreenHandler = new Handler();
    private boolean isLockScreenCountDownRunning = false;

    private boolean isRunningBreaking = false;
    private boolean isRunningBL = false;
    private boolean isRunningNonInteract = false;
    private boolean isRunningReInteract = false;

    public static final String INTENT_SET_FINISH_NOTIF = "set_finish_notif";


    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public ClockService() {
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public ClockService getService() {
            Logger.log(TAG, "localbinder getservice");
            // Return this instance of service so clients can call public methods
            return ClockService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.log(TAG, "onBind()");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log(TAG, "onCreate()");

        /****** FOREGROUND ************/


        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        createNotificationChannel();

        Notification notification =
                new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle("Breaking is counting down")
                        // .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_miracle_head2)
                        .setContentIntent(pendingIntent)
                        .setShowWhen(false) // to hide timestamp
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .build();

        startForeground(1, notification);


        /****** FOREGROUND ************/


        // start BL service
        Logger.log(TAG, "(start BL foreground) send intent to " + BlueLightFilterService.TAG);
        Intent intent = new Intent(this, BlueLightFilterService.class);
        startForegroundService(intent);

        // get run
        Logger.log(TAG, "get run ...");
        run = SQLRequest.getRun();
        Logger.log(TAG, "Run id : " + run.getId());

        pm = run.getCurrentProtectionMode();
        pctrl = run.getParentalControl();

        // register run change listener
        /*Logger.log(TAG, "register run change listener...");
        runChangeListener = new RunChangeListener();
        ReActiveAndroid.registerForModelChanges(Run.class, runChangeListener);
        Logger.log(TAG, "run change listener registered.");*/

        // register receiver for receiving user interaction
        Logger.log(TAG, "register receiver for receiving user interaction...");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_DREAMING_STARTED);
        intentFilter.addAction(Intent.ACTION_DREAMING_STOPPED);
        userInteractionReceiver = new UserInteractionReceiver();
        registerReceiver(userInteractionReceiver, intentFilter);
        Logger.log(TAG, "user interaction receiver registered.");

        // initialisation
        resetBreakingTimer();
        resetLockScreenCountDown();


        // start timers
        startBreakingTimer();
        startBLTimer();
        startLockScreenCountDown();
    }

    private final Runnable cptBreakRunnable = new Runnable() {
        private final String TAG = ClockService.TAG + " cptBreakRunnable";

        @Override
        public void run() {

            if (cptBreakSec > 0) {

                Logger.log(TAG, "break tick ! " + cptBreakSec);
                cptBreakSec -= 1;
                breakEveryHandler.postDelayed(this, 1000);

            } else if (cptBreakSec == 0) {

                BreakingMode mode = pm.getBreakingMode();

                // start breaking
                Intent intent;
                switch (mode) {
                    case LIGHT:
                        Logger.log(TAG, "Light : Push Notification...");
                        intent = new Intent(getApplicationContext(), NotificationService.class);
                        startService(intent);
                        break;

                    case MEDIUM:
                        Logger.log(TAG, "Medium : Start Notification Screen...");
                        intent = new Intent(ClockService.this, NotificationScreen.class);
                        startActivity(intent);
                        break;

                    case STRONG:
                        Logger.log(TAG, "Strong : Send intent to " + BlueLightFilterService.TAG);
                        intent = new Intent(getApplicationContext(), BlueLightFilterService.class);
                        intent.putExtra(BlueLightFilterService.INTENT_EXTRA_CODE, BlueLightFilterService.ADD_NOTIF_CODE);
                        startForegroundService(intent);
                        break;
                }

                breakFinished = false;

                // reset
                cptBreakSec = pm.getBreakingEvery_sec();
                isRunningBreaking = false;

                // post runnable
                final long delayMillis;
                if (mode == BreakingMode.MEDIUM) {
                    delayMillis = 1000;
                } else {
                    delayMillis = pm.getBreakingFor_sec() * 1000;
                }

                final Handler temporaryHandler = new Handler(); // pour traiter savoir exacter quand terminer le breaking
                temporaryHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (breakFinished) { // breakingFinished is setted to True by NotificationScreen
                            Logger.log(TAG, "break finished");
                            if (!isRunningNonInteract && !isRunningReInteract) {
                                startBreakingTimer();
                            } else {
                                Logger.log(TAG, "but, is running non-interact or re_interact, so no finish yet");
                                Logger.log(TAG, "non-in is " + isRunningNonInteract + " re_in is "+ isRunningReInteract);
                                temporaryHandler.postDelayed(this, 500);
                            }
                        } else {
                            Logger.log(TAG, "break not finished");
                            temporaryHandler.postDelayed(this, 500);
                        }

                    }
                }, delayMillis);

            } else {
                //error
                Logger.err(TAG, "cptBreakSec is less than 0, it is " + cptBreakSec);
            }
        };
    };

    private final Runnable cptBlueLightChangeRunnable = new Runnable() {
        private final String TAG = ClockService.TAG + " cptChangeBluelightRunnable";

        @Override
        public void run() {

            if (cptBluelightChangeSec > 0) {

                Logger.log(TAG, "BL tick ! " + cptBluelightChangeSec);
                cptBluelightChangeSec -= 1;

            } else if (cptBluelightChangeSec == 0) {

                // change blue light filter
                changeBlueLightFilter();

                // reset
                cptBluelightChangeSec = pm.getBlueLightFilterChangeEvery_sec();
            }

            // post runnable
            changeBlueLightEveryHandler.postDelayed(this, 1000);
        }
    };

    private Runnable cptLockAndUnlockScreenRunnable = new Runnable() {
        private final String TAG = ClockService.TAG + " cptLockUnLockRunnable";

        @Override
        public void run() {
            //if(!isLockScreen) {

                if (cptLockScreenSec > 0) {
                    Logger.log(TAG, "lock tick ! " + cptLockScreenSec);
                    cptLockScreenSec -= 1;
                    // post runnable
                    lockAndUnlockScreenHandler.postDelayed(this, 1000);

                } else if (cptLockScreenSec == 0) {

                    // lock screen
                    lockScreen();

                    // reset
                    resetLockScreenCountDown();
                    isLockScreenCountDownRunning = false;
                }

            //} else { // unlock

            //}
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(TAG, "onStartCommand()");

        boolean bool = intent.getBooleanExtra(INTENT_SET_FINISH_NOTIF, false);

        if (bool) setFinishedBreaking();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.log(TAG, "onDestroy()");

        stopBreakingTimer();
        stopBLTimer();
        stopLockScreenCountDown();

        // remove blue light filter
        removeBlueLightFilter();
        Logger.log(TAG, "blue light filter removed.");

        // unregister receivers
        unregisterReceiver(userInteractionReceiver);
        Logger.log(TAG, "user interaction receiver unregistered.");
        /*ReActiveAndroid.unregisterForModelStateChanges(Run.class, runChangeListener);
        Logger.log(TAG, "run change listener unregistered.");*/
    }

    /* ********* METHODS ***************/

    private void resetBreakingTimer() {
        Logger.log(TAG, "resetBreakingTimer()");
        cptBreakSec = pm.getBreakingEvery_sec();

        Logger.log(TAG, "break every " + cptBreakSec);
        Logger.log(TAG, "break for " + pm.getBreakingFor_sec());
    }

    private void resetBLTimer() {
        cptBluelightChangeSec = pm.getBlueLightFilterChangeEvery_sec();
        Logger.log(TAG, "change blue light every " + cptBluelightChangeSec);
    }

    private void resetLockScreenCountDown() {
        Logger.log(TAG, "reset lock screen cpt");
        cptLockScreenSec = pctrl.getLockScreenInSec();
        cptUnLockScreenSec = pctrl.getLockScreenInSec();
        Logger.log(TAG, "lock screen every " + cptLockScreenSec);
        Logger.log(TAG, "unlock screen every " + cptUnLockScreenSec);
    }

    private void startBreakingTimer() {
        Logger.log(TAG, "startBreakingTimer()");
        if (pm.isBreakingActivated() && !isRunningBreaking) {
            breakEveryHandler.post(cptBreakRunnable);
            isRunningBreaking = true;
        } else {
            Logger.log(TAG, "breaking is not activated or already running");
        }
    }

    private void startBLTimer() {
        Logger.log(TAG, "startBLTimer()");
        if (pm.isBluelightFiltering() && !isRunningBL) {
            changeBlueLightEveryHandler.post(cptBlueLightChangeRunnable);
            isRunningBL = true;
            changeBlueLightFilter();
        } else {
            Logger.log(TAG, "bluelight auto change is not activated or already running");
        }
    }

    private void startLockScreenCountDown() {
        if (pctrl.isLockScreenActivated() && !isLockScreenCountDownRunning) {
            Logger.log(TAG, "post lock screen handler");
            lockAndUnlockScreenHandler.post(cptLockAndUnlockScreenRunnable);
            isLockScreenCountDownRunning = true;
        } else {
            Logger.log(TAG, "lock screen runnable is already running");
        }
    }

    private void stopBreakingTimer() {
        if (isRunningBreaking) {
            breakEveryHandler.removeCallbacks(cptBreakRunnable);
            isRunningBreaking = false;
        } else {
            Logger.warn(TAG, "breaking runnable is not running");
        }
    }

    private void stopBLTimer() {
        if (isRunningBL) {
            changeBlueLightEveryHandler.removeCallbacks(cptBlueLightChangeRunnable);
            isRunningBL = false;
        } else {
            Logger.log(TAG, "BL runnable is not running");
        }
    }

    private void stopLockScreenCountDown() {
        if (isLockScreenCountDownRunning) {
            Logger.log(TAG, "remove lock screen runnable");
            lockAndUnlockScreenHandler.removeCallbacks(cptLockAndUnlockScreenRunnable);
            isLockScreenCountDownRunning = false;
        } else {
            Logger.log(TAG, "lock screen runnable is already stopped");
        }
    }


    private void changeBlueLightFilter() {
        Logger.log(TAG, "changeBlueLightFilter()");
        Logger.log(TAG, "send intent to " + BlueLightFilterService.TAG);
        Intent intent = new Intent(this, BlueLightFilterService.class);
        intent.putExtra(BlueLightFilterService.INTENT_EXTRA_CODE, BlueLightFilterService.ADD_CODE);
        //intent.putExtra(BlueLightFilterService.INTENT_EXTRA_LAYOUT_INDEX, nextFilterLayoutIndex);

        //startService(intent);
        startForegroundService(intent);

        //nextFilterLayoutIndex = (nextFilterLayoutIndex + 1) % BlueLightFilterService.layoutIDs.length;
    }

    private void removeBlueLightFilter() {
        Logger.log(TAG, "removeBlueLightFilter()");
        Logger.log(TAG, "send intent to " + BlueLightFilterService.TAG);
        Intent intent = new Intent(this, BlueLightFilterService.class);
        intent.putExtra(BlueLightFilterService.INTENT_EXTRA_CODE, BlueLightFilterService.REMOVE_AND_EXIT_CODE);
        startService(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    "No more eye strain notification channel",
                    NotificationManager.IMPORTANCE_MIN
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void lockScreen() {
        Logger.log(TAG, "lockScreen()");
        Intent intent = new Intent(ClockService.this, LockAndUnlockScreenService.class);
        intent.putExtra(LockAndUnlockScreenService.INTENT_EXTRA_LOCK_UNLOCK_CODE,
                LockAndUnlockScreenService.LOCK_CODE);
        startService(intent);
    }

    /* ******** PUBLIC METHODS TO BE CALLED BY EXTERNAL */

    public long getCptBreakSec() {
        return cptBreakSec;
    }

    public long getCptBLSec() {
        return cptBluelightChangeSec;
    }

    public long getCptLockScreenSec() {
        return cptLockScreenSec;
    }

    public void setFinishedBreaking() {
        Logger.log(TAG, "setFinishedBreaking()");
        breakFinished = true;
    }


    /* ********* BROADCAST RECEIVER ************/

    public class UserInteractionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.log(TAG, "onReceive() user interaction...");

            try {
                switch(Objects.requireNonNull(intent.getAction())){
                    case Intent.ACTION_SCREEN_OFF:
                        Logger.log(TAG, "action : screen off");
                        screenOffAction();
                        break;

                    case Intent.ACTION_SCREEN_ON:
                        Logger.log(TAG, "action : screen on");
                        screenOnAction();
                        break;

                    case Intent.ACTION_DREAMING_STARTED:
                        Logger.log(TAG, "action : dreaming start (~ screen off)");
                        screenOffAction();
                        break;

                    case Intent.ACTION_DREAMING_STOPPED:
                        Logger.log(TAG, "action : dreaming stop (~ screen on)");
                        screenOnAction();
                        break;

                    case Intent.ACTION_USER_PRESENT:
                        Logger.log(TAG, "action : user present");
                        break;

                    default:
                        Logger.warn(TAG, "action : default");
                        break;
                }
            } catch (NullPointerException e) {
                Logger.err(TAG, "Intent is null");
            }
        }

        private void screenOffAction() {
            Logger.log(TAG, "screenOffAction()");

            // stop timer
            if (run.isSmartDetectActivated()) {
                Logger.log(TAG, "smart detect on");
                stopBreakingTimer();
            }

            stopBLTimer();
            stopLockScreenCountDown();

            // put non-interact runnable
            if (! isRunningNonInteract) {
                Logger.log(TAG, "post delayed non-interact countdown runnable for " + Constants.DEFAULT_MAX_NON_INTERACT_MILLIS + "...");
                nonInteractHandler.postDelayed(nonInteractCountdownRunnable, Constants.DEFAULT_MAX_NON_INTERACT_MILLIS);
                isRunningNonInteract = true;
            } else {
                Logger.log(TAG, "non-interact handler already running");
            }

            // remove re-interact runnable
            if (isRunningReInteract) {
                Logger.log(TAG, "timer is stopped, remove re-interact countdown runnable...");
                reInteractHandler.removeCallbacks(reInteractCountdownRunnable);
                isRunningReInteract = false;
            } else {
                Logger.log(TAG, "re interact has no runnable");
            }
        }

        private void screenOnAction() {
            Logger.log(TAG, "screenOnAction()");

            //put re-interact runnable
            if (! isRunningReInteract) {
                Logger.log(TAG, "timer is stopped, post delayed re-interact countdown runnable for " + Constants.DEFAULT_MIN_INTERACT_MILLIS + "...");
                reInteractHandler.postDelayed(reInteractCountdownRunnable, Constants.DEFAULT_MIN_INTERACT_MILLIS);
                isRunningReInteract = true;
            }  else {
                Logger.log(TAG, "RE interact handler already running !!!!!!");
            }

            // remove non-interact runnable
            if (isRunningNonInteract) {
                Logger.log(TAG, "remove non-interact countdown runnable...");
                nonInteractHandler.removeCallbacks(nonInteractCountdownRunnable);
                isRunningNonInteract = false;
            }  else {
                Logger.log(TAG, "non interact has no runnable");
            }
        }

        private final Runnable nonInteractCountdownRunnable = new Runnable() {
            private final String TAG = "NON-InteractCountDownRunnable";
            @Override
            public void run() {
                if (run.isSmartDetectActivated()) {
                    Logger.log(TAG, "run() reset timer...");
                    Logger.log(TAG, "smart detect on");
                    resetBreakingTimer();
                    Logger.log(TAG, "timer reset.");
                }
                resetBLTimer();
                // reset lock screen count down
                resetLockScreenCountDown();

                isRunningNonInteract = false;
            }
        };

        private final Runnable reInteractCountdownRunnable = new Runnable() {
            private final String TAG = "RE-InteractCountDown";
            @Override
            public void run() {
                if (run.isSmartDetectActivated()) {
                    Logger.log(TAG, "smart detect on");
                    Logger.log(TAG, "run() restart timer...");
                    startBreakingTimer();
                }

                startBLTimer();
                startLockScreenCountDown();

                isRunningReInteract = false;
            }
        };
    }


    /* ********* LISTENERS *************/

    /**
     * Necessaire pour savoir quand le run.isTimerRunning est change
     */
    /*private class RunChangeListener implements OnModelChangedListener<Run> {

        @Override
        public void onModelChanged(@NonNull Run run, @NonNull ChangeAction action) {
            if (action == ChangeAction.UPDATE) {
                Logger.log(TAG, "onModelChange() \"Update\" Run id : " + run.getId());
                ClockService.this.run = run;
            }
        }
    }*/

}
