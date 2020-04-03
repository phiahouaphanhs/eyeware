package com.southiny.eyeware;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.southiny.eyeware.tool.Logger;

/*import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;*/

public class TestActivity extends AppCompatActivity {

    // int currentBackgroundColor = 0xffffffff;

    private View mOverlayView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setPreferenceName("MyColorPickerView");
        colorPickerView.setSelectorPoint(293, 139);
        colorPickerView.saveData();

        TextView textView = findViewById(R.id.color_text);
        String text = " x=" + colorPickerView.getSelectorX() + " y=" + colorPickerView.getSelectorY();
        textView.setText(text);

        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                LinearLayout linearLayout = findViewById(R.id.result_color);
                linearLayout.setBackgroundColor(colorEnvelope.getColor());

                String htmlCode = colorEnvelope.getColorHtml(); // String
                int[] rgb = colorEnvelope.getColorRGB(); // int[3]

                TextView textView = findViewById(R.id.color_text);
                String text = htmlCode + " x=" + colorPickerView.getSelectorX() + " y=" + colorPickerView.getSelectorY();
                textView.setText(text);

                String colorCode = '#' + htmlCode;
                if (Settings.canDrawOverlays(TestActivity.this)) {
                    removeFilter();
                    overlay(colorCode, 0.1F, 0.1F);
                }
            }
        });
    }

    private void removeFilter() {

        if (mOverlayView != null) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            assert wm != null;
            wm.removeViewImmediate(mOverlayView);
        } else {
            Logger.warn("test", "overlay view is null");
        }
    }


    private void overlay(final String colorCode, final float alpha, final float dim) {

        Logger.log("test:", colorCode);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,

                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,

                PixelFormat.TRANSLUCENT);

        // An alpha value to apply to this entire window.
        // An alpha of 1.0 means fully opaque and 0.0 means fully transparent
        params.alpha = alpha;

        // When FLAG_DIM_BEHIND is set, this is the amount of dimming to apply.
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        params.dimAmount = dim;

        //params.buttonBrightness = 0.8F;

        // params.screenBrightness = 0.2F;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Logger.log("yo:", "pass");
        assert inflater != null;
        mOverlayView = inflater.inflate(R.layout.screen_overlay, null);

        mOverlayView.setBackgroundColor(Color.parseColor(colorCode));

        mOverlayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        Logger.log("yes:", "pass");
        assert wm != null;
        try {
            wm.addView(mOverlayView, params);
            Logger.log("ya:", "passed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


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
