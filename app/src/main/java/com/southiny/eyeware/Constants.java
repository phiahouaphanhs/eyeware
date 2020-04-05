package com.southiny.eyeware;

import com.reactiveandroid.query.Delete;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.tool.AwardType;
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

    public static final int DEFAULT_DIM_MAX_PERCENT = 100; // %
    public static final int DEFAULT_DIM_MIN_PERCENT = 10; // %

    public static final int DEFAULT_ALPHA_MAX_PERCENT = 95; // %
    public static final int DEFAULT_ALPHA_MIN_PERCENT = 10; // %

    public static final ScreenFilter[] DEFAULT_SCREEN_FILTERS = {
            new ScreenFilter( "#555500", 0.47F,  0.14F, 1),
            new ScreenFilter("#FF0309", 0.47F,  0.14F, 2),
            new ScreenFilter( "#FFB102", 0.47F,  0.14F, 3),
            new ScreenFilter( "#00AA00", 0.47F,  0.14F, 4),
            new ScreenFilter( "#04FFD0", 0.47F,  0.14F, 5),
            new ScreenFilter( "#FFE401", 0.28F,  0.14F, 6),
            new ScreenFilter( "#B4A2FF", 0.47F,  0.14F, 7),
            new ScreenFilter( "#06F0FF", 0.36F,  0.15F, 8),
    };

    /*****************/

    public static final int MAX_NB_CHARACTER_IN_PL_NAME = 20;
    public static final int BREAKING_EVERY_MIN_SEC = 1 * 60;
    public static final int BREAKING_EVERY_MAX_SEC = 120 * 60;
    public static final int BREAKING_FOR_MIN_SEC = 5;
    public static final int BREAKING_FOR_MAX_SEC = 30 * 60;
    public static final int BLUELIGHT_FILTER_CHANGE_EVERY_MIN_SEC = 1 * 60;
    public static final int BLUELIGHT_FILTER_CHANGE_EVERY_MAX_SEC = 120 * 60;

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
            true,
            true,
            true,
            true,
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

    public static final int DEFAULT_EARN_SCREEN_FILTER_POINT_EVERY_SEC = 20;
    public static final int DEFAULT_UNIT_SCORE_SCREEN_FILTER = 10;
    public static final int DEFAULT_UNIT_SCORE_CHANGE_SCREEN_FILTER = 10;

    public static final long AWARD_SCORE_DEFAULT = 1;
    public static final long AWARD_SCORE_EXCELLENCY = 10000; // consecutively get 3 TODAY_PERFORMANCE or consecutively get 7 REACH_GOAL
    public static final long AWARD_SCORE_TODAY_PERFORMANCE = 1352; // get two time the goal point
    public static final long AWARD_SCORE_SURPRISE_BASE = 100;
    public static final long AWARD_SCORE_REACH_GOAL_BASE = 120; // level up
    public static final long AWARD_SCORE_CHAMPION = 2000;
    public static final long AWARD_SCORE_NEW_ARRIVAL = 123;
    public static final long AWARD_SCORE_CHECKOUT = 125;

    public static final int MAX_MINUS_SCORE_PERCENT = -90; // %

    public static final int FIRST_LEVEL = 1;
    public static final long DEFAULT_MIN_SCORE_OF_LEVEL_2 = 500;
    public static final long DEFAULT_MIN_SCORE_OF_LEVEL_3 = 800;
    public static final long DEFAULT_MIN_SCORE_OF_LEVEL_4 = 1400;
    public static final long DEFAULT_MIN_SCORE_OF_LEVEL_5 = 2800;
    public static final long DEFAULT_MIN_SCORE_OF_LEVEL_6 = 7000;
    public static final long DEFAULT_MIN_SCORE_OF_LEVEL_7 = 16000;
    private static final double LEVEL_SCORE_INCREASE_RATE = 1.5; // 0 < rate <= 1

    public static long getDefaultMinScoreOfLevel(int level) {
        assert level > 1;

        if (level == 2) return DEFAULT_MIN_SCORE_OF_LEVEL_2;

        if (level == 3) return DEFAULT_MIN_SCORE_OF_LEVEL_3;

        if (level == 4) return DEFAULT_MIN_SCORE_OF_LEVEL_4;

        if (level == 5) return DEFAULT_MIN_SCORE_OF_LEVEL_5;

        if (level == 6) return DEFAULT_MIN_SCORE_OF_LEVEL_6;

        if (level == 7) return DEFAULT_MIN_SCORE_OF_LEVEL_7;

        return getDefaultMinScoreOfLevelAbove7(level);
    }

    // level > 7
    private static long getDefaultMinScoreOfLevelAbove7(int level) {
        return (int) Math.pow(LEVEL_SCORE_INCREASE_RATE, (level-7)) * DEFAULT_MIN_SCORE_OF_LEVEL_7;
    }


}
