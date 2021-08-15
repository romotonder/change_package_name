package com.romo.tonder.visits.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.EventAdapter;
import com.romo.tonder.visits.dialogs.GlobalSearchDialog;
import com.romo.tonder.visits.fragments.FilterationBottomShitFrag;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.GPSTracker;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.EventModel;
import com.romo.tonder.visits.models.FilterChoiceEventBusModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

import static com.romo.tonder.visits.activities.ListingActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class EventActivity extends BaseActivity {
    private static final String TAG = EventActivity.class.getSimpleName();
    private static final String event_activity = "from_event";
    private Dialog dialog;
    private Context context;
    private int pageNo = 1;
    private int count = 10;
    private ProgressBar proBar;
    private SharedPreferences appPrefs;
    private NestedScrollView bodyArea;
    private RecyclerView eventRecycler;
    private ArrayList<EventModel> event_list;
    private EventAdapter adapter;
    private static final int NO_INTERNET = -1;
    private static final int NO_DATA = 0;
    private static final int EVENT = 1;
    private Intent nextPage = null;

    private ArrayList<CategoryOrRegionFilterListModel> categoryFilterList;
    private ArrayList<CategoryOrRegionFilterListModel> regionFilterList;
    private LinearLayout regionSelectionLayout;
    private LinearLayout categorySelectionLayout;
    private TextView regionListTextView, categoryListTextView;
    private ImageView locationSortingImageView;
    private ImageView regionSelectionImageView;
    private ImageView locationCheckedImageView;
    private ImageView globalSearchIcon;
    private ProgressBar locationProBar;
    private String selectedCategoryId = "";
    private String selectedRegionId = "";
    private String selectedRegionName = "";
    private boolean currentLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        context = this;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        bindActivity();
        getTotalList(pageNo, count);
        bodyArea.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    pageNo++;
                    proBar.setVisibility(View.VISIBLE);
                    if (currentLocation) {
                        getTotalListByLocation(pageNo, count);
                    } else {
                        getTotalList(pageNo, count);
                    }
                }
            }
        });
    }

    private void getTotalList(int pageNo, int count) {
        String url = Common.BASE_URL + "event";
        AndroidNetworking.post(url)
                .addBodyParameter("pageNo", String.valueOf(pageNo))
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .addBodyParameter("regionId", selectedRegionId)
                .addBodyParameter("categoryId", selectedCategoryId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        proBar.setVisibility(View.GONE);
                        locationSortingImageView.setVisibility(View.VISIBLE);
                        locationProBar.setVisibility(View.GONE);
                        parseResponseforListing(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

        if (categoryFilterList.size() == 0 && regionFilterList.size() == 0)
            getFilterList();

    }

    private void parseResponseforListing(JSONObject response) {
        try {

            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
                JSONObject settings = data.getJSONObject("oSettings");
                JSONArray results = data.getJSONArray("oResults");
                for (int i = 0; i < results.length(); i++) {
                    EventModel bean = new EventModel();
                    JSONObject jsonObject = results.getJSONObject(i);
                    bean.setEventId(jsonObject.getString("ID"));
                    bean.setEventPostTitle(jsonObject.getString("postTitle"));
                    bean.setEventTagLine(jsonObject.getString("tagLine"));
                    bean.setEventCoverImg(jsonObject.getString("coverImg"));
                    bean.setEventLogo(jsonObject.getString("logo"));

                    JSONObject oaddress = jsonObject.getJSONObject("oAddress");
                    bean.setEventAdd(oaddress.getString("address"));
                    bean.setEventLat(oaddress.getString("lat"));
                    bean.setEventLag(oaddress.getString("lag"));
                    bean.setGoogleMapUrl(oaddress.getString("googleMapUrl"));
                    bean.setEventHourMode(jsonObject.getString("hourMode"));
                    bean.setEventPublistDate(jsonObject.getString("publishDate"));
                    bean.setEventPeopleInterested(jsonObject.getString("peopleInterested"));
                    bean.setEventbyUser(jsonObject.getString("byUser"));
                    bean.setEventHostedBy(jsonObject.getString("hostedBy"));
                    bean.setViewType(EVENT);
                    event_list.add(bean);
                }
                adapter = new EventAdapter(event_list, context);
                eventRecycler.setAdapter(adapter);
            } else {
                showNoData();
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNoData() {
        event_list.clear();
        EventModel bean = new EventModel();
        bean.setViewType(NO_DATA);
        event_list.add(bean);
        adapter.notifyDataSetChanged();
    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        eventRecycler = findViewById(R.id.listing_area);
        eventRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        event_list = new ArrayList<>();
        adapter = new EventAdapter(event_list, this);
        eventRecycler.setAdapter(adapter);
        bodyArea = findViewById(R.id.body_area);
        proBar = findViewById(R.id.progressBar);

        categoryFilterList = new ArrayList<>();
        regionFilterList = new ArrayList<>();
        globalSearchIcon = findViewById(R.id.global_search_icon);
        locationCheckedImageView = findViewById(R.id.location_checked_image_view);
        regionSelectionImageView = findViewById(R.id.region_selection_image_view);
        locationSortingImageView = findViewById(R.id.location_sorting_image_view);
        regionSelectionLayout = findViewById(R.id.region_selection_layout);
        categorySelectionLayout = findViewById(R.id.category_selection_layout);
        regionListTextView = findViewById(R.id.region_list_text_view);
        categoryListTextView = findViewById(R.id.category_list_text_view);
        locationProBar = findViewById(R.id.location_progress_bar);
        regionSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regionFilterList != null) {
                    FilterationBottomShitFrag bottomSheetDialog = new FilterationBottomShitFrag();
                    bottomSheetDialog.newInstance(regionFilterList, Common.FILTER_TYPE_REGION, selectedRegionId, false);
                    bottomSheetDialog.show(getSupportFragmentManager(), Common.FILTER_TYPE_BOTTOM_SHEET);
                }

            }
        });
        categorySelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryFilterList != null) {
                    FilterationBottomShitFrag bottomSheetDialog = new FilterationBottomShitFrag();
                    bottomSheetDialog.newInstance(categoryFilterList, Common.FILTER_TYPE_CATEGORY, selectedCategoryId, false);
                    bottomSheetDialog.show(getSupportFragmentManager(), Common.FILTER_TYPE_BOTTOM_SHEET);
                }

            }
        });
        locationSortingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLocation) {
                    event_list = new ArrayList<>();
                    locationCheckedImageView.setVisibility(View.GONE);
                    locationSortingImageView.setVisibility(View.GONE);
                    locationProBar.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(selectedRegionId) && !selectedRegionId.equals("0")) {
                        regionListTextView.setText(selectedRegionName);
                    } else {
                        regionListTextView.setText(regionFilterList.get(0).getTermName());
                    }
                    regionSelectionLayout.setEnabled(true);
                    currentLocation = false;
                    getTotalList(pageNo, count);
                    locationSortingImageView.setImageTintList(null);
                    regionSelectionImageView.setVisibility(View.VISIBLE);
                } else {
                    locationSortingImageView.setVisibility(View.GONE);
                    locationProBar.setVisibility(View.VISIBLE);
                    if (checkLocationPermission()) {
                        event_list = new ArrayList<>();
                        currentLocation = true;
                        getTotalListByLocation(pageNo, count);
                        regionSelectionLayout.setEnabled(false);
                        regionListTextView.setText("Near by");
                        regionSelectionImageView.setVisibility(View.GONE);
                    } else {
                        locationSortingImageView.setVisibility(View.VISIBLE);
                        locationProBar.setVisibility(View.GONE);
                    }

                }
            }
        });
        globalSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = "55.056595", lng = "8.824121";
                if (checkLocationPermission()) {
                    lat = String.valueOf(new GPSTracker(EventActivity.this).getLatitude());
                    lng = String.valueOf(new GPSTracker(EventActivity.this).getLongitude());
                }
                GlobalSearchDialog searchDialog = new GlobalSearchDialog(EventActivity.this, categoryFilterList, regionFilterList, Common.SEARCH_TYPE_EVENT, lat, lng);
                searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                searchDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                searchDialog.setCanceledOnTouchOutside(true);
                searchDialog.show();
            }
        });
    }

    private void getTotalListByLocation(int pageNo, int count) {
        String regionId = selectedRegionId;
        String lat = "55.056595", lng = "8.824121";
        if (currentLocation) {
            regionId = "";
            lat = String.valueOf(new GPSTracker(EventActivity.this).getLatitude());
            lng = String.valueOf(new GPSTracker(EventActivity.this).getLongitude());
        }

        String url = Common.BASE_URL + "event";
        AndroidNetworking.post(url)
                .addBodyParameter("pageNo", String.valueOf(pageNo))
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .addBodyParameter("regionId", regionId)
                .addBodyParameter("categoryId", selectedCategoryId)
                .addBodyParameter("latitude", lat)
                .addBodyParameter("longitude", lng)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        locationSortingImageView.setVisibility(View.VISIBLE);
                        locationProBar.setVisibility(View.GONE);
                        locationCheckedImageView.setVisibility(View.VISIBLE);
                        parseResponseforListing(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });


        if (categoryFilterList.size() == 0 && regionFilterList.size() == 0)
            getFilterList();
    }

    private void getFilterList() {
        CategoryOrRegionFilterListModel categoryOrRegionFilterListModel = new CategoryOrRegionFilterListModel();
        categoryOrRegionFilterListModel.setTermId("0");
        categoryOrRegionFilterListModel.setTermName("All category");
        CategoryOrRegionFilterListModel categoryOrRegionFilterListModel1 = new CategoryOrRegionFilterListModel();
        categoryOrRegionFilterListModel1.setTermId("0");
        categoryOrRegionFilterListModel1.setTermName("All region");
        categoryFilterList.add(categoryOrRegionFilterListModel);
        regionFilterList.add(categoryOrRegionFilterListModel1);
        String urlCategoryFilter = Common.BASE_URL + "category-list";
        String urlRegionFilter = Common.BASE_URL + "region-list";
        AndroidNetworking.get(urlCategoryFilter)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseForFilterList(response, categoryFilterList);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
        AndroidNetworking.get(urlRegionFilter)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseForFilterList(response, regionFilterList);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponseForFilterList(JSONObject response, ArrayList<CategoryOrRegionFilterListModel> filterListModels) {
        try {
            if (response.getString("status").equals("Success")) {
//                JSONObject data = response.getJSONObject("Data");
//                JSONObject data = response.getJSONObject()
//                JSONObject settings = data.getJSONObject("oSettings");
//                response.getJSONArray()
                JSONArray results = response.getJSONArray("Data");
                for (int i = 0; i < results.length(); i++) {
                    CategoryOrRegionFilterListModel categoryOrRegionFilterListModel = new CategoryOrRegionFilterListModel();
                    JSONObject jsonObject = results.getJSONObject(i);

                    if (jsonObject.has("termId")) {
                        categoryOrRegionFilterListModel.setTermId(jsonObject.getString("termId"));
                    }
                    if (jsonObject.has("termName")) {
                        categoryOrRegionFilterListModel.setTermName(jsonObject.getString("termName"));
                    }

                    filterListModels.add(categoryOrRegionFilterListModel);
                }
//                Log.i("indranil", String.valueOf(categoryFilterList.size()));
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
                Log.i("indranil", failureMsg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkLocationPermission() {


        if (ContextCompat.checkSelfPermission(EventActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(EventActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("required to fetch your current location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(EventActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(EventActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    public void home(View view) {
        if (getIntent().hasExtra(Common.FROM_HOME_ACTIVITY)) {
            if (getIntent().getStringExtra("from_home").equals(Common.FROM_HOME_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), HomeActivity.class);
                nextPage.putExtra(Common.FROM_EVENT_ACTIVITY, event_activity);
                startActivity(nextPage);
            }
        }else{
            nextPage = new Intent(getApplicationContext(), HomeActivity.class);
            nextPage.putExtra(Common.FROM_EVENT_ACTIVITY, event_activity);
            startActivity(nextPage);
        }
    }

    public void listing(View view) {
        if (getIntent().hasExtra(Common.FROM_LISTING_ACTIVITY)) {
            if (getIntent().getStringExtra("from_listing").equals(Common.FROM_LISTING_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), ListingActivity.class);
                nextPage.putExtra(Common.FROM_EVENT_ACTIVITY, event_activity);
                startActivity(nextPage);
            }
        }else {
            nextPage = new Intent(getApplicationContext(), ListingActivity.class);
            nextPage.putExtra(Common.FROM_EVENT_ACTIVITY, event_activity);
            startActivity(nextPage);
        }
    }

    public void profileAccount(View view) {
        if (getIntent().hasExtra(Common.FROM_USER_ACTIVITY)) {
            if (getIntent().getStringExtra("from_user").equals(Common.FROM_USER_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), UserAccount.class);
                nextPage.putExtra(Common.FROM_EVENT_ACTIVITY, event_activity);
                startActivity(nextPage);
            }
        }else{
            nextPage = new Intent(getApplicationContext(), UserAccount.class);
            nextPage.putExtra(Common.FROM_EVENT_ACTIVITY, event_activity);
            startActivity(nextPage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void events(View view) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(EventActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    @Subscribe
    public void updateListBasedOnFilterChoice(FilterChoiceEventBusModel filterChoiceEventBusModel) {
        String filterType = filterChoiceEventBusModel.getFilterType();
        switch (filterType) {
            case Common.FILTER_TYPE_CATEGORY:
                if (filterChoiceEventBusModel.getName().equals("All category")) {
                    selectedCategoryId = "";
                } else {
                    selectedCategoryId = filterChoiceEventBusModel.getId();
                }
                categoryListTextView.setText(filterChoiceEventBusModel.getName());
                break;
            case Common.FILTER_TYPE_REGION:
                if (filterChoiceEventBusModel.getName().equals("All region")) {
                    selectedRegionId = "";
                } else {
                    selectedRegionId = filterChoiceEventBusModel.getId();
                    selectedRegionName = filterChoiceEventBusModel.getName();
                }
                regionListTextView.setText(filterChoiceEventBusModel.getName());
                break;
        }
        if (currentLocation) {
            event_list = new ArrayList<>();
            getTotalListByLocation(pageNo, count);
        } else {
            event_list = new ArrayList<>();
            getTotalList(pageNo, count);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
