/*
 * Code based off the PhotoSortrView from Luke Hutchinson's MTPhotoSortr
 * example (http://code.google.com/p/android-multitouch-controller/)
 *
 * License:
 *   Dual-licensed under the Apache License v2 and the GPL v2.
 */
package com.app.imagecreator.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageEntity extends MultiTouchEntity {

    private static final double INITIAL_SCALE_FACTOR = 0.15;

    private transient Drawable mDrawable;

    private Bitmap bitmap;

    public ImageEntity(Bitmap bitmap, Resources res) {
        super(res);

        this.bitmap = bitmap;
    }

    public ImageEntity(ImageEntity e, Resources res) {
        super(res);

        mDrawable = e.mDrawable;
        bitmap = e.bitmap;
        mScaleX = e.mScaleX;
        mScaleY = e.mScaleY;
        mCenterX = e.mCenterX;
        mCenterY = e.mCenterY;
        mAngle = e.mAngle;
    }

    public void draw(Canvas canvas) {
        canvas.save();

        float dx = (mMaxX + mMinX) / 2;
        float dy = (mMaxY + mMinY) / 2;

        mDrawable.setBounds((int) mMinX, (int) mMinY, (int) mMaxX, (int) mMaxY);

        canvas.translate(dx, dy);
        canvas.rotate(mAngle * 180.0f / (float) Math.PI);
        canvas.translate(-dx, -dy);

        mDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * Called by activity's onPause() method to free memory used for loading the images
     */
    @Override
    public void unload() {
        this.mDrawable = null;
    }

    /**
     * Called by activity's onResume() method to load the images
     */
    @Override
    public void load(Context context, float startMidX, float startMidY) {
        Resources res = context.getResources();
        getMetrics(res);

        mStartMidX = startMidX;
        mStartMidY = startMidY;

        mDrawable = new BitmapDrawable(res, bitmap);

        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();

        float centerX;
        float centerY;
        float scaleX;
        float scaleY;
        float angle;
        if (mFirstLoad) {
            centerX = startMidX;
            centerY = startMidY;

            float scaleFactor = (float) (Math.max(mDisplayWidth, mDisplayHeight) /
                    (float) Math.max(mWidth, mHeight) * INITIAL_SCALE_FACTOR);
            scaleX = scaleY = scaleFactor;
            angle = 0.0f;

            mFirstLoad = false;
        } else {
            centerX = mCenterX;
            centerY = mCenterY;
            scaleX = mScaleX;
            scaleY = mScaleY;
            angle = mAngle;
        }
        setPos(centerX, centerY, scaleX, scaleY, mAngle);
    }
}
