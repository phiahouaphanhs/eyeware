package com.southiny.eyeware.database.model;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;

@Table(name = ScreenFilter.TABLE_NAME, database = AppDatabase.class)
public class ScreenFilter extends Model {

    public static final String TABLE_NAME = "filter_color";

    public static final String COLUMN_COLOR_CODE = "color_code";
    public static final String COLUMN_DIM_AMOUNT = "dim_amount";
    public static final String COLUMN_SCREEN_ALPHA = "screen_alpha";
    public static final String COLUMN_IS_ACTIVATED = "is_activated";

    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_COLOR_CODE)
    private String colorCode;

    @Column(name = COLUMN_DIM_AMOUNT)
    private float dimAmount;

    @Column(name = COLUMN_SCREEN_ALPHA)
    private float screenAlpha;

    @Column(name = COLUMN_IS_ACTIVATED)
    private boolean isActivated;


    // should never be called
    public ScreenFilter() {
        ScreenFilter sc = Constants.DEFAULT_SCREEN_FILTERS[0];
        this.colorCode = sc.colorCode;
        this.dimAmount = sc.dimAmount;
        this.screenAlpha = sc.screenAlpha;
        isActivated = false;
    }

    public ScreenFilter(String code, float dimAmount, float screenAlpha) {
        this.colorCode = code;
        this.dimAmount = dimAmount;
        this.screenAlpha = screenAlpha;
        isActivated = false;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public float getDimAmount() {
        return dimAmount;
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
    }

    public float getScreenAlpha() {
        return screenAlpha;
    }

    public void setScreenAlpha(float screenAlpha) {
        this.screenAlpha = screenAlpha;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public static void initsave() {
        for (ScreenFilter sc : Constants.DEFAULT_SCREEN_FILTERS) {
            sc.save();
        }
    }
}
