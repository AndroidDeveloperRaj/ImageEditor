package com.app.imagecreator.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.amapps.imagecreator.R;
import com.app.imagecreator.activities.HomeActivity;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.Utility;

public class SaveConfirmationDialog extends Dialog implements
        android.view.View.OnClickListener, Constant {

    private Activity activity = null;
    private Button btnNo = null;
    private Button btnYes = null;
    private Bitmap bitmap = null;
    private String path = null;
    private String imgName = null;
    private Context context = null;

    public SaveConfirmationDialog(Activity activity, Context context, Bitmap bitmap, String imgName) {
        super(activity);
        this.activity = activity;
        this.bitmap = bitmap;
        this.imgName = imgName;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_save_confirmation);
        initControls();
    }

    private void initControls() {

        btnNo = (Button) findViewById(R.id.btnNo);
        btnNo.setOnClickListener(this);

        btnYes = (Button) findViewById(R.id.btnYes);
        btnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnNo:
                dismiss();
                break;

            case R.id.btnYes:
//		path = Utility.imgSavingThread(bitmap, imgName, context, activity , 1);
//		path = Utility.saveImage(bitmap, imgName, activity);
                dismiss();
                new SaveImage().execute();
//		Utility.longToast(activity, activity.getString(R.string.image_is_stored_at_) + path);
//		callHomeActivity();
                break;

            default:
                break;
        }
    }


    private class SaveImage extends AsyncTask<Integer, Integer, Integer> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(context, null,
                    "Proccesing");
        }

        @Override
        protected Integer doInBackground(Integer... value) {
            path = Utility.saveImage(bitmap, imgName, context);
            return 0;
        }

        protected void onPostExecute(Integer result) {

            Utility.longToast(activity, activity.getString(R.string.image_is_stored_at_) + path);
            callHomeActivity();

            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

    }


    private void callHomeActivity() {
        activity.startActivity(new Intent(activity, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        activity.finish();
    }

}
