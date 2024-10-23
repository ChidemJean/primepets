package com.monacoprime.primepets.utils;

import android.app.Activity;
import android.content.Context;
import android.text.BoringLayout;
import android.util.DisplayMetrics;

/**
 * Created by jean on 14/02/2017.
 */
public class MetricsUtils {

    public static float getScale(Context context){
        return context.getResources().getDisplayMetrics().density;
    }
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static DisplayMetrics getMetrics(Activity context){
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
