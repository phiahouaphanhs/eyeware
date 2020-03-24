package com.southiny.eyeware.tool;

import com.southiny.eyeware.R;

public enum Language {
    ENGLISH, FRENCH, THAI, LAO;

    /*public int getStringIndex() {
        switch (this) {
            case ENGLISH:
                return R.string.main_language_english;
            case FRENCH:
                return R.string.main_language_french;
            case LAO:
                return R.string.main_language_lao;
            case THAI:
                return R.string.main_language_thai;
            default:
                return R.string.main_language_english;
        }
    }*/



    public static Language getLanguageByOrdinal(int ordinal) {

        if (ordinal == ENGLISH.ordinal()) return ENGLISH;

        if (ordinal == FRENCH.ordinal()) return FRENCH;

        if (ordinal == LAO.ordinal()) return LAO;

        if (ordinal == THAI.ordinal()) return THAI;

        return ENGLISH;
    }

    @Override
    public String toString() {
        switch (this) {
            case ENGLISH:
                return "English";
            case FRENCH:
                return "French";
            case LAO:
                return "Lao";
            case THAI:
                return "Thai";
            default:
                return "Unknown";
        }
    }
}
