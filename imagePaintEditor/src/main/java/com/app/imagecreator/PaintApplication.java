package com.app.imagecreator;

import android.app.Application;

import com.app.imagecreator.preference.PreferenceData;
import com.app.imagecreator.utility.UncaughtExceptionUtil;

public class PaintApplication extends Application {

    public static PreferenceData preferenceData;

    @Override
    public void onCreate() {
        super.onCreate();
        UncaughtExceptionUtil.init();
        preferenceData = new PreferenceData(getApplicationContext());
    }
}
