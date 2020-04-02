package com.southiny.eyeware;

import com.reactiveandroid.query.Delete;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Language;
import com.southiny.eyeware.tool.ProtectionLevel;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public final class Constants {

    public static final String CHANNEL_ID = "NoMoreEyeStrainChannel";
    public static final long SPLASH_TIME = 200;
    public static final boolean DEFAULT_SMART_DETECT = true;
    public static final boolean DEFAULT_VIBRATION = true;
    public static final long DEFAULT_VIBRATION_DURATION_MILLIS = 500;
    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;


    /**************/

    public static final int DEFAULT_DIM_MAX_PERCENT = 70; // %
    public static final int DEFAULT_DIM_MIN_PERCENT = 0; // %

    public static final int DEFAULT_ALPHA_MAX_PERCENT = 100; // %
    public static final int DEFAULT_ALPHA_MIN_PERCENT = 0; // %

    public static final ScreenFilter[] DEFAULT_SCREEN_FILTERS = {
            new ScreenFilter("#000000", 0 /100F, 40 /100F),
            new ScreenFilter( "#8FAF8903", 0 /100F, 40 /100F),
            new ScreenFilter( "#92035E01", 0 /100F, 40 /100F),
            new ScreenFilter( "#8F4D0126", 0 /100F, 40 /100F),
            new ScreenFilter( "#95682D00", 0 /100F, 40 /100F),
            new ScreenFilter( "#941F012C", 0 /100F, 40 /100F),
            new ScreenFilter( "#925F0002", 0 /100F, 40 /100F),
            new ScreenFilter( "#9C040E42", 0 /100F, 40 /100F),
    };

    /*****************/

    public static final int HIGH_BREAKING_EVERY_SEC =  10 * 60;
    public static final int HIGH_BREAKING_FOR_SEC = 10;
    public static final boolean HIGH_BREAKING_ACTIVATE = true;
    public static final int HIGH_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 10 * 60;
    public static final boolean HIGH_BLUELIGHT_FILTER_CHANGE = true;
    public static final BreakingMode HIGH_BREAKING_MODE = BreakingMode.STRONG;
    public static final boolean[] HIGH_SCREEN_FILTERS_ACTIVATION = {
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            false
    };

    public static final int STANDARD_BREAKING_EVERY_SEC =  20 * 60;
    public static final int STANDARD_BREAKING_FOR_SEC = 20;
    public static final boolean STANDARD_BREAKING_ACTIVATE = true;
    public static final int STANDARD_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 20 * 60;
    public static final boolean STANDARD_BLUELIGHT_FILTER_CHANGE = true;
    public static final BreakingMode STANDARD_BREAKING_MODE = BreakingMode.MEDIUM;
    public static final boolean[] STANDARD_SCREEN_FILTERS_ACTIVATION = {
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            false
    };

    public static final int LOW_BREAKING_EVERY_SEC =  30 * 60;
    public static final int LOW_BREAKING_FOR_SEC = 10;
    public static final boolean LOW_BREAKING_ACTIVATE = true;
    public static final int LOW_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 30 * 60;
    public static final boolean LOW_BLUELIGHT_FILTER_CHANGE = true;
    public static final BreakingMode LOW_BREAKING_MODE = BreakingMode.LIGHT;
    public static final boolean[] LOW_SCREEN_FILTERS_ACTIVATION = {
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            false
    };

    public static final int GAMER_BREAKING_EVERY_SEC =  60 * 60;
    public static final int GAMER_BREAKING_FOR_SEC = 10; // not needed
    public static final boolean GAMER_BREAKING_ACTIVATE = false;
    public static final int GAMER_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 10 * 60;
    public static final boolean GAMER_BLUELIGHT_FILTER_CHANGE = true;
    public static final BreakingMode GAMER_BREAKING_MODE = BreakingMode.LIGHT;
    public static final boolean[] GAMER_SCREEN_FILTERS_ACTIVATION = {
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            false
    };


    /*******/

    public static final String DEFAULT_PASSWORD = "56777877";
    public static final boolean DEFAULT_PASSWORD_ACTIVATE = false;

    public static final int DEFAULT_LOCK_SCREEN_IN_SEC = 60 * 60;
    public static final boolean DEFAULT_LOCK_SCREEN_ACTIVATE = false;

    public static final int DEFAULT_UNLOCK_SCREEN_IN_SEC = 10;
    public static final boolean DEFAULT_UNLOCK_SCREEN_ACTIVATE = false;


    /******/

    public static final long DEFAULT_MAX_NON_INTERACT_MILLIS = 8 * 1000; // seconds of millis
    public static final long DEFAULT_MIN_INTERACT_MILLIS = 5 * 1000; // seconds of millis


    /***************/


}
