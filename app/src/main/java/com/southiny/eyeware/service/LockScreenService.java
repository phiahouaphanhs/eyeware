package com.southiny.eyeware.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.southiny.eyeware.tool.AdminReceiver;
import com.southiny.eyeware.tool.Logger;

public class LockScreenService extends Service {
    public static final String TAG = LockScreenService.class.getSimpleName();
    public static final String INTENT_EXTRA_LOCK_UNLOCK_CODE = "lock_unlock_code";
    public static final int LOCK_CODE = 0;
    public static final int UNLOCK_CODE = 1;

    private int addDeviceAdminRemainingAttempt = 10;


    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public LockScreenService() { }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LockScreenService getService() {
            Logger.log(TAG, "localbinder getservice");
            // Return this instance of service so clients can call public methods
            return LockScreenService.this;
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

        int code = intent.getIntExtra(INTENT_EXTRA_LOCK_UNLOCK_CODE, -1);

        Logger.log(TAG, "code : " + code);
        switch (code) {
            case LOCK_CODE:
                Logger.log(TAG, "lock screen");
                // TODO : lock screen

                lockMeNow();

                //lock screen
                /*Run run = SQLRequest.getRun();
                if (run.isParentalControl()) {
                    lockScreen();
                } else {
                    Logger.log(TAG, "manage lock screen is off.");
                }*/

                break;

            case UNLOCK_CODE:
                Logger.log(TAG, "unlock screen");
                // TODO : unlock screen
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

      private void lockMeNow() {
        Logger.log(TAG, "lockMeNow()");
        addDeviceAdminRemainingAttempt--;

          ComponentName componentName = new ComponentName(this, AdminReceiver.class);
          DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

          assert devicePolicyManager != null;
          if (devicePolicyManager.isAdminActive(componentName)) {
            Logger.log(TAG, "admin is active");

            //Logger.log(TAG, "start lock phone activity");
            devicePolicyManager.lockNow();
              //Intent intent = new Intent(LockScreenService.this, LockPhoneActivity.class);
              //startActivity(intent);
        }
        else {
            Logger.log(TAG, "admin is not active");

            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "For lock screen");
            /*intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_lock_screen_explanation));*/
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            if (addDeviceAdminRemainingAttempt > 0) {
                Logger.log(TAG, "re-attempt locking screen");
                lockMeNow();
            }
        }
    }

    private void unlockMeNow() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "myApp:MyWakeLock");

        wakeLock.acquire();
    }





}
