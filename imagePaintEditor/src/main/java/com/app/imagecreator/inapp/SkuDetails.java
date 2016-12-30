package com.app.imagecreator.inapp;

import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails {

    public final String productId;

    public final String title;

    public final String description;

    public final boolean isSubscription;

    public final String currency;

    public final Double priceValue;

    public final String priceText;

    public SkuDetails(JSONObject source) throws JSONException {
        String responseType = source.optString(Constants.RESPONSE_TYPE);
        if (responseType == null)
            responseType = Constants.PRODUCT_TYPE_MANAGED;
        productId = source.optString(Constants.RESPONSE_PRODUCT_ID);
        title = source.optString(Constants.RESPONSE_TITLE);
        description = source.optString(Constants.RESPONSE_DESCRIPTION);
        isSubscription = responseType.equalsIgnoreCase(Constants.PRODUCT_TYPE_SUBSCRIPTION);
        currency = source.optString(Constants.RESPONSE_PRICE_CURRENCY);
        priceValue = source.optDouble(Constants.RESPONSE_PRICE_MICROS) / 1000000;
        priceText = source.optString(Constants.RESPONSE_PRICE);
    }

    @Override
    public String toString() {
        return String.format("%s: %s(%s) %f in %s (%s)", productId, title, description, priceValue, currency, priceText);
    }
}
