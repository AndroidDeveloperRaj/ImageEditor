package com.app.imagecreator.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.amapps.imagecreator.R;
import com.app.imagecreator.inapp.BillingProcessor;
import com.app.imagecreator.inapp.TransactionDetails;
import com.app.imagecreator.preference.PreferenceData;
import com.app.imagecreator.utility.Constant;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements Constant {

    private static final long SPLASH_TIME = 2000;
    private PreferenceData preferenceData = null;
    private BillingProcessor billingProcessor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initControl();
        setSplashTime();
        setBillingProcess();
    }

    private void initControl() {

        preferenceData = new PreferenceData(this);
    }

    private void setSplashTime() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this,
                        HomeActivity.class));
                finish();

            }
        }, SPLASH_TIME);
    }

    private void setBillingProcess() {
        billingProcessor = new BillingProcessor(SplashActivity.this, LICENSE_KEY,
                new BillingProcessor.IBillingHandler() {
                    @Override
                    public void onProductPurchased(String productId,
                                                   TransactionDetails details) {
                        isProductPurchase();
                    }

                    @Override
                    public void onBillingError(int errorCode, Throwable error) {
                    }

                    @Override
                    public void onBillingInitialized() {

                        isProductPurchase();

                    }

                    @Override
                    public void onPurchaseHistoryRestored() {

                        isProductPurchase();
                    }
                });

    }

    private void isProductPurchase() {
        if (billingProcessor.isPurchased(PRODUCT_ID)) {
            preferenceData.setPurchased(true);
        } else {
            preferenceData.setPurchased(false);

        }

    }

    @Override
    public void onBackPressed() {
    }

}