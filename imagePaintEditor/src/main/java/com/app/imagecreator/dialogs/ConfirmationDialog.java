package com.app.imagecreator.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.amapps.imagecreator.R;
import com.app.imagecreator.activities.HomeActivity;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.Utility;

import java.io.File;

public class ConfirmationDialog extends Dialog implements
        android.view.View.OnClickListener, Constant {

    private Activity activity = null;
    private Button btnNo = null;
    private Button btnYes = null;
    private Button btnCancel = null;
    private Bitmap bitmap = null;
    private String imgName = null;
    private int id = 0;

    public ConfirmationDialog(Activity activity, Bitmap bitmap, String imgName, int id) {
        super(activity);
        this.activity = activity;
        this.bitmap = bitmap;
        this.imgName = imgName;
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmation);
        initControls();
    }

    private void initControls() {

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        btnNo = (Button) findViewById(R.id.btnNo);
        btnNo.setOnClickListener(this);

        btnYes = (Button) findViewById(R.id.btnYes);
        btnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCancel:
                dismiss();
                break;

            case R.id.btnNo:
                deleteImage();
                dismiss();
                if (id == 1) {
                    callHomeActivity();
                } else if (id == 2) {
                    callHomeActivity();
                } else {

                }
                break;

            case R.id.btnYes:
                Utility.saveImage(bitmap, imgName, activity);
                dismiss();
                if (id == 1) {
                    callHomeActivity();
                } else if (id == 2) {
                    callHomeActivity();
                    ;
                } else {

                }
                break;

            default:
                break;
        }
    }

    private void callHomeActivity() {
        activity.startActivity(new Intent(activity, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        activity.finish();
    }

    private void deleteImage() {
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + FOLDER_NAME + File.separator + imgName;
        File file = new File(path);
        file.delete();
    }
}

