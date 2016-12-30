package com.app.imagecreator.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;

import com.amapps.imagecreator.R;
import com.app.imagecreator.PaintApplication;
import com.app.imagecreator.dialogs.ColorPickerDialog;
import com.app.imagecreator.dialogs.ColorPickerDialog.OnColorDeSelectedListener;
import com.app.imagecreator.dialogs.ColorPickerDialog.OnColorSelectedListener;
import com.app.imagecreator.dialogs.ConfirmationDialog;
import com.app.imagecreator.dialogs.SaveConfirmationDialog;
import com.app.imagecreator.paintview.PaintView;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.OnDialogClick;
import com.app.imagecreator.utility.Utility;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class DrawaingActivity extends Activity implements OnClickListener,
        Constant, OnValueChangeListener, OnDialogClick {

    private ImageView imgArea = null;
    private PaintView sign = null;
    public static RelativeLayout relDrawingArea = null, relWithFrame = null;
    private LinearLayout linColor = null, linPen = null, linErase = null, linFrameView = null, linPenView = null, linFrame = null;
    private ImageView imgColor = null, imgFrame = null, imgPen = null, imgErase = null;

    public static String tempDir = null;
    public String current = null;
    private Bitmap bitmap = null;
    private float lastWidhth = 5;
    private int lastColor = Color.BLACK, color = Color.BLACK;

    private String path = null, imgName = null;

    private AdView adView = null;
    private LinearLayout linAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawaing);
        initControls();
    }

    private void initControls() {

        imgArea = (ImageView) findViewById(R.id.imgArea);
        relWithFrame = (RelativeLayout) findViewById(R.id.relWithFrame);
        relDrawingArea = (RelativeLayout) findViewById(R.id.relDrawingArea);

        if (imgName == null) {
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            imgName = "Image-" + n + ".jpg";
        }


        linAdView = (LinearLayout) findViewById(R.id.linAdView);
        adView = (AdView) findViewById(R.id.adView);
        if (!PaintApplication.preferenceData.isPurchased()) {
            linAdView.setVisibility(View.VISIBLE);
            Utility.loadAdd(adView);
        }

        ImageView imgOk = (ImageView) findViewById(R.id.imgOk);
        imgOk.setOnClickListener(this);

        ImageView imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(this);

        ImageView imgClear = (ImageView) findViewById(R.id.imgClear);
        imgClear.setOnClickListener(this);

        ImageView imgShare = (ImageView) findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);

        linColor = (LinearLayout) findViewById(R.id.linColor);
        linColor.setOnClickListener(this);

        imgColor = (ImageView) findViewById(R.id.imgColor);

        linFrame = (LinearLayout) findViewById(R.id.linFrame);
        linFrame.setOnClickListener(this);

        imgFrame = (ImageView) findViewById(R.id.imgFrame);

        LinearLayout linGallery = (LinearLayout) findViewById(R.id.linGallery);
        linGallery.setOnClickListener(this);

        LinearLayout linFilter = (LinearLayout) findViewById(R.id.linFilter);
        linFilter.setOnClickListener(this);

        linPen = (LinearLayout) findViewById(R.id.linPen);
        linPen.setBackgroundResource(R.drawable.bg_pink_box_h);
        linPen.setOnClickListener(this);

        imgPen = (ImageView) findViewById(R.id.imgPen);
        imgPen.setImageResource(R.drawable.ic_pen_h);

        linErase = (LinearLayout) findViewById(R.id.linErase);
        linErase.setOnClickListener(this);

        imgErase = (ImageView) findViewById(R.id.imgErase);

        linFrameView = (LinearLayout) findViewById(R.id.linFrameView);

        RelativeLayout relFrameColor = (RelativeLayout) findViewById(R.id.relFrameColor);
        relFrameColor.setOnClickListener(this);

        RelativeLayout relNoFrame = (RelativeLayout) findViewById(R.id.relNoFrame);
        relNoFrame.setOnClickListener(this);

        linPenView = (LinearLayout) findViewById(R.id.linPenView);

        RelativeLayout relPenColor = (RelativeLayout) findViewById(R.id.relPenColor);
        relPenColor.setOnClickListener(this);

        RelativeLayout relPenSize = (RelativeLayout) findViewById(R.id.relPenSize);
        relPenSize.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(PATH)) {
            path = bundle.getString(PATH);
            try {
                imgArea.setImageBitmap(Utility.decodeFile(
                        DrawaingActivity.this, new File(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bundle.containsKey(IMAGE_NAME)) {
                imgName = bundle.getString(IMAGE_NAME);
            }
        } else {
        }

        sign = new PaintView(this, null, color);
        relDrawingArea.addView(sign);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.linPen:
                if (linFrameView.getVisibility() == View.VISIBLE) {
                    hideFrameView();
                }
                setPenMode();
                break;

            case R.id.linErase:
                if (linFrameView.getVisibility() == View.VISIBLE) {
                    hideFrameView();
                }
                setEraseMode();
                break;

            case R.id.imgOk:
                hideLayouts();
                bitmap = Utility.convertToBitmap(relWithFrame);
                new SaveConfirmationDialog(this, this, bitmap, imgName).show();
                break;

            case R.id.imgCancel:
                bitmap = Utility.convertToBitmap(relWithFrame);
                new ConfirmationDialog(this, bitmap, imgName, 1).show();
                break;

            case R.id.imgClear:
                sign.clear();
                break;

            case R.id.imgShare:
                hideLayouts();
                bitmap = Utility.convertToBitmap(relWithFrame);
                new SaveImage().execute(2);
//			path = Utility.imgSavingThread(bitmap, imgName, this, this , 2);
//			path = Utility.saveImage(bitmap, imgName, this);
//			Utility.shareImage(this, path);
                break;

            case R.id.linGallery:
                Utility.loadFromGallary(this);
                break;

            case R.id.linColor:
                if (linFrameView.getVisibility() == View.VISIBLE) {
                    hideFrameView();
                }
                if (linPenView.getVisibility() == View.VISIBLE) {
                    linPenView.setVisibility(View.GONE);
                }
                setDefaultDeSelected();
                openColorPicker(R.id.linColor);
                linColor.setBackgroundResource(R.drawable.bg_orange_box_h);
                imgColor.setImageResource(R.drawable.ic_color_h);

                break;

            case R.id.linFrame:
                if (linFrameView.getVisibility() == View.GONE) {
                    if (linPenView.getVisibility() == View.VISIBLE) {
                        linPenView.setVisibility(View.GONE);
                    }
                    setDefaultDeSelected();
                    visibleFrameView();

                } else {
                    hideFrameView();
                    setDefaultSelected();
                }
                break;

            case R.id.linFilter:
                hideLayouts();
                bitmap = Utility.convertToBitmap(relWithFrame);
                new SaveImage().execute(3);
//			path = Utility.imgSavingThread(bitmap, imgName, this, this , 3);
//			path = Utility.saveImage(bitmap, imgName, this);
//			callFilterActivity();
                break;

            case R.id.relFrameColor:
                int paddings = (int) (getResources().getDimension(
                        R.dimen.activity_drwaing_relativeLayout_padding) / getResources()
                        .getDisplayMetrics().density);
                relWithFrame.setPadding(paddings, paddings, paddings, paddings);
                openColorPicker(R.id.linFrameView);
                hideFrameView();
                break;

            case R.id.relNoFrame:
                relWithFrame.setPadding(0, 0, 0, 0);
                hideFrameView();
                setDefaultSelected();
                break;

            case R.id.relPenColor:
                linPenView.setVisibility(View.GONE);
                openColorPicker(R.id.relPenColor);
                break;

            case R.id.relPenSize:
                linPenView.setVisibility(View.GONE);
                showNumberPickerDialog();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FETCH_FROM_GALLERY && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();

            File file = new File(path);
            try {
                imgArea.setImageBitmap(Utility.decodeFile(this, file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setPenMode() {
        if (linPenView.getVisibility() == View.GONE) {
            linPenView.setVisibility(View.VISIBLE);
            linPen.setBackgroundResource(R.drawable.bg_pink_box_h);
            imgPen.setImageResource(R.drawable.ic_pen_h);
            if (sign.eraserMode) {
                linErase.setBackgroundResource(R.drawable.bg_violet_box);
                imgErase.setImageResource(R.drawable.ic_erase);
                sign.paint.setXfermode(null);
                sign.paint.setAlpha(0xFF);
                sign.paint.setStrokeWidth(lastWidhth);
                sign.paint.setColor(lastColor);
                sign.eraserMode = false;
            }
        } else {
            linPenView.setVisibility(View.GONE);
        }
    }

    private void setEraseMode() {
        if (linPenView.getVisibility() == View.VISIBLE) {
            linPenView.setVisibility(View.GONE);
        }
        linPen.setBackgroundResource(R.drawable.bg_pink_box);
        imgPen.setImageResource(R.drawable.ic_pen);
        linErase.setBackgroundResource(R.drawable.bg_violet_box_h);
        imgErase.setImageResource(R.drawable.ic_erase_h);
        lastWidhth = sign.paint.getStrokeWidth();
        lastColor = sign.paint.getColor();
        sign.paint.setXfermode(null);
        sign.paint.setAlpha(0x00);
        sign.eraserMode = true;
        sign.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        sign.paint.setColor(Color.TRANSPARENT);
        sign.paint.setStrokeWidth(20);
    }

    private void showNumberPickerDialog() {
        final Dialog dialog = new Dialog(DrawaingActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(getString(R.string.np_title));
        dialog.setContentView(R.layout.dialog_number_picker);
        Button dialogBtnSet = (Button) dialog.findViewById(R.id.btnSet);
        Button dialogBtnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        final NumberPicker np = (NumberPicker) dialog
                .findViewById(R.id.numberPickerSize);
        np.setMaxValue(40);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setValue((int) sign.paint.getStrokeWidth());
        np.setOnValueChangedListener(this);
        dialogBtnSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sign.paint.setStrokeWidth(np.getValue());
                PaintApplication.preferenceData.setSize((float) np.getValue());
                dialog.dismiss();
                linPenView.setVisibility(View.GONE);
            }
        });
        dialogBtnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                linPenView.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    private void openColorPicker(final int id) {
        int defaultColor;
        if (id == R.id.linFrameView) {
            ColorDrawable cd = (ColorDrawable) relWithFrame.getBackground();
            defaultColor = cd.getColor();
        } else if (id == R.id.linColor) {
            ColorDrawable cd = (ColorDrawable) imgArea.getBackground();
            defaultColor = cd.getColor();
            if (defaultColor == 0)
                defaultColor = Color.WHITE;
        } else if (id == R.id.relPenColor) {
            defaultColor = sign.paint.getColor();
        } else {
            defaultColor = Color.BLACK;
        }

        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this,
                defaultColor, new OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {

                switch (id) {
                    case R.id.relPenColor:
                        sign.setColor(color);
                        break;
                    case R.id.linFrameView:
                        relWithFrame.setBackgroundColor(color);
                        setDefaultSelected();
                        break;
                    case R.id.linColor:
                        imgArea.setImageResource(android.R.color.transparent);
                        imgArea.setBackgroundColor(color);
                        setDefaultSelected();
                        linColor.setBackgroundResource(R.drawable.bg_orange_box);
                        imgColor.setImageResource(R.drawable.ic_color);
                        break;
                    default:
                        break;
                }
            }

        }, new OnColorDeSelectedListener() {

            @Override
            public void onDeColorSelected() {
                switch (id) {
                    case R.id.relPenColor:
                        break;
                    case R.id.linFrameView:
                        setDefaultSelected();
                        break;
                    case R.id.linColor:
                        setDefaultSelected();
                        linColor.setBackgroundResource(R.drawable.bg_orange_box);
                        imgColor.setImageResource(R.drawable.ic_color);
                        break;
                    default:
                        break;
                }
            }
        });
        colorPickerDialog.setCanceledOnTouchOutside(false);
        colorPickerDialog.show();

    }


    private class SaveImage extends AsyncTask<Integer, Integer, Integer> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(DrawaingActivity.this, null,
                    "Proccesing");
        }

        @Override
        protected Integer doInBackground(Integer... value) {
            path = Utility.saveImage(bitmap, imgName, DrawaingActivity.this);
            if (value[0] == 2) {
                Utility.shareImage(DrawaingActivity.this, path);
            }
            return value[0];
        }

        protected void onPostExecute(Integer result) {

//			if(result == 2)
//			{
//				Utility.shareImage(DrawaingActivity.this, path);
//			}
            if (result == 3) {
                callFilterActivity();
            }

            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

    }


    private void hideLayouts() {
        if (linFrameView.getVisibility() == View.VISIBLE) {
            hideFrameView();
        }
        if (linPenView.getVisibility() == View.VISIBLE) {
            linPenView.setVisibility(View.GONE);
        }
    }

    private void hideFrameView() {
        linFrameView.setVisibility(View.GONE);
        linFrame.setBackgroundResource(R.drawable.bg_green_box);
        imgFrame.setImageResource(R.drawable.ic_frame);
    }

    private void visibleFrameView() {
        linFrameView.setVisibility(View.VISIBLE);
        linFrame.setBackgroundResource(R.drawable.bg_green_box_h);
        imgFrame.setImageResource(R.drawable.ic_frame_h);
    }

    private void setDefaultSelected() {
        if (sign.eraserMode) {
            linErase.setBackgroundResource(R.drawable.bg_violet_box_h);
            imgErase.setBackgroundResource(R.drawable.ic_erase_h);
        } else {
            linPen.setBackgroundResource(R.drawable.bg_pink_box_h);
            imgPen.setImageResource(R.drawable.ic_pen_h);
        }
    }

    private void setDefaultDeSelected() {
        if (sign.eraserMode) {
            linErase.setBackgroundResource(R.drawable.bg_violet_box);
            imgErase.setBackgroundResource(R.drawable.ic_erase);
        } else {
            linPen.setBackgroundResource(R.drawable.bg_pink_box);
            imgPen.setImageResource(R.drawable.ic_pen);
        }
    }

    public void callFilterActivity() {
        Intent intent = new Intent(DrawaingActivity.this,
                FilterActivity.class);
        intent.putExtra(PATH, path);
        intent.putExtra(IMAGE_NAME, imgName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        bitmap.recycle();
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(int code) {
        startActivity(new Intent(DrawaingActivity.this, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        bitmap = Utility.convertToBitmap(relWithFrame);
        new ConfirmationDialog(this, bitmap, imgName, 1).show();
    }

}
