package com.southiny.eyeware;

import com.southiny.eyeware.tool.BreakingMode;
import com.southiny.eyeware.tool.Language;
import com.southiny.eyeware.tool.ProtectionLevel;

public final class Constants {

    public static final String CHANNEL_ID = "NoMoreEyeStrainChannel";
    public static final long SPLASH_TIME = 1000;
    public static final boolean DEFAULT_SMART_DETECT = true;
    public static final boolean DEFAULT_VIBRATION = true;
    public static final long DEFAULT_VIBRATION_DURATION_MILLIS = 500;
    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;


    /**************/

    public static final int DEFAULT_DIM_MAX_PERCENT = 70; // %
    public static final int DEFAULT_DIM_MIN_PERCENT = 0; // %

    public static final int DEFAULT_ALPHA_MAX_PERCENT = 100; // %
    public static final int DEFAULT_ALPHA_MIN_PERCENT = 0; // %


    /*****************/

    public static final int HIGH_BREAKING_EVERY_SEC =  10 * 60;
    public static final int HIGH_BREAKING_FOR_SEC = 10;
    public static final boolean HIGH_BREAKING_ACTIVATE = true;
    public static final int HIGH_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 10 * 60;
    public static final boolean HIGH_BLUELIGHT_FILTER_CHANGE = true;
    public static final int HIGH_DIM_AMOUNT_PERCENT = DEFAULT_DIM_MIN_PERCENT;
    public static final int HIGH_SCREEN_ALPHA_PERCENT = 40; // %
    public static final BreakingMode HIGH_BREAKING_MODE = BreakingMode.STRONG;

    public static final int STANDARD_BREAKING_EVERY_SEC =  20 * 60;
    public static final int STANDARD_BREAKING_FOR_SEC = 20;
    public static final boolean STANDARD_BREAKING_ACTIVATE = true;
    public static final int STANDARD_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 20 * 60;
    public static final boolean STANDARD_BLUELIGHT_FILTER_CHANGE = true;
    public static final int STANDARD_DIM_AMOUNT_PERCENT = DEFAULT_DIM_MIN_PERCENT;
    public static final int STANDARD_SCREEN_ALPHA_PERCENT = 40; // %
    public static final BreakingMode STANDARD_BREAKING_MODE = BreakingMode.MEDIUM;

    public static final int LOW_BREAKING_EVERY_SEC =  30 * 60;
    public static final int LOW_BREAKING_FOR_SEC = 10;
    public static final boolean LOW_BREAKING_ACTIVATE = true;
    public static final int LOW_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 30 * 60;
    public static final boolean LOW_BLUELIGHT_FILTER_CHANGE = true;
    public static final int LOW_DIM_AMOUNT_PERCENT = DEFAULT_DIM_MIN_PERCENT;
    public static final int LOW_SCREEN_ALPHA_PERCENT = 40; // %
    public static final BreakingMode LOW_BREAKING_MODE = BreakingMode.LIGHT;

    public static final int GAMER_BREAKING_EVERY_SEC =  60 * 60;
    public static final int GAMER_BREAKING_FOR_SEC = 10; // not needed
    public static final boolean GAMER_BREAKING_ACTIVATE = true;
    public static final int GAMER_BLUELIGHT_FILTER_CHANGE_EVERY_SEC = 10 * 60;
    public static final boolean GAMER_BLUELIGHT_FILTER_CHANGE = true;
    public static final int GAMER_DIM_AMOUNT_PERCENT = DEFAULT_DIM_MIN_PERCENT;
    public static final int GAMER_SCREEN_ALPHA_PERCENT = 40; // %
    public static final BreakingMode GAMER_BREAKING_MODE = BreakingMode.LIGHT;


    /*******/

    public static final String DEFAULT_PASSWORD = "56777877";
    public static final boolean DEFAULT_PASSWORD_ACTIVATE = false;

    public static final int DEFAULT_LOCK_SCREEN_IN_SEC = 10;
    public static final boolean DEFAULT_LOCK_SCREEN_ACTIVATE = false;

    public static final int DEFAULT_UNLOCK_SCREEN_IN_SEC = 10;
    public static final boolean DEFAULT_UNLOCK_SCREEN_ACTIVATE = false;


    /******/

    public static final long DEFAULT_MAX_NON_INTERACT_MILLIS = 8 * 1000; // seconds of millis
    public static final long DEFAULT_MIN_INTERACT_MILLIS = 5 * 1000; // seconds of millis




    /***************/

    public static final String[] DEFAULT_FILTER_COLORS = {"#8FAF8903", "#92035E01", "#8F4D0126",
    "#95682D00", "#941F012C", "#925F0002", "#000000", "#9C040E42"};
}
