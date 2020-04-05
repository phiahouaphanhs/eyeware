package com.southiny.eyeware;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.Scoring;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.service.BlueLightFilterService;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.ColorFilterLinearLayout;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.Utils;

import java.util.ArrayList;

import static com.southiny.eyeware.tool.Utils.zbs;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = Main2Activity.class.getSimpleName();
    public static boolean isActivityRunning;

    private Run run;
    private ProtectionMode pm;
    private ParentalControl pctrl;
    private AudioManager audioManager;

    private SeekBar dimBar, alphaBar;

    ClockService mCService;
    boolean mCBound = false;

    BlueLightFilterService mBLService;
    boolean mBLBound = false;

    private TextView breakMinuteTextView, breakSecondTextView,
            blMinuteTextView, blSecondTextView,
            lockScreenMinuteTextView, lockScreenSecondTextView,
            todayScoreTextView, totalScoreTextView, levelTextView;

    private ImageView breakingModeIcon, vibrationIcon, miracleIcon;

    private ArrayList<ColorFilterLinearLayout> filterCards = new ArrayList<>();
    private int currentSelectedFilterIndex;

    Handler updateTimerHandler = new Handler();

    // this is for update UI
    Runnable updateTimerRunnable = new Runnable() {
        private String TAG = Main2Activity.TAG + "updateTimer";
        private int cpt = 0;
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
            updateProgressBarAndSelectedFilter();

            time_sec = mCService.getCptLockScreenSec();
            min = (int) (time_sec / 60);
            sec = (int) (time_sec % 60);
            lockScreenMinuteTextView.setText(zbs(min));
            lockScreenSecondTextView.setText(zbs(sec));

            cpt++;
            if (cpt % Constants.DEFAULT_EARN_SCREEN_FILTER_POINT_EVERY_SEC == 0) {
                updateScoring();
            }

            updateTimerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Logger.log(TAG, "onCreate()");

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        breakMinuteTextView = findViewById(R.id.timer_minute_count);
        breakSecondTextView = findViewById(R.id.timer_second_count);

        blMinuteTextView = findViewById(R.id.bl_minute_count);
        blSecondTextView = findViewById(R.id.bl_second_count);

        lockScreenMinuteTextView = findViewById(R.id.lockscreen_minute_count);
        lockScreenSecondTextView = findViewById(R.id.lockscreen_second_count);

        totalScoreTextView = findViewById(R.id.total_score_text);
        todayScoreTextView = findViewById(R.id.today_score);
        levelTextView = findViewById(R.id.score_level_text);

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        Utils.moveUp(mainLayout, getApplicationContext());


        /***************************/


        Logger.log(TAG, "set current protection level...");
        run = SQLRequest.getRun();
        pm = run.getCurrentProtectionMode();
        pctrl = run.getParentalControl();

        /**********/

        final TextView plTextView = findViewById(R.id.current_protection_level_text_view);
        if (pm.isBreakingActivated()) {
            plTextView.setText(pm.getName());
            Logger.log(TAG, "current protection level is " + pm.getName());
        } else {
            plTextView.setTextColor(getColor(R.color.eyeware_white));
            Logger.log(TAG, "current protection level is deactivated");
        }

        Logger.log(TAG, "set on click to stop button");
        final Button stopButton = findViewById(R.id.stop_button);
        Utils.blinkBlink(stopButton, getApplicationContext());
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.blinkButterfly(stopButton, getApplicationContext());
            }
        }, 3000);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() stop button");
                Logger.log(TAG, "stop " + ClockService.TAG);
                Utils.clickAnimate(view, getApplicationContext());
                Utils.playClickSound(audioManager);
                Intent intent = new Intent(Main2Activity.this, ClockService.class);
                stopService(intent);

                Toast.makeText(Main2Activity.this, getString(R.string.home_timer_stop_message), Toast.LENGTH_SHORT).show();

                intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);

                Logger.log(TAG, "finished.");
                finish();
            }
        });

        vibrationIcon = findViewById(R.id.vibration_icon);
        Utils.blinkBlink(vibrationIcon, getApplicationContext());
        if (!run.isVibrationActivated()) {
            vibrationIcon.setImageResource(R.drawable.ic_vibration_grey_24dp);
        }
        // set on click to vibration icon);
        vibrationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() vibration icon");
                Utils.clickAnimate(view, getApplicationContext());
                Utils.playClickSound(audioManager);
                run.setVibrationActivated(!run.isVibrationActivated());
                run.save();
                if (run.isVibrationActivated()) {
                    Toast.makeText(Main2Activity.this, "Vibration On", Toast.LENGTH_SHORT).show();
                    vibrationIcon.setImageResource(R.drawable.ic_vibration_white_24dp);
                } else {
                    Toast.makeText(Main2Activity.this, "Vibration Off", Toast.LENGTH_SHORT).show();
                    vibrationIcon.setImageResource(R.drawable.ic_vibration_grey_24dp);
                }
            }
        });

        breakingModeIcon = findViewById(R.id.breaking_mode_icon_started);
        Utils.blinkBlink(breakingModeIcon, getApplicationContext());
        int dw = R.drawable.ic_close_white_24dp;
        BreakingMode mode = pm.getBreakingMode();
        switch (mode) {
            case STRONG:
                dw = R.drawable.ic_bm_strong_white_24dp;
                breakingModeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.clickAnimate(view, getApplicationContext());
                        Utils.playClickSound(audioManager);
                        String title = "Strong Discipline (Force-Cut)";
                        String message = "This mode will cover your screen with complete opaque filter during the break time.\n" +
                                "(!) There's no possibility to close the filter until the break time is ended.";
                        dialogBreakingModeInfo(title, message, R.drawable.ic_bm_strong_accent_24dp);
                    }
                });
                break;
            case MEDIUM:
                dw = R.drawable.ic_bm_medium_white_24dp;
                breakingModeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.clickAnimate(view, getApplicationContext());
                        Utils.playClickSound(audioManager);
                        String title = "Medium Discipline (Unforce-Cut)";
                        String message = "This mode will cover your screen with a notification screen during the break time.\n" +
                                "(!) You can close the notification screen at anytime.";
                        dialogBreakingModeInfo(title, message, R.drawable.ic_bm_medium_accent_24dp);
                    }
                });
                break;
            case LIGHT:
                dw = R.drawable.ic_bm_light_white_24dp;
                breakingModeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.clickAnimate(view, getApplicationContext());
                        Utils.playClickSound(audioManager);
                        String title = "Light Discipline (No-Cut)";
                        String message = "This mode will notify you with our floating notification when the break time starts, " +
                                "and will notify you again when the break time ends.\n" +
                                "Note that this mode will not work when 'Do Not Disturb' is turn on " +
                                "or the notification has been deactivated.";
                        dialogBreakingModeInfo(title, message, R.drawable.ic_bm_light_accent_24dp);
                    }
                });
                break;
        }
        breakingModeIcon.setImageResource(dw);

        miracleIcon = findViewById(R.id.miracle_icon_in_clock);
        Utils.move(miracleIcon, getApplicationContext());
        miracleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.clickAnimate(view, getApplicationContext());
                Utils.playClickSound(audioManager);
                dialogAboutApp();
            }
        });

        if (pm.isBluelightFiltering()) {
            ImageView filterIcon = findViewById(R.id.filter_icon);
            filterIcon.setImageResource(R.drawable.ic_style_cream_24dp);
        }

        if (pctrl.isLockScreenActivated()) {
            ImageView lockIcon = findViewById(R.id.lock_icon);
            lockIcon.setImageResource(R.drawable.ic_lock_open_cream_24dp);
        }

        /***************************/

        addFilterCards();

        // screen brightness
        dimBar = findViewById(R.id.brightness_level_seek_bar);
        dimBar.setEnabled(false);
        dimBar.setMax(Constants.DEFAULT_DIM_MAX_PERCENT);
        dimBar.setMin(Constants.DEFAULT_DIM_MIN_PERCENT);
        dimBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Logger.log(TAG, "dim value is " + seekBar.getProgress());
                Utils.playClickSound(audioManager);
                float dim = (float) (Constants.DEFAULT_DIM_MAX_PERCENT - seekBar.getProgress()) / 100F;
                mBLService.updateCurrentFilterDim(dim);
                Toast.makeText(Main2Activity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        // filter transparency
        alphaBar = findViewById(R.id.transparency_level_seek_bar);
        alphaBar.setEnabled(false);
        alphaBar.setMax(Constants.DEFAULT_ALPHA_MAX_PERCENT);
        alphaBar.setMin(Constants.DEFAULT_ALPHA_MIN_PERCENT);
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.playClickSound(audioManager);
                Logger.log(TAG, "alpha value is " + seekBar.getProgress());
                float alpha = (float) (Constants.DEFAULT_ALPHA_MAX_PERCENT - seekBar.getProgress()) / 100F;
                mBLService.updateCurrentFilterAlpha(alpha);

                filterCards.get(currentSelectedFilterIndex).setAlpha(alpha);

                Toast.makeText(Main2Activity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        /***********************/

        if (!pm.isBreakingActivated()) {
            ImageView checkedBreakingIcon = findViewById(R.id.icon_check_breaking);
            checkedBreakingIcon.setImageResource(R.drawable.ic_close_black_24dp);
        }

        if (!pm.isBluelightFiltering()) {
            ImageView checkedBLIcon = findViewById(R.id.icon_check_bluelight);
            checkedBLIcon.setImageResource(R.drawable.ic_close_black_24dp);
        }

        updateScoring();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(TAG, "onStart()");

        isActivityRunning = true;

        // Bind to ClockService
        Logger.log(TAG, "bind to " + ClockService.TAG + "...");
        Intent intent = new Intent(this, ClockService.class);
        bindService(intent, cConnection, Context.BIND_AUTO_CREATE);

        // Bind to BLService
        Logger.log(TAG, "bind to " + BlueLightFilterService.TAG + "...");
        Intent in = new Intent(this, BlueLightFilterService.class);
        bindService(in, blConnection, Context.BIND_AUTO_CREATE);

        Scoring scoring = run.getScoring();

        /* **** today checkout **********/


        int earn = scoring.getEarnCheckoutAward();
        if (earn > 0) {
            // checkout !
            long newPoint = Constants.AWARD_SCORE_CHECKOUT * earn;
            scoring.gainPoints(newPoint);
            scoring.setEarnCheckoutAward(0);
            scoring.save();
            Logger.log(TAG, "receive checkout point !!");
            dialogReceiveCheckoutPoint(newPoint);
        }

        /* **** level up checkout **********/

        earn = scoring.getEarnLevelUpAward();
        if (earn > 0) {
            // level up
            long newPoints = scoring.getScoreEarnedForLevelUp();
            scoring.gainPoints(newPoints);
            scoring.setEarnLevelUpAward(0);
            scoring.setScoreEarnedForLevelUp(0);
            scoring.save();
            Logger.log(TAG, "receive level up point !!");
            dialogReceiveLevelUpPoint(newPoints, earn, scoring.getScoreLevel());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(TAG, "onResume()");
        run = SQLRequest.getRun();
        if (run.isPermissionChanged()) {
            dialogPermissionChangedInfo();
            run.setPermissionChanged(false);
            run.save();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.log(TAG, "onStop()");
        isActivityRunning = false;
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

    /************* Service bind connection ************************/

    private ServiceConnection cConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Logger.log(TAG, "onServiceConnected() Clock");
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

            if (pm.isBluelightFiltering()) {
                dimBar.setEnabled(true);
                dimBar.setProgress(Constants.DEFAULT_DIM_MAX_PERCENT - (int)(mBLService.getCurrentFilterDim() * 100));
                alphaBar.setEnabled(true);
                alphaBar.setProgress(Constants.DEFAULT_ALPHA_MAX_PERCENT - (int)(mBLService.getCurrentFilterAlpha() * 100));

                currentSelectedFilterIndex = mBLService.getCurrentFilterIndex();
                filterCards.get(currentSelectedFilterIndex).setSelected();
            } else {
                dimBar.setEnabled(false);
                alphaBar.setEnabled(false);
            }

            for (int i = 0; i < filterCards.size(); i++) {
                ImageView image = filterCards.get(i).getImage();
                image.setOnClickListener(new FilterCardClickListener(i));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.log(TAG, "onServiceDisconnected() BL");
            mBLBound = false;
            Logger.log(TAG, BlueLightFilterService.TAG + " unbound.");

        }
    };

    /*********** PRIVATE METHODS ********************/

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
        if (pm.isBreakingActivated()| pm.isBluelightFiltering() | pctrl.isLockScreenActivated()) {
            updateTimerHandler.removeCallbacks(updateTimerRunnable);
        }
    }

    private void updateProgressBarAndSelectedFilter() {
        if (mBLBound) {
            float dim = mBLService.getCurrentFilterDim();
            float alpha = mBLService.getCurrentFilterAlpha();

            dimBar.setProgress(Constants.DEFAULT_DIM_MAX_PERCENT - (int)(dim * 100));
            alphaBar.setProgress(Constants.DEFAULT_ALPHA_MAX_PERCENT - (int)(alpha * 100));

            int index = mBLService.getCurrentFilterIndex();
            if (pm.isBluelightFiltering() && index != currentSelectedFilterIndex) {
                filterCards.get(currentSelectedFilterIndex).setUnSelected();
                currentSelectedFilterIndex = index;
                filterCards.get(currentSelectedFilterIndex).setSelected();
            }
        } else {
            Logger.err(TAG, "updateProgressBarAndSelectedFilter() mBLService not bound");
        }

    }

    private void updateScoring() {
        Scoring scoring = SQLRequest.getRun().getScoring();
        long todayScore = scoring.getScoreOfToday();
        long totalScore = scoring.getScoreTotal();
        int level = scoring.getScoreLevel();

        Logger.log(TAG, "updateScoring() level=" + level + " today=" + todayScore + " total=" + totalScore);

        todayScoreTextView.setText(String.valueOf(todayScore));
        totalScoreTextView.setText(String.valueOf(totalScore));
        levelTextView.setText(String.valueOf(level));


    }

    private void addFilterCards() {
        Logger.log(TAG, "addFilterCards()");

        LinearLayout filterGlobeLayout = new LinearLayout(this);
        filterGlobeLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                audioManager.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT,1.0f);
            }
        });
        filterGlobeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        filterGlobeLayout.setGravity(Gravity.CENTER);

        ArrayList<ScreenFilter> activatedSCs = pm.getActivatedScreenFiltersByOrder();

        for (int i = 0; i < activatedSCs.size(); i++) {
            ColorFilterLinearLayout filterCard =
                    new ColorFilterLinearLayout(this, i, activatedSCs.get(i).getColorCode(), activatedSCs.get(i).getScreenAlpha());
            filterGlobeLayout.addView(filterCard);
            filterCards.add(filterCard);
        }

        HorizontalScrollView testview = findViewById(R.id.filter_layout);
        testview.addView(filterGlobeLayout);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                for (View card : filterCards) {
                    Utils.moveLeftRight(card, Main2Activity.this.getApplicationContext());
                }
            }
        }, 400);
    }


    /******* DIALOGS ******************/

    private void dialogBreakingModeInfo(String title, String message, int dw) {
        Logger.log(TAG, "dialogBreakingModeInfo()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Alright", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(dw)
                .show();
    }

    private void dialogAboutApp() {
        Logger.log(TAG, "dialogAboutApp()");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.component_about_app, null);
        ImageView appIcon = layout.findViewById(R.id.app_icon);
        Utils.clockwiseLeftRight(appIcon, getApplicationContext());
        LinearLayout card1 = layout.findViewById(R.id.about_card1);
        LinearLayout card2 = layout.findViewById(R.id.about_card2);
        LinearLayout card3 = layout.findViewById(R.id.about_card3);
        Utils.moveLeftRight(card1, getApplicationContext());
        Utils.moveLeftRight(card2, getApplicationContext());
        Utils.moveLeftRight(card3, getApplicationContext());

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("About App")
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Utils.move(miracleIcon, getApplicationContext());
                            }
                        }, 2000);
                    }
                })
                .setIcon(R.drawable.ic_info_accent_24dp)
                .show();
    }

    private void dialogPermissionChangedInfo() {
        Logger.log(TAG, "dialogPermissionChangedInfo()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle("App Permissions Have Been Changed")
                .setMessage("Some functions might not work properly. Restart the app is recommended.")
                .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_report_yellow_24dp)
                .show();
    }

    private void dialogReceiveCheckoutPoint(long newPoints) {
        String title = "You have received +" + newPoints + " points !";
        String message = "Checkout " + getString(R.string.app_name) + " every day to earn daily checkout points";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Great !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_coin)
                .show();

    }

    private void dialogReceiveLevelUpPoint(long receivePoints, int nbLevelUp, int level) {
        String title = "You have received +" + receivePoints + " points !";
        String message = "For " + nbLevelUp + " level up !\n" +
                "You've reached level " + level + " which require more than " +
                Constants.getDefaultMinScoreOfLevel(level) + " points !";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Youpy !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_coin)
                .show();

    }



    /********* INNER CLASS *****************/


    private class FilterCardClickListener implements View.OnClickListener {

        private int index;

        public FilterCardClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View view) {
            Utils.clickAnimate(view, getApplicationContext());
            Utils.playClickSound(audioManager);
            Utils.blinkBlink(breakingModeIcon, getApplicationContext());
            Utils.blinkBlink(vibrationIcon, getApplicationContext());

            if (pm.isBluelightFiltering()) {
                filterCards.get(currentSelectedFilterIndex).setUnSelected();
                filterCards.get(index).setSelected();
                currentSelectedFilterIndex = index;
                mBLService.updateCurrentFilter(index);
                Toast.makeText(Main2Activity.this, "filter changed !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Main2Activity.this, "Screen filter is not activated", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
