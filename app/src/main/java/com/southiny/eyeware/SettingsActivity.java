package com.southiny.eyeware;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.Utils;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    private Run run;
    private ParentalControl pctrl;
    private AudioManager audioManager;

    private Switch computerModeSwitch, vibrateActivateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        run = SQLRequest.getRun();
        pctrl = run.getParentalControl();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        TextView resetToDefaultTextView = findViewById(R.id.reset_to_default_text);
        resetToDefaultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimate(view);
                playClickSound();
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
        vibrateActivateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClickSound();
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
        computerModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClickSound();
            }
        });

        TextView computerModeTextView = findViewById(R.id.computer_mode_text_label);
        computerModeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimate(view);
                playClickSound();
                dialogComputerModeInfo();
            }
        });

        TextView aboutAppTextView = findViewById(R.id.about_app);
        aboutAppTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimate(view);
                playClickSound();
                dialogAboutApp();
            }
        });

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimate(view);
                playClickSound();
                Logger.log(TAG, "back icon click()");
                finish();
            }
        });
    }

    private void dialogResetDefaultConfirmation() {
        Logger.log(TAG, "dialogResetDefaultConfirmation()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle(getApplicationContext().getString(R.string.reset_to_default_title))
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        playClickSound();
                        SQLRequest.deleteAllData();
                        SQLRequest.whatInDB();
                        SQLRequest.getRun();
                        Toast.makeText(SettingsActivity.this, "Reset to default complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(R.drawable.ic_report_yellow_24dp)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        playClickSound();
                    }
                })
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
            Utils.blinkButterfly(unlockImageView, getApplicationContext());
            unlockImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickAnimate(view);
                    playClickSound();
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
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Computer Mode")
                .setMessage("When the break time arrive, we will notify you even if you are not looking at the phone screen right now (the phone screen is off). " +
                        "This is useful when you want our app to discipline you while using other device such as computer, gaming console,...etc. " +
                        "Note that this mode will not work when the vibration is turn Off.")
                .setPositiveButton("Alright, turn it on", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        playClickSound();
                        computerModeSwitch.setChecked(true);
                    }
                })
                .setNegativeButton("No, turn it off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        playClickSound();
                        computerModeSwitch.setChecked(false);
                    }
                })
                .setIcon(R.drawable.ic_computer_blue_24dp)
                .show();
    }


    private void dialogAskIfWishToAlsoActivateVibrate() {
        Logger.log(TAG, "dialogAskIfWishToAlsoActivateVibrate()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Do you wish to turn on vibration ?")
                .setMessage("Computer mode will not work without vibration on.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        playClickSound();
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
                        playClickSound();
                        computerModeSwitch.setChecked(false);
                    }
                })
                .setIcon(R.drawable.ic_face_accent_24dp)
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
                        playClickSound();
                    }
                })
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

    private void playClickSound() {
        audioManager.playSoundEffect(SoundEffectConstants.CLICK,1.0f);
    }

    private void clickAnimate(View view) {
        Utils.fade(view, getApplicationContext());
    }
}
