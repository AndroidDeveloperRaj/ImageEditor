package com.app.imagecreator.paintview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.app.imagecreator.PaintApplication;

public class PaintView extends View {

    private static final float STROKE_WIDTH = 5f;
    public Paint paint = new Paint();
    private Path path;
    public boolean eraserMode = false;

    private Bitmap bitmap;
    private Canvas canvasPaint;
    public Paint bitmapPaint;


    public PaintView(Context context, AttributeSet attrs, int color) {
        super(context, attrs);
        paint.setAntiAlias(true);
        if (eraserMode) {
            paint.setColor(Color.TRANSPARENT);
        } else {
            paint.setColor(color);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(PaintApplication.preferenceData.getSize());
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);

        bitmap = Bitmap.createBitmap(2048, 2048, Bitmap.Config.ARGB_8888);
        canvasPaint = new Canvas(bitmap);
        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        bitmapPaint.setStrokeWidth(STROKE_WIDTH);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);

        canvas.drawPath(path, paint);
    }

    private float eventX, eventY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        eventX = x;
        eventY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - eventX);
        float dy = Math.abs(y - eventY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(eventX, eventY, (x + eventX) / 2, (y + eventY) / 2);
            eventX = x;
            eventY = y;
        }
    }

    private void touch_up() {
        path.lineTo(eventX, eventY);
        canvasPaint.drawPath(path, paint);
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void clear() {
        Paint paintNew = new Paint();
        paintNew.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        invalidate();
        canvasPaint.drawPaint(paintNew);
        paintNew.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

}
