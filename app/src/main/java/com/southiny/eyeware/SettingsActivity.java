package com.southiny.eyeware;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.tool.Logger;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    private Run run;
    private ParentalControl pctrl;

    private Switch computerModeSwitch, vibrateActivateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        run = SQLRequest.getRun();
        pctrl =run.getParentalControl();

        TextView resetToDefaultTextView = findViewById(R.id.reset_to_default_text);
        resetToDefaultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() reset to default");
                dialogRequestPassword();
            }
        });

        vibrateActivateSwitch = findViewById(R.id.vibration_activate_switch);
        vibrateActivateSwitch.setChecked(run.isVibrationActivated());
        vibrateActivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                run.setVibrationActivated(checked);
                run.save();
                if (checked) {
                    Toast.makeText(SettingsActivity.this, "Vibration On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Vibration Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        computerModeSwitch = findViewById(R.id.computer_mode_activate_switch);
        computerModeSwitch.setChecked(!run.isSmartDetectActivated());
        computerModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                Logger.log(TAG, "set smart detect " + (!checked));

                if (checked) {
                    if (run.isVibrationActivated()) {
                        run.setSmartDetectActivated(!checked); // must be opposite if checked
                        run.save();
                        Toast.makeText(SettingsActivity.this, "Computer Mode On", Toast.LENGTH_SHORT).show();
                    } else {
                        dialogAskIfWishToAlsoActivateVibrate();
                    }

                } else {
                    run.setSmartDetectActivated(!checked); // must be opposite of checked
                    run.save();
                    Toast.makeText(SettingsActivity.this, "Computer Mode Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView computerModeTextView = findViewById(R.id.computer_mode_text_label);
        computerModeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogComputerModeInfo();
            }
        });

        TextView aboutAppTextView = findViewById(R.id.about_app);
        aboutAppTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAboutApp();
            }
        });

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "back icon click()");
                finish();
            }
        });
    }

    private void dialogResetDefaultConfirmation() {
        Logger.log(TAG, "dialogResetDefaultConfirmation()");
        new AlertDialog.Builder(this)
                .setTitle(getApplicationContext().getString(R.string.reset_to_default_title))
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLRequest.deleteAllData();
                        SQLRequest.whatInDB();
                        SQLRequest.getRun();
                        Toast.makeText(SettingsActivity.this, "Reset to default complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    private void dialogRequestPassword() {
        Logger.log(TAG, "dialogRequestPassword()");

        if (pctrl.getPassword().equals(Constants.DEFAULT_PASSWORD)) {
            // no need to verify password cauz it is default pwd
            dialogResetDefaultConfirmation();
        } else {

            LayoutInflater inflater = this.getLayoutInflater();
            View passwordLayout = inflater.inflate(R.layout.activity_password, null);

            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(passwordLayout)
                    .show();

            final EditText passwordInputEditText = passwordLayout.findViewById(R.id.password_input);
            final TextView passwordErrorTextView = passwordLayout.findViewById(R.id.password_error_message_text);
            ImageView unlockImageView = passwordLayout.findViewById(R.id.unlock_icon);
            unlockImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean passed = onDone(passwordInputEditText, passwordErrorTextView);
                    if (passed) {
                        dialog.dismiss();
                    }
                }
            });

            passwordInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        //do what you want on the press of 'done'
                        boolean passed = onDone(passwordInputEditText, passwordErrorTextView);
                        if (passed) {
                            dialog.dismiss();
                        }
                    }
                    return false;
                }
            });
        }

    }

    private void dialogComputerModeInfo() {
        Logger.log(TAG, "dialogComputerModeInfo()");
        new AlertDialog.Builder(this)
                .setTitle("Computer Mode")
                .setMessage("When this mode is turn on, we will notify you the break time even when the phone screen is off. " +
                        "This is useful when you want our app to discipline you while using other device such as computer, gaming console,...etc. " +
                        "Note that this mode will not work when the vibration is turn Off.")
                .setPositiveButton("Alright, turn it on", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        computerModeSwitch.setChecked(true);
                        /*if (!run.isVibrationActivated()) {
                            dialogAskIfWishToAlsoActivateVibrate();
                        } else {
                            run.setSmartDetectActivated(true);
                            run.save();
                            Toast.makeText(SettingsActivity.this, "Computer Mode On", Toast.LENGTH_SHORT).show();
                            computerModeSwitch.setChecked(true);
                        }*/
                    }
                })
                .setNegativeButton("No, turn it off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        computerModeSwitch.setChecked(false);
                        /*run.setSmartDetectActivated(false);
                        run.save();
                        Toast.makeText(SettingsActivity.this, "Computer Mode Off", Toast.LENGTH_SHORT).show();
                        computerModeSwitch.setChecked(false);*/
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    private void dialogAskIfWishToAlsoActivateVibrate() {
        Logger.log(TAG, "dialogAskIfWishToAlsoActivateVibrate()");
        new AlertDialog.Builder(this)
                .setTitle("Do you wish to also turn on vibration ?")
                .setMessage("Computer mode will not work without the vibration.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        run.setSmartDetectActivated(true);
                        run.setVibrationActivated(true);
                        run.save();
                        Toast.makeText(SettingsActivity.this, "Computer Mode On", Toast.LENGTH_SHORT).show();
                        computerModeSwitch.setChecked(true);
                        vibrateActivateSwitch.setChecked(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        computerModeSwitch.setChecked(false);
                    }
                })
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


    private boolean onDone(final EditText passwordInputEditText, final TextView passwordErrorTextView) {
        String enterPassword = passwordInputEditText.getText().toString();
        String correctPassword = pctrl.getPassword();

        boolean passed = validatePassword(enterPassword, correctPassword);

        if (passed) {
            Logger.log(TAG, "passed !");

            dialogResetDefaultConfirmation();

        } else {
            Logger.log(TAG, "incorrect !");
            passwordErrorTextView.setText(R.string.password_incorrect_error_message);

        }

        return passed;
    }

    private boolean validatePassword(String enterPassword, String correctPassword) {
        if (enterPassword.length() != correctPassword.length()) {
            return false;
        } else {
            for (int i = 0; i < correctPassword.length(); i++) {
                if (enterPassword.charAt(i) != correctPassword.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
    }
}
