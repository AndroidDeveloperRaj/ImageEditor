/**************************************************************************
 * This is a class that creates a View on which a hashtable containing
 * TouchPoint data can be visually depicted. Basically, the touch points
 * can be drawn on the screen using colored markers
 **************************************************************************/

package com.app.imagecreator.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

import com.app.imagecreator.utility.TouchPoint;

import java.util.Enumeration;
import java.util.Hashtable;

public class Slate extends View {

    Hashtable<Integer, TouchPoint> ptrMap;
    final int RADIUS_POINT = 15;

    public Slate(Context context) {
        super(context);
        this.setTag("drawingSurface");
        ptrMap = null;
    }

    protected void onDraw(Canvas c) {

        //ensure that this map contains some valid point information
        if (ptrMap != null && !ptrMap.isEmpty()) {

            Paint painter = new Paint();
            painter.setColor(Color.GRAY);

            Enumeration<TouchPoint> touchPoints = ptrMap.elements();
            while (touchPoints.hasMoreElements()) {

                TouchPoint current = touchPoints.nextElement();
                if (current.isDown) {

                    //draw a bright outline
                    painter.setStyle(Style.STROKE);
                    c.drawCircle(current.getX(), current.getY(), RADIUS_POINT, painter);

                    //draw a translucent inner filling
                    painter.setStyle(Style.FILL);
                    painter.setAlpha(80);
                    c.drawCircle(current.getX(), current.getY(), RADIUS_POINT, painter);
                }
            }
        }
    }

    /**********************************************************************
     * this will redraw the touch points based on the map that is passed
     * as the argument
     **********************************************************************/
    public void update(Hashtable<Integer, TouchPoint> map) {
        ptrMap = map;
        invalidate();
    }
}






