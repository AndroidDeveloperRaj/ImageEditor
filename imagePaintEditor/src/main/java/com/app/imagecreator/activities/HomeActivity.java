package com.app.imagecreator.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amapps.imagecreator.R;
import com.app.imagecreator.PaintApplication;
import com.app.imagecreator.adapters.ImageViewPagerAdapter;
import com.app.imagecreator.customviews.CirclePageIndicator;
import com.app.imagecreator.customviews.CustomTextview;
import com.app.imagecreator.inapp.BillingProcessor;
import com.app.imagecreator.inapp.BillingProcessor.IBillingHandler;
import com.app.imagecreator.inapp.TransactionDetails;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.Utility;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends Activity implements OnClickListener, Constant {

    private static final int FETCH_FROM_CAMERA_FULLVIEW = 2;
    private static final String CAMERA_IMAGE_URI = "cameraImageUri";

    private LinearLayout linGallary = null, linCamera = null, linPaint = null, linRateMe = null, linAdView = null;
    private ImageView imgSetting = null;
    private CustomTextview txtSetting = null;

    private Uri outputFileUri;
    private String picturePath;

    private ViewPager viewPager = null;
    private CirclePageIndicator circlePageIndicator = null;
    private ImageViewPagerAdapter adapter = null;
    public String[] allFiles, list;
    private int[] defaultImgs = {R.drawable.def_five, R.drawable.def_three,
            R.drawable.def_four, R.drawable.def_one, R.drawable.def_two};
    private int position;
    private com.google.android.gms.ads.AdView adView = null;
    public static BillingProcessor billingProcessor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initControls();
    }

    private void initControls() {

        linGallary = (LinearLayout) findViewById(R.id.linGallary);
        linGallary.setOnClickListener(this);

        linCamera = (LinearLayout) findViewById(R.id.linCamera);
        linCamera.setOnClickListener(this);

        linPaint = (LinearLayout) findViewById(R.id.linPaint);
        linPaint.setOnClickListener(this);

        linRateMe = (LinearLayout) findViewById(R.id.linRateMe);
        linRateMe.setOnClickListener(this);

        linAdView = (LinearLayout) findViewById(R.id.linAdView);

        imgSetting = (ImageView) findViewById(R.id.imgSetting);
        txtSetting = (CustomTextview) findViewById(R.id.txtSetting);

        viewPager = (ViewPager) findViewById(R.id.homeItemPager);

        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.homeItemIndicator);

        adView = (com.google.android.gms.ads.AdView) findViewById(R.id.adView);
        if (!PaintApplication.preferenceData.isPurchased()) {
            linAdView.setVisibility(View.VISIBLE);
            Utility.loadAdd(adView);
        }

        if (PaintApplication.preferenceData.isPurchased()) {
            imgSetting.setImageResource(R.drawable.ic_star);
            txtSetting.setText(getString(R.string.home_button_rate_me));
        } else {
            imgSetting.setImageResource(R.drawable.ic_setting);
            txtSetting.setText(getString(R.string.settings));
        }


        list = new String[5];

        String path = Environment.getExternalStorageDirectory().toString()
                + File.separator + FOLDER_NAME;
        File file = new File(path);
        allFiles = file.list();
        int i, j;
        String latest = null;
        long time = 0;
        if (allFiles != null) {
            for (i = 0, j = (allFiles.length - 1); i < 5
                    && j >= (allFiles.length - 5); i++, j--) {
                if (j >= 0) {
                    Utility.log("img name", allFiles[j]);
                    Utility.log("path", path + File.separator + allFiles[j]);
                    list[i] = path + File.separator + allFiles[j];

                    File file1 = new File(list[i]);
                    if (file1.exists()) {
                        if (file1.lastModified() > time) {
                            latest = list[i];
                            time = file1.lastModified();
                        }
                    }
                    Utility.log("path", list[i]);
                } else {
                    break;
                }
            }
        }

        list[0] = latest;
        list[1] = list[2] = list[3] = list[4] = null;

        adapter = new ImageViewPagerAdapter(this, list, defaultImgs);
        viewPager.setAdapter(adapter);
        circlePageIndicator.setViewPager(viewPager);
        refreshImage(0);

        billingProcessor = new BillingProcessor(this, LICENSE_KEY,
                new IBillingHandler() {

                    @Override
                    public void onPurchaseHistoryRestored() {

                        isProductPurchase();
                    }

                    @Override
                    public void onProductPurchased(String productId,
                                                   TransactionDetails details) {

                        isProductPurchase();
                    }

                    @Override
                    public void onBillingInitialized() {

                        isProductPurchase();
                    }

                    @Override
                    public void onBillingError(int errorCode, Throwable error) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linGallary:
                Utility.loadFromGallary(this);
                break;

            case R.id.linCamera:
                loadFromCamera();
                break;

            case R.id.linPaint:
                Intent intent = new Intent(HomeActivity.this,
                        DrawaingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.linRateMe:
                //Utility.rateMe(this);
                if (billingProcessor.isPurchased(PRODUCT_ID)) {
                    Utility.rateMe(this);
                } else {
                    //new SettingDialog(this).show();
                    showSettingsDialog();
                }
                break;

            default:
                break;
        }
    }

    private void loadFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues = new ContentValues();
        outputFileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, FETCH_FROM_CAMERA_FULLVIEW);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FETCH_FROM_CAMERA_FULLVIEW
                && resultCode == RESULT_OK) {

            String path = getRealPathfromUri(outputFileUri);
            // Utility.log("ImgPath", path);
            Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
            intent.putExtra(PATH, path);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (requestCode == FETCH_FROM_GALLERY && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
            intent.putExtra(PATH, picturePath);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);

    }

    private String getRealPathfromUri(Uri capturedUri2) {
        String path = null;
        try {
            String projection[] = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(capturedUri2,
                    projection, null, null, null);
            int columnIndex = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outputFileUri != null) {
            outState.putString(CAMERA_IMAGE_URI, outputFileUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(CAMERA_IMAGE_URI)) {
            outputFileUri = Uri.parse(savedInstanceState
                    .getString(CAMERA_IMAGE_URI));
        }
    }

    private void refreshImage(int pos) {
        position = pos + 1;
        Timer mTimerPromotions = new Timer();
        mTimerPromotions.schedule(new RefreshPromotionsTask(), 5000, 3000);
    }

    class RefreshPromotionsTask extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager != null && viewPager.getChildCount() > 0) {
                        if (viewPager.getCurrentItem() + 1 < list.length) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        } else {
                            viewPager.setCurrentItem(0, false);
                        }
                    }
                }
            });
        }
    }

    private void isProductPurchase() {
        com.google.android.gms.ads.AdView mAdView = (com.google.android.gms.ads.AdView) findViewById(R.id.adView);
        if (billingProcessor.isPurchased(PRODUCT_ID)) {
            imgSetting.setImageResource(R.drawable.ic_star);
            txtSetting.setText(getString(R.string.home_button_rate_me));
            if (mAdView != null) {
                mAdView.setVisibility(View.GONE);
            }
            PaintApplication.preferenceData.setPurchased(true);
        } else {
            PaintApplication.preferenceData.setPurchased(false);
            imgSetting.setImageResource(R.drawable.ic_setting);
            txtSetting.setText(getString(R.string.settings));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isProductPurchase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingProcessor != null)
            billingProcessor.release();
    }

    private void showSettingsDialog() {

        String[] items = new String[2];
        items[0] = ("Rate Me");
        items[1] = ("Remove Ads($2.99)");
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Settings")
                //.setMessage("Are you sure you want to delete this entry?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Utility.rateMe(HomeActivity.this);
                            dialog.dismiss();
                        } else if (which == 1) {
                            HomeActivity.billingProcessor.purchase(HomeActivity.this, PRODUCT_ID);
                            dialog.dismiss();
                        }
                    }
                })
                .setIcon(R.drawable.ic_heart)
                .show();
    }

}
