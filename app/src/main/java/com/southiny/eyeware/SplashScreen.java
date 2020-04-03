package com.southiny.eyeware;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.Logger;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends Activity {

    public static final String TAG = SplashScreen.class.getSimpleName();

    public DatabaseConfig appDatabase = new DatabaseConfig.Builder(AppDatabase.class)
            .disableMigrationsChecking()
            .addModelClasses(Run.class, ProtectionMode.class, ScreenFilter.class, ParentalControl.class)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(TAG, "onCreate()");

        // init logger

        // create database
        Logger.log(TAG, "initialise database...");
        try {
            ReActiveAndroid.init(new ReActiveConfig.Builder(this)
            .addDatabaseConfigs(appDatabase)
            .build());
            Logger.log(TAG, "successfully initialise database");
        } catch (RuntimeException e) {
            Logger.err(TAG, "database changed, version must be incremented");
            e.printStackTrace();

            // switch to the most recent activity (other app)
            Logger.warn(TAG, "switch to the most recent app...");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        }

        // remove title bar
        Logger.log(TAG, "remove title bar...");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // remove notification bar
        Logger.log(TAG, "remove notification bar...");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content AFTER ABOVE sequence (to avoid crash)
        Logger.log(TAG, "set content view to overlay_view splash screen...");
        this.setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(TAG, "onStart()");

        // set splash time
        Logger.log(TAG, "post handler to be triggered at " + Constants.SPLASH_TIME + " ...");
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.log(TAG, "handler triggered");

                Run run = SQLRequest.getRun();
                Intent intent;

                ArrayList<ScreenFilter> scs = run.getCurrentProtectionMode().getActivatedScreenFiltersByOrder();
                for (int i = 0; i < scs.size(); i++) {
                    Logger.log(TAG, scs.get(i).print());
                }


                // start main activity
                if (isServiceRunning(ClockService.class.getName(), SplashScreen.this)) {
                  //if (run.isTimerRunning()) {
                    Logger.log(TAG, "ClockService is running !!!");

                    if (run.getParentalControl().isPasswordActivated()) {
                        Logger.log(TAG, "start " + PasswordActivity.class.getSimpleName() + "...");
                        intent = new Intent(SplashScreen.this, PasswordActivity.class);
                    } else {
                        Logger.log(TAG, "start " + Main2Activity.class.getSimpleName() + "...");
                        intent = new Intent(SplashScreen.this, Main2Activity.class);
                    }
                } else {
                    Logger.log(TAG, "ClockService is NOT running !!!");
                    Logger.log(TAG, "start " + MainActivity.class.getSimpleName() + "...");
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                }

                startActivity(intent);
                Logger.log(TAG, "finished.");
                finish();
            }
        }, Constants.SPLASH_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.log(TAG, "onPause()");
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
    }

    public static boolean isServiceRunning(String serviceName, Context context){
        boolean isRunning = false;

        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos;
        if (am != null) {
            runningServiceInfos = am.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos) {
                if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                    //service run in foreground
                    //isRunning = runningServiceInfo.foreground;
                    isRunning = true;
                }
            }
        } else {
            Logger.err(TAG, "activityManager is null");
        }

        return isRunning;
    }
}
