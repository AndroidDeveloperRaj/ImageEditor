package com.app.imagecreator.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.amapps.imagecreator.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class Utility implements Constant {

    private static Bitmap b;
    private static final String TAG_FILE_SIZE = "file size";
    private static final String ERROR = "Error";

    public static Bitmap decodeFile(Context context, File f) throws IOException {
        if (f == null) {
            return null;
        }
        try {
            // TODO Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            scale = findScale(f);
            b = imgOptimization(f.getAbsolutePath(), scale);
            Matrix matrix = getRotation(context, Uri.fromFile(f));
            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                    matrix, true);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    private static int findScale(File f) {
        int scale;
        double fileSizeInKB = f.length() / 1024;
        double fileSizeInMB = fileSizeInKB / 1024;
        if (fileSizeInMB < 0.5)
            scale = 1;
        else if (fileSizeInMB > 0.5 && fileSizeInMB < 5)
            scale = 2;
        else if (fileSizeInMB > 5 && fileSizeInMB < 10)
            scale = 4;
        else
            scale = 8;
        return scale;
    }

    public static Matrix getRotation(Context context, Uri selectedImage) {

        Matrix matrix = new Matrix();
        ExifInterface exif;
        try {
            exif = new ExifInterface(selectedImage.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Utility.log("orientation", orientation + "");
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                matrix.postScale((float) b.getWidth(), (float) b.getHeight());
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }


    public static void rateMe(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI
                    + activity.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(GOOGLE_PLAY_URI + activity.getPackageName())));
        }
    }

    private static Bitmap imgOptimization(String imgPathName, int scale) {

        Bitmap immagex = null;
        try {
            log("scale", scale + "");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            immagex = BitmapFactory.decodeFile(imgPathName, options);
        } catch (Exception e2) {
            log(ERROR, "error");
            e2.printStackTrace();
        }
        return immagex;
    }


    private static ProgressDialog progress;
    private static String path;


//	public static String imgSavingThread(final Bitmap finalBitmap , final String imgName ,final Context context , final Activity activity, final int from ) {
//		
//		 progress = new ProgressDialog(context); // thread code
//		 progress.setMessage("Loading...");
//
//	        Runnable viewOrders = new Runnable() // thread code
//	        {
//	            public void run() // thread code
//	            {
//	                path = saveImage(finalBitmap, imgName, context);
////	                activity.runOnUiThread(returnRes);
//	                progress.cancel();
//	                if(context instanceof FilterActivity)
//	                {
//	                	FilterActivity activity = (FilterActivity) context;
//	                	if(from == 1)
//	                	{
//	                		longToast(activity, activity.getString(R.string.image_is_stored_at_) + path);
//	                		activity.startActivity(new Intent(activity, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//	                		activity.finish();
//	                	}
//	                	else if(from == 2)
//	                	{
//	                		shareImage(activity, path);
//	                	}
//	                	else if(from == 3)
//	                	{
//	                		activity.callTextActivity();
//	                	}
//	                	else
//	                	{
//	                		
//	                	}
//	                		
//	                }
//	                else if(context instanceof DrawaingActivity)
//	                {
//	                	DrawaingActivity activity = (DrawaingActivity) context;
//		                	if(from == 1)
//		                	{
//		                		longToast(activity, activity.getString(R.string.image_is_stored_at_) + path);
//		                		activity.startActivity(new Intent(activity, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//		                		activity.finish();
//		                	}
//		                	else if(from == 2)
//		                	{
//		                		shareImage(activity, path);
//		                	}
//		                	else if(from == 3)
//		                	{
//		                		activity.callFilterActivity();;
//		                	}
//		                	else
//		                	{
//		                		
//		                	}
//	                }
//	                else if(context instanceof AddTextActivity)
//	                {
//	                	AddTextActivity activity = (AddTextActivity) context;
//	                	if(from == 1)
//	                	{
//	                		activity.callFilterActivity();
//	                		PaintApplication.preferenceData.setCordinates(200, 200);
//	                	}
//	                }
//	            }
//	        };
//	        Thread thread1 = new Thread(null, viewOrders, "Background"); // thread
//	        thread1.start(); // thread code
//	        progress.show();
//	        return path;
//		
//	}


//	private static Runnable returnRes = new Runnable() // thread code
//    {
//        public void run() // thread code
//        {
//        	progress.cancel();
//        }
//    };

    public static String saveImage(Bitmap finalBitmap, String imgName,
                                   Context context) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + File.separator + FOLDER_NAME);
        myDir.mkdirs();
        File file = new File(myDir, imgName);
        if (file.exists())
            file.delete();

        String imagePath = file.getAbsolutePath();

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        MediaScannerConnection.scanFile(context, new String[]{imagePath},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });

        return imagePath;
    }

    public static void log(String tag, String value) {
        if (Constant.IS_DEBUG)
            Log.e(tag, value);
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static OnDialogClick dialogClick = null;

    public static void openAlertDialog(Context context, Fragment fragment,
                                       String message, final int code) {
        if (message == null) {
            message = context.getString(R.string.message_null_check_this_);
            return;
        }
        if (code != 0) {
            if (fragment != null)
                dialogClick = (OnDialogClick) fragment;
            else
                dialogClick = (OnDialogClick) context;
        }
        new AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                if (dialogClick != null)
                                    dialogClick.onDialogPositiveClick(code);
                            }
                        }).show();
    }


    public static Bitmap convertToBitmap(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void shareImage(Activity activity, String imagePath) {

        Intent share = new Intent(Intent.ACTION_SEND);

        share.setType("image/*");

        File imageFileToShare = new File(imagePath);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        activity.startActivity(Intent.createChooser(share, "Share Image!"));

    }

    public static Bitmap drawText(TextView textView) {
        textView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

        textView.setDrawingCacheEnabled(true);
        textView.buildDrawingCache();
        return textView.getDrawingCache();
    }

    /******************************************************************************
     * Gets the distance between two points (x1,y1) and (x2,y2)
     ******************************************************************************/
    public static float getDistance(int x1, int y1, int x2, int y2) {
        float dist = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1)
                * (y2 - y1));
        return dist;
    }

    /******************************************************************************
     * Gets the elevation of a line formed by two points (x1,y1) and (x2,y2)
     ******************************************************************************/
    public static float getInclination(int x1, int y1, int x2, int y2) {

        if ((x2 - x1) == 0)
            return 90.0f;

        float m = ((float) (y2 - y1)) / (x2 - x1);
        float angle = (float) ((float) ((float) Math.atan(m)) * 180 / Math.PI);
        return angle;
    }

	/*
     * Routine to check memory map for VM and Native Heap
	 */

    public static void logHeap() {

        Double allocated = new Double(Debug.getNativeHeapAllocatedSize());// new
        Double available = new Double(Debug.getNativeHeapSize());// 1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize());// 1048576.0;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

//		Log.i("gettings", "debug. =================================");
//		Log.i("gettings",
//				"debug.heap native: allocated " + df.format(allocated)
//						+ "MB of " + df.format(available) + "MB ("
//						+ df.format(free) + "MB free)");

        if ((Runtime.getRuntime().maxMemory() / 1024) - allocated / 1024 <= (6 * 1024)) {

//			Log.i("gettings",
//					"debug. for shut down=================================");

        }

    }

    public static void loadFromGallary(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            activity.startActivityForResult(intent, FETCH_FROM_GALLERY);
        } catch (ActivityNotFoundException e) {
            openAlertDialog(activity, null, "Gallery Not Found", 123);
        }
    }

    public static Bitmap decodeFile(Context context, Bitmap bitmap, int width,
                                    int height) throws IOException {
        if (bitmap == null) {
            return null;
        }
//		try {
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//
//			FileInputStream fis = new FileInputStream(f);
//			b = BitmapFactory.decodeStream(fis, null, o);
//			fis.close();
//
//			int scale = 1;
//			scale = findScale(f);
//			b = imgOptimization(f.getAbsolutePath(), scale);

        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);

//			fis.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
        return bitmap;
    }

    public static void loadAdd(final com.google.android.gms.ads.AdView mAdView) {
        mAdView.setVisibility(View.GONE);

        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice("D8531AF26FF68D8CE69D93814B59B92A");
        com.google.android.gms.ads.AdRequest adRequest = builder.build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

        });
    }

    public static boolean isNotNull(Object object) {
        if (object != null
                && !(object.toString().trim().equals("") || object.toString()
                .trim().equalsIgnoreCase("null")))
            return true;
        else
            return false;
    }
}
