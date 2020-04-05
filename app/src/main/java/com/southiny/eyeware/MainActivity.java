package com.southiny.eyeware;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.Award;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.Scoring;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.AdminReceiver;
import com.southiny.eyeware.tool.AwardType;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;
import com.southiny.eyeware.tool.Utils;

import java.util.Calendar;

import static com.southiny.eyeware.tool.Utils.zbs;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Run run;
    private ProtectionMode pm;
    private ParentalControl pctrl;
    private AudioManager audioManager;

    TextView lockScreenTextView;
    Button standardButton, highButton, lowButton, gamerButton;
    ImageView standardEditIcon, highEditIcon, lowEditIcon, gamerEditIcon;
    ImageView standardCheckIcon, highCheckIcon, lowCheckIcon, gamerCheckIcon;
    ImageView overlayPermissionIcon, adminPermissionIcon;
    Switch passwordActivateSwitch, lockScreenActivateSwitch, unlockScreenActivateSwitch;
    ConstraintLayout lockScreenLayout, breakForFoot;
    LinearLayout startStopTimerLayout, breakingBody, filterArm, lockArm;
    ImageView breakingModeIconFoot;

    public static final int RESULT_ENABLE_ADMIN_PERMISSION = 11;
    public static final int RESULT_ENABLE_OVERLAY_PERMISSION = 12;
    public static final int RESULT_ENABLE_ADMIN_PERMISSION_FOR_START = 13;
    public static final int RESULT_ENABLE_OVERLAY_PERMISSION_FOR_START = 14;
    private boolean lockScreenOnAfterAdminPermissionGranted = false;
    private int nbClick; // for animation

    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(TAG, "onCreate()");

        Logger.log(TAG, "set content view to overlay_view activity_main");
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(TAG, "onStart()");

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        Logger.log(TAG, "get protection mode from run...");
        run = SQLRequest.getRun();
        pm = run.getCurrentProtectionMode();
        pctrl = run.getParentalControl();

        run.setPermissionChanged(false);
        run.save();

        Scoring scoring = run.getScoring();

        /* ***** check scoring and ************/

        if (run.isNewArrival()) {
            // new arrival points !
            scoring.gainPoints(Constants.AWARD_SCORE_NEW_ARRIVAL);
            run.setNewArrival(false);
            run.save();
            Award award = new Award(AwardType.NEW_ARRIVAL_AWARD, Constants.AWARD_SCORE_NEW_ARRIVAL,
                    System.currentTimeMillis(), Award.NO_EXPIRATION);
            award.save();
            Logger.log(TAG, "receive new arrival point !!");
            String message = "Greeting new arrival !\n" + Constants.AWARD_SCORE_NEW_ARRIVAL + " points is a gift for you\nEnjoy our community !";
            dialogReceivePoints(Constants.AWARD_SCORE_NEW_ARRIVAL, message, "Nice !");
        }

        // check excellency award
        if (scoring.getScoreEarnedForExcellency() > 0) {
            // earn excellency !
            long newPoints = scoring.getScoreEarnedForExcellency();
            scoring.earnExcellencyAward(newPoints);
            Logger.log(TAG, "receive excellency award !");
            String message = "For Excellency ! You've done incredibly well these last days\n Congratulation for your achievement !";
            dialogReceivePoints(newPoints, message, "Oh my gosh !");
        }

        // check reset scoring (must after check excellency award)
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTimeInMillis(scoring.getCheckoutDayTimestamp());
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DAY_OF_YEAR) != lastDay.get(Calendar.DAY_OF_YEAR)) {
            Logger.log(TAG, "reset scoring : day has changed, reset...");
            scoring.newDay();
        } else {
            Logger.log(TAG, "reset scoring : still same day");
        }

        // check checkout award (must after check reset scoring)
        if (scoring.getEarnCheckoutAward() > 0) {
            // checkout !
            long newPoints = Constants.AWARD_SCORE_CHECKOUT * scoring.getEarnCheckoutAward();
            scoring.earnCheckoutAward(newPoints);
            Logger.log(TAG, "receive checkout point !!");
            String message = "Checkout " + getString(R.string.app_name) + " every day to earn daily checkout points";
            dialogReceivePoints(newPoints, message, "Great !");
        }


        /* **** find view by id ************/

        standardCheckIcon = findViewById(R.id.standard_level_check_icon);
        highCheckIcon = findViewById(R.id.high_level_check_icon);
        lowCheckIcon = findViewById(R.id.low_level_check_icon);
        gamerCheckIcon = findViewById(R.id.gamer_level_check_icon);

        standardButton = findViewById(R.id.standard_button);
        highButton = findViewById(R.id.high_button);
        lowButton = findViewById(R.id.low_button);
        gamerButton = findViewById(R.id.gamer_button);

        standardEditIcon = findViewById(R.id.standard_mode_edit_icon);
        highEditIcon = findViewById(R.id.high_mode_edit_icon);
        lowEditIcon = findViewById(R.id.low_mode_edit_icon);
        gamerEditIcon = findViewById(R.id.gamer_mode_edit_icon);

        startStopTimerLayout = findViewById(R.id.timer_start_stop);
        final ImageView settingsIcon = findViewById(R.id.settings_icon);

        passwordActivateSwitch = findViewById(R.id.switch_password_activate);
        TextView changePasswordTextView = findViewById(R.id.change_password_for_parental_control);
        TextView forgetPasswordTextView = findViewById(R.id.forget_password_for_parental_control);

        lockScreenTextView = findViewById(R.id.lock_screen_label);
        lockScreenLayout = findViewById(R.id.constraintLayout_lock_screen);
        lockScreenActivateSwitch = findViewById(R.id.switch_lock_screen);
        TextView changeLockScreenTimeTextView = findViewById(R.id.change_lock_screen_time);

        overlayPermissionIcon = findViewById(R.id.overlay_permission_on_off_button);
        adminPermissionIcon = findViewById(R.id.admin_permission_on_off_button);

        breakingBody = findViewById(R.id.linearLayout0);
        lockArm = findViewById(R.id.linearLayout3);
        filterArm = findViewById(R.id.linearLayout4);
        breakForFoot = findViewById(R.id.constraintLayout6);
        breakingModeIconFoot = findViewById(R.id.breaking_mode_icon);

        final LinearLayout parentalControlsIconLayout = findViewById(R.id.linearLayout_parental_control_icons);
        final LinearLayout parentalControlTextLayout = findViewById(R.id.parental_control_text_layout);


        /* ********************* animation ***********/

        Utils.blinkButterfly(parentalControlsIconLayout, getApplicationContext());
        Utils.blinkButterfly(lockArm, getApplicationContext());
        Utils.blinkButterfly(filterArm, getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.blinkButterfly(breakingBody, getApplicationContext());
            }
        }, 200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.blinkButterfly(breakingModeIconFoot, getApplicationContext());
            }
        }, 400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.blinkButterfly(breakForFoot, getApplicationContext());
            }
        }, 500);

        Utils.clockwiseLeftRightFade(startStopTimerLayout, getApplicationContext());

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        Utils.moveUp(mainLayout, getApplicationContext());

        breakingBody.setOnClickListener(new RobotOnClickListener());
        filterArm.setOnClickListener(new RobotOnClickListener());
        lockArm.setOnClickListener(new RobotOnClickListener());
        breakForFoot.setOnClickListener(new Robot2OnClickListener(false));
        breakingModeIconFoot.setOnClickListener(new Robot2OnClickListener(true));


        /* *** set info on screen ***/

        initInfoOnScreen();

        /* *** set on click listeners ***/

        // set onclick to start button
        Logger.log(TAG, "set on click listener to start/stop timer");
        startStopTimerLayout.setOnClickListener(new View.OnClickListener() {
            private final String TAG = MainActivity.TAG + " start verification";

            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick()");
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());

                if (pm.isBluelightFiltering() | pm.isBreakingActivated() | pctrl.isLockScreenActivated()) {

                    boolean blVerified = false;
                    if (pm.isBluelightFiltering()) { // is bl ?
                        Logger.log(TAG, "has bl");
                        if (Settings.canDrawOverlays(getApplicationContext())) { // can bl ?
                            Logger.log(TAG, "can bl");
                            blVerified = true;
                        } else {
                            Logger.log(TAG, "cannot bl");
                            blVerified = false;
                            dialogAskOverlayPermissionForStartBL();
                        }
                    } else {
                        Logger.log(TAG, "no bl");
                        blVerified = true;
                    }

                    boolean bmVerified = false;
                    if (blVerified && pm.getBreakingMode() == BreakingMode.STRONG) { // is strMode ?
                        Logger.log(TAG, "has strMode");
                        if(Settings.canDrawOverlays(getApplicationContext())) { // can strMode ?
                            Logger.log(TAG, "can strMode");
                            bmVerified = true;
                        } else {
                            Logger.log(TAG, "cannot strMode");
                            bmVerified = false;
                            dialogAskOverlayPermissionForStartStrongBM();
                        }
                    } else {
                        Logger.log(TAG, "no strMode");
                        bmVerified = true;
                    }

                    boolean lcVerified = false;
                    if (blVerified && bmVerified && pctrl.isLockScreenActivated()) { // is lock ?
                        Logger.log(TAG, "has lock");
                        if (devicePolicyManager.isAdminActive(compName)) { // can lock ?
                            Logger.log(TAG, "can lock");
                            lcVerified = true;
                        } else {
                            Logger.log(TAG, "cannot lock");
                            lcVerified = false;
                            dialogAskDeviceAdminPermissionForStartLockScreen();
                        }
                    } else {
                        Logger.log(TAG, "no lock");
                        lcVerified = true;
                    }


                    if (blVerified && bmVerified && lcVerified) { // start
                        Logger.log(TAG, "start " + ClockService.TAG);
                        Intent intent = new Intent(MainActivity.this, ClockService.class);
                        startForegroundService(intent);

                        Logger.log(TAG, "start " + Main2Activity.TAG);
                        Intent in = new Intent(MainActivity.this, Main2Activity.class);
                        startActivity(in);

                        Toast.makeText(MainActivity.this, getString(R.string.home_timer_start_message), Toast.LENGTH_SHORT).show();

                        Logger.log(TAG, "finished.");
                        finish();
                    } else { // don't start
                        Logger.log(TAG, "cannot start program");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please activate one or more features", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set on click to settings icon
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() settings icon");
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // set onclick to protection mode button
        standardButton.setOnClickListener(new PLButtonClickListener(run.getProtectionModeStandard()));
        highButton.setOnClickListener(new PLButtonClickListener(run.getProtectionModeHigh()));
        lowButton.setOnClickListener(new PLButtonClickListener(run.getProtectionModeLow()));
        gamerButton.setOnClickListener(new PLButtonClickListener(run.getProtectionModeGamer()));

        // set on click to edit icons
        standardEditIcon.setOnClickListener(new PLEditIconClickListener(ProtectionLevel.STANDARD));
        highEditIcon.setOnClickListener(new PLEditIconClickListener(ProtectionLevel.HIGH));
        lowEditIcon.setOnClickListener(new PLEditIconClickListener(ProtectionLevel.LOW));
        gamerEditIcon.setOnClickListener(new PLEditIconClickListener(ProtectionLevel.GAMER));

        Utils.blinkBlink(standardButton, getApplicationContext());
        Utils.blinkBlink(highButton, getApplicationContext());
        Utils.blinkBlink(lowButton, getApplicationContext());
        Utils.blinkBlink(gamerButton, getApplicationContext());
        Utils.blinkBlink(standardEditIcon, getApplicationContext());
        Utils.blinkBlink(highEditIcon, getApplicationContext());
        Utils.blinkBlink(lowEditIcon, getApplicationContext());
        Utils.blinkBlink(gamerEditIcon, getApplicationContext());

        // set onchange to password switch
        passwordActivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pctrl.setPasswordActivated(isChecked);
                pctrl.save();
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "parental control on", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "parental control off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        passwordActivateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
            }
        });

        changePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() change password text");
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                dialogChangePassword();
            }
        });

        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() forget password");
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                dialogForgetPassword();
            }
        });

        // todo : add others

        parentalControlsIconLayout.setOnClickListener(new View.OnClickListener() {
            private boolean bool = true;
            @Override
            public void onClick(View view) {
                if (bool) {
                    parentalControlTextLayout.setVisibility(View.VISIBLE);
                } else {
                    parentalControlTextLayout.setVisibility(View.GONE);
                }

                bool = !bool;
            }
        });

        lockScreenActivateSwitch.setChecked(pctrl.isLockScreenActivated());
        lockScreenActivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked && !devicePolicyManager.isAdminActive(compName)) {
                    lockScreenActivateSwitch.setChecked(!checked);
                    dialogAskDeviceAdminPermissionForTurnOnLockScreen();

                } else if (checked) {
                    lockScreenLayout.setBackground(getDrawable(R.drawable.layout_round_shape_yellow_shade_blue));
                    pctrl.setLockScreenActivated(true);
                    pctrl.save();
                    Toast.makeText(MainActivity.this, "Lock Screen has turn on", Toast.LENGTH_SHORT).show();

                } else { // not checked
                    lockScreenLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
                    pctrl.setLockScreenActivated(false);
                    pctrl.save();
                    Toast.makeText(MainActivity.this, "Lock Screen has turn off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lockScreenActivateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
            }
        });

        changeLockScreenTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() change lock screen time text");
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                dialogChangeLockScreenTime();
            }
        });

        unlockScreenActivateSwitch = findViewById(R.id.switch_unlock_screen);
        unlockScreenActivateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
            }
        });
        unlockScreenActivateSwitch.setEnabled(false);

        overlayPermissionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, RESULT_ENABLE_OVERLAY_PERMISSION);
            }
        });

        adminPermissionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());

                if (devicePolicyManager.isAdminActive(compName)) {
                    devicePolicyManager.removeActiveAdmin(compName);
                    adminPermissionIcon.setImageResource(R.drawable.ic_build_gray_24dp);
                } else {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "To make "
                            + getString(R.string.app_name) + " lock screen automatically, please grant us the admin permission");
                    startActivityForResult(intent, RESULT_ENABLE_ADMIN_PERMISSION);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(TAG, "onResume()");
        // start main activity
        if (SplashScreen.isServiceRunning(ClockService.class.getName(), this)) {
            Logger.log(TAG, "ClockService is running !!!");
            Intent intent;
            Logger.log(TAG, "start " + WaitActivity.class.getSimpleName() + "...");
            intent = new Intent(MainActivity.this, WaitActivity.class);

            startActivity(intent);
            finish();
        }
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

    /******* private methods *******/
    

    private void initInfoOnScreen() {
        Logger.log(TAG, "initInfoOnScreen()");

        initDashboardInfo();

        passwordActivateSwitch.setChecked(pctrl.isPasswordActivated());

        String lockScreenLabel = getString(R.string.lock_screen_activate_label) + " in "
                + (pctrl.getLockScreenInSec() / 60) + " mins";
        lockScreenTextView.setText(lockScreenLabel);
        lockScreenActivateSwitch.setChecked(pctrl.isLockScreenActivated());


        if (Settings.canDrawOverlays(getApplicationContext())) {
            overlayPermissionIcon.setImageResource(R.drawable.ic_layers_blue_24dp);
        } else {
            overlayPermissionIcon.setImageResource(R.drawable.ic_layers_gray_24dp);
        }

        if (devicePolicyManager.isAdminActive(compName)) {
            adminPermissionIcon.setImageResource(R.drawable.ic_build_blue_24dp);
        } else {
            adminPermissionIcon.setImageResource(R.drawable.ic_build_gray_24dp);
        }

    }

    private void initDashboardInfo() {
        // set breaking time
        long sec = pm.getBreakingEvery_sec();
        int min = (int) (sec / 60);
        Logger.log(TAG, "set breaking time every " + min + "min...");
        TextView minuteTextView = findViewById(R.id.timer_minute);
        minuteTextView.setText(zbs(min));

        // set breaking for
        sec = pm.getBreakingFor_sec();
        Logger.log(TAG, "set breaking for " + sec + "sec...");
        TextView breakForSecTextView = findViewById(R.id.break_for_timer_second);
        breakForSecTextView.setText(zbs((int) sec));

        // set breaking mode
        int dw = R.drawable.ic_close_white_24dp;
        BreakingMode mode = pm.getBreakingMode();
        switch (mode) {
            case STRONG:
                dw = R.drawable.ic_bm_strong_white_24dp;
                break;
            case MEDIUM:
                dw = R.drawable.ic_bm_medium_white_24dp;
                break;
            case LIGHT:
                dw = R.drawable.ic_bm_light_white_24dp;
                break;
        }

        breakingModeIconFoot.setImageResource(dw);

        // set bluelight time
        sec = pm.getBlueLightFilterChangeEvery_sec();
        min = (int) (sec / 60);
        Logger.log(TAG, "set bluelight time for " + min + "min...");
        TextView blSecondTextView = findViewById(R.id.bl_timer_second);
        blSecondTextView.setText(zbs((int) min));

        // set lockscreen time
        sec = pctrl.getLockScreenInSec();
        min = (int) (sec / 60);
        Logger.log(TAG, "set lock screen every " + min + "min...");
        TextView lockscreenMinuteTextView = findViewById(R.id.lockscreen_timer_minute);
        lockscreenMinuteTextView.setText(zbs(min));

        // set activated
        ConstraintLayout breakingLayout = findViewById(R.id.computer_mode_field);
        ConstraintLayout breakingForLayout = findViewById(R.id.constraintLayout6);
        if (!pm.isBreakingActivated()) {
            breakingLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
            breakingModeIconFoot.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
            breakingForLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
        } else {
            breakingLayout.setBackground(getDrawable(R.drawable.layout_round_shape_yellow_shade_blue));
            breakingModeIconFoot.setBackground(getDrawable(R.drawable.layout_round_shape_accent));
            breakingForLayout.setBackground(getDrawable(R.drawable.layout_round_shape_accent));
        }
        ConstraintLayout blLayout = findViewById(R.id.constraintLayout_bl_filter);
        if (!pm.isBluelightFiltering()) {
            blLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
        } else {
            blLayout.setBackground(getDrawable(R.drawable.layout_round_shape_yellow_shade_blue));
        }
        if(!pctrl.isLockScreenActivated()) {
            lockScreenLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
        } else {
            lockScreenLayout.setBackground(getDrawable(R.drawable.layout_round_shape_yellow_shade_blue));
        }

        // set button names
        standardButton.setText(run.getProtectionModeStandard().getName());
        highButton.setText(run.getProtectionModeHigh().getName());
        lowButton.setText(run.getProtectionModeLow().getName());
        gamerButton.setText(run.getProtectionModeGamer().getName());

        // set selected button
        ProtectionLevel pl = pm.getProtectionLevel();
        setSelectedButton(pl);
    }

    private void setSelectedButton(ProtectionLevel pl) {
        Logger.log(TAG, "setSelectedButton " + pl.toString());

        switch (pl) {
            case STANDARD:
                _setSelected(standardButton, standardCheckIcon);
                _setUnselected(highButton, highCheckIcon);
                _setUnselected(lowButton, lowCheckIcon);
                _setUnselected(gamerButton, gamerCheckIcon);
                break;

            case HIGH:
                _setUnselected(standardButton, standardCheckIcon);
                _setSelected(highButton, highCheckIcon);
                _setUnselected(lowButton, lowCheckIcon);
                _setUnselected(gamerButton, gamerCheckIcon);
                break;

            case LOW:
                _setUnselected(standardButton, standardCheckIcon);
                _setUnselected(highButton, highCheckIcon);
                _setSelected(lowButton, lowCheckIcon);
                _setUnselected(gamerButton, gamerCheckIcon);
                break;
            case GAMER:
                _setUnselected(standardButton, standardCheckIcon);
                _setUnselected(highButton, highCheckIcon);
                _setUnselected(lowButton, lowCheckIcon);
                _setSelected(gamerButton, gamerCheckIcon);
                break;
            default:
                Logger.err(TAG, "protection level is unknown");
        }
    }

    private void _setSelected(Button button, ImageView checkIcon) {
        button.setBackground(getDrawable(R.drawable.layout_round_shape_dark_blue_shade_green));
        checkIcon.setVisibility(View.VISIBLE);
    }

    private void _setUnselected(Button button, ImageView checkIcon) {
        button.setBackground(getDrawable(R.drawable.layout_round_shape_dark_black_shade_blue));
        checkIcon.setVisibility(View.GONE);
    }



    /** DIALOGS *********************************************/

    private void dialogAskOverlayPermissionForStartBL() {
        Logger.log(TAG, "dialogAskOverlayPermissionForStartBL()");
        String title = "Overlay Permission";
        String message = "To apply blue light filter, " +
                "please grant us the overlay permission (\"Appear on top\")." +
                " You can turn this off later on. " +
                "Grant the permission ?";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.playClickSound(audioManager);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, RESULT_ENABLE_OVERLAY_PERMISSION_FOR_START);
                    }
                })
                .setNegativeButton("No, I don't need blue light filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                        dialogTurnOffBL();
                    }
                })
                .setIcon(R.drawable.ic_layers_accent_24dp)
                .show();
    }

    private void dialogTurnOffBL() {
        Logger.log(TAG, "dialogTurnOffBL()");
        String title = "Turn off screen filtering ?";
        String message = "Turn it off and start the program";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.playClickSound(audioManager);
                        // turn off
                        pm.setBluelightFiltering(false);
                        pm.save();
                        ConstraintLayout blLayout = findViewById(R.id.constraintLayout_bl_filter);
                        blLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
                        Toast.makeText(MainActivity.this, "Screen filtering Off", Toast.LENGTH_SHORT).show();

                        // re call start
                        startStopTimerLayout.callOnClick();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_face_accent_24dp)
                .show();
    }

    private void dialogAskOverlayPermissionForStartStrongBM() {
        Logger.log(TAG, "dialogAskOverlayPermissionForStartStrongBM()");
        String title = "Overlay Permission";
        String message = "To apply strong breaking mode, " +
                "please grant us the overlay permission (\"Appear on top\"). " +
                "You can turn this off later on. " +
                "Grant the permission ?";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle("Overlay Permission")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.playClickSound(audioManager);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, RESULT_ENABLE_OVERLAY_PERMISSION_FOR_START);
                    }
                })
                .setNegativeButton("No, I don't need strong breaking mode", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                        dialogChangeBreakingMode();
                    }
                })
                .setIcon(R.drawable.ic_layers_accent_24dp)
                .show();
    }

    private void dialogChangeBreakingMode() {
        Logger.log(TAG, "dialogChangeBreakingMode()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle("Change breaking mode ?")
                .setMessage("Change to Medium breaking mode")
                .setPositiveButton("Change and start program", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                        // change bm
                        pm.setBreakingMode(BreakingMode.MEDIUM);
                        pm.save();
                        ImageView breakingModeIcon = findViewById(R.id.breaking_mode_icon);
                        breakingModeIcon.setImageResource(R.drawable.ic_bm_medium_white_24dp);
                        Toast.makeText(MainActivity.this, "Change to Medium breaking mode", Toast.LENGTH_SHORT).show();

                        // re call start
                        startStopTimerLayout.callOnClick();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_face_accent_24dp)
                .show();
    }

    private void dialogAskDeviceAdminPermissionForStartLockScreen() {
        Logger.log(TAG, "dialogAskDeviceAdminPermissionForStartLockScreen()");
        String title = "Device Admin Permission";
        String message = "To manage lock screen, please grant us the device admin permission. " +
                "You can turn this off later on. " +
                "Grant the permission ?";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.playClickSound(audioManager);
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "To make " +
                                getString(R.string.app_name) + " lock screen automatically, please grant us the admin permission");
                        startActivityForResult(intent, RESULT_ENABLE_ADMIN_PERMISSION_FOR_START);
                    }
                })
                .setNegativeButton("No, I don't need auto lock screen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                        dialogTurnOffLockScreen();
                    }
                })
                .setIcon(R.drawable.ic_build_accent_24dp)
                .show();
    }

    private void dialogTurnOffLockScreen() {
        Logger.log(TAG, "dialogTurnOffLockScreen()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle("Turn off auto lock screen ?")
                .setMessage("Turn it off and start the program")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                        // turn off lock screen
                        lockScreenActivateSwitch.setChecked(false);

                        // re call start
                        startStopTimerLayout.callOnClick();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_face_accent_24dp)
                .show();
    }

    private void dialogAskDeviceAdminPermissionForTurnOnLockScreen() {
        Logger.log(TAG, "dialogAskDeviceAdminPermissionForTurnOnLockScreen()");
        String title = "Device Admin Permission";
        String message = "To manage lock screen, please grant us the device admin permission. " +
                "You can turn this off later on. " +
                "Grant the permission ?";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.playClickSound(audioManager);
                        lockScreenOnAfterAdminPermissionGranted = true;
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "To make " +
                                getString(R.string.app_name) + " lock screen automatically, please grant us the admin permission");
                        startActivityForResult(intent, RESULT_ENABLE_ADMIN_PERMISSION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                        lockScreenActivateSwitch.setChecked(false);
                    }
                })
                .setIcon(R.drawable.ic_build_accent_24dp)
                .show();
    }

    private void dialogChangePassword() {
        Logger.log(TAG, "dialogChangePassword()");

        LayoutInflater inflater = this.getLayoutInflater();
        View changePasswordLayout = inflater.inflate(R.layout.component_change_password_form, null);
        final EditText oldPasswordInputEditText = changePasswordLayout.findViewById(R.id.password_old_input);
        final EditText newPasswordInputEditText = changePasswordLayout.findViewById(R.id.password_new_input);
        final TextView passwordNotMatchTextView = changePasswordLayout.findViewById(R.id.password_not_match_text_view);
        final TextView newPasswordNotValidTextView = changePasswordLayout.findViewById(R.id.new_password_not_valid_text_view);
        final TextView passwordOldTextView = changePasswordLayout.findViewById(R.id.password_old_text);
        final TextView okButton = changePasswordLayout.findViewById(R.id.change_password_button_ok);
        final TextView cancelButton = changePasswordLayout.findViewById(R.id.change_password_button_cancel);

        if (pctrl.getPassword().equals(Constants.DEFAULT_PASSWORD)) {
            oldPasswordInputEditText.setText(pctrl.getPassword());
            oldPasswordInputEditText.setVisibility(View.GONE);
            passwordOldTextView.setVisibility(View.GONE);
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(changePasswordLayout)
                .setIcon(R.drawable.ic_security_accent_24dp)
                .show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                String oldPassword = oldPasswordInputEditText.getText().toString();
                String newPassword = newPasswordInputEditText.getText().toString();
                Logger.log(TAG, "new password is " + newPassword);

                newPasswordNotValidTextView.setVisibility(View.GONE);
                passwordNotMatchTextView.setVisibility(View.GONE);


                if (oldPassword.equals(pctrl.getPassword())) {
                    if (newPassword.length() < 3) {
                        newPasswordNotValidTextView.setVisibility(View.VISIBLE);
                        newPasswordNotValidTextView.setText("New password should has at least 3 characters.");
                    } else {
                        pctrl.setPassword(newPassword);
                        pctrl.save();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "password change to " + newPassword, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    passwordNotMatchTextView.setVisibility(View.VISIBLE);
                    passwordNotMatchTextView.setText("Password not matched.");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                Logger.log(TAG, "cancel !");
                dialog.dismiss();
            }
        });
    }

    private void dialogChangeLockScreenTime() {
        Logger.log(TAG, "dialogChangeLockScreenTime()");

        LayoutInflater inflater = this.getLayoutInflater();
        View changeLockScreenLayout = inflater.inflate(R.layout.component_change_lockscreen_time_form, null);
        final EditText lockScreenTimeInputEditText = changeLockScreenLayout.findViewById(R.id.lock_screen_time_input_text);
        final TextView okButton = changeLockScreenLayout.findViewById(R.id.change_lock_screen_time_button_ok);
        final TextView cancelButton = changeLockScreenLayout.findViewById(R.id.change_lock_screen_time_button_cancel);
        final TextView lockScreenTimeNotValidTextView = changeLockScreenLayout.findViewById(R.id.lock_screen_time_not_valid_text_view);

        lockScreenTimeInputEditText.setText(String.valueOf(pctrl.getLockScreenInSec() / 60));

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Lock Screen Time")
                //.setMessage(message)
                .setView(changeLockScreenLayout)
                .setIcon(R.drawable.ic_lock_open_black_24dp)
                .show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                int timeInMinute = Integer.valueOf(lockScreenTimeInputEditText.getText().toString());
                if (timeInMinute < 3) {
                    lockScreenTimeNotValidTextView.setText("Lock screen time should be more than 3 mins.");
                    lockScreenTimeNotValidTextView.setVisibility(View.VISIBLE);
                } else if (timeInMinute > 300) {
                    lockScreenTimeNotValidTextView.setText("Lock screen time should not exceed 300 mins (5 hours).");
                    lockScreenTimeNotValidTextView.setVisibility(View.VISIBLE);
                } else {
                    Logger.log(TAG, "new lock screen time is " + timeInMinute + " mins");
                    pctrl.setLockScreenInSec(timeInMinute * 60);
                    pctrl.save();

                    String lockScreenLabel = getString(R.string.lock_screen_activate_label) + " in "
                            + (timeInMinute) + " mins";
                    lockScreenTextView.setText(lockScreenLabel);
                    TextView lockscreenMinuteTextView = findViewById(R.id.lockscreen_timer_minute);
                    lockscreenMinuteTextView.setText(zbs(timeInMinute));

                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "lock screen time change to " + timeInMinute + " mins", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.playClickSound(audioManager);
                Utils.clickAnimate(view, getApplicationContext());
                Logger.log(TAG, "cancel !");
                dialog.dismiss();
            }
        });
    }

    private void dialogForgetPassword() {
        Logger.log(TAG, "dialogForgetPassword()");
        String title = "Forget password, what can I do ?";
        String message = "In order to prevent children from resetting password, " +
                "we don't support password reset. " +
                "A solution to this could be uninstall and re-install the application. " +
                "Factory default password is " + Constants.DEFAULT_PASSWORD + ".";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_info_accent_24dp)
                .show();

    }

    private void dialogReceivePoints(long newPoints, String message, String buttonTitle) {
        String title = "You have received +" + newPoints + " points !";

        new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.playClickSound(audioManager);
                    }
                })
                .setIcon(R.drawable.ic_coin)
                .show();
    }


    /* **********************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log(TAG, "on activity result()");
        switch(requestCode) {
            case RESULT_ENABLE_ADMIN_PERMISSION:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(MainActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                    adminPermissionIcon.setImageResource(R.drawable.ic_build_blue_24dp);

                    if (lockScreenOnAfterAdminPermissionGranted) {
                        lockScreenActivateSwitch.setChecked(true);
                        lockScreenOnAfterAdminPermissionGranted = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                    adminPermissionIcon.setImageResource(R.drawable.ic_build_gray_24dp);
                    lockScreenActivateSwitch.setChecked(false);
                }
                pctrl.save();
                break;

            case RESULT_ENABLE_OVERLAY_PERMISSION:
                if (resultCode == 0) {
                    overlayPermissionIcon.setImageResource(R.drawable.ic_layers_blue_24dp);
                    Toast.makeText(MainActivity.this,
                            "You have enabled the App On Top features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "You have disabled the Appear On Top features", Toast.LENGTH_SHORT).show();
                    overlayPermissionIcon.setImageResource(R.drawable.ic_layers_gray_24dp);
                }
                break;

            case RESULT_ENABLE_OVERLAY_PERMISSION_FOR_START:
                if (resultCode ==  0) {
                    // re call start
                    Toast.makeText(MainActivity.this,
                            "You have enabled the App On Top features", Toast.LENGTH_SHORT).show();
                    overlayPermissionIcon.setImageResource(R.drawable.ic_layers_blue_24dp);
                    startStopTimerLayout.callOnClick();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Problem to enable the Appear On Top features", Toast.LENGTH_SHORT).show();
                    overlayPermissionIcon.setImageResource(R.drawable.ic_layers_gray_24dp);
                    dialogTurnOffBL();
                }
                break;

            case RESULT_ENABLE_ADMIN_PERMISSION_FOR_START:
                if (resultCode ==  Activity.RESULT_OK) {
                    // re call start
                    Toast.makeText(MainActivity.this,
                            "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                    adminPermissionIcon.setImageResource(R.drawable.ic_build_blue_24dp);
                    startStopTimerLayout.callOnClick();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                    adminPermissionIcon.setImageResource(R.drawable.ic_build_gray_24dp);
                    dialogTurnOffLockScreen();
                }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /* **************** inner class ****************/

    private class PLButtonClickListener implements View.OnClickListener {

        private int clickAt;
        private ProtectionMode protectionMode;

        public PLButtonClickListener(ProtectionMode protectionMode) {
            this.protectionMode = protectionMode;
        }

        @Override
        public void onClick(View view) {
            nbClick++;
            clickAt = nbClick;

            Utils.playClickSound(audioManager);
            Utils.clickAnimate(view, getApplicationContext());
            Utils.clockwiseRoundFast(startStopTimerLayout, getApplicationContext());

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (nbClick == clickAt) {
                        Utils.clockwiseLeftRightFade(startStopTimerLayout, getApplicationContext());
                    }
                }
            }, 10000);


            Logger.log(TAG, "onClick() " + protectionMode.getName() + " button");
            pm = protectionMode;
            run.setCurrentProtectionMode(protectionMode);
            run.save();
            initDashboardInfo();
        }
    }

    private class PLEditIconClickListener implements View.OnClickListener {

        private ProtectionLevel protectionLevel;

        public PLEditIconClickListener(ProtectionLevel protectionLevel) {
            this.protectionLevel = protectionLevel;
        }

        @Override
        public void onClick(View view) {
            Utils.playClickSound(audioManager);
            Utils.clickAnimate(view, getApplicationContext());
            Logger.log(TAG, "startProtectionLevelActivity " + protectionLevel.toString());
            Utils.clickAnimate(view, getApplicationContext());
            Intent intent = new Intent(MainActivity.this, ProtectionLevelEditActivity.class);
            intent.putExtra(ProtectionLevelEditActivity.INTENT_EXTRA_PROTECTION_LEVEL_ORDINAL,
                    protectionLevel.ordinal());
            startActivity(intent);
        }
    }



    private class RobotOnClickListener implements View.OnClickListener {

        private int clickAt;

        @Override
        public void onClick(final View view) {
            nbClick++;
            clickAt = nbClick;

            Utils.blinkBlink(view, MainActivity.this.getApplicationContext());
            Utils.zoom(startStopTimerLayout, MainActivity.this.getApplicationContext());

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickAt == nbClick) {
                        Utils.blinkButterfly(view, MainActivity.this.getApplicationContext());
                        Utils.blinkBlink(startStopTimerLayout, MainActivity.this.getApplicationContext());

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickAt == nbClick)
                                    Utils.clockwiseLeftRightFade(startStopTimerLayout, MainActivity.this.getApplicationContext());
                            }
                        }, 4000);
                    }
                }
            }, 6000);
        }
    }

    private class Robot2OnClickListener implements View.OnClickListener {

        private int clickAt;
        private boolean leftRight;

        public Robot2OnClickListener(boolean leftRight) {
            this.leftRight = leftRight;
        }

        @Override
        public void onClick(final View view) {
            nbClick++;
            clickAt = nbClick;

            Utils.blinkBlink(view, MainActivity.this.getApplicationContext());

            if (leftRight) {
                Utils.moveLeftRight(startStopTimerLayout, MainActivity.this.getApplicationContext());
                Utils.moveDown(filterArm, MainActivity.this.getApplicationContext());
                Utils.moveUp(lockArm, MainActivity.this.getApplicationContext());
            } else {
                Utils.moveRightLeft(startStopTimerLayout, MainActivity.this.getApplicationContext());
                Utils.moveUp(filterArm, MainActivity.this.getApplicationContext());
                Utils.moveDown(lockArm, MainActivity.this.getApplicationContext());
            }

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickAt == nbClick) {
                        Utils.blinkButterfly(view, MainActivity.this.getApplicationContext());
                        Utils.blinkButterfly(filterArm, MainActivity.this.getApplicationContext());
                        Utils.blinkButterfly(lockArm, MainActivity.this.getApplicationContext());                        Utils.blinkBlink(startStopTimerLayout, MainActivity.this.getApplicationContext());

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickAt == nbClick)
                                    Utils.clockwiseLeftRightFade(startStopTimerLayout, MainActivity.this.getApplicationContext());
                            }
                        }, 4000);
                    }
                }
            }, 6000);
        }
    }

}