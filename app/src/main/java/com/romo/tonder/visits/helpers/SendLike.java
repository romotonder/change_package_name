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

public class  SendLike {
    private Context mContext;

    public SendLike(Context context) {
        this.mContext = context;
    }
    public void userEventLikeDislike(String event_id){
        SharedPreferences appPrefs;
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();

        String url = Common.BASE_URL + "discussion-like";
        AndroidNetworking.post(url)
                .addBodyParameter("discussionId", event_id)
                .addBodyParameter("userId", appPrefs.getString("userID",""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponselike(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponselike(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                String data = response.getString("Data");
                if (data.equals("Liked.")) {

                } else {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void userListReviewLikeDislike(String id){
        SharedPreferences appPrefs;
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();

        String url = Common.BASE_URL + "review-like";
        AndroidNetworking.post(url)
                .addBodyParameter("reviewId", id)
                .addBodyParameter("userId", appPrefs.getString("userID",""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseReviewlike(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponseReviewlike(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                String data = response.getString("Data");
                if (data.equals("Liked.")) {

                } else {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
