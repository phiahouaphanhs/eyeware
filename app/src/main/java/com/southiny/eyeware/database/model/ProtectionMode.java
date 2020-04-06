package com.southiny.eyeware.database.model;

import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.ProtectionLevel;

import java.util.ArrayList;
import java.util.List;

@Table(name = ProtectionMode.TABLE_NAME, database = AppDatabase.class)
public class ProtectionMode extends Model {

    public static final String TABLE_NAME = "protection_mode";

    public static final String COLUMN_NAME = "protection_name";
    public static final String COLUMN_BREAK_ACTIVATED = "break_activated";
    public static final String COLUMN_BREAKING_EVERY = "breaking_every";
    public static final String COLUMN_BREAKING_FOR = "breaking_for";
    public static final String COLUMN_BLUELIGHT_FILTERING = "bluelight_filtering";
    public static final String COLUMN_BLUELIGHT_FILTER_COLOR_CHANGE_EVERY = "blue_light_filter_change_every";

    public static final String COLUMN_PROTECTION_LEVEL_ORDINAL = "protection_level_ordinal";

    public static final String COLUMN_IS_CURRENT_PROTECTION_MODE = "is_current";

    public static final String COLUMN_BREAKING_MODE_ORDINAL = "breaking_mode_ordinal";

    public static final String COLUMN_SCREEN_FILTER_0 = "screen_filter_0";
    public static final String COLUMN_SCREEN_FILTER_1 = "screen_filter_1";
    public static final String COLUMN_SCREEN_FILTER_2 = "screen_filter_2";
    public static final String COLUMN_SCREEN_FILTER_3 = "screen_filter_3";
    public static final String COLUMN_SCREEN_FILTER_4 = "screen_filter_4";
    public static final String COLUMN_SCREEN_FILTER_5 = "screen_filter_5";
    public static final String COLUMN_SCREEN_FILTER_6 = "screen_filter_6";
    public static final String COLUMN_SCREEN_FILTER_7 = "screen_filter_7";

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

    @Column(name = COLUMN_IS_CURRENT_PROTECTION_MODE)
    private boolean isCurrent;

    @Column(name = COLUMN_BREAKING_MODE_ORDINAL)
    private int breakingModeOrdinal;

    @Column(name = COLUMN_SCREEN_FILTER_0)
    private ScreenFilter screenFilter0;

    @Column(name = COLUMN_SCREEN_FILTER_1)
    private ScreenFilter screenFilter1;

    @Column(name = COLUMN_SCREEN_FILTER_2)
    private ScreenFilter screenFilter2;

    @Column(name = COLUMN_SCREEN_FILTER_3)
    private ScreenFilter screenFilter3;

    @Column(name = COLUMN_SCREEN_FILTER_4)
    private ScreenFilter screenFilter4;

    @Column(name = COLUMN_SCREEN_FILTER_5)
    private ScreenFilter screenFilter5;

    @Column(name = COLUMN_SCREEN_FILTER_6)
    private ScreenFilter screenFilter6;

    @Column(name = COLUMN_SCREEN_FILTER_7)
    private ScreenFilter screenFilter7;


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

        breakingModeOrdinal = Constants.STANDARD_BREAKING_MODE.ordinal();

        setScreenFilters(Constants.STANDARD_SCREEN_FILTERS_ACTIVATION);


    }

    public void setHigh() {
        protectionLevelOrdinal = ProtectionLevel.HIGH.ordinal(); // this is the real id
        name = ProtectionLevel.HIGH.toString();
        breakingActivated = Constants.HIGH_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.HIGH_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.HIGH_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.HIGH_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.HIGH_BLUELIGHT_FILTER_CHANGE;

        breakingModeOrdinal = Constants.HIGH_BREAKING_MODE.ordinal();

        setScreenFilters(Constants.HIGH_SCREEN_FILTERS_ACTIVATION);
    }

    public void setLow() {
        protectionLevelOrdinal = ProtectionLevel.LOW.ordinal(); // this is the real id
        name = ProtectionLevel.LOW.toString();
        breakingActivated = Constants.LOW_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.LOW_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.LOW_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.LOW_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.LOW_BLUELIGHT_FILTER_CHANGE;

        breakingModeOrdinal = Constants.LOW_BREAKING_MODE.ordinal();

        setScreenFilters(Constants.LOW_SCREEN_FILTERS_ACTIVATION);
    }

    public void setGamer() {
        protectionLevelOrdinal = ProtectionLevel.GAMER.ordinal(); // this is the real id
        name = ProtectionLevel.GAMER.toString();
        breakingActivated = Constants.GAMER_BREAKING_ACTIVATE;
        breakingEvery_sec = Constants.GAMER_BREAKING_EVERY_SEC;
        breakingFor_sec = Constants.GAMER_BREAKING_FOR_SEC;
        blueLightFilterChangeEvery_sec = Constants.GAMER_BLUELIGHT_FILTER_CHANGE_EVERY_SEC;
        bluelightFiltering = Constants.GAMER_BLUELIGHT_FILTER_CHANGE;

        breakingModeOrdinal = Constants.GAMER_BREAKING_MODE.ordinal();

        setScreenFilters(Constants.GAMER_SCREEN_FILTERS_ACTIVATION);
    }

    private void setScreenFilters (final ScreenFilter[] screenFilters) {

        screenFilter0 = screenFilters[0];

        screenFilter1 = screenFilters[1];

        screenFilter2 = screenFilters[2];

        screenFilter3 = screenFilters[3];

        screenFilter4 = screenFilters[4];

        screenFilter5 = screenFilters[5];

        screenFilter6 = screenFilters[6];

        screenFilter7 = screenFilters[7];
    }

    public void reset() {

        if (protectionLevelOrdinal == ProtectionLevel.STANDARD.ordinal()) this.setStandard();

        else if (protectionLevelOrdinal == ProtectionLevel.HIGH.ordinal()) this.setHigh();

        else if (protectionLevelOrdinal == ProtectionLevel.LOW.ordinal()) this.setLow();

        else if (protectionLevelOrdinal == ProtectionLevel.GAMER.ordinal()) this.setGamer();
    }

    public Long getId() {
        return id;
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

    public ArrayList<ScreenFilter> getActivatedScreenFiltersByOrder() {
        ArrayList<ScreenFilter> screenFilters = new ArrayList<>();

        if (screenFilter0.isActivated()) screenFilters.add(screenFilter0);

        if (screenFilter1.isActivated()) {
            if (screenFilter1.getOrder() > screenFilter0.getOrder()) {
                screenFilters.add(screenFilter1);
            } else {
                screenFilters.add(0, screenFilter1);
            }
        }

        if (screenFilter2.isActivated()) {
            int i = positionToAdd(screenFilter2, screenFilters);
            screenFilters.add(i, screenFilter2);
        }

        if (screenFilter3.isActivated()) {
            int i = positionToAdd(screenFilter3, screenFilters);
            screenFilters.add(i, screenFilter3);
        }

        if (screenFilter4.isActivated()) {
            int i = positionToAdd(screenFilter4, screenFilters);
            screenFilters.add(i, screenFilter4);
        }

        if (screenFilter5.isActivated()) {
            int i = positionToAdd(screenFilter5, screenFilters);
            screenFilters.add(i, screenFilter5);
        }

        if (screenFilter6.isActivated()) {
            int i = positionToAdd(screenFilter6, screenFilters);
            screenFilters.add(i, screenFilter6);
        }

        if (screenFilter7.isActivated()) {
            int i = positionToAdd(screenFilter7, screenFilters);
            screenFilters.add(i, screenFilter7);
        }

        return screenFilters;
    }

    private int positionToAdd(ScreenFilter screenFilter, ArrayList<ScreenFilter> screenFilters) {
        boolean add = false;
        int i;
        for (i = 0; !add && i < screenFilters.size(); i++) {
            if (screenFilter.getOrder() < screenFilters.get(i).getOrder()) {
                add = true;
            }
        }
        return i;
    }

    public int getNbActivatedScreenFilters() {
        int nb = 0;

        if (screenFilter0.isActivated()) nb ++;
        if (screenFilter1.isActivated()) nb ++;
        if (screenFilter2.isActivated()) nb ++;
        if (screenFilter3.isActivated()) nb ++;
        if (screenFilter4.isActivated()) nb ++;
        if (screenFilter5.isActivated()) nb ++;
        if (screenFilter6.isActivated()) nb ++;
        if (screenFilter7.isActivated()) nb ++;

        return nb;
    }

    public ScreenFilter getScreenFilter0() {
        return screenFilter0;
    }

    public void setScreenFilter0(ScreenFilter screenFilter0) {
        this.screenFilter0 = screenFilter0;
    }

    public ScreenFilter getScreenFilter1() {
        return screenFilter1;
    }

    public void setScreenFilter1(ScreenFilter screenFilter1) {
        this.screenFilter1 = screenFilter1;
    }

    public ScreenFilter getScreenFilter2() {
        return screenFilter2;
    }

    public void setScreenFilter2(ScreenFilter screenFilter2) {
        this.screenFilter2 = screenFilter2;
    }

    public ScreenFilter getScreenFilter3() {
        return screenFilter3;
    }

    public void setScreenFilter3(ScreenFilter screenFilter3) {
        this.screenFilter3 = screenFilter3;
    }

    public ScreenFilter getScreenFilter4() {
        return screenFilter4;
    }

    public void setScreenFilter4(ScreenFilter screenFilter4) {
        this.screenFilter4 = screenFilter4;
    }

    public ScreenFilter getScreenFilter5() {
        return screenFilter5;
    }

    public void setScreenFilter5(ScreenFilter screenFilter5) {
        this.screenFilter5 = screenFilter5;
    }

    public ScreenFilter getScreenFilter6() {
        return screenFilter6;
    }

    public void setScreenFilter6(ScreenFilter screenFilter6) {
        this.screenFilter6 = screenFilter6;
    }

    public ScreenFilter getScreenFilter7() {
        return screenFilter7;
    }

    public void setScreenFilter7(ScreenFilter screenFilter7) {
        this.screenFilter7 = screenFilter7;
    }

    public ScreenFilter[] getScreenFilters() {
        ScreenFilter[] screenFilters = new ScreenFilter[8];

        screenFilters[0] = screenFilter0;
        screenFilters[1] = screenFilter1;
        screenFilters[2] = screenFilter2;
        screenFilters[3] = screenFilter3;
        screenFilters[4] = screenFilter4;
        screenFilters[5] = screenFilter5;
        screenFilters[6] = screenFilter6;
        screenFilters[7] = screenFilter7;

        return screenFilters;

    }
}
