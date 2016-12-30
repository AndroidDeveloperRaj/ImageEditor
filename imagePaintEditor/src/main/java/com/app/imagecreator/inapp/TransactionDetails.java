package com.app.imagecreator.inapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class TransactionDetails {

    public final String productId;

    public final String orderId;

    public final String purchaseToken;

    public final Date purchaseTime;

    public final PurchaseInfo purchaseInfo;

    public TransactionDetails(PurchaseInfo info) throws JSONException {
        JSONObject source = new JSONObject(info.responseData);
        purchaseInfo = info;
        productId = source.getString(Constants.RESPONSE_PRODUCT_ID);
        orderId = source.optString(Constants.RESPONSE_ORDER_ID);
        purchaseToken = source.getString(Constants.RESPONSE_PURCHASE_TOKEN);
        purchaseTime = new Date(source.getLong(Constants.RESPONSE_PURCHASE_TIME));
    }

    @Override
    public String toString() {
        return String.format("%s purchased at %s(%s). Token: %s, Signature: %s", productId, purchaseTime, orderId, purchaseToken, purchaseInfo.signature);
    }
}
