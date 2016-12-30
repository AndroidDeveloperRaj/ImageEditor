package com.app.imagecreator.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class PreferenceData {

    private SharedPreferences pref = null;
    private Editor editor = null;

    public static final String BRUSH_SIZE = "brushSize";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String IS_PURCHASED = "isPurchased";

    public PreferenceData(Context context) {
        pref = context.getSharedPreferences("paint", 0);
        editor = pref.edit();
    }

    public void setSize(Float size) {

        editor.putFloat(BRUSH_SIZE, size);
        editor.commit();
    }

    public float getSize() {
        return pref.getFloat(BRUSH_SIZE, 5);
    }

    public void setCordinates(float x, float y) {

        editor.putFloat(X, x);
        editor.putFloat(Y, y);
        editor.commit();
    }

    public HashMap<String, Float> getCordinates() {
        HashMap<String, Float> cordinates = new HashMap<String, Float>();
        cordinates.put(X, pref.getFloat(X, 200));
        cordinates.put(Y, pref.getFloat(Y, 200));
        return cordinates;
    }

    public void setPurchased(boolean isPurchased) {

        editor.putBoolean(IS_PURCHASED, isPurchased);
        editor.commit();
    }

    public boolean isPurchased() {
        return pref.getBoolean(IS_PURCHASED, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
