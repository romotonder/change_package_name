package com.romo.tonder.visits.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.ListingApater;
import com.romo.tonder.visits.dialogs.GlobalSearchDialog;
import com.romo.tonder.visits.fragments.FilterationBottomShitFrag;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.GPSTracker;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.FilterChoiceEventBusModel;
import com.romo.tonder.visits.models.ListingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

public class ListingActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = ListingActivity.class.getSimpleName();
    private static final String listing_activity = "from_listing";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private SharedPreferences appPrefs;
    private NestedScrollView bodyArea;

    private RecyclerView listingRecycler;
    private RecyclerView horizontalRecycler;
    private RelativeLayout mapLayout;
    private FloatingActionButton switchViewActionButton;
    private ArrayList<ListingModel> listingList;

    private ArrayList<CategoryOrRegionFilterListModel> categoryFilterList;
    private ArrayList<CategoryOrRegionFilterListModel> regionFilterList;
    private LinearLayout regionSelectionLayout;
    private LinearLayout categorySelectionLayout;
    private TextView regionListTextView, categoryListTextView;
    private ImageView locationSortingImageView;
    private ImageView regionSelectionImageView;
    private ImageView locationCheckedImageView;
    private ImageView globalSearchIcon;

    private ListingApater adapter;
    private static final int NO_INTERNET = -1;
    private static final int NO_DATA = 0;
    private static final int LISTING = 1;
    private double lat = 0, lng = 0;
//    double lat = 55.0584, lng = 8.8265;
    private boolean mapViewState = false;
    private Bitmap mapMarkerIconBitmap = null;
    private GoogleMap map;
    private int loadingStatus = 1;
    private String selectedCategoryId = "";
    private String selectedRegionId = "";
    private String selectedRegionName = "";
    private boolean currentLocation = false;
    private int horizontalChildPosition = 0;


    private Dialog dialog;
    private Context context;
    private int pageNo = 1;
    private int count = 10;
    private ProgressBar proBar;
    private ProgressBar locationProBar;
    private Intent nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing2);
        EventBus.getDefault().register(this);
        context = this;

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

        String url = Common.BASE_URL + "listing";
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
//                        loadingStatus = loadingStatus + 1;
//                        if (loadingStatus == 4)
                        proBar.setVisibility(View.GONE);
                        locationSortingImageView.setVisibility(View.VISIBLE);
                        locationProBar.setVisibility(View.GONE);
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

    private void getTotalListByLocation(int pageNo, int count) {
        String regionId = selectedRegionId;
//        String lat = "", lng = "";

        String lat = "55.0584", lng = "8.8265";
//        String lat = "55.05706820519615", lng = "8.824555305004235";
        if (currentLocation) {
            regionId = "";
            lat = String.valueOf(new GPSTracker(ListingActivity.this).getLatitude());
            lng = String.valueOf(new GPSTracker(ListingActivity.this).getLongitude());
        }
//        String url = Common.BASE_URL + "listing";
        String url = Common.BASE_URL_NEARBY;
        AndroidNetworking.post(url)
                .addBodyParameter("pageNo", String.valueOf(pageNo))
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .addBodyParameter("type", "listing")
                .addBodyParameter("region", regionId)
                .addBodyParameter("category", selectedCategoryId)
                .addBodyParameter("lat", lat)
                .addBodyParameter("lag", lng)

//                .addBodyParameter("regionId", regionId)
//                .addBodyParameter("categoryId", selectedCategoryId)
//                .addBodyParameter("latitude", lat)
//                .addBodyParameter("longitude", lng)


                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
//                        loadingStatus = loadingStatus + 1;
//                        if (loadingStatus == 4)
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
                        loadingStatus = loadingStatus + 1;
//                        if (loadingStatus == 4)
//                        proBar.setVisibility(View.GONE);
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
                        loadingStatus = loadingStatus + 1;
//                        if (loadingStatus == 4)
//                        proBar.setVisibility(View.GONE);
                        parseResponseForFilterList(response, regionFilterList);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponseforListing(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
//                JSONObject settings = data.getJSONObject("oSettings");
                JSONArray results = data.getJSONArray("oResults");
                for (int i = 0; i < results.length(); i++) {
                    ListingModel bean = new ListingModel();
                    JSONObject jsonObject = results.getJSONObject(i);
                    bean.setListId(jsonObject.getString("ID"));
                    if (jsonObject.has("postTitle")) {
                        bean.setListPostTitle(jsonObject.getString("postTitle"));
                    }
                    if (jsonObject.has("tagLine")) {
                        bean.setListTagLine(jsonObject.getString("tagLine"));
                    }
                    if (jsonObject.has("coverImg")) {
                        bean.setListCoverImg(jsonObject.getString("coverImg"));
                    }
                    if (jsonObject.has("distance")) {
                        bean.setListDistance(jsonObject.getString("distance"));
                        String distance = jsonObject.getString("distance");
                        if (!TextUtils.isEmpty(distance)) {
                            String distanceInNumberAsString = distance.substring(0, distance.length() - 2);
                            distanceInNumberAsString = distanceInNumberAsString.replace(",", "");
                            float no = Float.parseFloat(distanceInNumberAsString);
                            bean.setDistanceInNumber(no);
                        } else {
                            if (currentLocation)
                                continue;
                        }
                    }
                    if (jsonObject.has("logo")) {
                        bean.setListLogo(jsonObject.getString("logo"));
                    }
                    JSONObject oaddress = jsonObject.getJSONObject("oAddress");
                    if (oaddress.has("address")) {
                        bean.setListAdd(oaddress.getString("address"));
                    }
                    if (oaddress.has("lat")) {
                        bean.setListLat(oaddress.getString("lat"));
                    }
                    if (oaddress.has("lag")) {
                        bean.setListLong(oaddress.getString("lag"));
                    }
                    if (oaddress.has("googleMapUrl")) {
                        bean.setGoogleMapUrl(oaddress.getString("googleMapUrl"));
                    }
                    if (jsonObject.has("hourMode")) {
                        bean.setListHourMode(jsonObject.getString("hourMode"));
                    }
                    if (jsonObject.has("publishDate")) {
                        bean.setListPublistDate(jsonObject.getString("publishDate"));
                    }
                    if (jsonObject.has("currentDate")) {
                        String currentDate = jsonObject.getString("currentDate");
                    }
                    if (jsonObject.has("peopleInterested")) {
                        bean.setListPeopleInterested(jsonObject.getString("peopleInterested"));
                    }
                    if (jsonObject.has("byUser")) {
                        bean.setByUser(jsonObject.getString("byUser"));
                    }
                    if (!jsonObject.isNull("userReview")) {
                        JSONObject oUserReview = jsonObject.getJSONObject("userReview");
                        String mode = oUserReview.getString("mode");
                        String average = oUserReview.getString("average");
                        String totalScore = oUserReview.getString("totalScore");
                        bean.setListMode(mode);
                        bean.setListAverage(average);
                        bean.setListTotleScore(totalScore);
                    }
                    if (jsonObject.has("hourDetails") && (jsonObject.getJSONArray("hourDetails") != null)) {
                        JSONArray jbusinesshr = null;
                        JSONObject jobject = null;
                        String hour_button_color = "";
                        jbusinesshr = jsonObject.getJSONArray("hourDetails");
                        String firstOpenHour = "", firstCloseHour = "", secondOpenHour = "", secondCloseHour = "";
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        boolean isOpenClosed = false;
                        for (int index = 0; index < jbusinesshr.length(); index++) {
                            jobject = jbusinesshr.getJSONObject(index);
                            String day_id = jobject.getString("ID");
                            String objectID = jobject.getString("objectID");
                            String dayOfWeek = jobject.getString("dayOfWeek");
                            String isOpen = jobject.getString("isOpen");
                            if (jobject.getString("firstOpenHour") != null
                                    && jobject.getString("firstCloseHour") != null) {
                                firstOpenHour = jobject.getString("firstOpenHour");
                                firstCloseHour = jobject.getString("firstCloseHour");

                            }
//                            if (jobject.getString("secondOpenHour") != null
//                                    && !jobject.getString("secondOpenHour").equals("")) {
//                                secondOpenHour = jobject.getString("secondOpenHour");
//                            }
//                            if (jobject.getString("secondCloseHour") != null && !jobject.getString("secondCloseHour").equals("")) {
//                                secondCloseHour = jobject.getString("secondCloseHour");
//                            }
//                            String firstCloseHourUTC = jobject.getString("firstCloseHourUTC");
//                            String secondOpenHourUTC = jobject.getString("secondOpenHourUTC");
//                            String secondCloseHourUTC = jobject.getString("secondCloseHourUTC");
                            String today = "";
                            today = Common.dayOfWeek(day);
                            if (!dayOfWeek.equals("")) {
                                if (dayOfWeek.equalsIgnoreCase(today)) {
                                    if (!firstOpenHour.equals("") && !firstCloseHour.equals("")) {
                                        isOpenClosed = Common.compareTime(firstOpenHour, firstCloseHour);
                                        Log.d(TAG, "isOpen: " + Common.isNowOpen + today);
                                    }
                                }
                            }
                        }
                        String isOpen = "";
                        if (!jsonObject.getString("hourMode").equals("")) {
                            if (jsonObject.getString("hourMode").equals("open_for_selected_hours")) {
                                if (isOpenClosed) {
                                    isOpen = "open";
                                    //isOpen = getResources().getString(R.string.open);
                                } else {
                                    isOpen = "closed";
                                    //isOpen = getResources().getString(R.string.closed);
                                }
                            } else if (jsonObject.getString("hourMode").equals("always_open")) {
                                isOpen = "open_always";
                            } else {
                                isOpen = "closed";
                            }
                            bean.setIsOpen(isOpen);
                        }
                    }
                    bean.setViewType(Common.LISTING_PAGE);
                    listingList.add(bean);

                }
//                if (currentLocation)
//                    Collections.sort(listingList);

                adapter = new ListingApater(listingList, false, this);
                listingRecycler.setAdapter(adapter);

                ListingApater newAdapter = new ListingApater(listingList, true, this);
                horizontalRecycler.setAdapter(newAdapter);
                horizontalRecycler.scrollToPosition(horizontalChildPosition);

                displayInMapFrag();
            } else {
                showNoData();
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Log.i("indranil", String.valueOf(categoryFilterList.size()));
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
                Log.i("indranil", failureMsg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNoData() {
        listingList.clear();
        ListingModel bean = new ListingModel();
        bean.setViewType(NO_DATA);
        listingList.add(bean);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        listingList = new ArrayList<>();
        listingRecycler = findViewById(R.id.listing_area);
        listingRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ListingApater(listingList, false, ListingActivity.this);
        listingRecycler.setAdapter(adapter);
        categoryFilterList = new ArrayList<>();
        regionFilterList = new ArrayList<>();
        bodyArea = findViewById(R.id.body_area);
        proBar = findViewById(R.id.progressBar);
        locationProBar = findViewById(R.id.location_progress_bar);

        globalSearchIcon = findViewById(R.id.global_search_icon);
        locationCheckedImageView = findViewById(R.id.location_checked_image_view);
        regionSelectionImageView = findViewById(R.id.region_selection_image_view);
        locationSortingImageView = findViewById(R.id.location_sorting_image_view);
        regionSelectionLayout = findViewById(R.id.region_selection_layout);
        categorySelectionLayout = findViewById(R.id.category_selection_layout);
        regionListTextView = findViewById(R.id.region_list_text_view);
        categoryListTextView = findViewById(R.id.category_list_text_view);
        switchViewActionButton = findViewById(R.id.switch_view_action_button);
        mapLayout = findViewById(R.id.map_layout);
        horizontalRecycler = findViewById(R.id.horizontal_recycler);
        horizontalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalRecycler.setAdapter(adapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(horizontalRecycler);
        horizontalRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int position = getCurrentItem();
                    horizontalChildPosition = position;
                    if (position != -1)
                        updateMapCameraByPosition(position);
//                    Log.i("indranil", String.valueOf(listingList.size()));
                    if (position + 1 == listingList.size()) {
                        pageNo++;
                        proBar.setVisibility(View.VISIBLE);
                        if (currentLocation) {
                            getTotalListByLocation(pageNo, count);
                        } else {
                            getTotalList(pageNo, count);
                        }
                    }
                }
            }
        });

        mapLayout.setAlpha(0);

        switchViewActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapViewState) {
                    adapter = new ListingApater(listingList, false, ListingActivity.this);
                    mapViewState = false;
                    mapLayout.setVisibility(View.GONE);
                    mapLayout.setAlpha(0);
                    listingRecycler.setAdapter(adapter);
                    bodyArea.setVisibility(View.VISIBLE);
                } else {
                    mapLayout.setVisibility(View.VISIBLE);
                    mapLayout.setAlpha(1);
                    ListingApater newAdapter = new ListingApater(listingList, true, ListingActivity.this);
                    mapViewState = true;
                    horizontalRecycler.setAdapter(newAdapter);
                    positionMapCamera(0);
                    bodyArea.setVisibility(View.GONE);
                }
            }
        });

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
                    listingList = new ArrayList<>();
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
                        listingList = new ArrayList<>();
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
                    lat = String.valueOf(new GPSTracker(ListingActivity.this).getLatitude());
                    lng = String.valueOf(new GPSTracker(ListingActivity.this).getLongitude());
                }
                GlobalSearchDialog searchDialog = new GlobalSearchDialog(ListingActivity.this,
                        categoryFilterList, regionFilterList, Common.SEARCH_TYPE_LISTING, lat, lng);
                searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                searchDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                searchDialog.setCanceledOnTouchOutside(true);
                searchDialog.show();
            }
        });
    }


    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("required to fetch your current location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ListingActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void updateMapCameraByPosition(int position) {
        try {
            ListingModel model = listingList.get(position);
            if (model != null) {
                map.clear();
                resetMapMarkerWithIcon(position);
                positionMapCamera(position);
            }
        } catch (Exception e) {
            Log.i("indranil", String.valueOf(e));
        }
    }

    private int getCurrentItem() {
        return ((LinearLayoutManager) horizontalRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    public void home(View view) {
        if (getIntent().hasExtra(Common.FROM_HOME_ACTIVITY)) {
            if (getIntent().getStringExtra("from_home").equals(Common.FROM_HOME_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), HomeActivity.class);
                nextPage.putExtra(Common.FROM_LISTING_ACTIVITY, listing_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), HomeActivity.class);
            nextPage.putExtra(Common.FROM_LISTING_ACTIVITY, listing_activity);
            startActivity(nextPage);
        }
    }

    public void events(View view) {
        if (getIntent().hasExtra(Common.FROM_EVENT_ACTIVITY)) {
            if (getIntent().getStringExtra("from_event").equals(Common.FROM_EVENT_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), EventActivity.class);
                nextPage.putExtra(Common.FROM_LISTING_ACTIVITY, listing_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), EventActivity.class);
            nextPage.putExtra(Common.FROM_LISTING_ACTIVITY, listing_activity);
            startActivity(nextPage);
        }
    }

    public void profileAccount(View view) {
        if (getIntent().hasExtra(Common.FROM_USER_ACTIVITY)) {
            if (getIntent().getStringExtra("from_user").equals(Common.FROM_USER_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), UserAccount.class);
                nextPage.putExtra(Common.FROM_LISTING_ACTIVITY, listing_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), UserAccount.class);
            nextPage.putExtra(Common.FROM_LISTING_ACTIVITY, listing_activity);
            startActivity(nextPage);
        }
    }

    private void getIconBitMap(ListingModel model, int position) {
        Glide.with(this)
                .asBitmap()
                .load(model.getListLogo())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mapMarkerIconBitmap = Bitmap.createScaledBitmap(resource, 100, 100, false);
                        Bitmap bitmap = null;
                        if (listingList.size() > 0) {
                            ListingModel activeModel = listingList.get(position);
                            try {
                                lat = Double.parseDouble(model.getListLat());
                                lng = Double.parseDouble(model.getListLong());
                            } catch (Exception e) {
                                lat = 0;
                                lng = 0;
                            }
                            if (mapMarkerIconBitmap != null) {
                                if (model.equals(activeModel)) {
                                    bitmap = createCustomMarker(ListingActivity.this, mapMarkerIconBitmap, true);
                                    addMarkersInMap(model.getListPostTitle(), bitmap, 10);
                                } else {
                                    bitmap = createCustomMarker(ListingActivity.this, mapMarkerIconBitmap, false);
                                    addMarkersInMap(model.getListPostTitle(), bitmap, 1);
                                }
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.i("indranil", "marker adding failed");
                    }
                });
    }

    private void displayInMapFrag() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            updateMapCameraByPosition(0);
            resetMapMarkerWithIcon(0);
        }
    }

    private void resetMapMarkerWithIcon(int position) {
        for (int a = 0; a < listingList.size(); a++) {
            getIconBitMap(listingList.get(a), position);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String title = marker.getTitle();
                int position = 0;
                for (int a = 0; a < listingList.size(); a++) {
                    String name = listingList.get(a).getListPostTitle();
                    if (title.equalsIgnoreCase(name)) {
                        position = a;
                        break;
                    }
                }
                horizontalRecycler.smoothScrollToPosition(position);
                return false;
            }
        });
    }

    private void positionMapCamera(int position) {
        if (map != null) {
            String lt = listingList.get(position).getListLat();
            String ln = listingList.get(position).getListLong();
            if (!TextUtils.isEmpty(lt) && !TextUtils.isEmpty(ln)) {
                Double latitude = Double.parseDouble(lt);
                Double longitude = Double.parseDouble(ln);
                LatLng pos = new LatLng(latitude - 0.0030, longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12.6f));
            }

        }
    }


    private void addMarkersInMap(String title, Bitmap markerIcon, float zIndex) {
        if (map != null) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .zIndex(zIndex)
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));

        }
    }

    private Bitmap createCustomMarker(Context context, Bitmap map, boolean enlargeIcon) {
        View marker;
        if (enlargeIcon) {
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout_enlarged, null);
        } else {
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        }
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageBitmap(map);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }

    public void listing(View view) {
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
            listingList = new ArrayList<>();
            getTotalListByLocation(pageNo, count);
        } else {
            listingList = new ArrayList<>();
            getTotalList(pageNo, count);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}