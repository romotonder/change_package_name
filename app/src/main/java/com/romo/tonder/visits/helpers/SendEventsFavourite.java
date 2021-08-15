package com.romo.tonder.visits.helpers;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class SendEventsFavourite {
    private Context mContext;

    public SendEventsFavourite(Context context) {
        this.mContext = context;
    }

    public void userFavourite(String event_id,String user_id) {
        String url = Common.BASE_URL + "discussion-favourite";
        AndroidNetworking.post(url)
                .addBodyParameter("eventId", event_id)
                .addBodyParameter("userId", user_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseFavorite(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseFavorite(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                String data = response.getString("Data");
                if (data.equals("Favourite.")) {

                } else {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
