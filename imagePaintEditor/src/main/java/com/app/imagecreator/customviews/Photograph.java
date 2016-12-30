package com.app.imagecreator.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.app.imagecreator.utility.TouchPoint;

public class Photograph extends View {

    final static int MAX_BORDER = 5;

    final static float MIN_SCALE_VAL = 0.5f;

    final static int HIGHLIGHT_WIDTH = 10;

    final static int MARKER_SIZE = 15;

    private int border;

    private int left, top, right, bottom;

    private int wBmp, hBmp;

    private float angleBmp;

    private float sx, sy;

    private float tx, ty;

    private float aspectRatio;

    private Bitmap source;

    private Bitmap scrubbedSource;

    private String label;

    public boolean isHighQuality;
    public boolean isHighlighted;
    public boolean isMarked;
    public boolean isPlayable;
    public boolean isLocked;

    private Paint painter;

    private PointF topLeft, topRight, botLeft, botRight;

    private TouchPoint[] ptsOfContact = new TouchPoint[3];

    private Rect rectInvalidate;

    public Object extra;

    /***************************************************************************
     * Class constructor
     *
     * @param context         - the context of the application
     * @param content         - a bitmap containing the image to be loaded
     * @param angle           - the angle of inclination for this image
     * @param initWidth       - the initial width for the image
     * @param borderThickness - thickness of the "white border" to be drawn around the
     *                        picture. This value will be truncated to a max of five
     *                        pixels
     ***************************************************************************/
    public Photograph(Context context, Bitmap content, Bitmap lowQuality, float angle, int initWidth, int borderThickness, boolean isMovie) {

        super(context);
        source = content;
        scrubbedSource = lowQuality;

        angleBmp = angle;

        if (source == null || scrubbedSource == null) {
            aspectRatio = 1.0f;
            scrubbedSource = null;
            source = null;
        } else
            aspectRatio = ((float) source.getWidth() / source.getHeight());

        wBmp = initWidth;
        hBmp = (int) (wBmp / aspectRatio);

        // truncate border thickness to a max of five pixels
        border = (borderThickness > MAX_BORDER)
                ? MAX_BORDER
                : borderThickness;

        // no scaling as of now (scaling is a multiplication operation)
        sx = 1;
        sy = 1;
        tx = 0;
        tx = 0;

        initCoordinates();
        ptsOfContact[0] = null;
        ptsOfContact[1] = null;
        rectInvalidate = new Rect();

        label = "";
        isHighQuality = true;
        isHighlighted = false;
        isMarked = false;
        isPlayable = isMovie;
        isLocked = false;

        //Utility.logHeap();
    }

    private void initCoordinates() {

        left = (int) ((this.getWidth() / 2) + tx);
        top = (int) (this.getHeight() / 2 + ty);
        right = left + wBmp;
        bottom = top + hBmp;
    }

    /*******************************************************************************
     * Returns the coordinates of the center of the picture
     *
     * @return coords of center of image
     *******************************************************************************/
    public PointF getCenterImage() {

        int xc = left + wBmp / 2;
        int yc = top + hBmp / 2;
        return new PointF(xc, yc);
    }


    /***************************************************************************
     * Draw routine for this view. For every view, the drawn area is the part
     * which actually shows the image. The view may be much larger (in order
     * to accomodate the rotation and scaling of this image), so a majority of
     * the area in the view is empty.
     * <p>
     * In this scenario a touch on any part of the view (empty/drawn) will
     * generate an event. But we need to know if the touched point is within
     * the draw area only. In order to segregate the drawn area from the empty
     * area, we will define a "region of interest". This region is nothing but
     * a set of four points (basically the corners of the drawn image).
     * <p>
     * As the image is scaled and rotated, these points must also be
     * transformed accordingly the logic for the same is implemented in this
     * routine
     ***************************************************************************/
    protected void onDraw(Canvas c) {


        painter = new Paint();
        painter.setAntiAlias(true);
        initCoordinates();

        int pivotX = (left + wBmp / 2);
        int pivotY = (top + hBmp / 2);

        transformROI(pivotX, pivotY);
        setAreaToInvalidate();


        c.save();


        // Step 1 - TRANSFORMATIONS APPLIED ONTO THE CANVAS
        c.scale(sx, sy, pivotX, pivotY);
        c.rotate(angleBmp, pivotX, pivotY);

        // Step 2 -DRAW THE AURA / HIGHLIGHT FOR THIS IMAGE IF NECESSARY (if
        // the image is currently being touched by at least one pointer,
        // draw this aura)
        int ptrCount = getCountPointsOfContact();
        if (ptrCount > 0) {
            painter.setColor(Color.RED);
            painter.setAlpha(50);
            RectF highlightRect = new RectF(left - border
                    - HIGHLIGHT_WIDTH, top - border
                    - HIGHLIGHT_WIDTH, right + border
                    + HIGHLIGHT_WIDTH, bottom + border
                    + HIGHLIGHT_WIDTH);
        }

        // Step 3 - DRAW THE WHITE PICTURE BORDER
        painter.setColor(Color.TRANSPARENT);
        Rect whiteBorder = new Rect(left - border, top - border, right
                + border, bottom + border);
        c.drawRect(whiteBorder, painter);
        painter.setColor(Color.RED);
        painter.setStrokeWidth(10);
        int offset = wBmp / 5;
        // Step 4 - DRAW THE ACTUAL BITMAP FOR THIS PICTURE


        Rect paintedArea = new Rect(left, top, right, bottom);
        painter = new Paint();
        painter.setAntiAlias(true);

        if (source != null && isHighQuality)
            c.drawBitmap(source, null, paintedArea, painter);
        else if (scrubbedSource != null && !isHighQuality)
            c.drawBitmap(scrubbedSource, null, paintedArea, painter);

        // step 5 - Draw the MEDIA PLAYER icon if neccessary
        if (isPlayable) {

            painter.setColor(Color.WHITE);
            painter.setAlpha(200);

            painter.setStyle(Style.STROKE);
            painter.setStrokeWidth(5);
            c.drawCircle(pivotX, pivotY, wBmp / 6, painter);

            painter.setStyle(Style.FILL);
            Path triangle = new Path();
            triangle.moveTo(pivotX - wBmp / 24, pivotY - wBmp / 12);
            triangle.lineTo(pivotX - wBmp / 24, pivotY + wBmp / 12);
            triangle.lineTo(pivotX + wBmp / 12, pivotY);
            c.drawPath(triangle, painter);
        }

        // Step 6 - DRAW THE CHECKBOX IF NECESSARY (check the isMarked flag)
        if (isMarked) {

            Rect selectableArea = new Rect(left - border, top - border, right
                    + border, bottom + border);
            painter.setColor(Color.argb(50, 0, 50, 0));
            c.drawRect(selectableArea, painter);

            RectF markerRectOutline = new RectF(left - border * 2, top
                    - border * 2, left + MARKER_SIZE, top
                    + MARKER_SIZE);
            RectF markerRectFill = new RectF(left - border * 2 + 1, top
                    - border * 2 + 1, left + MARKER_SIZE
                    - 1, top + MARKER_SIZE - 1);

            painter.setColor(Color.argb(255, 0, 178, 0));
            c.drawRoundRect(markerRectOutline, 3, 3, painter);
            painter.setColor(Color.argb(250, 0, 128, 0));
            c.drawRoundRect(markerRectFill, 3, 3, painter);

            painter.setColor(Color.WHITE);
            painter.setStrokeWidth(3);

            c.drawLine(markerRectOutline.left + 5, markerRectOutline.bottom - 10, markerRectOutline.left + 10, markerRectOutline.bottom - 5, painter); // draw
            c.drawLine(markerRectOutline.left + 8, markerRectOutline.bottom - 5, markerRectOutline.right - 5, markerRectOutline.top + 5, painter); // draw

        }

        // Step 7 - Draw the LOCK if necessary
        if (isLocked) {

            RectF rectLockBase = new RectF(right - border, top, right
                    + MARKER_SIZE, top + MARKER_SIZE);

            painter.setColor(Color.GRAY);
            painter.setStyle(Style.STROKE);
            c.drawCircle(rectLockBase.left + rectLockBase.width() / 2, rectLockBase.top, rectLockBase.width() / 3, painter);

            painter.setColor(Color.argb(250, 249, 200, 30));
            painter.setStyle(Style.FILL);
            c.drawRoundRect(rectLockBase, 3, 3, painter);

            int lockCenterX = (int) (rectLockBase.left + rectLockBase.width() / 2);
            int lockCenterY = (int) (rectLockBase.top + rectLockBase.height() / 2);

            painter.setColor(Color.BLACK);
            painter.setStrokeWidth(1);
            c.drawCircle(lockCenterX, lockCenterY, 2, painter);
            c.drawLine(lockCenterX, lockCenterY, lockCenterX, lockCenterY
                    + border * 2, painter);
        }

        c.restore();


    }

    /*******************************************************************************
     * This routine is used to transform (scale and rotate) the REGION OF
     * INTEREST points by the specified rotation and scaling factors. This is
     * needed as the region of interest is merely a set of points, which will
     * not move along with the canvas. Therefore its binding upon us to make
     * sure it always tracks the drawn the area even after the canvas is
     * rotated or scaled
     *
     * @param pivotX - the x coordinate of the point about which the
     *               scaling/rotation is done
     * @param pivotY - the y coordinate of the point about which the
     *               scaling/rotation is done
     *******************************************************************************/
    private void transformROI(int pivotX, int pivotY) {


        topLeft = getRotatedPoint(left, top, pivotX, pivotY, angleBmp);
        topRight = getRotatedPoint(right, top, pivotX, pivotY, angleBmp);
        botLeft = getRotatedPoint(left, bottom, pivotX, pivotY, angleBmp);
        botRight = getRotatedPoint(right, bottom, pivotX, pivotY, angleBmp);

        topLeft = getScaledPoint(topLeft.x, topLeft.y, pivotX, pivotY, sx, sy);
        topRight = getScaledPoint(topRight.x, topRight.y, pivotX, pivotY, sx, sy);
        botLeft = getScaledPoint(botLeft.x, botLeft.y, pivotX, pivotY, sx, sy);
        botRight = getScaledPoint(botRight.x, botRight.y, pivotX, pivotY, sx, sy);

    }

    /*******************************************************************************
     * This method is used to dynamically compute the area to redraw. The
     * reason we need to constantly do this is because redrawing the view is
     * quite expensive, specially if the number of view in the layout are very
     * high. So in order to save a little time in the drawing routine, we only
     * draw the part of this view convered by this area.
     *******************************************************************************/
    private void setAreaToInvalidate() {

		/*
         * The logic used here is simple. Our drawn area can be at any
		 * angle. Hence we must find a square of size large enough to
		 * accomodate it - basically a square big enough to contain the
		 * drawing even if it is rotated a full 360 degrees.
		 * 
		 * So we calculate the diagonal of the drawn area (which is a
		 * rectangle). A circle of diameter equal to this diagonal will be
		 * large enough, but in order to use the invalidate method, we need
		 * a rect, not a circle. So we take a square of size = diameter
		 * (like a square drawn around this hypothetical circle.
		 */

        int A = (int) Math.pow((botLeft.x - topLeft.x), 2);
        int B = (int) Math.pow((botLeft.y - topLeft.y), 2);
        int diag = (int) Math.sqrt(A + B);

        int centerX = left + wBmp / 2;
        int centerY = top + hBmp / 2;

        rectInvalidate.left = centerX - diag;
        rectInvalidate.top = centerY - diag;
        rectInvalidate.bottom = centerY + diag;
        rectInvalidate.right = centerX + diag;

    }

    /*******************************************************************************
     * routine to get the coords of a point after the specified translation is
     * applied to that point
     *
     * @param x   - x coord of the point
     * @param y   - y coord of the point
     * @param tx2 - translation to be applied along x axis
     * @param ty2 - translation to be applied along y axis
     * @return the PointF object containing the revised coordinates of the
     * point
     *******************************************************************************/
    private PointF getTranslatedPoint(float x, float y, float tx2, float ty2) {
        PointF toReturn = new PointF(x + tx2, y + ty2);
        return toReturn;
    }

    /***************************************************************************
     * This returns the coordinates of a point after a scaling of sx and sy
     * would be performed on it
     *
     * @param x  - current X coordinate of the point
     * @param y  - current Y coordinate of the point
     * @param xc - X coord of the center of scaling (the point about which
     *           scaling will happen)
     * @param yc - Y coord of the center of scaling (the point about which
     *           scaling will happen)
     * @param sx - scaling factor along x direction
     * @param sy - scaling factor along y direction
     * @return A PointF object that contains the coords of the point (x,y) if
     * a scaling of sx and sy are applied.
     ***************************************************************************/
    private PointF getScaledPoint(float x, float y, int xc, int yc, float sx, float sy) {

		/*
		 * Logic for this formula is simple. If we transfer the coordinate
		 * system from (0,0) to (xc, yc) any point (x,y) will now be
		 * represented as (x-xc,y-yc). Therefore scaling by sx and sy would
		 * result in the scaled point (sx*(x-xc), sy*(y-yc)) -> lets call
		 * this (H,K)
		 * 
		 * now we transfer the point back to the origin (by adding xc and xy
		 * respectively) so we get (H + xc),(K + yc) --> solving these you
		 * will get (x*sx - xc*(sx-1)),(y*sy - yc*(sy-1))
		 */
        PointF scaledPoint = new PointF();
        scaledPoint.x = x * sx - xc * (sx - 1);
        scaledPoint.y = y * sy - yc * (sy - 1);
        return scaledPoint;
    }

    /**************************************************************************
     * This returns the coordinates of a point after a specified rotation may
     * be applied to it.
     *
     * @param x       - current x coordinate of the point
     * @param y       - current y coordinate of the point
     * @param xc      - x coordinate of the pivot
     * @param yc      - y coordinate of the pivot
     * @param degrees - angle of rotation in degrees
     * @return a PointF object that contains the point coordinates after
     * rotation
     **************************************************************************/
    private PointF getRotatedPoint(int x, int y, int xc, int yc, float degrees) {

        PointF rotatedPointCoords = new PointF();

        if (degrees != 0) {

            float radians = (float) (degrees * Math.PI / 180);
            float kc = (float) Math.cos(radians);
            float ks = (float) Math.sin(radians);

            int xNew = (int) (xc + (kc * (x - xc) - ks * (y - yc)));
            int yNew = (int) (yc + (ks * (x - xc) + kc * (y - yc)));

            rotatedPointCoords.x = xNew;
            rotatedPointCoords.y = yNew;
        } else {

            rotatedPointCoords.x = x;
            rotatedPointCoords.y = y;
        }

        return rotatedPointCoords;
    }

    /*******************************************************************************
     * This routine returns a value that represents the orientation of a point
     * along a line. The orientation can be among the following values: - the
     * point lies on the line (return value = 0) - the point lies on the right
     * of line (return value > 0) - the point lies on the left of line(return
     * value < 0)
     *
     * @param lOrigin - the starting vertex of the line
     * @param lEnd    - ending vertex of the line
     * @param p       - coordinate of the point whose orientation is to be found
     * @return - the orientation (0,positive,negative) -> (on line,left,right)
     *******************************************************************************/
    private int getPointOrientationAboutLine(PointF lOrigin, PointF lEnd, PointF p) {

        int result = -1;

        int x0 = (int) lOrigin.x;
        int y0 = (int) lOrigin.y;
        int x1 = (int) lEnd.x;
        int y1 = (int) lEnd.y;
        int x = (int) p.x;
        int y = (int) p.y;

        result = ((y - y0) * (x1 - x0)) - ((x - x0) * (y1 - y0));
        return result;
    }

    /*******************************************************************************
     * returns true if the point (x,y) lies in the "Region of Interest" or the
     * drawn area of the view
     *******************************************************************************/
    public boolean isPointInROI(int x, int y) {

        boolean result = false;
        PointF pt = new PointF(x, y);

        int orient1 = getPointOrientationAboutLine(topLeft, topRight, pt);
        int orient2 = getPointOrientationAboutLine(topRight, botRight, pt);
        int orient3 = getPointOrientationAboutLine(botRight, botLeft, pt);
        int orient4 = getPointOrientationAboutLine(botLeft, topLeft, pt);

        if (orient1 >= 0 && orient2 >= 0 && orient3 >= 0 && orient4 >= 0)
            result = true;

        return result;
    }

    public void setLabel(String message) {
        label = message;
        invalidate(rectInvalidate);
    }

    /**************************************************************************
     * Set the angle of inclination of the drawing
     *
     * @param degrees - the angle in degrees by which the drawing needs to be
     *                rotated
     **************************************************************************/
    public void setAngle(float degrees) {
        angleBmp = degrees;
        isHighQuality = true;
        invalidate(rectInvalidate);
    }

    /**************************************************************************
     * Get the current angle value
     *
     * @return - the current Angle value
     **************************************************************************/
    public float getAngle() {
        return angleBmp;
    }

    /**************************************************************************
     * Set the scale of the image. The final value for scale is determined by
     * multiplying by scalefactor and granularity. This is perfect when you
     * have a change in an integral quantity that needs to be used to affect
     * the scale of the image. In that case the finer control can be obtained
     * over the scaling operation using granularity
     *
     * @param scaleFactor - an integral value (for coarse adjustment)
     * @param granularity - a float value (for fine adjustment)
     **************************************************************************/

    public void setScale(int scaleFactor, float granularity) {
        if (isLocked)
            return;
        float sxNew = sx + scaleFactor * granularity;
        float syNew = sy + scaleFactor * granularity;

        if (sxNew > MIN_SCALE_VAL)
            sx = sxNew;
        if (syNew > MIN_SCALE_VAL)
            sy = syNew;

        isHighQuality = true;
        invalidate();
    }

    public void setMarker(boolean isChecked) {
        isMarked = isChecked;
        invalidate(rectInvalidate);
    }

    /*******************************************************************************
     * translates the drawing by specified pixels along x and y directions
     *
     * @param x - amount to translate along x
     * @param y - amount to translate along y
     *******************************************************************************/
    public void translate(int x, int y) {
        if (isLocked)
            return;
        tx += x;
        ty += y;
        isHighQuality = true;
        invalidate();
    }

    /******************************************************************************
     * redraws or invalidates the part of the view that needs to be redrawn
     *******************************************************************************/
    public void refresh() {
        invalidate(rectInvalidate);
    }

    /*******************************************************************************
     * Add a touch point to the points of contact. This routine is to let the
     * view know who is in contact with it
     *
     * @param point - the TouchPoint object bearing the information about this
     *              touch point
     *******************************************************************************/
    public void addTouchPoint(TouchPoint point) {

        for (int i = 0; i < ptsOfContact.length; i++) {
            if (ptsOfContact[i] == null) {
                ptsOfContact[i] = point;
                break;
            }
        }
    }

    /*******************************************************************************
     * removes the touch point with the specified id, if it exists in the
     * points of contact
     *
     * @param pId - ID of the point to be removed
     *******************************************************************************/
    public void removeTouchPoint(int pId) {

        for (int i = 0; i < ptsOfContact.length; i++) {
            if (ptsOfContact[i] != null) {
                if (ptsOfContact[i].pointerId == pId) {
                    ptsOfContact[i] = null;
                    break;
                }
            }
        }
    }

    /*******************************************************************************
     * @return - returns the number of points currently in contact with this
     * view
     *******************************************************************************/
    public int getCountPointsOfContact() {
        int count = 0;
        for (int i = 0; i < ptsOfContact.length; i++) {
            if (ptsOfContact[i] != null)
                count++;
        }

        return count;
    }

    /*******************************************************************************
     * @return - the array of TouchPoint objects containing the points of
     * contact
     *******************************************************************************/
    public TouchPoint[] getPointsOfContact() {
        return ptsOfContact;
    }
}
