package com.southiny.eyeware;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

/*import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;*/
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.service.ClockService;
import com.southiny.eyeware.service.LockAndUnlockScreenService;
import com.southiny.eyeware.tool.AdminReceiver;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;

import static com.southiny.eyeware.tool.Utils.zbs;

public class MainActivity extends AppCompatActivity {

   // int currentBackgroundColor = 0xffffffff;

    public static final String TAG = MainActivity.class.getSimpleName();

    private Run run;
    private ProtectionMode pm;
    private ParentalControl pctrl;

    Button standardButton, highButton, lowButton, gamerButton;
    ImageView standardEditIcon, highEditIcon, lowEditIcon, gamerEditIcon;
    ImageView standardCheckIcon, highCheckIcon, lowCheckIcon, gamerCheckIcon;
    Switch passwordActivateSwitch, lockScreenActivateSwitch, unlockScreenActivateSwitch;

    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;

    //private boolean showPermissionGrantedIndication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(TAG, "onCreate()");

        // set toolbar
        /*Logger.log(TAG, "set tool bar...");
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);*/

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
        ImageView settingsIcon = findViewById(R.id.settings_icon);

        passwordActivateSwitch = findViewById(R.id.switch_password_activate);
        TextView changePasswordTextView = findViewById(R.id.change_password_for_parental_control);

        /* *** set info on screen *****/

        initInfoOnScreen();

        /* *** set on click listeners ***/

        // set onclick to start button
        Logger.log(TAG, "set on click listener to start/stop timer");
        startStopTimerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick()");

                if (pm.isBluelightFiltering() && !Settings.canDrawOverlays(getApplicationContext())) { // if BL is on but no "appears on top permission"
                    dialogAskOverlayPermission();
                } else {
                    Logger.log(TAG, "start " + ClockService.TAG);
                    Intent intent = new Intent(MainActivity.this, ClockService.class);
                    startForegroundService(intent);

                    Logger.log(TAG, "start " + Main2Activity.TAG);
                    Intent in = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(in);

                    Toast.makeText(MainActivity.this, getString(R.string.home_timer_start_message), Toast.LENGTH_SHORT).show();

              /*  Intent in = new Intent(MainActivity.this, NotificationScreen.class);
                startActivity(in);*/
                    Logger.log(TAG, "finished.");
                    finish();
                }
            }
        });

        // set on click to settings icon
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onclick() settings icon");
                Intent intent = new Intent(MainActivity.this, LockPhoneActivity.class);
                startActivity(intent);

                /*Intent intent = new Intent(MainActivity.this, LockAndUnlockScreenService.class);
                intent.putExtra(LockAndUnlockScreenService.INTENT_EXTRA_LOCK_UNLOCK_CODE,
                        LockAndUnlockScreenService.LOCK_CODE);
                startService(intent);*/
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

        // todo : add others

        lockScreenActivateSwitch = findViewById(R.id.switch_lock_screen);
        lockScreenActivateSwitch.setChecked(pctrl.isLockScreenActivated());
        lockScreenActivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked && !devicePolicyManager.isAdminActive(compName)) {
                    lockScreenActivateSwitch.setChecked(!checked);
                    dialogAskDeviceAdminPermission();
                }
            }
        });

        unlockScreenActivateSwitch = findViewById(R.id.switch_unlock_screen);
        unlockScreenActivateSwitch.setChecked(pctrl.isUnlockScreenActivated());
        unlockScreenActivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked && !devicePolicyManager.isAdminActive(compName)) {
                    lockScreenActivateSwitch.setChecked(!checked);
                    dialogAskDeviceAdminPermission();
                }
            }
        });




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

    /******* private methods *******/

    private void initInfoOnScreen() {
        Logger.log(TAG, "initInfoOnScreen()");

        // set breaking time
        long breakingTime_sec = pm.getBreakingEvery_sec();
        int min = (int) (breakingTime_sec / 60);
        int sec = (int) (breakingTime_sec % 60);
        Logger.log(TAG, "set breaking time for " + min + "min" + sec + "sec...");
        TextView minuteTextView = findViewById(R.id.timer_minute);
        TextView secondTextView = findViewById(R.id.timer_second);
        minuteTextView.setText(zbs(min));
        secondTextView.setText(zbs(sec));

        // set button names
        standardButton.setText(run.getProtectionModeStandard().getName());
        highButton.setText(run.getProtectionModeHigh().getName());
        lowButton.setText(run.getProtectionModeLow().getName());
        gamerButton.setText(run.getProtectionModeGamer().getName());

        // set selected button
        ProtectionLevel pl = pm.getProtectionLevel();
        setSelectedButton(pl);

        passwordActivateSwitch.setChecked(pctrl.isPasswordActivated());
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




    private void dialogAskOverlayPermission() {
        Logger.log(TAG, "dialogAskOverlayPermission()");
        new AlertDialog.Builder(this)
                .setTitle("Overlay permission")
                .setMessage("To apply blue light filter, please grant us the overlay permission (\"Appear on top\")." +
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
                .setNegativeButton("No, I don't need blue light filter", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        //showPermissionGrantedIndication = true;
    }

    private void dialogAskDeviceAdminPermission() {
        Logger.log(TAG, "dialogAskDeviceAdminPermission()");
        new AlertDialog.Builder(this)
                .setTitle("Device admin permission")
                .setMessage("To manage lock screen, please grant us the device admin permission. " +
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
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        //showPermissionGrantedIndication = true;
    }


    private void dialogChangePassword() {
        Logger.log(TAG, "dialogChangePassword()");

        LayoutInflater inflater = this.getLayoutInflater();
        View changePasswordLayout = inflater.inflate(R.layout.change_password_form, null);
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
                //.setMessage(message)
                .setView(changePasswordLayout)
                .setIcon(R.drawable.ic_security_black_24dp)
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

    private void checkAdminDevicePermission() {
        Logger.log(TAG, "checkAdminDevicePermission()");

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log(TAG, "on actiivity result()");
        switch(requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK) {
                    lockScreenActivateSwitch.setChecked(true);
                    pctrl.setLockScreenActivated(true);
                    Toast.makeText(MainActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
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









    /*private void dialogOverlayPermissionGrantedInfo(String message) {
        Logger.log(TAG, "dialogOverlayPermissionGrantedInfo()");

        new AlertDialog.Builder(this)
                .setTitle("Overlay permission")
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }*/



   /* private void highButtonClick() {
        Logger.log(TAG, "highButtonClick()");

        String title = "High Protection";
        String message = "High protection is recommended for people who really care for their eyes or people who already have vision problem.\n" +
                "This mode will : \n" +
                "- Cut your screen every " + (Constants.HIGH_BREAKING_EVERY_SEC / 60) + " minutes, \n" +
                "- Cut for " + Constants.HIGH_BREAKING_FOR_SEC + " seconds then resume, \n" +
                "- Change screen filter color every " + (Constants.HIGH_BLUELIGHT_FILTER_CHANGE_EVERY_SEC / 60) +" minutes.";

        dialogProtectionButton(title, message, ProtectionLevel.HIGH);
    }

    private void standardButtonClick() {
        Logger.log(TAG, "standardButtonClick()");

        String title = "Standard Protection";
        String message = "Standard protection is recommended for normal people.\n" +
                "This mode will : \n" +
                "- Cut your screen every " + (Constants.STANDARD_BREAKING_EVERY_SEC / 60) + " minutes, \n" +
                "- Cut for " + Constants.STANDARD_BREAKING_FOR_SEC + " seconds then resume, \n" +
                "- Change screen filter color every " + (Constants.STANDARD_BLUELIGHT_FILTER_CHANGE_EVERY_SEC / 60) +" minutes.";

        dialogProtectionButton(title, message, ProtectionLevel.STANDARD);
    }

    private void lowButtonClick() {
        Logger.log(TAG, "lowButtonClick()");

        String title = "Low Protection";
        String message = "Low protection is recommended for people who must focus on the screen." +
                "Therefore, we try to disturb you as less as possible.\n" +
                "This mode will : \n" +
                "- Cut your screen every " + (Constants.LOW_BREAKING_EVERY_SEC /60) + " minutes, \n" +
                "- Cut for " + Constants.LOW_BREAKING_FOR_SEC + " seconds then resume, \n" +
                "- Change screen filter color every " + (Constants.LOW_BLUELIGHT_FILTER_CHANGE_EVERY_SEC / 60) +" minutes.";

        dialogProtectionButton(title, message, ProtectionLevel.LOW);
    }

    private void gamerButtonClick() {
        Logger.log(TAG, "gamerButtonClick()");

        String title = "Gamer Protection";
        String message = "this is testùmlesrkùqsmlfdlqsùllrlerzl!kdfjqslkdfjsqdflksf:lksdjqsdk!mlqaozieaozeiae";
        dialogProtectionButton(title, message, ProtectionLevel.GAMER);
    }*/






    /* private void checkAppearOnTopPermission() {
        // check "appear on top permission" grant
        Logger.log(TAG, "check \"appear on top permission\" grant...");
        if (!Settings.canDrawOverlays(this)) {
            Logger.log(TAG, "permission not granted.");
            if (showPermissionGrantedIndication) {
                dialogOverlayPermissionGrantedInfo("Permission not granted, blue light filter deactivated.");
            } else {
                Logger.log(TAG, "ask for permission...");
                dialogAskOverlayPermission();
            }
        } else {
            Logger.log(TAG, "permission granted.");
            if (showPermissionGrantedIndication) {
                dialogOverlayPermissionGrantedInfo("Permission granted, thank you.");
            }
        }
    }*/



    //Button goldButton = findViewById(R.id.button_change_color_gold);
        /*settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getApplicationContext())
                        .setTitle("Choose color")
                        .initialColor(currentBackgroundColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(MainActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                Toast.makeText(MainActivity.this, "onColorSelected: " + selectedColor, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });*/


}