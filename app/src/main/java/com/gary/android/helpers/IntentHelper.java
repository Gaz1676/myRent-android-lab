package com.gary.android.helpers;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;

import java.io.Serializable;

public class IntentHelper {
    public static void startActivity(Activity parent, Class classname) {
        Intent intent = new Intent(parent, classname);
        parent.startActivity(intent);
    }

    public static void startActivityWithData(Activity parent, Class classname, String extraID, Serializable extraData) {
        Intent intent = new Intent(parent, classname);
        intent.putExtra(extraID, extraData);
        parent.startActivity(intent);
    }

    public static void startActivityWithDataForResult(Activity parent, Class classname, String extraID, Serializable extraData, int idForResult) {
        Intent intent = new Intent(parent, classname);
        intent.putExtra(extraID, extraData);
        parent.startActivityForResult(intent, idForResult);
    }

    // to support enhanced navigation, we define an additional Helper method in the IntentHelper class
    public static void navigateUp(Activity parent) {
        Intent upIntent = NavUtils.getParentActivityIntent(parent);
        NavUtils.navigateUpTo(parent, upIntent);
    }
}