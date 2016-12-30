package com.app.imagecreator.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amapps.imagecreator.R;
import com.app.imagecreator.PaintApplication;
import com.app.imagecreator.dialogs.ConfirmationDialog;
import com.app.imagecreator.dialogs.SaveConfirmationDialog;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.Effects;
import com.app.imagecreator.utility.OnDialogClick;
import com.app.imagecreator.utility.Utility;
import com.google.android.gms.ads.AdView;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FilterActivity extends Activity implements Constant,
        OnClickListener, OnDialogClick, OnSeekBarChangeListener {

    private ImageView imgToBeFiltered = null;
    public static String path = null, imgName = null;
    private LinearLayout linRotation = null, linFlip = null, linCrop = null,
            linEffects = null, linBrighten = null, linType = null,
            linAdView = null;
    private ImageView imgRotation = null, imgFlip = null, imgCrop = null,
            imgEffects = null, imgBrighten = null;

    private Bitmap bitmap = null, bitmapTemp = null, originalBitmap = null,
            bitmapBrightness = null;

    private ImageView imgSave = null, imgCancel = null, imgShare = null;
    private RelativeLayout seekBrightness = null;
    private SeekBar seekBarBrightness = null;
    private int brightness = 0;
    private HorizontalScrollView scrollEffects = null;
    private LinearLayout linRotationView = null, linFlipView = null;
    final int PIC_CROP = 1;

    private CropImageView cropImageView = null;
    private RelativeLayout linCropView = null, relSaveCrop = null,
            relCancelCrop = null;

    private AdView adView = null;

    private int arrImgViewId[] = {R.id.imgNormal, R.id.imgGrayScale, R.id.imgFlea, R.id.imgTinted, R.id.imgSnowEffect,
            R.id.imgWithReflection, R.id.imgSepia, R.id.imgSketch, R.id.imgBlur, R.id.imgSharpen,
            R.id.imgShade, R.id.imgInversion, R.id.imgSaturation, R.id.imgPoster, R.id.imgSmooth,
            R.id.imgMeanRemoval, R.id.imgEmboss};

    private ImageView arrImgView[] = new ImageView[arrImgViewId.length];
    private Bitmap arrBitmap[] = new Bitmap[arrImgViewId.length];

    // a variable used to assign sequential tags to the views

    public static final float CORNER_DEGREE = 80;
    private final int HALF_BRIGHTNESS_VALUE = 100;

    int[][] matrix_sharpen = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initControls();
    }

    private void initControls() {

        for (int i = 0; i < arrImgView.length; i++) {
            arrImgView[i] = (ImageView) findViewById(arrImgViewId[i]);
            arrImgView[i].setOnClickListener(this);
        }

        linAdView = (LinearLayout) findViewById(R.id.linAdView);
        adView = (AdView) findViewById(R.id.adView);
        if (!PaintApplication.preferenceData.isPurchased()) {
            linAdView.setVisibility(View.VISIBLE);
            Utility.loadAdd(adView);
        }

        imgToBeFiltered = (ImageView) findViewById(R.id.imgToBeFiltered);

        Bundle bundle = getIntent().getExtras();
        path = bundle.getString(PATH);
        if (bundle.containsKey(IMAGE_NAME)) {
            imgName = bundle.getString(IMAGE_NAME);
        } else {
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            imgName = "Image-" + n + ".jpg";
        }


        file = new File(path);
        try {
            bitmap = Utility.decodeFile(this, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmapTemp = bitmap;
        originalBitmap = bitmap;
        imgToBeFiltered.setImageBitmap(bitmap);

        linRotation = (LinearLayout) findViewById(R.id.linRotation);
        linRotation.setOnClickListener(this);

        imgRotation = (ImageView) findViewById(R.id.imgRotation);

        linFlip = (LinearLayout) findViewById(R.id.linFlip);
        linFlip.setOnClickListener(this);

        imgFlip = (ImageView) findViewById(R.id.imgFlip);

        linCrop = (LinearLayout) findViewById(R.id.linCrop);
        linCrop.setOnClickListener(this);

        imgCrop = (ImageView) findViewById(R.id.imgCrop);

        linEffects = (LinearLayout) findViewById(R.id.linEffects);
        linEffects.setOnClickListener(this);

        imgEffects = (ImageView) findViewById(R.id.imgEffects);

        linBrighten = (LinearLayout) findViewById(R.id.linBrighten);
        linBrighten.setOnClickListener(this);

        imgBrighten = (ImageView) findViewById(R.id.imgBrighten);

        linType = (LinearLayout) findViewById(R.id.linType);
        linType.setOnClickListener(this);

        imgSave = (ImageView) findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(this);

        imgShare = (ImageView) findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);

        linRotationView = (LinearLayout) findViewById(R.id.linRotationView);

        RelativeLayout relLeftRotation = (RelativeLayout) findViewById(R.id.relLeftRotation);
        relLeftRotation.setOnClickListener(this);

        RelativeLayout relRightRotation = (RelativeLayout) findViewById(R.id.relRightRotation);
        relRightRotation.setOnClickListener(this);

        linFlipView = (LinearLayout) findViewById(R.id.linFlipView);

        RelativeLayout relFlipHorizontal = (RelativeLayout) findViewById(R.id.relFlipHorizontal);
        relFlipHorizontal.setOnClickListener(this);

        RelativeLayout relFlipVertical = (RelativeLayout) findViewById(R.id.relFlipVertical);
        relFlipVertical.setOnClickListener(this);


        seekBrightness = (RelativeLayout) findViewById(R.id.relSeekBrightness);
        seekBarBrightness = (SeekBar) findViewById(R.id.seekBrightness);
        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);

        scrollEffects = (HorizontalScrollView) findViewById(R.id.scrollEffects);

        linCropView = (RelativeLayout) findViewById(R.id.linCropView);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        relSaveCrop = (RelativeLayout) findViewById(R.id.relSaveCrop);
        relSaveCrop.setOnClickListener(this);

        relCancelCrop = (RelativeLayout) findViewById(R.id.relCancelCrop);
        relCancelCrop.setOnClickListener(this);

    }

    private void allImageEffects(File file) {
        Bitmap originalBitmap = null;
        try {

            BitmapDrawable bd = (BitmapDrawable) this.getResources()
                    .getDrawable(R.drawable.bg_normal);
            originalBitmap = Utility.decodeFile(FilterActivity.this, bitmapTemp, bd
                    .getBitmap().getWidth(), bd.getBitmap().getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        iterativeEffects(0, originalBitmap);
    }

    private void iterativeEffects(int cnt, Bitmap originalBitmap) {

        Bitmap bit = null;
        switch (cnt) {
            case 0:
                bit = originalBitmap;
                break;
            case 1:
                bit = Effects.doGreyscale(originalBitmap);
                break;
            case 2:
                bit = Effects.applyFleaEffect(originalBitmap);
                break;

            case 3:
                bit = Effects.tintImage(originalBitmap, 50);
                break;

            case 4:
                bit = Effects.applySnowEffect(originalBitmap);
                break;

            case 5:
                bit = Effects.applyReflection(originalBitmap);
                break;

            case 6:
                bit = Effects.createSepia(originalBitmap);
                break;

            case 7:
                bit = Effects.changeToSketch(originalBitmap, 5, 110);
                break;

            case 8:
                bit = Effects.blur(originalBitmap, 3);
                break;

            case 9:
                bit = Effects.processingBitmap(originalBitmap, matrix_sharpen);
                break;

            case 10:
                bit = Effects.shadowImage(originalBitmap);
                break;

            case 11:
                bit = Effects.doInvert(originalBitmap);
                break;

            case 12:
                bit = Effects.applySaturationFilter(originalBitmap, 2);
                break;

            case 13:
                bit = Effects.decreaseColorDepth(originalBitmap, 64);
                break;

            case 14:
                bit = Effects.applySmoothEffect(originalBitmap, 5);
                break;

            case 15:
                bit = Effects.applyMeanRemoval(originalBitmap);
                break;

            case 16:
                bit = Effects.emboss(originalBitmap);
                break;
            default:
                break;
        }

//		arrImgView[cnt].setImageBitmap(Effects.getCroppedBitmap(bit,
//				bit.getWidth()));
        arrBitmap[cnt] = bit;
        cnt++;
        Utility.log("TAG", " cnt " + cnt);
        if (cnt < arrImgView.length)
            iterativeEffects(cnt, originalBitmap);
        else {
            Utility.log("TAG", "endTime : " + System.currentTimeMillis());


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgSave:
                linRotationView.setVisibility(View.GONE);
                linFlipView.setVisibility(View.GONE);
                scrollEffects.setVisibility(View.GONE);
                seekBrightness.setVisibility(View.GONE);
                bitmap = bitmapTemp;
                new SaveConfirmationDialog(this, this, bitmap, imgName).show();
                break;

            case R.id.imgCancel:
                bitmap = bitmapTemp;
                new ConfirmationDialog(this, bitmap, imgName, 2).show();
                break;

            case R.id.imgShare:
                bitmap = bitmapTemp;
                new SaveImage().execute(2);
//			path = Utility.imgSavingThread(bitmap, imgName, this, this , 2);
//			path = Utility.saveImage(bitmap, imgName, this);
//			Utility.shareImage(this, path);
                break;

            case R.id.linRotation:
                if (linRotationView.getVisibility() == View.GONE) {
                    if (seekBrightness.getVisibility() == View.VISIBLE) {
                        hideSeekBar();
                    } else if (scrollEffects.getVisibility() == View.VISIBLE) {
                        hideScrollView();
                    } else if (linFlipView.getVisibility() == View.VISIBLE) {
                        hideFlipView();
                    } else if (linCropView.getVisibility() == View.VISIBLE) {
                        hideCropeView();
                    }
                    visibleRotationView();
                } else {
                    hideRotationView();
                }
                break;

            case R.id.linFlip:
                if (linFlipView.getVisibility() == View.GONE) {
                    if (seekBrightness.getVisibility() == View.VISIBLE) {
                        hideSeekBar();
                    } else if (scrollEffects.getVisibility() == View.VISIBLE) {
                        hideScrollView();
                    } else if (linRotationView.getVisibility() == View.VISIBLE) {
                        hideRotationView();
                    } else if (linCropView.getVisibility() == View.VISIBLE) {
                        hideCropeView();
                    }
                    visibleFlipView();
                } else {
                    hideFlipView();
                }
                break;

            case R.id.relLeftRotation:
                bitmap = Effects.leftRotation(bitmapTemp);
                bitmapTemp = bitmap;
                originalBitmap = bitmap;
                imgToBeFiltered.setImageBitmap(bitmap);
                break;

            case R.id.relRightRotation:
                bitmap = Effects.rightRotation(bitmapTemp);
                bitmapTemp = bitmap;
                originalBitmap = bitmap;
                imgToBeFiltered.setImageBitmap(bitmap);
                break;

            case R.id.relFlipHorizontal:
                bitmap = Effects.doFlipHorizonatal(bitmapTemp);
                bitmapTemp = bitmap;
                originalBitmap = bitmap;
                imgToBeFiltered.setImageBitmap(bitmap);
                break;

            case R.id.relFlipVertical:
                bitmap = Effects.doFlipVertical(bitmapTemp);
                bitmapTemp = bitmap;
                originalBitmap = bitmap;
                imgToBeFiltered.setImageBitmap(bitmap);
                break;

            case R.id.linCrop:
                if (linCropView.getVisibility() == View.GONE) {
                    if (seekBrightness.getVisibility() == View.VISIBLE) {
                        hideSeekBar();
                    } else if (scrollEffects.getVisibility() == View.VISIBLE) {
                        hideScrollView();
                    } else if (linRotationView.getVisibility() == View.VISIBLE) {
                        hideRotationView();
                    } else if (linFlipView.getVisibility() == View.VISIBLE) {
                        hideFlipView();
                    }
                    linCropView.setVisibility(View.VISIBLE);
                    cropImageView.setImageBitmap(bitmapTemp);
                }
                bitmapTemp = bitmap;
                break;

            case R.id.linEffects:
                if (scrollEffects.getVisibility() == View.GONE) {

//				if (seekBrightness.getVisibility() == View.VISIBLE) {
//					hideSeekBar();
//				} else if (linCropView.getVisibility() == View.VISIBLE) {
//					hideCropeView();
//				} else if (linRotationView.getVisibility() == View.VISIBLE) {
//					hideRotationView();
//				} else if (linFlipView.getVisibility() == View.VISIBLE) {
//					hideFlipView();
//				}
                    new applyallEffects().execute();
//				allImageEffects(file);
                } else {
                    hideScrollView();
                }
                break;

            case R.id.relSaveCrop:
                bitmap = cropImageView.getCroppedImage();
                originalBitmap = bitmap;
                bitmapTemp = bitmap;
                imgToBeFiltered.setImageBitmap(bitmap);
                hideCropeView();
                break;

            case R.id.relCancelCrop:
                hideCropeView();
                break;

            case R.id.linBrighten:
                if (seekBrightness.getVisibility() == View.GONE) {
                    if (scrollEffects.getVisibility() == View.VISIBLE) {
                        hideScrollView();
                    } else if (linCropView.getVisibility() == View.VISIBLE) {
                        hideCropeView();
                    } else if (linRotationView.getVisibility() == View.VISIBLE) {
                        hideRotationView();
                    } else if (linFlipView.getVisibility() == View.VISIBLE) {
                        hideFlipView();
                    }
                    visibleSeekBar();
                    changeBrightness();
                } else {
                    hideSeekBar();
                }
                break;

            case R.id.imgNormal:
                if (v.getBackground() == null)
                    setImageBackground(0);
                break;

            case R.id.imgGrayScale:
                if (v.getBackground() == null)
                    setImageBackground(1);
                break;

            case R.id.imgFlea:
                if (v.getBackground() == null)
                    setImageBackground(2);
                break;

            case R.id.imgTinted:
                if (v.getBackground() == null)
                    setImageBackground(3);
                break;

            case R.id.imgSnowEffect:
                if (v.getBackground() == null)
                    setImageBackground(4);
                break;

            case R.id.imgSepia:
                if (v.getBackground() == null)
                    setImageBackground(6);
                break;

            case R.id.imgSketch:
                if (v.getBackground() == null)
                    setImageBackground(7);
                break;

            case R.id.imgBlur:
                if (v.getBackground() == null)
                    setImageBackground(8);
                break;

            case R.id.imgSharpen:
                if (v.getBackground() == null)
                    setImageBackground(9);
                break;

            case R.id.imgShade:
                if (v.getBackground() == null)
                    setImageBackground(10);
                break;

            case R.id.imgInversion:
                if (v.getBackground() == null)
                    setImageBackground(11);
                break;

            case R.id.imgSaturation:
                if (v.getBackground() == null)
                    setImageBackground(12);
                break;

            case R.id.imgPoster:
                if (v.getBackground() == null)
                    setImageBackground(13);
                break;

            case R.id.imgSmooth:
                if (v.getBackground() == null)
                    setImageBackground(14);
                break;

            case R.id.imgMeanRemoval:
                if (v.getBackground() == null)
                    setImageBackground(15);
                break;

            case R.id.imgEmboss:
                if (v.getBackground() == null)
                    setImageBackground(16);
                break;

            case R.id.linType:
                if (linRotationView.getVisibility() == View.VISIBLE) {
                    hideRotationView();
                } else if (linFlipView.getVisibility() == View.VISIBLE) {
                    hideFlipView();
                } else if (linCropView.getVisibility() == View.VISIBLE) {
                    hideCropeView();
                } else if (scrollEffects.getVisibility() == View.VISIBLE) {
                    hideScrollView();
                } else if (seekBrightness.getVisibility() == View.VISIBLE) {
                    hideSeekBar();
                }
                new SaveImage().execute(3);
//			path = Utility.imgSavingThread(bitmap, imgName, this, this , 3);
//			path = Utility.saveImage(bitmapTemp, imgName, this);
//			callTextActivity();
                break;

            default:
                break;
        }

    }

    private void changeBrightness() {

        seekBarBrightness.setProgress(100);
        seekBarBrightness.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        new applybrightness().execute(brightness);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        brightness = progress;
    }

    @Override
    public void onDialogPositiveClick(int code) {
        switch (code) {
            case DIALOG_CODE_TEXT_DIALOG:
                break;

            case DIALOG_CODE_WS_RESPONSE:
                startActivity(new Intent(FilterActivity.this, HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;

            default:
                break;
        }
    }

    private class applyEffects extends AsyncTask<Integer, Integer, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(FilterActivity.this, null,
                    getString(R.string.proccess_dialog_messege));
        }

        @Override
        protected String doInBackground(Integer... id) {
            switch (id[0]) {

                case R.id.imgNormal:
                    bitmapTemp = originalBitmap;
                    break;

                case R.id.imgGrayScale:
                    bitmapTemp = Effects.doGreyscale(originalBitmap);
                    break;

                case R.id.imgFlea:
                    bitmapTemp = Effects.applyFleaEffect(originalBitmap);
                    break;

                case R.id.imgTinted:
                    bitmapTemp = Effects.tintImage(originalBitmap, 50);
                    break;

                case R.id.imgSnowEffect:
                    bitmapTemp = Effects.applySnowEffect(originalBitmap);
                    break;

                case R.id.imgSepia:
                    bitmapTemp = Effects.createSepia(originalBitmap);
                    break;

                case R.id.imgSketch:
                    bitmapTemp = Effects.changeToSketch(originalBitmap, 5, 110);
                    break;

                case R.id.imgBlur:
                    bitmapTemp = Effects.blur(originalBitmap, 3);
                    break;

                case R.id.imgSharpen:
                    bitmapTemp = Effects.processingBitmap(originalBitmap,
                            matrix_sharpen);
                    break;

                case R.id.imgShade:
                    bitmapTemp = Effects.shadowImage(originalBitmap);
                    break;

                case R.id.imgInversion:
                    bitmapTemp = Effects.doInvert(originalBitmap);
                    break;

                case R.id.imgSaturation:
                    bitmapTemp = Effects.applySaturationFilter(originalBitmap, 2);
                    break;

                case R.id.imgPoster:
                    bitmapTemp = Effects.decreaseColorDepth(originalBitmap, 64);
                    break;

                case R.id.imgSmooth:
                    bitmapTemp = Effects.applySmoothEffect(originalBitmap, 5);
                    break;

                case R.id.imgMeanRemoval:
                    bitmapTemp = Effects.applyMeanRemoval(originalBitmap);
                    break;

                case R.id.imgEmboss:
                    bitmapTemp = Effects.emboss(originalBitmap);
                    break;

                default:
                    break;
            }
            return null;
        }

        protected void onPostExecute(String result) {
            imgToBeFiltered.setImageBitmap(bitmapTemp);
            progress.dismiss();
        }

    }

    private class applybrightness extends AsyncTask<Integer, Integer, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(FilterActivity.this, null,
                    "Proccesing");
        }

        @Override
        protected String doInBackground(Integer... value) {
            if (value[0] > HALF_BRIGHTNESS_VALUE) {
                brightness = brightness - HALF_BRIGHTNESS_VALUE;
                bitmapTemp = Effects.doBrightness(bitmapBrightness, brightness);
            } else if (value[0] < HALF_BRIGHTNESS_VALUE) {
                brightness = HALF_BRIGHTNESS_VALUE - brightness;
                bitmapTemp = Effects.doDarktness(bitmapBrightness, brightness);
            }
            return null;
        }

        protected void onPostExecute(String result) {
            imgToBeFiltered.setImageBitmap(bitmapTemp);
            progress.dismiss();
        }

    }

    private class applyallEffects extends AsyncTask<Integer, Integer, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(FilterActivity.this, null,
                    "Proccesing");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... value) {
            allImageEffects(file);
            return null;
        }

        protected void onPostExecute(String result) {
            if (seekBrightness.getVisibility() == View.VISIBLE) {
                hideSeekBar();
            } else if (linCropView.getVisibility() == View.VISIBLE) {
                hideCropeView();
            } else if (linRotationView.getVisibility() == View.VISIBLE) {
                hideRotationView();
            } else if (linFlipView.getVisibility() == View.VISIBLE) {
                hideFlipView();
            }
            for (int i = 0; i < arrBitmap.length; i++) {
                arrImgView[i].setImageBitmap(Effects.getCroppedBitmap(arrBitmap[i],
                        arrBitmap[i].getWidth()));
            }
            visibleScrollView();
            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

    }


    private class SaveImage extends AsyncTask<Integer, Integer, Integer> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(FilterActivity.this, null,
                    "Proccesing");
        }

        @Override
        protected Integer doInBackground(Integer... value) {
            path = Utility.saveImage(bitmap, imgName, FilterActivity.this);
            if (value[0] == 2) {
                Utility.shareImage(FilterActivity.this, path);
            }
            return value[0];
        }

        protected void onPostExecute(Integer result) {

//			if(result == 2)
//			{
//				Utility.shareImage(FilterActivity.this, path);
//			}
            if (result == 3) {
                callTextActivity();
            }

            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

    }


    @Override
    public void onBackPressed() {
        if (linCropView.getVisibility() == View.VISIBLE) {
            linCropView.setVisibility(View.GONE);
        } else {
            bitmap = bitmapTemp;
            new ConfirmationDialog(this, bitmap, imgName, 2).show();
        }
    }


    private void setImageBackground(int position) {

        for (int i = 0; i < arrImgView.length; i++) {

            arrImgView[i]
                    .setBackgroundResource(position == i ? R.drawable.bg_circle_effect_h
                            : 0);
        }
        new applyEffects().execute(arrImgViewId[position]);
    }

    private void hideSeekBar() {
        seekBrightness.setVisibility(View.GONE);
        bitmapBrightness = null;
        linBrighten.setBackgroundResource(R.drawable.bg_orange_box);
        imgBrighten.setImageResource(R.drawable.ic_brightness);
    }

    private void visibleSeekBar() {
        seekBrightness.setVisibility(View.VISIBLE);
        bitmapBrightness = bitmapTemp;
        linBrighten.setBackgroundResource(R.drawable.bg_orange_box_h);
        imgBrighten.setImageResource(R.drawable.ic_brightness_h);
    }

    private void hideScrollView() {
        scrollEffects.setVisibility(View.GONE);
        linEffects.setBackgroundResource(R.drawable.bg_blue_box);
        imgEffects.setImageResource(R.drawable.ic_effects);
    }

    private void visibleScrollView() {
        scrollEffects.setVisibility(View.VISIBLE);
        linEffects.setBackgroundResource(R.drawable.bg_blue_box_h);
        imgEffects.setImageResource(R.drawable.ic_effects_h);
    }

    private void hideRotationView() {
        linRotationView.setVisibility(View.GONE);
        linRotation.setBackgroundResource(R.drawable.bg_yellow_box);
        imgRotation.setImageResource(R.drawable.ic_rotation);
    }

    private void visibleRotationView() {
        linRotationView.setVisibility(View.VISIBLE);
        linRotation.setBackgroundResource(R.drawable.bg_yellow_box_h);
        imgRotation.setImageResource(R.drawable.ic_rotation_h);
    }

    private void hideFlipView() {
        linFlipView.setVisibility(View.GONE);
        linFlip.setBackgroundResource(R.drawable.bg_violet_box);
        imgFlip.setImageResource(R.drawable.ic_flip);
    }

    private void visibleFlipView() {
        linFlipView.setVisibility(View.VISIBLE);
        linFlip.setBackgroundResource(R.drawable.bg_violet_box_h);
        imgFlip.setImageResource(R.drawable.ic_flip_h);
    }

    private void hideCropeView() {
        linCropView.setVisibility(View.GONE);
        linCrop.setBackgroundResource(R.drawable.bg_pink_box);
        imgCrop.setImageResource(R.drawable.ic_crop);
    }

    public void callTextActivity() {
        Intent intent = new Intent(FilterActivity.this, TextActivity.class);
        intent.putExtra(PATH, path);
        intent.putExtra(IMAGE_NAME, imgName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
