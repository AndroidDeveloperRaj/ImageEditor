package com.app.imagecreator.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amapps.imagecreator.R;
import com.app.imagecreator.PaintApplication;
import com.app.imagecreator.customviews.PhotoSortrView;
import com.app.imagecreator.customviews.Photograph;
import com.app.imagecreator.customviews.Slate;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.TouchPoint;
import com.app.imagecreator.utility.Utility;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class AddTextActivity extends Activity implements OnClickListener,
        Constant, OnTouchListener, AnimationListener {

    private ImageView imgOk = null, imgCross = null, imgWithText = null;
    private TextView txtUserText = null;
    private String path = null, imgName = null;
    private Bitmap bitmap = null;
    private boolean isInteractive = true;
    private FrameLayout grandParent = null, parent = null;
    Hashtable<Integer, TouchPoint> ptrMap = new Hashtable<Integer, TouchPoint>();
    int tagCount = 0;
    Slate drawingSurface;
    private String type = "AshleyScriptMTStd.otf";
    private String userText = null;
    private int txtColor = 0;
    private PhotoSortrView pic;
    public boolean editMode = false;
    float x, y;
    int width, height;
    private AdView adView = null;
    private LinearLayout linAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        initControl();
    }

    private void initControl() {

        txtUserText = (TextView) findViewById(R.id.txtUserText);
        Typeface tf2 = Typeface.createFromAsset(this.getAssets(), type);
        txtUserText.setTypeface(tf2);

        Bundle bundle = getIntent().getExtras();
        editMode = bundle.getBoolean("Mode");
        if (pic != null) {
            parent.removeView(pic);
        }
        path = bundle.getString(PATH);
        imgName = bundle.getString(IMAGE_NAME);
        userText = bundle.getString(USER_TEXT);
        if (userText != null) {
            txtUserText.setText(userText);
            type = bundle.getString(TEXT_TYPE);
            if (type != null) {
                Typeface tf = Typeface.createFromAsset(this.getAssets(), type);
                txtUserText.setTypeface(tf);
            }
            txtColor = bundle.getInt(TEXT_COLOR);
            if (txtColor != 0) {
                txtUserText.setTextColor(txtColor);
            }
        } else {
            txtUserText.setText("");
        }

        linAdView = (LinearLayout) findViewById(R.id.linAdView);
        adView = (AdView) findViewById(R.id.adView);
        if (!PaintApplication.preferenceData.isPurchased()) {
            linAdView.setVisibility(View.VISIBLE);
            Utility.loadAdd(adView);
        }

        imgOk = (ImageView) findViewById(R.id.imgOk);
        imgOk.setOnClickListener(this);

        imgCross = (ImageView) findViewById(R.id.imgCross);
        imgCross.setOnClickListener(new ImageDeleteListener(this, pic));

        imgWithText = (ImageView) findViewById(R.id.imgWithText);
        File file = new File(path);
        try {
            bitmap = Utility.decodeFile(this, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgWithText.setImageBitmap(bitmap);
        drawingSurface = new Slate(this);

        grandParent = (FrameLayout) findViewById(R.id.grandParent);

        parent = (FrameLayout) findViewById(R.id.parent);
        parent.setTag(tagCount - 1);
        parent.setOnTouchListener(this);
        parent.addView(drawingSurface);

        Bitmap bitmapText;
        if (txtUserText.getText().toString() != null) {
            bitmapText = Utility.drawText(txtUserText);
            addDrawableToParent(bitmapText);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgOk:
                bitmap = Utility.convertToBitmap(parent);
                new SaveImage().execute();
                bitmap = Utility.convertToBitmap(parent);
//			path = Utility.imgSavingThread(bitmap, imgName, this, this , 1);
                break;

            default:
                break;
        }

    }

    public void callFilterActivity() {
        Intent intent = new Intent(AddTextActivity.this, FilterActivity.class);
        intent.putExtra(PATH, path);
        intent.putExtra(IMAGE_NAME, imgName);
        intent.putExtra(FROM, "AddText");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void addDrawableToParent(Bitmap bitmap) {

        isInteractive = false;

        Bitmap s = bitmap.copy(bitmap.getConfig(), true);
        pic = new PhotoSortrView(this);
        pic.addImages(this, s);
        parent.addView(pic);

        MyGestureListener listener = new MyGestureListener(pic);
        pic.gd = new GestureDetector(getApplicationContext(), listener, null,
                true);

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isInteractive = true;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {
        isInteractive = false;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        if (isInteractive) {

            int action = event.getAction();
            int count = event.getPointerCount();
            int ptrIndex = 0, ptrId;
            int actionResolved;

            actionResolved = action & MotionEvent.ACTION_MASK;
            if (actionResolved < 7 && actionResolved > 4)
                actionResolved = actionResolved - 5;

            if (count > 1)
                ptrIndex = (action & MotionEvent.ACTION_POINTER_ID_MASK) >>> MotionEvent.ACTION_POINTER_ID_SHIFT;
            ptrId = event.getPointerId(ptrIndex);


            switch (actionResolved) {
                case MotionEvent.ACTION_DOWN:
                    handleActionDown(ptrIndex, ptrId, event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    handleActionMove(count, event);
                    break;

                case MotionEvent.ACTION_UP:
                    handleActionUp(ptrIndex, ptrId, event);
                    break;
            }

            drawingSurface.update(ptrMap);
        }
        return true;

    }

    private void handleActionDown(int ptrIndex, int ptrId, MotionEvent event) {

        int x, y;
        x = (int) event.getX(ptrIndex);
        y = (int) event.getY(ptrIndex);

        View touchedView = getTouchedView(x, y);
        TouchPoint tp = new TouchPoint(x, y, ptrId, ptrIndex,
                touchedView.getTag());
        tp.setValidity(false);
        tp.isDown = true;

        if (touchedView instanceof Photograph) {
            Photograph pic = ((Photograph) touchedView);
            pic.addTouchPoint(new TouchPoint(x, y, ptrId, ptrIndex, null));
            pic.isHighQuality = true;

            int numPtsContact = pic.getCountPointsOfContact();
            if (numPtsContact == 3) {
                pic.isMarked = !pic.isMarked;
                pic.isLocked = !pic.isLocked;
            }

            touchedView.bringToFront();
            parent.invalidate();
            tp.extra = touchedView.getTag();
            tp.setValidity(true);

        }

        ptrMap.put(ptrId, tp);
    }

    private void handleActionUp(int ptrIndex, int ptrId, MotionEvent event) {

        int x, y;
        x = (int) event.getX(ptrIndex);
        y = (int) event.getY(ptrIndex);

        TouchPoint tp = ptrMap.get(ptrId);

        if (tp.isValid()) {
            Photograph pic = (Photograph) (parent.findViewWithTag(tp.extra));
            pic.removeTouchPoint(ptrId);
            pic.isHighQuality = false;
        }

        tp.setValidity(false);
        tp.isDown = false;
        ptrMap.put(ptrId, tp);

    }

    private void handleActionMove(int ptrCount, MotionEvent event) {

        int x, y;

        for (int index = 0; index < ptrCount; index++) {

            int ptrId = event.getPointerId(index);
            x = (int) event.getX(index);
            y = (int) event.getY(index);

            TouchPoint tp = ptrMap.get(ptrId);

            if (tp != null && tp.isValid()) {

                int dx = x - tp.getX();
                int dy = y - tp.getY();

                if (dx == 0 && dy == 0) {
                    continue;
                } else if ((Math.abs(dx) >= 3 || Math.abs(dy) >= 3)) {
                    Photograph pic = (Photograph) (parent
                            .findViewWithTag(tp.extra));
                    int numPtsContact = pic.getCountPointsOfContact();
                    pic.refresh();

                    try {
                        if (numPtsContact == 1) {
                            pic.translate(dx, dy);
                        } else if (numPtsContact == 2) {

                            TouchPoint[] ptsOfContact = pic
                                    .getPointsOfContact();
                            TouchPoint fixed = (ptsOfContact[0].pointerId != ptrId) ? ptsOfContact[0]
                                    : ptsOfContact[1];

                            float oldDistBetweenPoints = Utility.getDistance(
                                    fixed.getX(), fixed.getY(), tp.getX(),
                                    tp.getY());
                            float newDistBetweenPoints = Utility.getDistance(
                                    fixed.getX(), fixed.getY(), x, y);

                            float diff = newDistBetweenPoints
                                    - oldDistBetweenPoints;

                            float angleOld = Utility.getInclination(
                                    fixed.getX(), fixed.getY(), tp.getX(),
                                    tp.getY());
                            float angleNew = Utility.getInclination(
                                    fixed.getX(), fixed.getY(), x, y);

                            float theta = angleNew - angleOld;
                            if (Math.abs(theta) > 100)
                                theta = 0;

                            pic.setAngle(pic.getAngle() + theta);
                            pic.setScale((int) diff, 0.005f);

                        }
                    } catch (NullPointerException e) {
                        Utility.log("Exception", Log.getStackTraceString(e));
                    }

                }
            }

            try {
                tp.putXY(x, y);
                ptrMap.put(ptrId, tp);
            } catch (NullPointerException e) {

            }

        }

    }

    private View getTouchedView(int x, int y) {

        Photograph pic = null;
        int numViews = parent.getChildCount();

        for (int i = numViews - 1; i >= 0; i--) {

            if (parent.getChildAt(i) instanceof Photograph) {
                pic = (Photograph) (parent.getChildAt(i));
                if (pic.isPointInROI(x, y) == true) {
                    return pic;
                }
            }
        }

        return parent;
    }

    private class ImageDeleteListener implements OnClickListener {
        PhotoSortrView view;

        public ImageDeleteListener(Context context, PhotoSortrView view) {
            this.view = view;
        }

        @Override
        public void onClick(final View v) {

            parent.removeView(view);
            PaintApplication.preferenceData.setCordinates(200, 200);
            finish();
        }
    }

    private class SaveImage extends AsyncTask<Integer, Integer, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(AddTextActivity.this, null,
                    "Proccesing");
        }

        @Override
        protected String doInBackground(Integer... value) {
            path = Utility.saveImage(bitmap, imgName, AddTextActivity.this);
            return null;
        }

        protected void onPostExecute(String result) {
            callFilterActivity();
            PaintApplication.preferenceData.setCordinates(200, 200);
            progress.dismiss();
        }

    }

    private class MyGestureListener extends
            GestureDetector.SimpleOnGestureListener {

        PhotoSortrView view;

        public MyGestureListener(PhotoSortrView view) {
            this.view = view;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            width = view.getWidth();
            height = view.getHeight();
            callTextActivity();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
    }

    public void callTextActivity() {
        Intent intent = new Intent(AddTextActivity.this, TextActivity.class);
        intent.putExtra(PATH, path);
        intent.putExtra(IMAGE_NAME, imgName);
        intent.putExtra(USER_TEXT, userText);
        intent.putExtra(TEXT_TYPE, type);
        intent.putExtra(TEXT_COLOR, txtColor);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
