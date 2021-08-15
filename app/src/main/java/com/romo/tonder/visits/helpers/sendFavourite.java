package com.romo.tonder.visits.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.romo.tonder.visits.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class sendFavourite {
    private Context mContext;

    public sendFavourite(Context context) {
        this.mContext = context;
    }
    public void listingFavourite(String listing_id){
        SharedPreferences appPrefs;
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();

        String url = Common.BASE_URL + "listing-favourite";
        AndroidNetworking.post(url)
                .addBodyParameter("listingId", listing_id)
                .addBodyParameter("userId", appPrefs.getString("userID",""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: Fav" + response);
                        parseResponse(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponse(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                String data = response.getString("Data");
                if (data.equals("Favourite.")) {
                    Log.d(TAG, "Favourite: ");

                } else {
                    Log.d(TAG, "Unfavourite: ");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
