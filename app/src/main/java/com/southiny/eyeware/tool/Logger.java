package com.southiny.eyeware.tool;

import android.util.Log;

import java.io.File;
import java.util.Calendar;

import static com.southiny.eyeware.tool.Utils.zbs;

public final class Logger {

    private static final String ERR_PREFIX = "[ERROR]";
    private static final String WARN_PREFIX = "[WARNING]";
    private static File os;

    public static void log(String tag, String message) {
        Log.d(currentTime() + " " + tag, message);
    }

    public static void err(String tag, String message) {
        Log.e(currentTime() + " " + tag, ERR_PREFIX + " " + message);
    }

    public static void warn(String tag, String message) {
        Log.w(currentTime() + " " + tag, WARN_PREFIX + " " + message);
    }

    private static String currentTime() {
        Calendar dateTime = Calendar.getInstance();
        return "(" + zbs(dateTime.get(Calendar.HOUR_OF_DAY)) + ":"
                + zbs(dateTime.get(Calendar.MINUTE)) + ":"
                + zbs(dateTime.get(Calendar.SECOND)) + ")";
    }

    public static void init() {
    }


}
