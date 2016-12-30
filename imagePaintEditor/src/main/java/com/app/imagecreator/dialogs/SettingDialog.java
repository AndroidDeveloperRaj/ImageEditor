package com.app.imagecreator.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.amapps.imagecreator.R;
import com.app.imagecreator.activities.HomeActivity;
import com.app.imagecreator.customviews.CustomTextview;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.Utility;

public class SettingDialog extends Dialog implements android.view.View.OnClickListener, Constant {

    private Activity activity = null;
    private CustomTextview txtRateMe = null;
    public CustomTextview txtRemoveAds = null;
    public CustomTextview txtCancel = null;

    public SettingDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_setting);
        initControls();
    }

    private void initControls() {

        txtRateMe = (CustomTextview) findViewById(R.id.txtRateMe);
        txtRateMe.setOnClickListener(this);

        txtRemoveAds = (CustomTextview) findViewById(R.id.txtRemoveAds);
        txtRemoveAds.setOnClickListener(this);

        txtCancel = (CustomTextview) findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);

        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtRateMe:
                Utility.rateMe(activity);
                dismiss();
                break;

            case R.id.txtRemoveAds:
                HomeActivity.billingProcessor.purchase(activity, PRODUCT_ID);
                dismiss();
                break;

            case R.id.txtCancel:
                dismiss();

            default:
                break;
        }
    }

}
