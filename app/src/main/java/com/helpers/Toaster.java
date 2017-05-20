package com.helpers;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Gajanan Patil on 3/15/2017.
 */

@SuppressWarnings("SameParameterValue")
public class Toaster {
    public static void makeToast(Activity activity, String message) {
        getToast(activity, message, Toast.LENGTH_SHORT, Gravity.CENTER).show();
    }

    public static void makeToast(Activity activity, String message, int duration) {
        getToast(activity, message, duration,  Gravity.CENTER).show();
    }

    public static void makeToast(Activity activity, String message, int duration, int gravity) {
        getToast(activity, message, duration,  gravity).show();
    }

    private  static Toast getToast(Activity activity, String message, int duration,  int gravity) {
        Toast toast = Toast.makeText(activity, message, duration);
        toast.setGravity(gravity, toast.getXOffset() /2, toast.getYOffset() /2);
        return toast;
    }
}
