package com.example.etsy.myetsy;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static boolean haveConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void displayMessage(Context context, int message_resource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message_resource);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static SearchResponse parseJsonFeed(JSONObject response) {
        int nextPage = -1;
        int count = 0;
        String error = null;

        try {
            JSONArray listingArray = response.getJSONArray("results");

            count = response.getInt("count");

            for (int i = 0; i < listingArray.length(); i++) {
                JSONObject listingObj = (JSONObject) listingArray.get(i);

                String image = listingObj.isNull("MainImage") ? null : listingObj.getJSONObject("MainImage").getString("url_75x75");
                String title = listingObj.isNull("title") ? null : listingObj.getString("title");

                ListingCache.getInstance().addListing(new Listing(title, image));
            }

            nextPage = response.getJSONObject("pagination").isNull("next_page") ? -1: response.getJSONObject("pagination").getInt("next_page");

        } catch (JSONException e) {
            error = e.toString();
        }
        if (error != null) {
            return new SearchResponse().setError(error);
        } else {
            return new SearchResponse().setNextPage(nextPage).setCount(count);
        }
    }

}
