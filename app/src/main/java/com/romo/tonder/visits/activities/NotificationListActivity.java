package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.NotificationListingAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.NotificationListings;
import com.romo.tonder.visits.models.Notifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Response;

public class NotificationListActivity extends BaseActivity {
    public static final String TAG = ListingActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<Notifications> notificationsArrayList;
    private String listType;
    private boolean fromNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.notification_list_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        notificationsArrayList = new ArrayList<>();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_back_arrow_24_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listType = getIntent().getExtras().getString(Common.NOTIFICATION_LIST_TYPE);
        if (getIntent().getExtras().getString(Common.NOTIFIED) != null) {
            fromNotification = true;
        }
        progressBar.setVisibility(View.VISIBLE);
        callForNotificationList();
    }

    private void callForNotificationList() {
        String url = Common.BASE_URL + "my-notification";
        String userID = ((MyApplication) getApplicationContext()).getAppPrefs().getString("userID", "");
        AndroidNetworking.post(url)
                .addBodyParameter("userId", userID)
                .addBodyParameter("displayType", listType)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);

                        parseResponseforListing(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforListing(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                JSONArray results = response.getJSONArray("oData");
                ArrayList<NotificationListings> listings = new ArrayList<>();


                if (results.length() > 0) {
                    notificationsArrayList = new ArrayList<>();

                    for (int a = 0; a < results.length(); a++) {
                        JSONObject jsonObject = results.getJSONObject(a);
                        Notifications notification = new Notifications();


                        if (jsonObject.has("notificationId")) {
                            notification.setNotificationId(jsonObject.getString("notificationId"));
                        }

                        if (jsonObject.has("notificationType")) {
                            notification.setNotificationType(jsonObject.getString("notificationType"));
                        }

                        if (jsonObject.has("notificationTitle")) {
                            notification.setNotificationTitle(jsonObject.getString("notificationTitle"));
                        }

                        if (jsonObject.has("notificationDetails")) {
                            notification.setNotificationDetails(jsonObject.getString("notificationDetails"));
                        }

                        if (jsonObject.has("notificationImage")) {
                            notification.setNotificationImage(jsonObject.getString("notificationImage"));
                        }

                        if (jsonObject.has("redirectLink")) {
                            notification.setRedirectLink(jsonObject.getString("redirectLink"));
                        }

                        if (jsonObject.has("notificationStatus")) {
                            notification.setNotificationStatus(jsonObject.getString("notificationStatus"));
                        }

                        try {
                            JSONObject s = jsonObject.getJSONObject("data");
                            if (s.has("promoCode")) {
                                String promo = s.getString("promoCode");
                                notification.setPromoCode(promo);
                            }
                            if (s.has("data")) {
                                String data = s.getString("data");
                                notification.setData(data);
                            }
                        } catch (Exception e) {

                        }


                        notificationsArrayList.add(notification);
                    }
                }

                Log.i(TAG, String.valueOf(results));
                Log.i(TAG, String.valueOf(notificationsArrayList));
                NotificationListingAdapter notificationListingAdapter = new NotificationListingAdapter(this, null, notificationsArrayList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notificationListingAdapter);
                progressBar.setVisibility(View.GONE);
            } else {

                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        callForNotificationList();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromNotification) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}