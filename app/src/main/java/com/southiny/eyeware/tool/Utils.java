package com.southiny.eyeware.tool;

public final class Utils {

    public static String zbs(int value) {
        if (value >= 0 & value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    public static String getTransparencyCodeByAlpha(float alpha) {
        double a1 = Math.round(alpha * 100) / 100.0d;
        int a2 = (int) Math.round(a1 * 255);
        String hex = Integer.toHexString(a2).toUpperCase();
        if (hex.length() == 1) {
            hex = "0" + hex;
        }

        return hex;
    }
}
