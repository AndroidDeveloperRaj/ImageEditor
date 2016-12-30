package com.app.imagecreator.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextview extends TextView {

    public CustomTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextview(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "AshleyScriptMTStd.otf");
        setTypeface(typeface);
    }

}
