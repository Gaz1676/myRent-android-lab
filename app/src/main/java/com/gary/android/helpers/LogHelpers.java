package com.gary.android.helpers;

import android.util.Log;

public class LogHelpers {

    public static void info(Object parent, String message)
    {
        Log.i(parent.getClass().getSimpleName(), message);
    }
}