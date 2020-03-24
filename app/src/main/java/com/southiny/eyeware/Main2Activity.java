package com.southiny.eyeware;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.service.BlueLightFilterService;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;

import static com.southiny.eyeware.tool.Utils.zbs;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = Main2Activity.class.getSimpleName();

    //private Run run;
    private ProtectionMode pm;

    private SeekBar dimBar, alphaBar;

    ClockService mCService;
    boolean mCBound = false;

    BlueLightFilterService mBLService;
    boolean mBLBound = false;

    private TextView minuteTextView;
    private TextView secondTextView;

    Handler updateTimerHandler = new Handler();

    // this is for update UI
    Runnable updateTimerRunnable = new Runnable() {
        private String TAG = Main2Activity.TAG + "updateTimer";
        @Override
        public void run() {
            Logger.log(TAG, "tick !");
            long breakingTime_sec = mCService.getCptBreakSec();
            int min = (int) (breakingTime_sec / 60);
            int sec = (int) (breakingTime_sec % 60);
            minuteTextView.setText(zbs(min));
            secondTextView.setText(zbs(sec));

            updateTimerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Logger.log(TAG, "onCreate()");

        minuteTextView = findViewById(R.id.timer_minute_count);
        secondTextView = findViewById(R.id.timer_second_count);

        Logger.log(TAG, "set current protection level...");
        pm = SQLRequest.getRun().getCurrentProtectionMode();
        TextView plTextView = findViewById(R.id.current_protection_level_text_view);

        if (pm.isBreakingActivated()) {
            ProtectionLevel pl = pm.getProtectionLevel();
            plTextView.setText(pl.toString());
            Logger.log(TAG, "current protection level is " + pl.toString());
        } else {
            plTextView.setText("DESACTIVATED");
            Logger.log(TAG, "current protection level is deactivated");
        }

        Logger.log(TAG, "set on click to stop button");
        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() stop button");
                Logger.log(TAG, "stop " + ClockService.TAG);
                Intent intent = new Intent(Main2Activity.this, ClockService.class);
                stopService(intent);

                Logger.log(TAG, "start " + MainActivity.TAG);
                Intent in = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(in);

                Toast.makeText(Main2Activity.this, getString(R.string.home_timer_stop_message), Toast.LENGTH_SHORT).show();

                Logger.log(TAG, "finished.");
                finish();
            }
        });

        TextView thirdLineBreakingTextView = findViewById(R.id.thirdLine_breaking);
        String breakingThirdLine = "Break every " + (pm.getBreakingEvery_sec() / 60) + " minutes. Break for " + pm.getBreakingFor_sec() + " seconds each time.";
        thirdLineBreakingTextView.setText(breakingThirdLine);

        TextView thirdLineBluelightTextView = findViewById(R.id.thirdLine_bluelight);
        String bluelightThirdLine = "Change color every " + (pm.getBlueLightFilterChangeEvery_sec() / 60) + " minutes.";
        thirdLineBluelightTextView.setText(bluelightThirdLine);

        TextView exitAppTextView = findViewById(R.id.exit_message_text_view);
        exitAppTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // screen brightness
        dimBar = findViewById(R.id.brightness_level_seek_bar);
        dimBar.setEnabled(false);
        dimBar.setMax(Constants.DEFAULT_DIM_MAX_PERCENT);
        dimBar.setMin(Constants.DEFAULT_DIM_MIN_PERCENT);
        dimBar.setProgress(Constants.DEFAULT_DIM_MAX_PERCENT - (int)(pm.getDimAmount() * 100));
        dimBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Logger.log(TAG, "dim value is " + seekBar.getProgress());
                float dim = (float) (Constants.DEFAULT_DIM_MAX_PERCENT - seekBar.getProgress()) / 100F;
                pm.setDimAmount(dim);
                pm.save();
                mBLService.updateFilterParam();
            }
        });

        // filter transparency
        alphaBar = findViewById(R.id.transparency_level_seek_bar);
        alphaBar.setEnabled(false);
        alphaBar.setMax(Constants.DEFAULT_ALPHA_MAX_PERCENT);
        alphaBar.setMin(Constants.DEFAULT_ALPHA_MIN_PERCENT);
        alphaBar.setProgress(Constants.DEFAULT_ALPHA_MAX_PERCENT - (int)(pm.getScreenAlpha() * 100));
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Logger.log(TAG, "alpha value is " + seekBar.getProgress());
                float alpha = (float) (Constants.DEFAULT_ALPHA_MAX_PERCENT - seekBar.getProgress()) / 100F;
                pm.setScreenAlpha(alpha);
                pm.save();
                mBLService.updateFilterParam();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(TAG, "onStart()");

        // Bind to ClockService
        Logger.log(TAG, "bind to " + ClockService.TAG + "...");
        Intent intent = new Intent(this, ClockService.class);
        bindService(intent, cConnection, Context.BIND_AUTO_CREATE);

        // Bind to BLService
        Logger.log(TAG, "bind to " + BlueLightFilterService.TAG + "...");
        Intent in = new Intent(this, BlueLightFilterService.class);
        bindService(in, blConnection, Context.BIND_AUTO_CREATE);
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

        stopUpdateTimer();

        Logger.log(TAG, "unbind " + ClockService.TAG + "...");
        unbindService(cConnection);
        mCBound = false;

        Logger.log(TAG, "unbind " + BlueLightFilterService.TAG + "...");
        unbindService(blConnection);
        mBLBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection cConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Logger.log(TAG, "onServiceConnected() Clock");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ClockService.LocalBinder binder = (ClockService.LocalBinder) service;
            mCService = binder.getService();
            mCBound = true;
            Logger.log(TAG, ClockService.TAG + " bound.");

            updateTimerInRealTime();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.log(TAG, "onServiceDisconnected() Clock");
            mCBound = false;
            Logger.log(TAG, ClockService.TAG + " unbound.");

        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection blConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Logger.log(TAG, "onServiceConnected() BL");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BlueLightFilterService.LocalBinder binder = (BlueLightFilterService.LocalBinder) service;
            mBLService = binder.getService();
            mBLBound = true;
            Logger.log(TAG, BlueLightFilterService.TAG + " bound.");

            dimBar.setEnabled(true);
            alphaBar.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.log(TAG, "onServiceDisconnected() BL");
            mBLBound = false;
            Logger.log(TAG, BlueLightFilterService.TAG + " unbound.");

        }
    };

    /**
     * pre-require : activity is bound to Watch service
     */
    private void updateTimerInRealTime() {
        Logger.log(TAG, "updateTimerInRealTime()");
        if (pm.isBreakingActivated()) {
            updateTimerHandler.post(updateTimerRunnable);
        }
    }

    private void stopUpdateTimer() {
        Logger.log(TAG, "stopUpdateTimer()");
        if (pm.isBreakingActivated()) {
            updateTimerHandler.removeCallbacks(updateTimerRunnable);
        }
    }

}
