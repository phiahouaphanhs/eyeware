package com.southiny.eyeware;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.service.BlueLightFilterService;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;

import static com.southiny.eyeware.tool.Utils.zbs;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = Main2Activity.class.getSimpleName();

    private Run run;
    private ProtectionMode pm;
    private ParentalControl pctrl;

    private SeekBar dimBar, alphaBar;

    ClockService mCService;
    boolean mCBound = false;

    BlueLightFilterService mBLService;
    boolean mBLBound = false;

    private TextView breakMinuteTextView, breakSecondTextView,
            blMinuteTextView, blSecondTextView,
            lockScreenMinuteTextView, lockScreenSecondTextView;

    Handler updateTimerHandler = new Handler();

    // this is for update UI
    Runnable updateTimerRunnable = new Runnable() {
        private String TAG = Main2Activity.TAG + "updateTimer";
        @Override
        public void run() {
            Logger.log(TAG, "tick !");
            long time_sec = mCService.getCptBreakSec();
            int min = (int) (time_sec / 60);
            int sec = (int) (time_sec % 60);
            breakMinuteTextView.setText(zbs(min));
            breakSecondTextView.setText(zbs(sec));

            time_sec = mCService.getCptBLSec();
            min = (int) (time_sec / 60);
            sec = (int) (time_sec % 60);
            blMinuteTextView.setText(zbs(min));
            blSecondTextView.setText(zbs(sec));

            time_sec = mCService.getCptLockScreenSec();
            min = (int) (time_sec / 60);
            sec = (int) (time_sec % 60);
            lockScreenMinuteTextView.setText(zbs(min));
            lockScreenSecondTextView.setText(zbs(sec));

            updateTimerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Logger.log(TAG, "onCreate()");

        breakMinuteTextView = findViewById(R.id.timer_minute_count);
        breakSecondTextView = findViewById(R.id.timer_second_count);

        blMinuteTextView = findViewById(R.id.bl_minute_count);
        blSecondTextView = findViewById(R.id.bl_second_count);

        lockScreenMinuteTextView = findViewById(R.id.lockscreen_minute_count);
        lockScreenSecondTextView = findViewById(R.id.lockscreen_second_count);


        Logger.log(TAG, "set current protection level...");
        run = SQLRequest.getRun();
        pm = run.getCurrentProtectionMode();
        pctrl = run.getParentalControl();

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

        if (!pm.isBreakingActivated()) {
            ImageView checkedBreakingIcon = findViewById(R.id.icon_check_breaking);
            checkedBreakingIcon.setImageResource(R.drawable.ic_close_black_24dp);
        }

        if (!pm.isBluelightFiltering()) {
            ImageView checkedBLIcon = findViewById(R.id.icon_check_bluelight);
            checkedBLIcon.setImageResource(R.drawable.ic_close_black_24dp);
        }



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
                Toast.makeText(Main2Activity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Main2Activity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView vibrationIcon = findViewById(R.id.vibration_icon);
        if (!run.isVibrationActivated()) {
            vibrationIcon.setImageResource(R.drawable.ic_vibration_grey_24dp);
            vibrationIcon.setPadding(8,8,8,8);
        }
        // set on click to vibration icon
        vibrationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() vibration icon");
                run.setVibrationActivated(!run.isVibrationActivated());
                run.save();
                if (run.isVibrationActivated()) {
                    Toast.makeText(Main2Activity.this, "Vibration On", Toast.LENGTH_SHORT).show();
                    vibrationIcon.setImageResource(R.drawable.ic_vibration_white_24dp);
                    vibrationIcon.setPadding(8,8,8,8);
                } else {
                    Toast.makeText(Main2Activity.this, "Vibration Off", Toast.LENGTH_SHORT).show();
                    vibrationIcon.setImageResource(R.drawable.ic_vibration_grey_24dp);
                    vibrationIcon.setPadding(8,8,8,8);
                }
            }
        });

        ImageView breakingModeIcon = findViewById(R.id.breaking_mode_icon_started);
        int dw = R.drawable.ic_close_white_24dp;
        BreakingMode mode = pm.getBreakingMode();
        switch (mode) {
            case STRONG:
                dw = R.drawable.ic_camera_rear_white_24dp;
                breakingModeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = "Strong Discipline";
                        String message = "This will cover your screen with complete opaque filter " +
                                "during the break time, with no possibility to close the filter.\nThis is the forcing-cut mode.";
                        dialogBreakingModeInfo(title, message);
                    }
                });
                break;
            case MEDIUM:
                dw = R.drawable.ic_compare_black_24dp;
                breakingModeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = "Medium Discipline";
                        String message = "This will cut your screen with our closable notification screen " +
                                "during the break time.\nThis is the unforcing-cut mode.";
                        dialogBreakingModeInfo(title, message);
                    }
                });
                break;
            case LIGHT:
                dw = R.drawable.ic_notifications_white_24dp;
                breakingModeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = "Light Discipline";
                        String message = "This will notify with our floating notification when the break time starts, " +
                                "and notify again when the break time ends. Note that this mode will not work when 'Do Not Disturb' mode is turn on " +
                                "or phone notification has been deactivated.\nThis is the no-cut mode.";
                        dialogBreakingModeInfo(title, message);
                    }
                });
                break;
        }
        breakingModeIcon.setImageResource(dw);

        ImageView miracleIcon = findViewById(R.id.miracle_icon_in_clock);
        miracleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAboutApp();
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
        if (pm.isBreakingActivated() | pm.isBluelightFiltering() | pctrl.isLockScreenActivated()) {
            updateTimerHandler.post(updateTimerRunnable);
        }
    }

    private void stopUpdateTimer() {
        Logger.log(TAG, "stopUpdateTimer()");
        if (pm.isBreakingActivated()) {
            updateTimerHandler.removeCallbacks(updateTimerRunnable);
        }
    }

    private void dialogBreakingModeInfo(String title, String message) {
        Logger.log(TAG, "dialogBreakingModeInfo()");
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Alright", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void dialogAboutApp() {
        Logger.log(TAG, "dialogAboutApp()");
        new AlertDialog.Builder(this)
                .setTitle("About App")
                .setMessage("This app is developed for fighting against the world vision impairment.\n" +
                        "© 2020 - All right reserved.\n" +
                        getString(R.string.version_text))
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_miracle_head2)
                .show();
    }

}
