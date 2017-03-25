package com.app.imagecreator.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button {

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        setTypeface(typeface);
    }

}
