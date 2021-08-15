package com.romo.tonder.visits.activities;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.ListingApater;
import com.romo.tonder.visits.dialogs.GlobalSearchDialog;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.ListingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Response;

public class CategoryActivity extends BaseActivity {
    private static final String TAG = ListingActivity.class.getSimpleName();
    private Context context;
    private SharedPreferences appPrefs;
    private Dialog dialog;
    private int pageno=1;
    private ArrayList<ListingModel> catwiseList;
    private RecyclerView listingRecycler;
    private ListingApater adapter;
    private AppCompatTextView categoryHeading;
    private ProgressBar proBar;
    private NestedScrollView bodyArea;

    //searching
    private ArrayList<CategoryOrRegionFilterListModel> categoryFilterList;
    private ArrayList<CategoryOrRegionFilterListModel> regionFilterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        context = this;
        if (getIntent().hasExtra("cat_id")){
            if (!TextUtils.isEmpty(getIntent().getStringExtra("cat_id"))){
                bindActivity();
                String cat_id=getIntent().getStringExtra("cat_id");
                getCatList(cat_id,pageno,appPrefs.getString("preferredLanguage", ""));
                populateData();

                bodyArea.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            pageno++;
                            proBar.setVisibility(View.VISIBLE);
                            getCatList(cat_id,pageno,appPrefs.getString("preferredLanguage", ""));

                        }
                    }
                });
            }
        }
    }

    private void populateData() {
        if (getIntent().hasExtra("cat_name")){
            if (!getIntent().getStringExtra("cat_name").equals(""))
                categoryHeading.setText(getIntent().getStringExtra("cat_name"));
            else
                categoryHeading.setText("");
        }

    }

    private void getCatList(String cat_id, int pageno, String preferredLanguage) {
        //dialog.show();
        String url = Common.BASE_URL + "listing";
        AndroidNetworking.post(url)
                .addBodyParameter("pageNo", String.valueOf(pageno))
                .addBodyParameter("language", preferredLanguage)
                .addBodyParameter("categoryId", cat_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        proBar.setVisibility(View.GONE);
                        parseResponse(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponse(JSONObject response) {
        //dialog.dismiss();
        try {
            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
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
                    if (jsonObject.has("logo")) {
                        bean.setListLogo(jsonObject.getString("logo"));
                    }
                    JSONObject oaddress = jsonObject.getJSONObject("oAddress");
                    if (oaddress.has("address")) {
                        bean.setListAdd(oaddress.getString("address"));
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
                    if ((jsonObject.getJSONArray("hourDetails") != null)) {
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
                            if (jobject.getString("firstOpenHour") != null && jobject.getString("firstCloseHour") != null) {
                                firstOpenHour = jobject.getString("firstOpenHour");
                                firstCloseHour = jobject.getString("firstCloseHour");

                            }
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
                    bean.setViewType(Common.CATEGORY_LISTING);
                    catwiseList.add(bean);
                    if (catwiseList.size()>0){
                        adapter.notifyDataSetChanged();
                    }

                }
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        catwiseList = new ArrayList<>();
        listingRecycler = findViewById(R.id.listing_area);
        listingRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ListingApater(catwiseList, false, CategoryActivity.this);
        listingRecycler.setAdapter(adapter);
        categoryHeading=(AppCompatTextView)findViewById(R.id.cat_heading);
        proBar = findViewById(R.id.progressBar);
        bodyArea=findViewById(R.id.body_area);
        categoryFilterList = new ArrayList<>();
        regionFilterList = new ArrayList<>();

    }

    public void home(View view) {
    }

    public void events(View view) {
        Intent intent =new Intent(this, EventActivity.class);
        startActivity(intent);
        finish();
    }

    public void profileAccount(View view) {
        Intent intent =new Intent(this, UserAccount.class);
        startActivity(intent);
        finish();
    }

    public void backToHome(View view) {
        finish();
    }

    public void listingArea(View view) {
        Intent intent =new Intent(this, ListingActivity.class);
        startActivity(intent);
        finish();
    }
    /*global search*/
    public void gotoSearchDialog(View view) {
        String lat = "55.056595", lng = "8.824121";
        getFilterList();
        if (categoryFilterList.size()>0 || regionFilterList.size()>0){
            GlobalSearchDialog searchDialog = new GlobalSearchDialog(CategoryActivity.this, categoryFilterList, regionFilterList, Common.SEARCH_TYPE_LISTING, lat, lng);
            searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            searchDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            searchDialog.setCanceledOnTouchOutside(true);
            searchDialog.show();
        }

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
}