package com.app.imagecreator.utility;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UncaughtExceptionUtil {

 /*
  * Usage: Inside the application class, call UncaughtExceptionUtil.init(); in onCreate() method. This will
  * automatically ensure that a log file is written to the external directory before crashing. Note: Remove call from
  * Release Version. For debugging purposes only.
  */


    public static void init() {
        final UncaughtExceptionHandler sDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            final SimpleDateFormat dateFormatWithCurrentTimeZone = new SimpleDateFormat("yyyyMMddHHmmssZ",
                    Locale.US);

            private String formatDate(Date date) {
                String result = dateFormatWithCurrentTimeZone.format(date);
                result = result.replace('+', 'a');
                result = result.replace('-', 's');
                return result;
            }

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                String path = Environment.getExternalStorageDirectory() + File.separator + "Paint" + "/logcat/" + formatDate(new Date()) + ".txt";
                File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Paint" + "/logcat/");
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                File file = new File(path);
                file.delete();
                if (!file.exists())
                    try {
                        file.createNewFile();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                PrintStream err;
                try {
                    err = new PrintStream(file);
                    ex.printStackTrace(err);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    sDefaultExceptionHandler.uncaughtException(thread, ex);
                }
            }
        });
    }

}
