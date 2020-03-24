package com.southiny.eyeware.database.model;

import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;

@Table(name = ProtectionMode.TABLE_NAME, database = AppDatabase.class)
public class ProtectionMode extends Model {

    public static final String TABLE_NAME = "protection_mode";

    public static final String COLUMN_NAME = "protection_name";
    public static final String COLUMN_BREAK_ACTIVATED = "break_activated";
    public static final String COLUMN_BREAKING_EVERY = "breaking_every";
    public static final String COLUMN_BREAKING_FOR = "breaking_for";
    public static final String COLUMN_BLUELIGHT_FILTERING = "bluelight_filtering";
    public static final String COLUMN_BLUELIGHT_FILTER_COLOR_CHANGE_EVERY = "blue_light_filter_change_every";


    public static final String COLUMN_DIM_AMOUNT = "dim_amount";
    public static final String COLUMN_SCREEN_ALPHA = "screen_alpha";

    public static final String COLUMN_PROTECTION_LEVEL_ORDINAL = "protection_level_ordinal";
    public static final String COLUMN_SCREEN_FILTER = "screen_filter_id";

    public static final String COLUMN_IS_CURRENT_PROTECTION_MODE = "is_current";

    public static final String COLUMN_BREAKING_MODE_ORDINAL = "breaking_mode_ordinal";

    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_NAME)
    private String name;

    @Column(name = COLUMN_PROTECTION_LEVEL_ORDINAL)
    private int protectionLevelOrdinal;

    @Column(name = COLUMN_BREAK_ACTIVATED)
    private boolean breakingActivated;

    @Column(name = COLUMN_BREAKING_EVERY)
    private int breakingEvery_sec;

    @Column(name = COLUMN_BREAKING_FOR)
    private int breakingFor_sec;

    @Column(name = COLUMN_BLUELIGHT_FILTER_COLOR_CHANGE_EVERY)
    private int blueLightFilterChangeEvery_sec;

    @Column(name = COLUMN_BLUELIGHT_FILTERING)
    private boolean bluelightFiltering;

    @Column(name = COLUMN_DIM_AMOUNT)
    private float dimAmount;

    @Column(name = COLUMN_SCREEN_ALPHA)
    private float screenAlpha;

    @Column(name = COLUMN_IS_CURRENT_PROTECTION_MODE)
    private boolean isCurrent;

    @Column(name = COLUMN_BREAKING_MODE_ORDINAL)
    private int breakingModeOrdinal;


    @Column(name = COLUMN_SCREEN_FILTER)
    private ScreenFilter screenFilter;


    // non utilise, mais necessaire
    public ProtectionMode() {
        setStandard();
        isCurrent = true;
    }

    public static ProtectionMode getStandardInstance() {
        ProtectionMode pm = new ProtectionMode();
        pm.setStandard();
        return pm;
    }

    public static ProtectionMode getHighInstance() {
        ProtectionMode pm = new ProtectionMode();
        pm.setHigh();
        return pm;
    }

    public static ProtectionMode getLowInstance() {
        ProtectionMode pm = new ProtectionMode();
        pm.setLow();
        return pm;
    }

    public static ProtectionMode getGamerInstance() {
        ProtectionMode pm = new ProtectionMode();
        pm.setGamer();
        return pm;
    }

    public void setStandard() {
        protectionLevelOrdinal = ProtectionLevel.STANDARD.ordinal(); // this is the real id
        name = ProtectionLevel.STANDARD.toString();
        breakingActivated = Constants.STANDARD_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.STANDARD_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.STANDARD_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.STANDARD_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.STANDARD_BLUELIGHT_FILTER_CHANGE;

        dimAmount = Constants.STANDARD_DIM_AMOUNT_PERCENT / 100F;
        screenAlpha = Constants.STANDARD_SCREEN_ALPHA_PERCENT / 100F;

        breakingModeOrdinal = Constants.STANDARD_BREAKING_MODE.ordinal();

        screenFilter = new ScreenFilter();
    }

    public void setHigh() {
        protectionLevelOrdinal = ProtectionLevel.HIGH.ordinal(); // this is the real id
        name = ProtectionLevel.HIGH.toString();
        breakingActivated = Constants.HIGH_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.HIGH_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.HIGH_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.HIGH_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.HIGH_BLUELIGHT_FILTER_CHANGE;

        dimAmount = Constants.HIGH_DIM_AMOUNT_PERCENT / 100F;
        screenAlpha = Constants.HIGH_SCREEN_ALPHA_PERCENT / 100F;

        breakingModeOrdinal = Constants.HIGH_BREAKING_MODE.ordinal();

        screenFilter = new ScreenFilter();
    }

    public void setLow() {
        protectionLevelOrdinal = ProtectionLevel.LOW.ordinal(); // this is the real id
        name = ProtectionLevel.LOW.toString();
        breakingActivated = Constants.LOW_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.LOW_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.LOW_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.LOW_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.LOW_BLUELIGHT_FILTER_CHANGE;

        dimAmount = Constants.LOW_DIM_AMOUNT_PERCENT / 100F;
        screenAlpha = Constants.LOW_SCREEN_ALPHA_PERCENT / 100F;

        breakingModeOrdinal = Constants.LOW_BREAKING_MODE.ordinal();

        screenFilter = new ScreenFilter();
    }

    public void setGamer() {
        protectionLevelOrdinal = ProtectionLevel.GAMER.ordinal(); // this is the real id
        name = ProtectionLevel.GAMER.toString();
        breakingActivated = Constants.GAMER_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.GAMER_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.GAMER_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.GAMER_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.GAMER_BLUELIGHT_FILTER_CHANGE;

        dimAmount = Constants.GAMER_DIM_AMOUNT_PERCENT / 100F;
        screenAlpha = Constants.GAMER_SCREEN_ALPHA_PERCENT / 100F;

        breakingModeOrdinal = Constants.GAMER_BREAKING_MODE.ordinal();

        screenFilter = new ScreenFilter();
    }

    public void reset() {

        if (protectionLevelOrdinal == ProtectionLevel.STANDARD.ordinal()) this.setStandard();

        else if (protectionLevelOrdinal == ProtectionLevel.HIGH.ordinal()) this.setHigh();

        else if (protectionLevelOrdinal == ProtectionLevel.LOW.ordinal()) this.setLow();

        else if (protectionLevelOrdinal == ProtectionLevel.GAMER.ordinal()) this.setGamer();
    }

    /******/



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBreakingActivated() {
        return breakingActivated;
    }

    public void setBreakingActivated(boolean breakingActivated) {
        this.breakingActivated = breakingActivated;
    }

    public int getBreakingEvery_sec() {
        return breakingEvery_sec;
    }

    public void setBreakingEvery_sec(int breakingEvery_sec) {
        this.breakingEvery_sec = breakingEvery_sec;
    }

    public int getBreakingFor_sec() {
        return breakingFor_sec;
    }

    public void setBreakingFor_sec(int breakingFor_sec) {
        this.breakingFor_sec = breakingFor_sec;
    }

    public int getBlueLightFilterChangeEvery_sec() {
        return blueLightFilterChangeEvery_sec;
    }

    public void setBlueLightFilterChangeEvery_sec(int blueLightFilterChangeEvery_sec) {
        this.blueLightFilterChangeEvery_sec = blueLightFilterChangeEvery_sec;
    }

    public boolean isBluelightFiltering() {
        return bluelightFiltering;
    }

    public void setBluelightFiltering(boolean bluelightFiltering) {
        this.bluelightFiltering = bluelightFiltering;
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

    public ScreenFilter getScreenFilter() {
        return screenFilter;
    }

    public void setScreenFilter(ScreenFilter screenFilter) {
        this.screenFilter = screenFilter;
    }

    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.getProtectionLevelByOrdinal(protectionLevelOrdinal);
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevelOrdinal = protectionLevel.ordinal();
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public BreakingMode getBreakingMode() {
        return BreakingMode.getBreakingModeByOrdinal(breakingModeOrdinal);
    }

    public void setBreakingMode(BreakingMode breakingMode) {
        this.breakingModeOrdinal = breakingMode.ordinal();
    }
}
