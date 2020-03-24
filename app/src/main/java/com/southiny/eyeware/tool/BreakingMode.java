package com.southiny.eyeware.tool;

public enum BreakingMode {
    LIGHT, MEDIUM, STRONG;


    public static BreakingMode getBreakingModeByOrdinal(int ordinal) {

        if (ordinal == LIGHT.ordinal()) return LIGHT;

        if (ordinal == MEDIUM.ordinal()) return MEDIUM;

        if (ordinal == STRONG.ordinal()) return STRONG;

        return LIGHT;
    }

    @Override
    public String toString() {
        switch (this) {
            case LIGHT:
                return "Notify";
            case MEDIUM:
                return "Cut out";
            case STRONG:
                return "Force";
            default:
                return "Unknown";
        }
    }
}
