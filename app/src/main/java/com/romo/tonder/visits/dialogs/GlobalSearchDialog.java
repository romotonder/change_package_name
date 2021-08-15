package com.romo.tonder.visits.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.fragments.FilterationBottomShitFrag;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.EventModel;
import com.romo.tonder.visits.models.FilterChoiceGlobalSearchEventBusModel;
import com.romo.tonder.visits.models.ListingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GlobalSearchDialog extends Dialog {

    public static final String TAG = "GlobalSearchDialog";
    private Context context;
    private SharedPreferences appPrefs;
    private Activity activity;
    private ImageView goBackImageView;
    private SeekBar distanceSeekBar;
    private TextView activeDistancetextView, categoryTextView, regionTextView, searchTypeTextView;
    private EditText searchTitleEditText;
    private LinearLayout searchByCategoryLayout, searchByregionLayout, searchTitleLayout, searchNowLayout, searchTypeLayout;
    private ProgressBar proBar;

    private ArrayList<CategoryOrRegionFilterListModel> categoryFilterList;
    private ArrayList<CategoryOrRegionFilterListModel> regionFilterList;
    private ArrayList<ListingModel> listingListResults;
    private ArrayList<EventModel> eventListResults;
    private String selectedCategoryId = "";
    private String selectedRegionId = "";
    private String globalSearchType;
    private String lat = "55.056595", lag = "8.824121", km = "1000";
    private int pageNo = 1;
    private int count = 10;

    public GlobalSearchDialog(@NonNull Context context, ArrayList<CategoryOrRegionFilterListModel> categoryFilterList,
                              ArrayList<CategoryOrRegionFilterListModel> regionFilterList, String globalSearchType, String lat, String lag) {
        super(context, R.style.DialogFragmentTheme);
        this.context = context;
        this.categoryFilterList = categoryFilterList;
        this.regionFilterList = regionFilterList;
        this.globalSearchType = globalSearchType;
        this.lat = lat;
        this.lag = lag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_search_dialog);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        bindView();

        addListeners();
    }

    private void addListeners() {
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                activeDistancetextView.setText("within " + progress + " km");
                km = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        searchByregionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterationBottomShitFrag bottomSheetDialog = new FilterationBottomShitFrag();
                bottomSheetDialog.newInstance(regionFilterList, Common.FILTER_TYPE_REGION, selectedRegionId, true);
                bottomSheetDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), Common.FILTER_TYPE_BOTTOM_SHEET);
                ;
            }
        });

        searchByCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterationBottomShitFrag bottomSheetDialog = new FilterationBottomShitFrag();
                bottomSheetDialog.newInstance(categoryFilterList, Common.FILTER_TYPE_CATEGORY, selectedCategoryId, true);
                bottomSheetDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), Common.FILTER_TYPE_BOTTOM_SHEET);
            }
        });

        searchTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<CategoryOrRegionFilterListModel> searchTypes = new ArrayList<>();
                CategoryOrRegionFilterListModel searchTypeListing = new CategoryOrRegionFilterListModel();
                CategoryOrRegionFilterListModel searchTypeEvent = new CategoryOrRegionFilterListModel();
                searchTypeListing.setTermId(Common.SEARCH_TYPE_LISTING);
                searchTypeListing.setTermName("Listing");
                searchTypeEvent.setTermId(Common.SEARCH_TYPE_EVENT);
                searchTypeEvent.setTermName("Event");
                searchTypes.add(searchTypeListing);
                searchTypes.add(searchTypeEvent);

                FilterationBottomShitFrag bottomSheetDialog = new FilterationBottomShitFrag();
                bottomSheetDialog.newInstance(searchTypes, Common.GLOBAL_SEARCH_TYPE, globalSearchType, true);
                bottomSheetDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), Common.FILTER_TYPE_BOTTOM_SHEET);
            }
        });

        searchNowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchTitleEditText.getText().toString();
                //if (!TextUtils.isEmpty(search)) {
                    proBar.setVisibility(View.VISIBLE);
                    if (globalSearchType.equals(Common.SEARCH_TYPE_LISTING))
                        getTotalListings(pageNo, count, search);
                    else
                        getTotalEvents(pageNo, count, search);
//                } else
//                    Toast.makeText(context, "please enter title to search", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalListings(int pageNo, int count, String search) {

        listingListResults = new ArrayList<>();
        String url = Common.BASE_URL + "listing-search";
        AndroidNetworking.post(url)

                .addBodyParameter("pageNo", String.valueOf(pageNo))
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .addBodyParameter("search", search)
                .addBodyParameter("category", selectedCategoryId)
                .addBodyParameter("region", selectedRegionId)
                .addBodyParameter("lat", lat)
                .addBodyParameter("lag", lag)
                .addBodyParameter("km", km)

                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        loadingStatus = loadingStatus + 1;
//                        if (loadingStatus == 4)
                        proBar.setVisibility(View.GONE);
//                        locationSortingImageView.setVisibility(View.VISIBLE);
//                        locationProBar.setVisibility(View.GONE);
                        parseResponseforListing(response);
                    }

                    @Override
                    public void onError(ANError anError) {
//                        dialog.dismiss();
                        proBar.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });


//        if (categoryFilterList.size() == 0 && regionFilterList.size() == 0)
//            getFilterList();
    }

    private void getTotalEvents(int pageNo, int count, String search) {

        listingListResults = new ArrayList<>();
        String url = Common.BASE_URL + "event-search";
        AndroidNetworking.post(url)
                .addBodyParameter("pageNo", String.valueOf(pageNo))
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .addBodyParameter("search", search)
                .addBodyParameter("category", selectedCategoryId)
                .addBodyParameter("region", selectedRegionId)
                .addBodyParameter("lat", lat)
                .addBodyParameter("lag", lag)
                .addBodyParameter("km", km)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {

                        parseResponseforEvents(response);
                        proBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        proBar.setVisibility(View.GONE);
                        //dialog.dismiss();
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
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
                            float no = Float.parseFloat(distanceInNumberAsString);
                            bean.setDistanceInNumber(no);
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
                    if (!jsonObject.isNull("hourDetails")) {
                        JSONArray jbusinesshr = null;
                        JSONObject jobject = null;
                        String hour_button_color = "";

                    }
                    bean.setViewType(Common.LISTING_PAGE);
                    listingListResults.add(bean);
                }
//                if (currentLocation)
//                Collections.sort(listingListResults);
                displaySearchResults();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(context, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseResponseforEvents(JSONObject response) {
        try {

            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
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
                    bean.setViewType(1);
                    eventListResults.add(bean);
                }
                displaySearchResults();
            } else {

                String failureMsg = response.getString("msg");
                Toast.makeText(context, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindView() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        goBackImageView = findViewById(R.id.go_back_image_view);
        distanceSeekBar = findViewById(R.id.distance_seek_bar);
        activeDistancetextView = findViewById(R.id.active_distance_text_view);
        categoryTextView = findViewById(R.id.category_text_view);
        regionTextView = findViewById(R.id.region_text_view);
        searchByCategoryLayout = findViewById(R.id.category_layout);
        searchByregionLayout = findViewById(R.id.region_layout);
        searchTitleLayout = findViewById(R.id.search_title_layout);
        searchNowLayout = findViewById(R.id.search_now_layout);
        searchTitleEditText = findViewById(R.id.search_title_edit_text);
        searchTypeLayout = findViewById(R.id.search_type_layout);
        searchTypeTextView = findViewById(R.id.search_type_text_view);
        proBar = findViewById(R.id.progressBar);
        listingListResults = new ArrayList<>();
        eventListResults = new ArrayList<>();
        activity = (Activity) context;
        distanceSeekBar.setMax(50);
        distanceSeekBar.setProgress(25);
        if (globalSearchType.equals(Common.SEARCH_TYPE_LISTING)) {
            searchTypeTextView.setText("Listing");
        } else searchTypeTextView.setText("Events");
    }

    private void displaySearchResults() {
        SearchResultDialog searchResultDialog = new SearchResultDialog(context, listingListResults, eventListResults, globalSearchType);
        searchResultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchResultDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        searchResultDialog.setCanceledOnTouchOutside(true);
        searchResultDialog.show();
    }

    @Subscribe
    public void updateCategoryOrPlaceChoice(FilterChoiceGlobalSearchEventBusModel filterChoiceGlobalSearchEventBusModel) {
        switch (filterChoiceGlobalSearchEventBusModel.getFilterType()) {
            case Common.FILTER_TYPE_CATEGORY:
                if (filterChoiceGlobalSearchEventBusModel.getName().equals("All category")) {
                    selectedCategoryId = "";
                } else {
                    selectedCategoryId = filterChoiceGlobalSearchEventBusModel.getId();
                }
                selectedCategoryId = filterChoiceGlobalSearchEventBusModel.getId();
                categoryTextView.setText(filterChoiceGlobalSearchEventBusModel.getName());
                break;

            case Common.FILTER_TYPE_REGION:
                if (filterChoiceGlobalSearchEventBusModel.getName().equals("All region")) {
                    selectedRegionId = "";
                } else {
                    selectedRegionId = filterChoiceGlobalSearchEventBusModel.getId();
                }
                selectedRegionId = filterChoiceGlobalSearchEventBusModel.getId();
                regionTextView.setText(filterChoiceGlobalSearchEventBusModel.getName());
                break;

            case Common.GLOBAL_SEARCH_TYPE:
                globalSearchType = filterChoiceGlobalSearchEventBusModel.getId();
                if (globalSearchType.equals(Common.SEARCH_TYPE_LISTING))
                    searchTypeTextView.setText("Listing");
                else
                    searchTypeTextView.setText("Events");
                break;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
