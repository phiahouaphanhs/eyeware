package com.southiny.eyeware.tool;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.southiny.eyeware.R;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TODO: document your custom view class.
 */
public class ColorFilterLinearLayout extends LinearLayout {

    private static final int min = 1000;
    private static final int max = 2000;
    private static final int srcIcon = R.drawable.ic_style_cream_24dp;

    private int id;
    private String colorCode = "#000000";
    private float alpha;
    private ImageView image;


    public ColorFilterLinearLayout(Context context, int id, String colorCode, float alpha) {
        super(context);
        this.id = id;
        this.colorCode = colorCode;
        this.alpha = alpha;
        create();

    }

    private void create() {
        LayoutParams params = new LinearLayout.LayoutParams(300, 200);
        this.setId(id);
        this.setBackground(this.getContext().getDrawable(R.drawable.layout_round_shape_black_shade));
        this.setGravity(Gravity.CENTER);
        params.setMargins(8,24,8,24);
        this.setLayoutParams(params);

        this.image = new ImageView(this.getContext());
        int randomID = ThreadLocalRandom.current().nextInt(min, max + 1);
        this.image.setId(randomID);
        String colorCode = '#' + Utils.getTransparencyCodeByAlpha(this.alpha) + this.colorCode.substring(1);
        this.image.setBackgroundColor(Color.parseColor(colorCode));
        this.image.setPadding(10,10,10,10);
        this.image.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.image.setImageResource(srcIcon);

        this.addView(this.image);

    }

    public ImageView getImage() {
        return image;
    }

    public void setSelected() {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = 300;
        this.setLayoutParams(params);
    }

    public void setUnSelected() {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = 200;
        this.setLayoutParams(params);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        String colorCode = '#' + Utils.getTransparencyCodeByAlpha(alpha) + this.colorCode.substring(1);
        this.image.setBackgroundColor(Color.parseColor(colorCode));
    }
}
