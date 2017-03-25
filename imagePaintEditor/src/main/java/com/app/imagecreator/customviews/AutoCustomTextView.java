package com.app.imagecreator.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class AutoCustomTextView extends
        com.app.imagecreator.customviews.AutoResizeTextView {

    public AutoCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCustomTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        setTypeface(typeface);
    }
}
