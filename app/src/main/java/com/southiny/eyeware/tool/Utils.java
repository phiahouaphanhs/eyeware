package com.southiny.eyeware.tool;

public final class Utils {

    public static String zbs(int value) {
        if (value >= 0 & value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }
}
