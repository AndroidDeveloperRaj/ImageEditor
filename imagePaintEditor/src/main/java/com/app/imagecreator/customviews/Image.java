package com.app.imagecreator.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class Image extends View {

    public Image(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    public Image(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Image(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Image(Context context) {
        super(context);
    }


}
