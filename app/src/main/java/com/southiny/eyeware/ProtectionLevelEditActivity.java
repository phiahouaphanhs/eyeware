package com.southiny.eyeware;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;


public class ProtectionLevelEditActivity extends AppCompatActivity {
    public static final String TAG = ProtectionLevelEditActivity.class.getSimpleName();

    public static final String INTENT_EXTRA_PROTECTION_LEVEL_ORDINAL = "protection_level_ordinal";

    private Run run;
    private ProtectionMode pm;
    private ScreenFilter sf;
    private EditText breakEveryMinEditText, breakForSecEditText, bluelightChangeEveryMinEditText, plNameEditText;
    private Switch breakingSwitch, bluelightSwitch;
    private ImageView breakingLightIcon, breakingMediumIcon, breakingStrongIcon;
    private Button goldColorButton, greenColorButton, pinkColorButton, brownColorButton, purpleColorButton,
    redColorButton, orangeColorButton, blueColorButton;
    private SeekBar alphaBar, dimBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(TAG, "onCreate()");

        Logger.log(TAG, "set content view to overlay_view activity_main");
        setContentView(R.layout.activity_protection_level_edit);

        Intent intent = getIntent();
        int ordinal = intent.getIntExtra(INTENT_EXTRA_PROTECTION_LEVEL_ORDINAL, -1);
        run = SQLRequest.getRun();

        Logger.log(TAG, "received ordinal = " + ordinal);

        if (ordinal == ProtectionLevel.STANDARD.ordinal()) {
            pm = run.getProtectionModeStandard();
            Logger.log(TAG, "is standard mode");
        } else if (ordinal == ProtectionLevel.HIGH.ordinal()) {
            pm = run.getProtectionModeHigh();
            Logger.log(TAG, "is high mode");
        } else if (ordinal == ProtectionLevel.LOW.ordinal()) {
            pm = run.getProtectionModeLow();
            Logger.log(TAG, "is low mode");
        } else if (ordinal == ProtectionLevel.GAMER.ordinal()) {
            pm = run.getProtectionModeGamer();
            Logger.log(TAG, "is gamer mode");
        } else {
            Logger.err(TAG, "ERROR : no protection mode found");
        }

        sf = pm.getScreenFilter();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(TAG, "onStart()");

        // find view by id
        plNameEditText = findViewById(R.id.protection_level_name_edit_text);
        breakEveryMinEditText = findViewById(R.id.break_every_input_text);
        breakForSecEditText = findViewById(R.id.break_for_input_text);
        bluelightChangeEveryMinEditText = findViewById(R.id.bluelight_change_input_text);
        breakingSwitch = findViewById(R.id.breaking_activate_switch);
        bluelightSwitch = findViewById(R.id.bluelight_activate_switch);
        breakingLightIcon = findViewById(R.id.notification_cut_icon);
        breakingMediumIcon = findViewById(R.id.unforce_screen_cut_icon);
        breakingStrongIcon = findViewById(R.id.force_screen_cut_icon);

        goldColorButton = findViewById(R.id.button_change_color_gold);
        greenColorButton = findViewById(R.id.button_change_color_green);
        pinkColorButton = findViewById(R.id.button_change_color_pink);
        brownColorButton = findViewById(R.id.button_change_color_brown);
        purpleColorButton = findViewById(R.id.button_change_color_purple);
        redColorButton = findViewById(R.id.button_change_color_red);
        orangeColorButton = findViewById(R.id.button_change_color_orange);
        blueColorButton = findViewById(R.id.button_change_color_blue);

        dimBar = findViewById(R.id.brightness_level_seek_bar);
        alphaBar = findViewById(R.id.transparency_level_seek_bar);


        setInfoOnScreen();

        Button applyButton = findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() apply button");
                // get input values
                String name = plNameEditText.getText().toString();
                int breakEverySec = Integer.valueOf(breakEveryMinEditText.getText().toString()) * 60;
                int breakForSec = Integer.valueOf(breakForSecEditText.getText().toString());
                int blueLightChangeEverySec = Integer.valueOf(bluelightChangeEveryMinEditText.getText().toString()) * 60;

                Logger.log(TAG, "break every sec : " + breakEverySec);
                Logger.log(TAG, "break for sec : " + breakForSec);
                Logger.log(TAG, "bluelight change every sec : " + blueLightChangeEverySec);

                boolean valid = validateInput(name, breakEverySec, breakForSec, blueLightChangeEverySec);

                // update
                if (valid) {
                    pm.setName(name);

                    pm.setBreakingEvery_sec(breakEverySec);
                    pm.setBreakingFor_sec(breakForSec);
                    pm.setBreakingActivated(breakingSwitch.isChecked());

                    pm.setBlueLightFilterChangeEvery_sec(blueLightChangeEverySec);
                    pm.setBluelightFiltering(bluelightSwitch.isChecked());

                    pm.save();

                    run.setCurrentProtectionMode(pm);
                    run.save();

                    Logger.log(TAG, "updated.");
                    Intent intent = new Intent(ProtectionLevelEditActivity.this, MainActivity.class);
                    startActivity(intent);

                    Toast.makeText(ProtectionLevelEditActivity.this, "Saved !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        ImageView resetIcon = findViewById(R.id.reset_icon);
        resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResetConfirmation();
            }
        });

        breakingLightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pm.setBreakingMode(BreakingMode.LIGHT);
                setSelectedBreakingMode(BreakingMode.LIGHT);
                Toast.makeText(ProtectionLevelEditActivity.this, "Light Discipline", Toast.LENGTH_SHORT).show();

            }
        });

        breakingMediumIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pm.setBreakingMode(BreakingMode.MEDIUM);
                setSelectedBreakingMode(BreakingMode.MEDIUM);
                Toast.makeText(ProtectionLevelEditActivity.this, "Medium Discipline", Toast.LENGTH_SHORT).show();

            }
        });

        breakingStrongIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pm.setBreakingMode(BreakingMode.STRONG);
                setSelectedBreakingMode(BreakingMode.STRONG);
                Toast.makeText(ProtectionLevelEditActivity.this, "Strong Discipline", Toast.LENGTH_SHORT).show();

            }
        });

        breakingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Toast.makeText(ProtectionLevelEditActivity.this, "Breaking On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProtectionLevelEditActivity.this, "Breaking Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bluelightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Toast.makeText(ProtectionLevelEditActivity.this, "Screen filtering On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProtectionLevelEditActivity.this, "Screen filtering Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //goldColorButton.setBackgroundColor(Color.);
        goldColorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialogChangeColorCode(1);
            }
        });

        // screen brightness
        dimBar.setEnabled(true);
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
                Toast.makeText(ProtectionLevelEditActivity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        // filter transparency
        alphaBar.setEnabled(true);
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
                Toast.makeText(ProtectionLevelEditActivity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();

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

    private void setInfoOnScreen() {
        Logger.log(TAG, "setInfoOnScreen()");
        plNameEditText.setText(pm.getName());

        breakingSwitch.setChecked(pm.isBreakingActivated());
        breakEveryMinEditText.setText(String.valueOf(pm.getBreakingEvery_sec() / 60));
        breakForSecEditText.setText(String.valueOf(pm.getBreakingFor_sec()));

        bluelightSwitch.setChecked(pm.isBluelightFiltering());
        bluelightChangeEveryMinEditText.setText(String.valueOf(pm.getBlueLightFilterChangeEvery_sec() / 60));

        setSelectedBreakingMode(pm.getBreakingMode());

    }

    private void setSelectedBreakingMode(BreakingMode breakingMode) {
        Logger.log(TAG, "setSelectedBreakingMode()");

        switch (breakingMode) {
            case LIGHT:
                _selectedBreakingMode(breakingLightIcon);
                _unSelectedBreakingMode(breakingMediumIcon);
                _unSelectedBreakingMode(breakingStrongIcon);
                break;

            case MEDIUM:
                _unSelectedBreakingMode(breakingLightIcon);
                _selectedBreakingMode(breakingMediumIcon);
                _unSelectedBreakingMode(breakingStrongIcon);
                break;

            case STRONG:
                _unSelectedBreakingMode(breakingLightIcon);
                _unSelectedBreakingMode(breakingMediumIcon);
                _selectedBreakingMode(breakingStrongIcon);
                break;
        }
    }

    private boolean validateInput(String name, int breakEverySec, int breakForSec, int blueLightChangeEverySec) {
        String errorMessage = "";
        boolean valid = true;

        if (name.length() > 20) {
            errorMessage += getResources().getString(R.string.error_message_input_pl_name);
            valid = false;
        }

        if (breakEverySec < 60) {
            if (!valid) errorMessage += "\n";
            errorMessage += getResources().getString(R.string.error_message_input_break_every);
            valid = false;
        }
        if (breakForSec < 5) {
            if (!valid) errorMessage += "\n";
            errorMessage += getResources().getString(R.string.error_message_input_break_for);
            valid = false;
        }
        if (blueLightChangeEverySec < 60) {
            if (!valid) errorMessage += "\n";
            errorMessage += getResources().getString(R.string.error_message_input_bluelight_change);
            valid = false;

        }

        if (!valid) {
            TextView inputErrMessageTextView = findViewById(R.id.settings_input_error_message);
            inputErrMessageTextView.setVisibility(View.VISIBLE);
            inputErrMessageTextView.setText(errorMessage);
        }

        return valid;
    }

    private void dialogResetConfirmation() {
        Logger.log(TAG, "dialogResetConfirmation()");
        new AlertDialog.Builder(this)
                //.setTitle(getApplicationContext().getString(R.string.reset_to_default_title))
                .setMessage("Reset this mode to default values ?")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pm.reset();
                        setInfoOnScreen();
                        Toast.makeText(ProtectionLevelEditActivity.this, "Reset complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    private void _selectedBreakingMode(ImageView icon) {
        icon.setBackground(getDrawable(R.drawable.layout_round_shape_blue));
    }

    private void _unSelectedBreakingMode(ImageView icon) {
        icon.setBackground(getDrawable(R.drawable.layout_round_shape_gray));
    }

    private void dialogChangeColorCode(final int index) {
        Logger.log(TAG, "dialogChangeColorCode(" + index + ")");

        LayoutInflater inflater = this.getLayoutInflater();
        final View changeColorCodeLayout = inflater.inflate(R.layout.change_color_code, null);
        final EditText colorCodeEditText = changeColorCodeLayout.findViewById(R.id.color_code_input_text);
        final CheckBox useThisFilterCheckBox = changeColorCodeLayout.findViewById(R.id.use_this_filter_checkbox);
        final TextView okButton = changeColorCodeLayout.findViewById(R.id.change_color_code_button_ok);
        final TextView cancelButton = changeColorCodeLayout.findViewById(R.id.change_color_code_button_cancel);

        colorCodeEditText.setText(sf.getCode(index));
        useThisFilterCheckBox.setChecked(sf.isActivated(index));

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Modify color code")
                //.setMessage(message)
                .setView(changeColorCodeLayout)
                .setIcon(R.drawable.ic_edit_black_24dp)
                .show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = colorCodeEditText.getText().toString();
                boolean checked = useThisFilterCheckBox.isChecked();

                if (validColorCode(code)) {
                    sf.setCode(index, code);
                    sf.setActivated(index, checked);
                    sf.save();
                    dialog.dismiss();
                } else {
                    TextView colorCodeNotValidTextView = changeColorCodeLayout.findViewById(R.id.color_code_not_valid_text_view);
                    colorCodeNotValidTextView.setText("The code you've entered is not a color code.");
                    colorCodeNotValidTextView.setVisibility(View.VISIBLE);
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

    private boolean validColorCode(String code) {
        return true;
    }

}
