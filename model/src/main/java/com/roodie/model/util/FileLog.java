package com.roodie.model.util;

/**
 * Created by Roodie on 14.02.2016.
 */

import android.util.Log;

import com.roodie.model.BuildVars;

public class FileLog {

    private static volatile FileLog Instance = null;
    public static FileLog getInstance() {
        FileLog localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileLog();
                }
            }
        }
        return localInstance;
    }

    public FileLog() {
    }


    public static void e(final String tag, final String message, final Throwable exception) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.e(tag, message, exception);
    }

    public static void e(final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.e(tag, message);
    }

    public static void e(final String tag, final Throwable e) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        e.printStackTrace();
    }

    public static void d(final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.d(tag, message);
    }

    public static void w(final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.w(tag, message);
    }

}
