package com.romo.tonder.visits.activities;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.FavouriteAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.FavouriteModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class FavuritesActivity extends BaseActivity {
    private static final String TAG = FavuritesActivity.class.getSimpleName();
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private SharedPreferences appPrefs;
    private Dialog dialog;
    private Context context;
    private RecyclerView listingArea;
    private ArrayList<FavouriteModels> favouriteList;
    private FavouriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favurites);
        bindActivity();
        getFavList(appPrefs.getString("userID",""));


    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        favouriteList = new ArrayList<>();
        listingArea = findViewById(R.id.listing_area);
        listingArea.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new FavouriteAdapter(favouriteList, FavuritesActivity.this);
        listingArea.setAdapter(adapter);
    }

    private void getFavList(String id) {

        String url = Common.BASE_URL + "my-favourite";
        AndroidNetworking.post(url)
                //.addBodyParameter("userId", appPrefs.getString("userID", ""))
                .addBodyParameter("userId", id)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponse(JSONObject response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject=null;
            JSONArray jsonArray=null;
            if (response.getString("status").equals("Success")) {
                if (!response.getString("favouriteLists").equals("null")){
                    jsonArray=response.getJSONArray("favouriteLists");
                    for (int index=0;index<jsonArray.length();index++){
                        FavouriteModels models=new FavouriteModels();
                        jsonObject = jsonArray.getJSONObject(index);
                        models.setListType(jsonObject.getString("listingType"));
                        models.setListID(jsonObject.getString("listingId"));
                        models.setListTitle(jsonObject.getString("listingTitle"));
                        models.setListTagline(jsonObject.getString("listingTagLine"));
                        models.setCoverImage(jsonObject.getString("coverImage"));
                        models.setViewType(1);
                        favouriteList.add(models);
                        if (favouriteList.size()>0){
                            adapter.notifyDataSetChanged();
                        }else {
                            showNoData();
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void showNoData() {
        favouriteList.clear();
        FavouriteModels model = new FavouriteModels();
        model.setViewType(3);
        favouriteList.add(model);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void back(View view) {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}