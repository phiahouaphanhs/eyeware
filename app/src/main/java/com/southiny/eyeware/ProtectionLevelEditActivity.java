package com.southiny.eyeware;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;
import com.southiny.eyeware.tool.Utils;

import java.util.ArrayList;

import static com.southiny.eyeware.MainActivity.RESULT_ENABLE;


public class ProtectionLevelEditActivity extends AppCompatActivity {
    public static final String TAG = ProtectionLevelEditActivity.class.getSimpleName();

    public static final String INTENT_EXTRA_PROTECTION_LEVEL_ORDINAL = "protection_level_ordinal";

    private View mOverlayView = null;
    private boolean askOverlayPermission = false;
    private String temp_selectedColorCode;
    private float temp_selectedDim, temp_selectedAlpha;
    private boolean temp_overlayIt;
    private RadioButton temp_checkedRadioButton = null;

    private Run run;
    private ProtectionMode pm;
    private ScreenFilter[] scs = new ScreenFilter[8];

    private EditText breakEveryMinEditText, breakForSecEditText, bluelightChangeEveryMinEditText, plNameEditText;
    private Switch breakingSwitch, bluelightSwitch;
    private ImageView breakingLightIcon, breakingMediumIcon, breakingStrongIcon;
    private ImageView[] colorIcons = new ImageView[8];
    private ImageView[] colorEditIcons = new ImageView[8];
    private TextView[] filterNumberTextViews = new TextView[8];

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

        scs = pm.getScreenFilters();

        for (int i = 0; i < scs.length; i++) {
            Logger.log(TAG, scs[i].print());
        }

        askOverlayPermission = true;
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

        colorIcons[0] = findViewById(R.id.button_change_color_gold);
        colorIcons[1] = findViewById(R.id.button_change_color_green);
        colorIcons[2] = findViewById(R.id.button_change_color_pink);
        colorIcons[3] = findViewById(R.id.button_change_color_brown);
        colorIcons[4] = findViewById(R.id.button_change_color_purple);
        colorIcons[5] = findViewById(R.id.button_change_color_red);
        colorIcons[6] = findViewById(R.id.button_change_color_orange);
        colorIcons[7] = findViewById(R.id.button_change_color_blue);

        colorEditIcons[0] = findViewById(R.id.edit_icon_gold);
        colorEditIcons[1] = findViewById(R.id.edit_icon_green);
        colorEditIcons[2] = findViewById(R.id.edit_icon_pink);
        colorEditIcons[3] = findViewById(R.id.edit_icon_brown);
        colorEditIcons[4] = findViewById(R.id.edit_icon_purple);
        colorEditIcons[5] = findViewById(R.id.edit_icon_red);
        colorEditIcons[6] = findViewById(R.id.edit_icon_orange);
        colorEditIcons[7] = findViewById(R.id.edit_icon_blue);

        filterNumberTextViews[0] = findViewById(R.id.textView1);
        filterNumberTextViews[1] = findViewById(R.id.textView2);
        filterNumberTextViews[2] = findViewById(R.id.textView3);
        filterNumberTextViews[3] = findViewById(R.id.textView4);
        filterNumberTextViews[4] = findViewById(R.id.textView5);
        filterNumberTextViews[5] = findViewById(R.id.textView6);
        filterNumberTextViews[6] = findViewById(R.id.textView7);
        filterNumberTextViews[7] = findViewById(R.id.textView8);


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

                    for (int i = 0; i < scs.length; i++) {
                        scs[i].save();
                    }

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

        for (int i = 0; i < colorEditIcons.length; i++) {
            ColorChangeListener ln = new ColorChangeListener(i);
            colorEditIcons[i].setOnClickListener(ln);
        }

        for (int i = 0; i < colorIcons.length; i++) {
            ColorActivateListener ln = new ColorActivateListener(i);
            colorIcons[i].setOnClickListener(ln);
        }
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

        removeFilter();

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

        for (int i = 0; i < scs.length; i++) {
            String colorCode = '#' + Utils.getTransparencyCodeByAlpha(scs[i].getScreenAlpha()) + scs[i].getColorCode().substring(1);
            colorIcons[i].setBackgroundColor(Color.parseColor(colorCode));
            setActivatedColor(i, scs[i].isActivated());
            filterNumberTextViews[i].setText(String.valueOf(scs[i].getOrder()));
        }
    }

    private void setActivatedColor(int index, boolean activated) {
        Logger.log(TAG, "setActivatedColor(" + index + ", " + activated + ")");
        if (activated) {
            colorIcons[index].setImageResource(R.drawable.ic_style_cream_24dp);
        } else {
            colorIcons[index].setImageResource(R.drawable.ic_style_grey_24dp);
        }
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
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                //.setTitle(getApplicationContext().getString(R.string.reset_to_default_title))
                .setMessage("Reset this mode to default values ?")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pm.reset();
                        scs = pm.getScreenFilters();
                        setInfoOnScreen();
                        Toast.makeText(ProtectionLevelEditActivity.this, "Reset complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(R.drawable.ic_face_accent_24dp)
                .show();

    }

    private void _selectedBreakingMode(ImageView icon) {
        icon.setBackground(getDrawable(R.drawable.layout_round_shape_blue));
    }

    private void _unSelectedBreakingMode(ImageView icon) {
        icon.setBackground(getDrawable(R.drawable.layout_round_shape_gray));
    }

    private void dialogChangeColorCode(final int index, boolean overlayIt) {
        Logger.log(TAG, "dialogChangeColorCode(" + index + ")");

        LayoutInflater inflater = this.getLayoutInflater();
        final View changeColorCodeLayout = inflater.inflate(R.layout.component_change_color_code, null);
        final TextView okButton = changeColorCodeLayout.findViewById(R.id.change_color_code_button_ok);
        final TextView cancelButton = changeColorCodeLayout.findViewById(R.id.change_color_code_button_cancel);
        final SeekBar dimBar = changeColorCodeLayout.findViewById(R.id.brightness_level_seek_bar);
        final SeekBar alphaBar = changeColorCodeLayout.findViewById(R.id.transparency_level_seek_bar);
        final LinearLayout globeLayout = changeColorCodeLayout.findViewById(R.id.change_color_layout);
        final ColorPickerView colorPickerView = changeColorCodeLayout.findViewById(R.id.colorPickerView);
        final TextView selectResultTextView = changeColorCodeLayout.findViewById(R.id.select_result_text);
        final TextView overlayPermissionMessage = changeColorCodeLayout.findViewById(R.id.overlay_permission_deactivated_message);

        final RadioButton radioButton1 = changeColorCodeLayout.findViewById(R.id.radio_button_1);
        final RadioButton radioButton2 = changeColorCodeLayout.findViewById(R.id.radio_button_2);
        final RadioButton radioButton3 = changeColorCodeLayout.findViewById(R.id.radio_button_3);
        final RadioButton radioButton4 = changeColorCodeLayout.findViewById(R.id.radio_button_4);
        final RadioButton radioButton5 = changeColorCodeLayout.findViewById(R.id.radio_button_5);
        final RadioButton radioButton6 = changeColorCodeLayout.findViewById(R.id.radio_button_6);
        final RadioButton radioButton7 = changeColorCodeLayout.findViewById(R.id.radio_button_7);
        final RadioButton radioButton8 = changeColorCodeLayout.findViewById(R.id.radio_button_8);

        final ArrayList<RadioButton> radioButtons = new ArrayList<>();
        radioButtons.add(radioButton1);
        radioButtons.add(radioButton2);
        radioButtons.add(radioButton3);
        radioButtons.add(radioButton4);
        radioButtons.add(radioButton5);
        radioButtons.add(radioButton6);
        radioButtons.add(radioButton7);
        radioButtons.add(radioButton8);


        for (int i = 0; i < radioButtons.size(); i++) {
            radioButtons.get(i).setOnCheckedChangeListener(new RadioButtonListener(radioButtons.get(i)));
        }
        radioButtons.get(scs[index].getOrder() - 1).setChecked(true); // must after add listeners

        // color
        Logger.log(TAG, scs[index].print());

        temp_selectedColorCode = scs[index].getColorCode();
        temp_selectedDim = scs[index].getDimAmount();
        temp_selectedAlpha = scs[index].getScreenAlpha();
        temp_overlayIt = overlayIt;

        colorPickerView.setPreferenceName("EyewareColorPicker_" + pm.getId() + "_" + index);

        String text = temp_selectedColorCode;
        selectResultTextView.setText(text);

        if (temp_overlayIt) {
            temp_overlayIt = overlay(temp_selectedColorCode, temp_selectedAlpha, temp_selectedDim);
        }
        else {
            overlayPermissionMessage.setText("No overlay permission, cannot test in real time (but your modification will still be taken into account).");
            overlayPermissionMessage.setVisibility(View.VISIBLE);
            String colorCode = '#' + Utils.getTransparencyCodeByAlpha(temp_selectedAlpha) + temp_selectedColorCode.substring(1);
            globeLayout.setBackgroundColor(Color.parseColor(colorCode));

        }

        colorPickerView.setColorListener(new ColorListener() {
            private int nb = 0;
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                Logger.log(TAG, "onColorSelected()");

                temp_selectedColorCode  = '#' + colorEnvelope.getColorHtml();

                String text = temp_selectedColorCode;
                selectResultTextView.setText(text);

                if (nb >= 2) { // car qu'il trigger 2 fois pour rien et ça nous embete
                    if (temp_overlayIt) {
                        removeFilter();
                        temp_overlayIt = overlay(temp_selectedColorCode, temp_selectedAlpha, temp_selectedDim);
                    } else {
                        globeLayout.setBackgroundColor(Color.parseColor('#' + Utils.getTransparencyCodeByAlpha(temp_selectedAlpha) + colorEnvelope.getColorHtml()));
                    }
                }

                nb++;

            }
        });

        // screen brightness
        dimBar.setMax(Constants.DEFAULT_DIM_MAX_PERCENT);
        dimBar.setMin(Constants.DEFAULT_DIM_MIN_PERCENT);
        dimBar.setProgress(Constants.DEFAULT_DIM_MAX_PERCENT - (int)(temp_selectedDim * 100));
        dimBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                temp_selectedDim = (float) (Constants.DEFAULT_DIM_MAX_PERCENT - seekBar.getProgress()) / 100F;
                Toast.makeText(ProtectionLevelEditActivity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
                if (temp_overlayIt) {
                    removeFilter();
                    temp_overlayIt = overlay(temp_selectedColorCode, temp_selectedAlpha, temp_selectedDim);
                } else {
                    String colorCode = '#' + Utils.getTransparencyCodeByAlpha(temp_selectedAlpha) + temp_selectedColorCode.substring(1);
                    globeLayout.setBackgroundColor(Color.parseColor(colorCode));                }
            }
        });

        // filter transparency
        alphaBar.setMax(Constants.DEFAULT_ALPHA_MAX_PERCENT);
        alphaBar.setMin(Constants.DEFAULT_ALPHA_MIN_PERCENT);
        alphaBar.setProgress(Constants.DEFAULT_ALPHA_MAX_PERCENT - (int)(temp_selectedAlpha * 100));
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                temp_selectedAlpha = (float) (Constants.DEFAULT_ALPHA_MAX_PERCENT - seekBar.getProgress()) / 100F;
                Toast.makeText(ProtectionLevelEditActivity.this, seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
                if (temp_overlayIt) {
                    removeFilter();
                    temp_overlayIt = overlay(temp_selectedColorCode, temp_selectedAlpha, temp_selectedDim);
                } else {
                    String colorCode = '#' + Utils.getTransparencyCodeByAlpha(temp_selectedAlpha) + temp_selectedColorCode.substring(1);
                    globeLayout.setBackgroundColor(Color.parseColor(colorCode));
                }
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(changeColorCodeLayout)
                .setIcon(R.drawable.ic_edit_black_24dp)
                .setCancelable(false)
                .show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logger.log(TAG, "ok !");
                if (temp_overlayIt) {
                    removeFilter();
                    mOverlayView = null;
                }

                scs[index].setColorCode(temp_selectedColorCode);
                scs[index].setDimAmount(temp_selectedDim);
                scs[index].setScreenAlpha(temp_selectedAlpha);

                String code = '#' + Utils.getTransparencyCodeByAlpha(temp_selectedAlpha) + temp_selectedColorCode.substring(1);
                colorIcons[index].setBackgroundColor(Color.parseColor(code));

                int order = Integer.valueOf(temp_checkedRadioButton.getText().toString());
                int previousOrder = scs[index].getOrder();

                int i = getFilterIndexHavingOrder(order);

                Logger.log(TAG, "order = " + order);
                Logger.log(TAG, "previousOrder = " + previousOrder);
                Logger.log(TAG, "filter having order is " + i);

                scs[index].setOrder(order);
                scs[i].setOrder(previousOrder);

                String text = order + "";
                filterNumberTextViews[index].setText(text);
                text = previousOrder + "";
                filterNumberTextViews[i].setText(text);


                colorPickerView.saveData();

                dialog.dismiss();

            }


            private int getFilterIndexHavingOrder(int order) {
                int index = -1;
                for (int i = 0; index == -1 &&i < scs.length; i++) {
                    if (scs[i].getOrder() == order) {
                        index = i;
                    }
                }
                return index;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "cancel !");
                if (temp_overlayIt) {
                    removeFilter();
                    mOverlayView = null;
                }
                dialog.dismiss();
            }
        });
    }


    private void dialogAskOverlayPermission() {
        Logger.log(TAG, "dialogAskOverlayPermission()");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Overlay Permission")
                .setMessage("To see in the filter color change in real time, please grant us the overlay permission (\"Appear on top\"). You can turn this off later on. " +
                        "Grant the permission ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    }
                })

                .setNeutralButton("No", null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Don't ask me again, I don't need to see it in real time", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askOverlayPermission = false;
                    }
                })
                .setIcon(R.drawable.ic_face_accent_24dp)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log(TAG, "on activity result()");
        switch(requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(ProtectionLevelEditActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProtectionLevelEditActivity.this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /* ********************************************/

    private boolean removeFilter() {

        if (mOverlayView != null && Settings.canDrawOverlays(ProtectionLevelEditActivity.this)) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            assert wm != null;
            wm.removeViewImmediate(mOverlayView);
            return true;
        } else {
            Logger.warn(TAG, "overlay view is null");
            return false;
        }
    }


    private boolean overlay(final String colorCode, final float alpha, final float dim) {

        if (Settings.canDrawOverlays(ProtectionLevelEditActivity.this)) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,

                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY |
                            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,

                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS ,

                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.BOTTOM;
            params.y = -1;

            params.alpha = alpha;

            params.dimAmount = dim;

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            mOverlayView = inflater.inflate(R.layout.screen_overlay, null);

            mOverlayView.setBackgroundColor(Color.parseColor(colorCode));

            mOverlayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

            assert wm != null;
            try {
                wm.addView(mOverlayView, params);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            Logger.err(TAG, "can not overlay, no permission");
            return false;
        }
    }


    /* *******************************************/


    private class ColorChangeListener implements View.OnClickListener {

        public int index = 0;

        public ColorChangeListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View view) {
            if (!Settings.canDrawOverlays(ProtectionLevelEditActivity.this)) {
                if (askOverlayPermission) {
                    dialogAskOverlayPermission();
                } else {
                    dialogChangeColorCode(index, false);
                }
            } else {
                dialogChangeColorCode(index, true);
            }
        }
    }

    private class ColorActivateListener implements View.OnClickListener {

        public int index = 0;

        public ColorActivateListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View view) {
            scs[index].setActivated(!scs[index].isActivated());
            setActivatedColor(index, scs[index].isActivated());

        }
    }

    private class RadioButtonListener implements CompoundButton.OnCheckedChangeListener {

        public RadioButton radioButton;

        public RadioButtonListener(RadioButton radioButton) {
            this.radioButton = radioButton;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            Logger.log(TAG, "on check change: " + checked);

            radioButton.setChecked(checked);
            if (checked) {
                if (temp_checkedRadioButton != null) {
                    temp_checkedRadioButton.setChecked(!checked);
                }

                Logger.log(TAG, "current radio button is " + radioButton.getText());
                temp_checkedRadioButton = radioButton;
            }
        }
    }





}
