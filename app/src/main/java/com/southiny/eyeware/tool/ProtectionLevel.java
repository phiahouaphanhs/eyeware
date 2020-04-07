package com.southiny.eyeware.tool;

public enum ProtectionLevel {
    STREAMING, READING, SOCIAL_MEDIA, DAYLIGHT, UNKNOWN;

    /*public int getStringIndex() {
        switch (this) {
            case STREAMING:
                return R.string.main_standard_mode;
            case READING:
                return R.string.main_high_mode;
            case SOCIAL_MEDIA:
                return R.string.main_low_mode;
            case CUSTOMISED:
                return R.string.main_customised_mode;
            case DAYLIGHT:
                return R.string.main_gamer_mode;
            default:
                return R.string.main_unknown_mode;
        }


    }*/



    public static ProtectionLevel getProtectionLevelByOrdinal(int ordinal) {

        if (ordinal == STREAMING.ordinal()) return STREAMING;

        if (ordinal == READING.ordinal()) return READING;

        if (ordinal == SOCIAL_MEDIA.ordinal()) return SOCIAL_MEDIA;

        if (ordinal == DAYLIGHT.ordinal()) return DAYLIGHT;

        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch (this) {
            case READING:
                return "READING";
            case STREAMING:
                return "STREAMING";
            case SOCIAL_MEDIA:
                return "SOCIAL MEDIA";
            case DAYLIGHT:
                return "SOFT / DAYLIGHT";
            default:
                return "UNKNOWN";
        }
    }
}
