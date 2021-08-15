package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.ListingApater;
import com.romo.tonder.visits.dialogs.GlobalSearchDialog;
import com.romo.tonder.visits.helpers.Common;
//import com.romo.tonder.visit.helpers.GPSTracker;
import com.romo.tonder.visits.helpers.GPSTracker;
import com.romo.tonder.visits.helpers.LocaleManager;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.ListingModel;
import com.romo.tonder.visits.models.NotificationListings;
import com.romo.tonder.visits.models.Notifications;
import com.romo.tonder.visits.models.UserChatListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

import static com.romo.tonder.visits.R.color.Gray;
import static com.romo.tonder.visits.R.color.title_text_color;
import static com.romo.tonder.visits.R.color.white_main_theme;


public class HomeActivity extends BaseActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String home_activity = "from_home";
    private SharedPreferences appPrefs;
    private LinearLayout userProfileAccount;
    private String appsLanguage = "";
    private TextView notificationCount;
    private Intent nextPage = null;
    private ArrayList<String> keyStores;

    private Dialog dialog;
    private Context context;

    private LinearLayout banner_area, category_area, heading_area, heading_area_two,
            heading_area_three, heading_area_four, events_area, listing_area_two;
    private LayoutInflater inflater;

    private RecyclerView listingRecycler;
    private ArrayList<ListingModel> listingList;
    private ListingApater adapter;
    private static final int NO_INTERNET = -1;
    private static final int NO_DATA = 0;
    private static final int LISTING = 1;
    public static final String KEY_INPUT_TASK = "input_task";
    //searching
    private ArrayList<CategoryOrRegionFilterListModel> categoryFilterList;
    private ArrayList<CategoryOrRegionFilterListModel> regionFilterList;

    private DatabaseReference mReferenceUser;
    private AppCompatTextView noOfMessage;
    private boolean isNew = false;
    public static HomeActivity instance;
    String lat = "", lng = "";


    @Override
    protected void onStart() {
        super.onStart();
      /*  try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ForegroundService.class));
            } else {
                startService(new Intent(this, ForegroundService.class));
            }
        } catch (Exception e) {
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;
        bindActivity();

        //just for testing checking UserId
        String userID = ((MyApplication) context.getApplicationContext()).getAppPrefs().getString("userID", "");
        Log.i(TAG, String.valueOf(userID));

        if (!appPrefs.getString("preferredLanguage", "").equals("")) {
            selectLaguage(appPrefs.getString("preferredLanguage", ""));
        } else {
            selectLaguage(getResources().getString(R.string.danish_lang));
        }
        getHomeScreenId();
        noOfNewMessage(appPrefs.getString("userID", ""));

        // call  notification ------------
        callPushNotification();

    }

    private void noOfNewMessage(String userID) {
        ArrayList chatId = new ArrayList();
        DatabaseReference mReferenceUser = FirebaseDatabase.getInstance().getReference().child("messages").child("users");
        DatabaseReference userRef = mReferenceUser.getRef().child("___" + userID + "___");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chatId.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    UserChatListModel userChatList = shot.getValue(UserChatListModel.class);
                    assert userChatList != null;
                    //chatId.add(userChatList.getUserID());
                    if (userChatList.getUserID() != Integer.parseInt(userID)) {
                        if (userChatList.isNew())
                            chatId.add(userChatList.isNew());
                    }
                }
                if (chatId.size() > 0) ;
                {
                    isNew = true;
                    noOfMessage.setText(String.valueOf(chatId.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());

            }
        });
    }

    private void getHomeScreenId() {
        dialog.show();
        String url = Common.BASE_URL + "home-screen";
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseForId(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!HomeActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseForId(JSONObject response) {
        try {
            if (response.getString("status").equals("success")) {
                JSONArray category = response.getJSONArray("oData");
                for (int i = 0; i < 9; i++) {
                    JSONObject id = category.getJSONObject(i);
                   // System.out.print(id);
                    Iterator<?> keys = id.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        keyStores.add(key);
                        Log.e(TAG, "parseResponseForId: " + keyStores.size());
                        /*if ( id.get(key) instanceof JSONObject ) {
                            JSONObject xx = new JSONObject(id.get(key).toString());
                            Log.d("res1",xx.getString("22642"));
                            Log.d("res2",xx.getString("22644"));

                        }*/
                    }
                }
               /* String key0 = keyStores.get(0);
                String key1 = keyStores.get(1);
                String key2 = keyStores.get(2);
                String key3 = keyStores.get(3);
                String key4 = keyStores.get(4);
                String key5 = keyStores.get(5);
                String key6 = keyStores.get(6);
                String key7 = keyStores.get(7);
                String key8 = keyStores.get(8);*/
                if (keyStores.get(0) != null) {
                    getHomeBanner(keyStores.get(0));
                }
                if (keyStores.get(1) != null) {
                    getHeadingSection(keyStores.get(1));
                }
                if (keyStores.get(2) != null) {
                    getCategory(keyStores.get(2));
                }
                if (keyStores.get(3) != null) {
                    getHeadingSection(keyStores.get(3));
                }
                if (keyStores.get(4) != null) {
                    getListingSection(keyStores.get(4));
                }
                if (keyStores.get(5) != null) {
                    getHeadingSection(keyStores.get(5));
                }
                if (keyStores.get(6) != null) {
                    getEventSection(keyStores.get(6));
                }
                if (keyStores.get(7) != null) {
                    getHeadingSection(keyStores.get(7));
                }
                if (keyStores.get(8) != null) {
                    getListingSectiontwo(keyStores.get(8));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getListingSection(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
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
                        //dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforListing(JSONObject response) {
        try {
            JSONObject data = null;
            JSONObject oaddress = null;
            JSONObject settings = null;
            JSONArray results = null;
            JSONObject resultObject = null;

            if (response.getString("status").equals("Success")) {
                data = response.getJSONObject("oData");
                settings = data.getJSONObject("oSettings");
                if (!data.isNull("oResults")) {
                    results = data.getJSONArray("oResults");
                    for (int i = 0; i < results.length(); i++) {
                        ListingModel bean = new ListingModel();
                        resultObject = results.getJSONObject(i);
                        String id = resultObject.getString("ID");
                        String title = resultObject.getString("postTitle");
                        String tag = resultObject.getString("tagLine");
                        String coverImg = resultObject.getString("coverImg");
                        String logoImg = resultObject.getString("logo");

                        if (!resultObject.getString("ID").equals("")) {
                            bean.setListId(resultObject.getString("ID"));
                        }
                        if (!resultObject.getString("postTitle").equals("")) {
                            bean.setListPostTitle(resultObject.getString("postTitle"));
                        } else {
                            bean.setListPostTitle("");
                        }
                        if (!resultObject.getString("tagLine").equals("")) {
                            bean.setListTagLine(resultObject.getString("tagLine"));
                        } else {
                            bean.setListTagLine("");
                        }
                        if (!resultObject.getString("coverImg").equals("")) {
                            bean.setListCoverImg(resultObject.getString("coverImg"));
                        } else {
                            bean.setListCoverImg("");
                        }
                        if (!resultObject.getString("logo").equals("")) {
                            bean.setListLogo(resultObject.getString("logo"));
                        } else {
                            bean.setListLogo("");
                        }
                        if (!resultObject.getString("hourMode").equals("")) {
                            bean.setListHourMode(resultObject.getString("hourMode"));
                        }
                        /*if (!resultObject.getString("publishDate").equals("")) {
                            bean.setListPublistDate(resultObject.getString("publishDate"));
                        }*/
                        /*if (!resultObject.getString("currentDate").equals("")) {
                            String currentDate = resultObject.getString("currentDate");
                        }*/
                        if (!resultObject.getString("peopleInterested").equals("")) {
                            bean.setListPeopleInterested(resultObject.getString("peopleInterested"));
                        }
                        /*if (!resultObject.getString("byUser").equals("")) {
                            bean.setByUser(resultObject.getString("byUser"));
                        }*/
                        if (!resultObject.isNull("userReview")) {
                            JSONObject oUserReview = resultObject.getJSONObject("userReview");
                            String mode = oUserReview.getString("mode");
                            String average = oUserReview.getString("average");
                            String totalScore = oUserReview.getString("totalScore");
                            bean.setListMode(mode);
                            bean.setListAverage(average);
                            bean.setListTotleScore(totalScore);
                        }
                        if (!resultObject.isNull("oAddress")) {
                            oaddress = resultObject.getJSONObject("oAddress");
                            String address = oaddress.getString("address");
                            if (!oaddress.getString("address").equals("")) {
                                bean.setListAdd(oaddress.getString("address"));
                            }
                            if (!oaddress.getString("lat").equals("")) {
                                bean.setListLat(oaddress.getString("lat"));
                            }
                            if (!oaddress.getString("lag").equals("")) {
                                bean.setListLong(oaddress.getString("lag"));
                            }
                            if (!oaddress.getString("googleMapUrl").equals("")) {
                                bean.setGoogleMapUrl(oaddress.getString("googleMapUrl"));
                            }
                        } else {
                            bean.setListAdd("");
                        }
                        bean.setViewType(Common.HOME_LISTING);
                        listingList.add(bean);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    showNoData();
                }

            } else {

                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
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
        adapter.notifyDataSetChanged();
    }

    public void openNotificationListing(View view) {
        Intent intent = new Intent(this, NotificationListActivity.class);
        intent.putExtra(Common.NOTIFICATION_LIST_TYPE, Common.NOTIFICATION_TYPE_BELL_ICON);
        startActivity(intent);
    }

    private void getListingSectiontwo(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);

                        if (!HomeActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }


                        parseResponseforListingTwo(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!HomeActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforListingTwo(JSONObject response) {
        try {

            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
                JSONObject settings = data.getJSONObject("oSettings");
                JSONArray results = data.getJSONArray("oResults");

                for (int i = 0; i < results.length(); i++) {
                    inflateListingTwo(results.getJSONObject(i));
                }
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void inflateListingTwo(JSONObject jsonObject) {
        try {
            View view = inflater.inflate(R.layout.item_listing_card_two, listing_area_two,
                    false);
            AppCompatTextView event_title = (AppCompatTextView) view.findViewById(R.id.title);
            AppCompatTextView event_tagLine = (AppCompatTextView) view.findViewById(R.id.tagLine);
            AppCompatTextView event_Add = (AppCompatTextView) view.findViewById(R.id.address);
            AppCompatImageView img_list = (AppCompatImageView) view.findViewById(R.id.ChildImg);
            AppCompatImageView img_location = (AppCompatImageView) view.findViewById(R.id.locationImg);
            CircleImageView logo_event = (CircleImageView) view.findViewById(R.id.logo);
            JSONObject j_address = jsonObject.getJSONObject("oAddress");
            final String listId = jsonObject.getString("ID");
            String event_id = "", post_title = "", post_date = "", post_tagLine = "", post_interested = "", post_address = "", post_hosting = "", post_image = "", post_logo = "";
            if (!jsonObject.getString("postTitle").equals("")) {
                post_title = jsonObject.getString("postTitle");
                event_title.setText(jsonObject.getString("postTitle"));
            } else {
                event_title.setText("");
            }
            if (!jsonObject.getString("tagLine").equals("")) {
                post_tagLine = jsonObject.getString("tagLine");
                event_tagLine.setText(jsonObject.getString("tagLine"));
            } else {
                event_tagLine.setText("");
            }
            if (!j_address.getString("address").equals("")) {
                event_Add.setText(j_address.getString("address"));
            } else {
                event_Add.setText("");
            }
            if (!jsonObject.getString("coverImg").equals("")) {
                post_image = jsonObject.getString("coverImg");
                Glide.with(context)
                        .load(jsonObject.getString("coverImg"))
                        .placeholder(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(img_list);
            } else {
                Glide.with(context)
                        .load(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(img_list);
            }

            if (!jsonObject.getString("logo").equals("")) {
                post_logo = jsonObject.getString("logo");
                Glide.with(context)
                        .load(jsonObject.getString("logo"))
                        .placeholder(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(logo_event);
            } else
                Glide.with(context)
                        .load(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(logo_event);
            String finalPost_title = post_title;
            String finalPost_tagLine = post_tagLine;
            String finalPost_image = post_image;
            String finalPost_logo = post_logo;
            img_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListingDetailsActivity.class);
                    intent.putExtra("id", listId);
                    intent.putExtra("post_title", finalPost_title);
                    intent.putExtra("tag_line", finalPost_tagLine);
                    intent.putExtra("cover_image", finalPost_image);
                    intent.putExtra("logo_image", finalPost_logo);
                    intent.putExtra("from_page", Common.FROM_LIST_PAGE);
                    context.startActivity(intent);
                }
            });


            listing_area_two.addView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*event section*/
    private void getEventSection(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforEvents(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforEvents(JSONObject response) {
        try {

            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
                JSONObject settings = data.getJSONObject("oSettings");
                JSONArray results = data.getJSONArray("oResults");
                for (int i = 0; i < results.length(); i++) {
                    infalteEvents(results.getJSONObject(i));
                }
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void infalteEvents(final JSONObject jsonObject) {
        try {
            View view = inflater.inflate(R.layout.item_events_homepage, events_area, false);
            AppCompatTextView event_title = view.findViewById(R.id.esubTitle);
            AppCompatTextView event_date = view.findViewById(R.id.edate);
            AppCompatTextView event_address = view.findViewById(R.id.eaddress);
            AppCompatTextView event_interested = view.findViewById(R.id.epeopleInterested);
            AppCompatTextView event_hosted = view.findViewById(R.id.ehostedBy);
            AppCompatImageView img_event = view.findViewById(R.id.eChildImg);
            String event_id = "", post_title = "", post_date = "", post_tagLine = "", post_interested = "", post_address = "", post_hosting = "", post_image = "";
            JSONObject j_address = null;

            if (!TextUtils.isEmpty(jsonObject.getString("ID"))) {
                event_id = jsonObject.getString("ID");
            }
            if (!TextUtils.isEmpty(jsonObject.getString("postTitle"))) {
                post_title = jsonObject.getString("postTitle");
                event_title.setText(post_title);
            } else {
                post_title = "";
                event_title.setText(post_title);
            }
            if (!TextUtils.isEmpty(jsonObject.getString("publishDate"))) {
                post_date = jsonObject.getString("publishDate");
                event_date.setText(post_date);
            } else {
                post_date = "";
                event_date.setText(post_date);
            }
            if (!TextUtils.isEmpty(jsonObject.getString("publishDate"))) {
                post_date = jsonObject.getString("publishDate");
                event_date.setText(post_date);
            } else {
                post_date = "";
                event_date.setText(post_date);
            }
            if (!TextUtils.isEmpty(jsonObject.getString("peopleInterested"))) {
                post_interested = jsonObject.getString("peopleInterested");
                event_interested.setText(post_interested);
            } else {
                post_interested = "";
                event_interested.setText(post_interested);
            }
            if (!jsonObject.isNull("oAddress")) {
                j_address = jsonObject.getJSONObject("oAddress");
                if (!TextUtils.isEmpty(j_address.getString("address"))) {
                    post_address = j_address.getString("address");
                    event_address.setText(post_address);
                } else {
                    post_address = "";
                    event_address.setText(post_address);
                }
            }
            if (!jsonObject.getString("hostedBy").equals("")) {
                event_hosted.setText("Hosted By :" + jsonObject.getString("hostedBy"));
            } else {
                event_hosted.setText("");
            }
            if (!TextUtils.isEmpty(jsonObject.getString("hostedBy"))) {
                post_hosting = "Hosted By :" + jsonObject.getString("hostedBy");
                event_hosted.setText(post_hosting);
            } else {
                post_hosting = "";
                event_hosted.setText(post_hosting);
            }
            if (!TextUtils.isEmpty(jsonObject.getString("coverImg"))) {
                post_image = jsonObject.getString("coverImg");

                Glide.with(context)
                        .load(post_image)
                        .thumbnail(0.5f)
                        .into(img_event);
            } else {
                Glide.with(context)
                        .load(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(img_event);
            }
            if (!TextUtils.isEmpty(jsonObject.getString("tagLine"))) {
                post_tagLine = jsonObject.getString("tagLine");
            }
            String finalEvent_id = event_id;
            String finalPost_title = post_title;
            String finalPost_tagLine = post_tagLine;
            String finalPost_image = post_image;
            img_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("event_id", finalEvent_id);
                    intent.putExtra("post_title", finalPost_title);
                    intent.putExtra("tag_line", finalPost_tagLine);
                    intent.putExtra("cover_image", finalPost_image);
                    intent.putExtra("from_page", "home_page");
                    context.startActivity(intent);
                }
            });
            events_area.addView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /*All heading section */
    private void getHeadingSection(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforHeading(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforHeading(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                JSONObject jsonObject = response.getJSONObject("oData");
                if (!jsonObject.getString("TYPE").equals("")) {
                    View view = null;
                    if (jsonObject.getString("TYPE").equals(Common.TYPE_HEADING_ONE)) {
                        view = inflater.inflate(R.layout.item_heading_section_homepage, heading_area, false);
                        inflateLayout(view, jsonObject);
                        heading_area.addView(view);
                    } else if (jsonObject.getString("TYPE").equals(Common.TYPE_HEADING_TWO)) {
                        view = inflater.inflate(R.layout.item_heading_section_homepage, heading_area_two, false);
                        inflateLayout(view, jsonObject);
                        heading_area_two.addView(view);
                    } else if (jsonObject.getString("TYPE").equals(Common.TYPE_HEADING_THREE)) {
                        view = inflater.inflate(R.layout.item_heading_section_homepage, heading_area_three, false);
                        inflateLayout(view, jsonObject);
                        heading_area_three.addView(view);
                    } else {
                        view = inflater.inflate(R.layout.item_heading_section_homepage, heading_area_four, false);
                        inflateLayout(view, jsonObject);
                        heading_area_four.addView(view);
                    }
                } else {
                    Toast.makeText(context, "Type mismatched", Toast.LENGTH_SHORT).show();
                }
            } else {
                String failureMsg = response.getString("msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void inflateLayout(View view, JSONObject jsonObject) {
        try {
            LinearLayout itemParent = view.findViewById(R.id.item_parent);
            AppCompatTextView txtHeading = view.findViewById(R.id.txtHeadingTitle);
            AppCompatTextView txtDescription = view.findViewById(R.id.txtHeadingDiscription);
            if (!jsonObject.getString("bg_color").equals("")) {
                itemParent.setBackgroundColor(Color.parseColor(jsonObject.getString("bg_color")));
            } else {
                itemParent.setBackgroundColor(getResources().getColor(white_main_theme));
            }
            if (!jsonObject.getString("heading").equals("")) {
                if (!jsonObject.getString("heading_color").equals("")) {
                    txtHeading.setTextColor(Color.parseColor(jsonObject.getString("heading_color")));
                } else {
                    txtHeading.setTextColor(getResources().getColor(title_text_color));
                }
                txtHeading.setText(jsonObject.getString("heading"));
            } else {
                txtHeading.setText("");
            }
            if (!jsonObject.getString("description").equals("")) {
                if (!jsonObject.getString("description_color").equals("")) {
                    txtDescription.setTextColor(Color.parseColor(jsonObject.getString("description_color")));
                } else {
                    txtDescription.setTextColor(getResources().getColor(Gray));
                }
                txtDescription.setText(jsonObject.getString("description"));
            } else {
                txtDescription.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCategory(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforCategory(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforCategory(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                JSONObject data = response.getJSONObject("oData");
                JSONObject settings = data.getJSONObject("oSettings");
                JSONArray results = settings.getJSONArray("oResults");
                for (int i = 0; i < results.length(); i++) {
                    infalteCategories(results.getJSONObject(i));
                }
            } else {
                String failureMsg = response.getString("msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void infalteCategories(JSONObject jsonObject) {
        try {
            View view = inflater.inflate(R.layout.item_category_homepage, category_area, false);
            TextView cat_title = view.findViewById(R.id.cat_title);
            ImageView img_cat = view.findViewById(R.id.listtChildImg);
            String cat_id = "", cat_name = "", cat_featured_img = "";
            if (!TextUtils.isEmpty(jsonObject.getString("term_id"))) {
                cat_id = jsonObject.getString("term_id");
            }
            if (!TextUtils.isEmpty(jsonObject.getString("name"))) {
                cat_name = jsonObject.getString("name");
                cat_title.setText(cat_name);
            }
            if (!jsonObject.getString("featuredImg").equals("")) {
                cat_featured_img = jsonObject.getString("featuredImg");
                Glide.with(context)
                        .load(jsonObject.getString("featuredImg"))
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .placeholder(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(img_cat);
            } else
                Glide.with(context)
                        .load(R.drawable.app_placeholder)
                        .thumbnail(0.5f)
                        .into(img_cat);
            String finalCat_id = cat_id;
            String finalCat_name = cat_name;
            img_cat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cat_page = new Intent(HomeActivity.this, CategoryActivity.class);
                    cat_page.putExtra("cat_id", finalCat_id);
                    cat_page.putExtra("cat_name", finalCat_name);
                    startActivity(cat_page);
                }
            });


            category_area.addView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getHomeBanner(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", appPrefs.getString("preferredLanguage", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforBanner(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforBanner(JSONObject response) {
        try {
            if (response.getString("status").equals("Success")) {
                JSONObject jsonObject = response.getJSONObject("oData");
                String type = jsonObject.getString("TYPE");
                View view = inflater.inflate(R.layout.item_bannar,
                        banner_area, false);
                ImageView bannerImg = view.findViewById(R.id.bannar_img);
                TextView heading = view.findViewById(R.id.bannar_heading);
                TextView description = view.findViewById(R.id.bannar_descrip);

                String heading_color = jsonObject.getString("heading_color");
                String bg_type = jsonObject.getString("bg_type");
                String image_bg = jsonObject.getString("image_bg");
                String slider_bg = jsonObject.getString("slider_bg");
                String bg_color = jsonObject.getString("bg_color");
                String head = jsonObject.getString("heading");

                if (!jsonObject.getString("heading").equals("")) {
                    if (!heading_color.equals(""))
                        heading.setTextColor(Color.parseColor(heading_color));
                    else
                        heading.setTextColor(getResources().getColor(white_main_theme));
                    heading.setText(jsonObject.getString("heading"));
                } else {
                    heading.setText("");
                }
                if (!jsonObject.getString("description").equals("")) {
                    description.setText(jsonObject.getString("description"));
                    description.setTextColor(getResources().getColor(white_main_theme));
                } else {
                    heading.setText("");
                }
                if (!jsonObject.getString("image_bg").equals("")) {
                    try {
                        Glide.with(context)
                                .load(jsonObject.getString("image_bg"))
                                .thumbnail(0.5f)
                                .error(R.drawable.app_placeholder)
                                .into(bannerImg);
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        Glide.with(context)
                                .load(R.drawable.app_placeholder)
                                .thumbnail(0.5f)
                                .into(bannerImg);
                    } catch (Exception e) {

                    }
                }
                banner_area.addView(view);
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        userProfileAccount = findViewById(R.id.llUserAccount);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        keyStores = new ArrayList<>();

        notificationCount = findViewById(R.id.notification_count);
        banner_area = findViewById(R.id.banner_area);
        category_area = findViewById(R.id.category_area);
        heading_area = findViewById(R.id.heading_area);
        heading_area_two = findViewById(R.id.heading_area_two);
        heading_area_three = findViewById(R.id.heading_area_three);
        events_area = findViewById(R.id.events_area);
        heading_area_four = findViewById(R.id.heading_area_four);
        listing_area_two = (LinearLayout) findViewById(R.id.listing_area_two);
        inflater = LayoutInflater.from(context);


        listingRecycler = findViewById(R.id.listing_area);
        //listingRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        listingRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listingList = new ArrayList<>();
        adapter = new ListingApater(listingList, false, this);
        listingRecycler.setAdapter(adapter);

        categoryFilterList = new ArrayList<>();
        regionFilterList = new ArrayList<>();
        noOfMessage = findViewById(R.id.noOfMsg);
    }

    /*apps laguage section*/
    private void selectLaguage(String appsLanguage) {
        if (appsLanguage.equals(getResources().getString(R.string.english_lang))) {
            setNewLocale(this, LocaleManager.ENGLISH);
        }
        if (appsLanguage.equals(getResources().getString(R.string.danish_lang))) {
            setNewLocale(this, LocaleManager.DANISH);
        }
        if (appsLanguage.equals(getResources().getString(R.string.german_lang))) {
            setNewLocale(this, LocaleManager.GERMAN);
        }

    }

    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
    }

    public void home(View view) {
    }

    public void events(View view) {
        if (getIntent().hasExtra(Common.FROM_EVENT_ACTIVITY)) {
            if (getIntent().getStringExtra("from_event").equals(Common.FROM_EVENT_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), EventActivity.class);
                nextPage.putExtra(Common.FROM_HOME_ACTIVITY, home_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), EventActivity.class);
            nextPage.putExtra(Common.FROM_HOME_ACTIVITY, home_activity);
            startActivity(nextPage);
        }
    }

    public void listing(View view) {
        if (getIntent().hasExtra("from_listing")) {
            if (getIntent().getStringExtra("from_listing").equals(Common.FROM_LISTING_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), ListingActivity.class);
                nextPage.putExtra(Common.FROM_HOME_ACTIVITY, home_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), ListingActivity.class);
            nextPage.putExtra(Common.FROM_HOME_ACTIVITY, home_activity);
            startActivity(nextPage);
        }
    }

    public void profileAccount(View view) {
        if (getIntent().hasExtra(Common.FROM_USER_ACTIVITY)) {
            if (getIntent().getStringExtra("from_user").equals(Common.FROM_USER_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), UserAccount.class);
                nextPage.putExtra(Common.FROM_HOME_ACTIVITY, home_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), UserAccount.class);
            nextPage.putExtra(Common.FROM_HOME_ACTIVITY, home_activity);
            startActivity(nextPage);
        }
    }


    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //    not required
    private void getHeadingSectionTwo(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", "English")
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforHeading(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void getHeadingSectionThree(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", "English")
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforHeading(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void getHeadingSectionfour(String s) {
        String url = Common.BASE_URL + "home-screen-detail";
        AndroidNetworking.post(url)
                .addBodyParameter("section", s)
                .addBodyParameter("language", "Danish")
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforHeading(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    public void gotoChatlist(View view) {
        if (isNew) {
            noOfMessage.setText("");
            isNew = false;
        }
        Intent chatlistpage = new Intent(this, ChatHistoryList.class);
        chatlistpage.putExtra("user_id", appPrefs.getString("userID", ""));
        startActivity(chatlistpage);
    }


    public void gotoSearchDialog(View view) {
        String lat = "55.056595", lng = "8.824121";
        getFilterList();
        if (categoryFilterList.size() > 0 || regionFilterList.size() > 0) {
            GlobalSearchDialog searchDialog = new GlobalSearchDialog(HomeActivity.this, categoryFilterList, regionFilterList, Common.SEARCH_TYPE_LISTING, lat, lng);
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

    public void sightToSee(View view) {
        Intent cat_page = new Intent(HomeActivity.this, CategoryActivity.class);
        cat_page.putExtra("cat_id", "202");
        cat_page.putExtra("cat_name", "Guidede ture");
        startActivity(cat_page);

    }

    public void goToFoodDrink(View view) {
        Intent cat_page = new Intent(HomeActivity.this, CategoryActivity.class);
        cat_page.putExtra("cat_id", "123");
        cat_page.putExtra("cat_name", "Fast food");
        startActivity(cat_page);
    }

    private void callForNotificationList() {
        String url = Common.BASE_URL + "my-notification";
        String userID = ((MyApplication) getApplicationContext()).getAppPrefs().getString("userID", "");
        AndroidNetworking.post(url)
                .addBodyParameter("userId", userID)
//        368
                .addBodyParameter("displayType", Common.NOTIFICATION_TYPE_BELL_ICON)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);

                        parseResponseforNotificationListing(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforNotificationListing(JSONObject response) {
        try {
            ArrayList<Notifications> notificationsArrayList = new ArrayList<>();
            if (response.getString("status").equals("Success")) {
//                JSONObject data = response.getJSONObject("oData");
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

                if (notificationsArrayList != null && notificationsArrayList.size() > 0) {
                    int c = 0;
                    for (int a = 0; a < notificationsArrayList.size(); a++) {
                        if (notificationsArrayList.get(a).getNotificationStatus().equals("0")) {
                            c += 1;
                        }
                    }
                    if (c > 9) {

                        notificationCount.setText(c + "+");
                        notificationCount.setVisibility(View.VISIBLE);
                    } else if (c != 0 && c < 10) {
                        notificationCount.setText(String.valueOf(c));
                        notificationCount.setVisibility(View.VISIBLE);
                    } else {
                        notificationCount.setVisibility(View.GONE);
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


    private void callPushNotification() {

        try {
            lat = String.valueOf(new GPSTracker(this).getLatitude());
            lat = String.valueOf(new GPSTracker(this).getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = Common.BASE_URL + "send-push-notification";
        String userID = ((MyApplication) context.getApplicationContext()).getAppPrefs().getString("userID", "");
        String dateTime = Common.GetCurrentDateTime();
        //System.out.print(dateTime);

        AndroidNetworking.post(url)
                .addBodyParameter("userId", userID)
                .addBodyParameter("curTime", dateTime)
                .addBodyParameter("notificationType", "Greetings Notification")
                .addBodyParameter("lat", lat)
                .addBodyParameter("lag", lng)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {

                        try {
                            Log.i("callPushNotification", "callPushNotification ::"+response);
                            if (response.getString("success").equals("1")) {
                                Log.i(TAG, "callPushNotification triggered ");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail()); //this line

                    }
                });
    }

    private void startMyOwnForeground() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = null;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", importance);
                builder = new NotificationCompat.Builder(this);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("Ho");
                builder.setContentText("Byyyy");
                builder.setSound(tone);
                //builder.setContentIntent(pendingIntent);
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setLightColor(Color.RED);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                manager.createNotificationChannel(channel);

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("channel_id");
            }
            manager.notify(1, builder.build());
           // triggerNotifcation();
        }
    }



//    @Override
    public void updateGreetingNotification(String title, String body, RemoteViews remoteView, RemoteViews remoteViewBig) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = null;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", importance);
                builder = new NotificationCompat.Builder(this);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle(title);
                builder.setContentText(body);

                builder.setCustomContentView(remoteView);
                builder.setCustomBigContentView(remoteViewBig);
                builder.setSound(tone);
                //builder.setContentIntent(pendingIntent);
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setLightColor(Color.RED);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                manager.createNotificationChannel(channel);

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("channel_id");
            }
            manager.notify(1, builder.build());
            //triggerNotifcation();
        }
    }
}
