
package com.app.imagecreator.inapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * With this PurchaseInfo a developer is able verify
 * a purchase from the google play store on his own
 * server. An example implementation of how to verify
 * a purchase you can find <a href="https://github.com/mgoldsborough/google-play-in-app-billing-verification/blob/master/library/GooglePlay/InAppBilling/GooglePlayResponseValidator.php#L64">here</a>
 */
public class PurchaseInfo {

    public enum PurchaseState {
        PurchasedSuccessfully, Canceled, Refunded, SubscriptionExpired;
    }

    public final String responseData;
    public final String signature;

    PurchaseInfo(String responseData, String signature) {
        this.responseData = responseData;
        this.signature = signature;
    }

    public class ResponseData {

        public String orderId;
        public String packageName;
        public String productId;
        public Date purchaseTime;
        public PurchaseState purchaseState;
        public String developerPayload;
        public String purchaseToken;
        public boolean autoRenewing;
    }

    public static PurchaseState getPurchaseState(int state) {
        switch (state) {
            case 0:
                return PurchaseState.PurchasedSuccessfully;
            case 1:
                return PurchaseState.Canceled;
            case 2:
                return PurchaseState.Refunded;
            case 3:
                return PurchaseState.SubscriptionExpired;
            default:
                return PurchaseState.Canceled;
        }
    }

    public ResponseData parseResponseData() {
        try {
            JSONObject json = new JSONObject(responseData);
            ResponseData data = new ResponseData();
            data.orderId = json.optString("orderId");
            data.packageName = json.optString("packageName");
            data.productId = json.optString("productId");
            long purchaseTimeMillis = json.optLong("purchaseTime", 0);
            data.purchaseTime = purchaseTimeMillis != 0 ? new Date(purchaseTimeMillis) : null;
            data.purchaseState = getPurchaseState(json.optInt("purchaseState", 1));
            data.developerPayload = json.optString("developerPayload");
            data.purchaseToken = json.getString("purchaseToken");
            data.autoRenewing = json.optBoolean("autoRenewing");
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
