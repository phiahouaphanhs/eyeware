package com.southiny.eyeware;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.tool.AdminReceiver;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;

import static com.southiny.eyeware.tool.Utils.zbs;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Run run;
    private ProtectionMode pm;
    private ParentalControl pctrl;

    TextView lockScreenTextView;
    Button standardButton, highButton, lowButton, gamerButton;
    ImageView standardEditIcon, highEditIcon, lowEditIcon, gamerEditIcon;
    ImageView standardCheckIcon, highCheckIcon, lowCheckIcon, gamerCheckIcon;
    Switch passwordActivateSwitch, lockScreenActivateSwitch, unlockScreenActivateSwitch;
    ConstraintLayout lockScreenLayout;

    public static final int RESULT_ENABLE = 11;
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

        Logger.log(TAG, "get protection mode from run...");
        run = SQLRequest.getRun();
        pm = run.getCurrentProtectionMode();
        pctrl = run.getParentalControl();

        run.setPermissionChanged(false);
        run.save();

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

        LinearLayout startStopTimerLayout = findViewById(R.id.timer_start_stop);
        final ImageView settingsIcon = findViewById(R.id.settings_icon);

        passwordActivateSwitch = findViewById(R.id.switch_password_activate);
        TextView changePasswordTextView = findViewById(R.id.change_password_for_parental_control);
        TextView forgetPasswordTextView = findViewById(R.id.forget_password_for_parental_control);

        lockScreenTextView = findViewById(R.id.lock_screen_label);
        lockScreenLayout = findViewById(R.id.constraintLayout_lock_screen);
        lockScreenActivateSwitch = findViewById(R.id.switch_lock_screen);
        TextView changeLockScreenTimeTextView = findViewById(R.id.change_lock_screen_time);

        /* *** set info on screen *****/

        initInfoOnScreen();

        /* *** set on click listeners ***/

        // set onclick to start button
        Logger.log(TAG, "set on click listener to start/stop timer");
        startStopTimerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick()");

                if (pm.isBluelightFiltering() | pm.isBreakingActivated() | pctrl.isLockScreenActivated()) {
                    boolean start = true;

                    if (!Settings.canDrawOverlays(getApplicationContext())) { // if BL is on but no "appears on top permission"
                        if (pm.isBluelightFiltering()) {
                            dialogAskOverlayPermissionForBL();
                            start = false;
                        } else {
                            if (pm.getBreakingMode() == BreakingMode.STRONG) {
                                dialogAskOverlayPermissionForStrongBM();
                                start = false;
                            }
                        }
                    }

                    if (pctrl.isLockScreenActivated() && !devicePolicyManager.isAdminActive(compName)) {
                        lockScreenActivateSwitch.setChecked(false);
                        dialogAskDeviceAdminPermission();
                        start = false;
                    }

                    if (start) {
                        Logger.log(TAG, "start " + ClockService.TAG);
                        Intent intent = new Intent(MainActivity.this, ClockService.class);
                        startForegroundService(intent);

                        Logger.log(TAG, "start " + Main2Activity.TAG);
                        Intent in = new Intent(MainActivity.this, Main2Activity.class);
                        startActivity(in);

                        Toast.makeText(MainActivity.this, getString(R.string.home_timer_start_message), Toast.LENGTH_SHORT).show();

                        Logger.log(TAG, "finished.");
                        finish();
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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        // set onclick to protection mode button

        standardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() standard button");
                ProtectionMode protectionMode = run.getProtectionModeStandard();
                changeProtectionMode(protectionMode);
            }
        });

        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() high button");
                ProtectionMode protectionMode = run.getProtectionModeHigh();
                changeProtectionMode(protectionMode);
            }
        });

        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() low button");
                ProtectionMode protectionMode = run.getProtectionModeLow();
                changeProtectionMode(protectionMode);
            }
        });

        gamerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() gamer button");
                ProtectionMode protectionMode = run.getProtectionModeGamer();
                changeProtectionMode(protectionMode);
            }
        });

        // set on click to edit icons

        standardEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() standard edit icon");
                startProtectionLevelActivity(ProtectionLevel.STANDARD);
            }
        });

        highEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() high edit icon");
                startProtectionLevelActivity(ProtectionLevel.HIGH);
            }
        });

        lowEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() low edit icon");
                startProtectionLevelActivity(ProtectionLevel.LOW);
            }
        });

        gamerEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() gamer edit icon");
                startProtectionLevelActivity(ProtectionLevel.GAMER);
            }
        });

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

        changePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() change password text");
                dialogChangePassword();
            }
        });

        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() forget password");
                dialogForgetPassword();
            }
        });

        // todo : add others


        lockScreenActivateSwitch.setChecked(pctrl.isLockScreenActivated());
        lockScreenActivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked && !devicePolicyManager.isAdminActive(compName)) {
                    lockScreenActivateSwitch.setChecked(!checked);
                    dialogAskDeviceAdminPermission();
                } else if (checked) {
                    lockScreenLayout.setBackground(getDrawable(R.drawable.layout_round_shape_yellow_shade_blue));
                    pctrl.setLockScreenActivated(true);
                    pctrl.save();
                    Toast.makeText(MainActivity.this, "Lock Screen is On", Toast.LENGTH_SHORT).show();

                } else { // not checked
                    lockScreenLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
                    pctrl.setLockScreenActivated(false);
                    pctrl.save();
                    Toast.makeText(MainActivity.this, "Lock Screen is Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changeLockScreenTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() change lock screen time text");
                dialogChangeLockScreenTime();
            }
        });

        unlockScreenActivateSwitch = findViewById(R.id.switch_unlock_screen);
        unlockScreenActivateSwitch.setChecked(pctrl.isUnlockScreenActivated());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(TAG, "onResume()");
        // start main activity
       /* if (SplashScreen.isServiceRunning(ClockService.class.getName(), this)) {
            Logger.log(TAG, "ClockService is running !!!");
            Intent intent;
            if (run.getParentalControl().isPasswordActivated()) {
                Logger.log(TAG, "start " + PasswordActivity.class.getSimpleName() + "...");
                intent = new Intent(MainActivity.this, PasswordActivity.class);
            } else {
                Logger.log(TAG, "start " + Main2Activity.class.getSimpleName() + "...");
                intent = new Intent(MainActivity.this, Main2Activity.class);
            }

            startActivity(intent);
            finish();
        }*/
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
        ImageView breakingModeIcon = findViewById(R.id.breaking_mode_icon);
        breakingModeIcon.setImageResource(dw);

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
        ConstraintLayout breakingLayout = findViewById(R.id.linearLayout_breaking);
        ConstraintLayout breakingForLayout = findViewById(R.id.constraintLayout6);
        if (!pm.isBreakingActivated()) {
            breakingLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
            breakingModeIcon.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
            breakingForLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
        } else {
            breakingLayout.setBackground(getDrawable(R.drawable.layout_round_shape_yellow_shade_blue));
            breakingModeIcon.setBackground(getDrawable(R.drawable.layout_round_shape_accent));
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

        passwordActivateSwitch.setChecked(pctrl.isPasswordActivated());

        String lockScreenLabel = getString(R.string.lock_screen_activate_label) + " in "
                + (pctrl.getLockScreenInSec() / 60) + " mins";
        lockScreenTextView.setText(lockScreenLabel);
        lockScreenActivateSwitch.setChecked(pctrl.isLockScreenActivated());
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


    private void startProtectionLevelActivity(ProtectionLevel pl) {
        Logger.log(TAG, "startProtectionLevelActivity");
        Intent intent = new Intent(MainActivity.this, ProtectionLevelEditActivity.class);
        intent.putExtra(ProtectionLevelEditActivity.INTENT_EXTRA_PROTECTION_LEVEL_ORDINAL,
                pl.ordinal());
        startActivity(intent);
    }

    private void changeProtectionMode(ProtectionMode protectionMode) {
        pm = protectionMode;
        run.setCurrentProtectionMode(protectionMode);
        run.save();
        initInfoOnScreen();
        Toast.makeText(MainActivity.this,
                protectionMode.getName() + " applied !", Toast.LENGTH_SHORT).show();

    }




    private void dialogAskOverlayPermissionForStrongBM() {
        Logger.log(TAG, "dialogAskOverlayPermissionForStrongBM()");
        new AlertDialog.Builder(this)
                .setTitle("Overlay Permission")
                .setMessage("To apply strong discipline mode, please grant us the overlay permission (\"Appear on top\"). You can turn this off later on. " +
                        "Grant the permission ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No, I don't need strong discipline mode", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogChangeBreakingMode();
                    }
                })
                .setIcon(R.drawable.ic_face_black_24dp)
                .show();
    }

    private void dialogAskOverlayPermissionForBL() {
        Logger.log(TAG, "dialogAskOverlayPermissionForBL()");
        new AlertDialog.Builder(this)
                .setTitle("Overlay Permission")
                .setMessage("To apply blue light filter, please grant us the overlay permission (\"Appear on top\"). You can turn this off later on. " +
                        "Grant the permission ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No, I don't need blue light filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pm.setBluelightFiltering(false);
                        pm.save();
                        ConstraintLayout blLayout = findViewById(R.id.constraintLayout_bl_filter);
                        blLayout.setBackground(getDrawable(R.drawable.layout_round_shape_gray_shade_white));
                    }
                })
                .setIcon(R.drawable.ic_face_black_24dp)
                .show();
    }

    private void dialogAskDeviceAdminPermission() {
        Logger.log(TAG, "dialogAskDeviceAdminPermission()");
        new AlertDialog.Builder(this)
                .setTitle("Device Admin Permission")
                .setMessage("To manage lock screen, please grant us the device admin permission. You can turn this off later on. " +
                        "Grant the permission ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkAdminDevicePermission();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No, I don't need auto lock screen", null)
                .setIcon(R.drawable.ic_face_black_24dp)
                .show();
    }

    private void dialogChangePassword() {
        Logger.log(TAG, "dialogChangePassword()");

        LayoutInflater inflater = this.getLayoutInflater();
        View changePasswordLayout = inflater.inflate(R.layout.component_change_password_form, null);
        final EditText oldPasswordInputEditText = changePasswordLayout.findViewById(R.id.password_old_input);
        final EditText newPasswordInputEditText = changePasswordLayout.findViewById(R.id.password_new_input);
        final TextView passwordNotMatchTextView = changePasswordLayout.findViewById(R.id.password_not_match_text_view);
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
                String oldPassword = oldPasswordInputEditText.getText().toString();
                if (oldPassword.equals(pctrl.getPassword())) {
                    String newPassword = newPasswordInputEditText.getText().toString();
                    Logger.log(TAG, "new password is " + newPassword);
                    pctrl.setPassword(newPassword);
                    pctrl.save();
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "password change to " + newPassword, Toast.LENGTH_SHORT).show();
                } else {
                    passwordNotMatchTextView.setVisibility(View.VISIBLE);
                    passwordNotMatchTextView.setText("Password not matched.");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                int timeInMinute = Integer.valueOf(lockScreenTimeInputEditText.getText().toString());
                if (timeInMinute < 3) {
                    lockScreenTimeNotValidTextView.setText("Please enter a number greater than or equals to 3 mins.");
                    lockScreenTimeNotValidTextView.setVisibility(View.VISIBLE);
                } else if (timeInMinute > 300) {
                    lockScreenTimeNotValidTextView.setText("Please enter a number less than or equals to 300 mins (5 hours).");
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
                Logger.log(TAG, "cancel !");
                dialog.dismiss();
            }
        });
    }

    private void dialogForgetPassword() {
        Logger.log(TAG, "dialogForgetPassword()");
        new AlertDialog.Builder(this)
                .setTitle("Forget password, what to do ?")
                .setMessage("In order to prevent this being done by children, " +
                        "we don't support password reset. " +
                        "A solution is to uninstall and re-install the app. " +
                        "Factory default password is " + Constants.DEFAULT_PASSWORD)
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_info_black_24dp)
                .show();

    }

    private void dialogChangeBreakingMode() {
        Logger.log(TAG, "dialogChangeBreakingMode()");
        new AlertDialog.Builder(this)
                .setTitle("Do you want to change the current breaking mode ?")
                .setMessage("Change to Medium Discipline Mode")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pm.setBreakingMode(BreakingMode.MEDIUM);
                        pm.save();
                        ImageView breakingModeIcon = findViewById(R.id.breaking_mode_icon);
                        breakingModeIcon.setImageResource(R.drawable.ic_bm_medium_white_24dp);

                        Toast.makeText(MainActivity.this, "Change to Medium discipline", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(R.drawable.ic_face_black_24dp)
                .show();

    }

    private void checkAdminDevicePermission() {
        Logger.log(TAG, "checkAdminDevicePermission()");

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log(TAG, "on activity result()");
        switch(requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK) {
                    lockScreenActivateSwitch.setChecked(true);
                    pctrl.setLockScreenActivated(true);
                    Toast.makeText(MainActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Lock Screen is On", Toast.LENGTH_SHORT).show();
                } else {
                    pctrl.setLockScreenActivated(false);
                    lockScreenActivateSwitch.setChecked(false);
                    Toast.makeText(MainActivity.this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                pctrl.save();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}