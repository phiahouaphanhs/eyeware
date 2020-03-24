package com.southiny.eyeware.tool;

import com.southiny.eyeware.R;

public enum ProtectionLevel {
    STANDARD, HIGH, LOW, GAMER, UNKNOWN;

    /*public int getStringIndex() {
        switch (this) {
            case STANDARD:
                return R.string.main_standard_mode;
            case HIGH:
                return R.string.main_high_mode;
            case LOW:
                return R.string.main_low_mode;
            case CUSTOMISED:
                return R.string.main_customised_mode;
            case GAMER:
                return R.string.main_gamer_mode;
            default:
                return R.string.main_unknown_mode;
        }


    }*/



    public static ProtectionLevel getProtectionLevelByOrdinal(int ordinal) {

        if (ordinal == STANDARD.ordinal()) return STANDARD;

        if (ordinal == HIGH.ordinal()) return HIGH;

        if (ordinal == LOW.ordinal()) return LOW;

        if (ordinal == GAMER.ordinal()) return GAMER;

        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch (this) {
            case STANDARD:
                return "STANDARD";
            case HIGH:
                return "HIGH";
            case LOW:
                return "LOW";
            case GAMER:
                return "GAMING/STREAMING";
            default:
                return "UNKNOWN";
        }
    }
}
