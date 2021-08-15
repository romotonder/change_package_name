package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.DiscussionAdapter;
import com.romo.tonder.visits.adapters.ListTermInfoAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.DiscussionModel;
import com.romo.tonder.visits.helpers.SendEventsFavourite;
import com.romo.tonder.visits.helpers.SendLike;
import com.romo.tonder.visits.interfaces.DiscussionClickInterfaces;
import com.romo.tonder.visits.models.TermInfoModel;
import com.romo.tonder.visits.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class EventDetailsActivity extends BaseActivity implements DiscussionClickInterfaces, OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private static final String TAG = EventDetailsActivity.class.getSimpleName();
    private ImageView btnBack;
    private Context context;

    private Dialog dialog;
    private SharedPreferences appPrefs;

    private AppCompatTextView tvpostTitle, toolbarTitle, tvtagLine, tvEventDetails, tvOpen, tvClose,
            tvAddress, tvPhone, tvEmail, tvWeb, descripTitle, descriptionBody, discussionBody, tvCategoryTitle,
            tvDescriptionTitle, tvDescriptionBody, tvDiscBody;

    private LinearLayout rootDiscussionListArea;
    private ImageView imgFavorite;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imgCover;
    private Toolbar toolbar;
    private String postTitle = "";
    private String coverImg = "";
    private String tagLine = "";

    private LayoutInflater inflater;
    private String isLike = "false";

    private ArrayList<DiscussionModel> discussionsList;
    private ArrayList<TermInfoModel> termList;
    private ListTermInfoAdapter termInfoAdapter;
    private DiscussionAdapter adapter;
    private RecyclerView discussionRecyler,terminfoRecycle;
    private static int DISCUSSION_LIST = 1;
    private Button btnDiscussion;
    private boolean isFavourite = false;
    private CardView descriptionCard,cardTermInfo;
    private String phoneNumber="",email_address="",webAddress="";
    private String[] permissions = {
            Manifest.permission.CALL_PHONE
    };
    private BottomSheetDialog bottomSheetDialog;
    private AppCompatTextView tvMail, tvCallUs, tvShare, tvCancel;
    private String eventID="";
    private double lattitude = 0, logitude = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_event_details);
        context = this;
        if (getIntent().hasExtra("event_id")){
            bindEvent();
            eventID=getIntent().getStringExtra("event_id");
            getDetails(eventID, appPrefs.getString("preferredLanguage", ""), appPrefs.getString("userID", ""));
            clickEvent();
        }
    }

    private void clickEvent() {
        btnDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EventDetailsActivity.this);
            View view = getLayoutInflater().inflate(R.layout.add_discussion, null);
                mBuilder.setView(view);
            final EditText comment = view.findViewById(R.id.add_comment);
            MaterialButton btnAdd = view.findViewById(R.id.btn_add);
            MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
            final AlertDialog dialog = mBuilder.create();
                btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!comment.getText().toString().equals("")) {
                        if (!EventDetailsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        addComment(eventID, comment.getText().toString());

                    } else {
                        Toast.makeText(EventDetailsActivity.this,
                                "Please write comments", Toast.LENGTH_SHORT).show();
                    }
                }
            });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
                dialog.show();            }
        });
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isConnected(context)) {
                    new SendEventsFavourite(context).userFavourite(getIntent().getStringExtra("id"),
                            appPrefs.getString("userID", ""));
                    if (isFavourite) {
                        isFavourite = false;
                        imgFavorite.setColorFilter(getResources().getColor(R.color.black));
                    } else {
                        isFavourite = true;
                        imgFavorite.setColorFilter(getResources().getColor(R.color.red_main_theme));
                    }

                } else {
                    Toast.makeText(context, "No Network Connections", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    private void addComment(String eventid, String comment) {
        dialog.show();
        String url = Common.BASE_URL + "event-discussion";
        AndroidNetworking.post(url)
                .addBodyParameter("eventDiscussionId", eventid)
                .addBodyParameter("userId", appPrefs.getString("userID",""))
                .addBodyParameter("type", "Add")
                .addBodyParameter("commentInfo", comment)
                //.addBodyParameter("commentId", userID)
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
                        if (!EventDetailsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponse(JSONObject response) {
        if (!EventDetailsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            JSONObject data = null;
            JSONArray list = null;
            String message = null;
            if (response.getString("status").equals("Success")) {
                message = response.getString("Data");
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                list = response.getJSONArray("commentList");
                if (list.length() > 0) {
                    discussionsList.clear();
                    for (int index = 0; index < list.length(); index++) {

                        JSONObject jsonObject = list.getJSONObject(index);
                        String eventId = jsonObject.getString("eventId");
                        String commentDesc = jsonObject.getString("commentDesc");
                        String userID = jsonObject.getString("userId");
                        String displayName = jsonObject.getString("displayName");
                        String commentDate = jsonObject.getString("commentDate");
                        String totalLikes = jsonObject.getString("totalLikes");
                        String countComment = jsonObject.getString("countComment");
                        boolean myLike = jsonObject.getBoolean("myLike");
                        //int viewTypeDiscussion=DISCUSSION_LIST;
                        DiscussionModel bean = new DiscussionModel();
                        bean.setEvent_id(eventId);
                        bean.setDisplay_name(displayName);
                        bean.setComment_date(commentDate);
                        bean.setTotal_likes(totalLikes);
                        bean.setCount_comments(countComment);
                        bean.setComment_desc(commentDesc);
                        bean.setMy_like(myLike);
                        bean.setViewType(Common.EVENT_DISCUSION);
                        discussionsList.add(bean);
                        if (discussionsList.size() > 0) {
                            adapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDetails(String listingID, String language, String userID) {
        dialog.show();
        String url = Common.BASE_URL + "event-listing-details";
        AndroidNetworking.post(url)
                .addBodyParameter("listingId", listingID)
                .addBodyParameter("language", language)
                .addBodyParameter("userId", userID)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseEventsResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!EventDetailsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseEventsResponse(JSONObject response) {
        if (!EventDetailsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            JSONObject data = null;
            JSONObject results = null;
            if (response.getString("status").equals("Success")) {
                data = response.getJSONObject("oData");
                results = data.getJSONObject("oResults");
                String id = results.getString("ID");
                postTitle = results.getString("postTitle");
                String tagLine = results.getString("tagLine");
                String interested=results.getString("peopleInterested");
                if (!tagLine.equals("")){
                    tvtagLine.setText(tagLine+" ");
                    if (!interested.equals("")){
                        tvtagLine.setText("");
                        tvtagLine.setText(tagLine+" , "+interested+" People interrested");
                    }
                }
                if (!results.isNull("postDescription")) {
                    if (!results.getString("postDescription").equals("")) {
                        descriptionCard.setVisibility(View.VISIBLE);
                        tvDescriptionBody.setText(Html.fromHtml(results.getString("postDescription")));
                    } else {
                        descriptionCard.setVisibility(View.GONE);
                    }
                } else {
                    descriptionCard.setVisibility(View.GONE);
                }
                String logo = results.getString("logo");
                String coverImg = results.getString("coverImg");
                //String hourMode=results.getString("hourMode");
                String publishDate = results.getString("publishDate");
                String currentDate = results.getString("currentDate");
                if (!results.getString("emailAddress").equals("")) {
                    email_address=results.getString("emailAddress").toLowerCase().trim();
                    tvEmail.setText(email_address);
                } else {
                    tvEmail.setText("");
                    tvEmail.setVisibility(View.GONE);
                }
                if (!results.getString("phoneNumber").equals("")) {
                    phoneNumber=results.getString("phoneNumber");
                    tvPhone.setText(phoneNumber);
                } else {
                    tvPhone.setText("");
                    tvPhone.setVisibility(View.GONE);
                }
                if (!results.getString("website").equals("")) {
                    webAddress=results.getString("website");
                    tvWeb.setText(webAddress);
                } else {
                    tvWeb.setText("");
                    tvWeb.setVisibility(View.GONE);
                }
                String hostedBy = results.getString("hostedBy");
                boolean myFavourite = results.getBoolean("myFavourite");
                String totalDiscussion = results.getString("totalDiscussion");
                if (!results.getString("totalDiscussion").equals("")) {
                    tvDiscBody.setText((results.getString("totalDiscussion")) + " " + "Discussion");
                } else {
                    tvDiscBody.setText("");
                }
                if (results.getBoolean("myFavourite")) {
                    isFavourite = true;
                    imgFavorite.setColorFilter(getResources().getColor(R.color.red_main_theme));
                } else {
                    isFavourite = false;
                    imgFavorite.setColorFilter(getResources().getColor(R.color.black));
                }
                JSONObject oaddress = results.getJSONObject("oAddress");
                String address = oaddress.getString("address");
                if (!oaddress.getString("address").equals("")) {
                    tvAddress.setText(oaddress.getString("address"));
                } else {
                    tvAddress.setText("");
                }
                if ((!oaddress.getString("lat").equals(""))
                        && !oaddress.getString("lat").equals("")){
                    try {
                        lattitude = Double.parseDouble(oaddress.getString("lat"));
                        logitude = Double.parseDouble(oaddress.getString("lat"));
                    } catch (Exception e) {
                        Log.e(TAG, String.valueOf(e));
                    }
                }
                if (!oaddress.getString("googleMapUrl").equals("")){
                    String googleMapUrl = oaddress.getString("googleMapUrl");
                }
                displayInMapFrag();


                JSONArray eventDetails = results.getJSONArray("eventDetails");
                if (eventDetails.length() > 0){
                    for (int i = 0; i < eventDetails.length(); i++) {
                        JSONObject jsonObject = eventDetails.getJSONObject(i);
                        String ID = jsonObject.getString("ID");
                        String objectID = jsonObject.getString("objectID");
                        String frequency = jsonObject.getString("frequency");
                        String specifyDays = jsonObject.getString("specifyDays");
                        String startsOn = jsonObject.getString("startsOn");
                        String endsOn = jsonObject.getString("endsOn");
                        String startsOnUTC = jsonObject.getString("startsOnUTC");
                        String endsOnUTC = jsonObject.getString("endsOnUTC");
                        String timezone = jsonObject.getString("timezone");
                        if (eventID.equals(objectID)) {
                            tvEventDetails.setText("");
                            if (frequency.equals("weekly") && !specifyDays.equals("")) {
                                tvEventDetails.setText(getString(R.string.every) + " " + specifyDays + " " + startsOn + "-" + " \n" + endsOn);
                            } else if (frequency.equals("occurs_once")) {
                                tvEventDetails.setText(specifyDays + " " + startsOn + " - " + "\n" + endsOn);
                            }
                            if (!startsOn.equals("")) {
                                String time=Common.getTime(startsOn);
                                tvOpen.setText("");
                                tvOpen.setText(getString(R.string.opening_at)+" \n "+time);
                            }
                            if (!endsOn.equals("")) {
                                String time=Common.getTime(endsOn);
                                tvClose.setText("");
                                tvClose.setText(getString(R.string.closed_at)+" \n "+time);
                            }
                        }
                    }
                }
                JSONArray eventDiscussionList=null;
                if (!results.isNull("eventDiscussionList")){
                    eventDiscussionList = results.getJSONArray("eventDiscussionList");
                    for (int i = 0; i < eventDiscussionList.length(); i++) {
                        JSONObject jsonObject = eventDiscussionList.getJSONObject(i);
                        String eventId = jsonObject.getString("eventId");
                        String userId = jsonObject.getString("userId");
                        String commentDesc = jsonObject.getString("commentDesc");
                        String displayName = jsonObject.getString("displayName");
                        String commentDate = jsonObject.getString("commentDate");
                        String totalLikes = jsonObject.getString("totalLikes");
                        String countComment = jsonObject.getString("countComment");
                        boolean myLike = jsonObject.getBoolean("myLike");
                        //int viewTypeDiscussion=DISCUSSION_LIST;
                        DiscussionModel bean = new DiscussionModel();
                        bean.setEvent_id(eventId);
                        bean.setUserId(userId);
                        bean.setDisplay_name(displayName);
                        bean.setComment_date(commentDate);
                        bean.setTotal_likes(totalLikes);
                        bean.setCount_comments(countComment);
                        bean.setComment_desc(commentDesc);
                        bean.setMy_like(myLike);
                        bean.setViewType(Common.EVENT_DISCUSION);
                        discussionsList.add(bean);
                        if (discussionsList.size()>0){
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else {

                }

                JSONObject termInfo = results.getJSONObject("termInfo");
                String tcatTitle = termInfo.getString("catTitle");
                /*if (!termInfo.isNull("catList")) {
                    if (!tcatTitle.equals("")) {
                        cardTermInfo.setVisibility(View.VISIBLE);
                        tvCategoryTitle.setText(tcatTitle);
                        JSONArray jtermInfoList = termInfo.getJSONArray("catList");
                        for (int i = 0; i < jtermInfoList.length(); i++) {
                            JSONObject jreviews = jtermInfoList.getJSONObject(i);
                            String termcatId = jreviews.getString("catId");
                            String termcatName = jreviews.getString("catName");
                            String termcatImage = jreviews.getString("catImage");
                            TermInfoModel termInfoModel = new TermInfoModel();

                            if (!jreviews.getString("catId").equals("")) {
                                termInfoModel.setTermID(jreviews.getString("catId"));
                            }
                            if (!jreviews.getString("catName").equals("")) {
                                termInfoModel.setTermName(jreviews.getString("catName"));
                            }
                            if (!jreviews.getString("catImage").equals("")) {
                                termInfoModel.setTermImage(jreviews.getString("catImage"));
                            }
                            termInfoModel.setViewType(TERM_INFO);
                            termList.add(termInfoModel);
                            if (termList.size() == 0) {

                            } else {
                                termInfoAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        cardTermInfo.setVisibility(View.GONE);
                        listingDetailsData.isCardTermInfo = false;
                        tvTerminfoTitle.setText("");
                    }
                }
                if (jsonArray.length()>0){
                    for (int i = 0; i <= jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String catId = jsonObject.getString("catId");
                        String catName = jsonObject.getString("catName");
                        String catImage = jsonObject.getString("catImage");
                    }

                }*/

                /*JSONObject serviceObject = results.getJSONObject("serviceObject");
                String catTitle = serviceObject.getString("catTitle");
                String catList = serviceObject.getString("catList");
                JSONObject termInfo = results.getJSONObject("termInfo");
                String tcatTitle = termInfo.getString("catTitle");
                JSONArray jsonArray = termInfo.getJSONArray("catList");
                for (int i = 0; i <= jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String catId = jsonObject.getString("catId");
                    String catName = jsonObject.getString("catName");
                    String catImage = jsonObject.getString("catImage");
                }*/
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void bindEvent() {
        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.back);
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        imgCover = findViewById(R.id.im_coverimage);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.txt_postTitle);
        tvpostTitle = findViewById(R.id.tv_postTitle);
        tvtagLine = findViewById(R.id.tv_tagLine);
        tvEventDetails = findViewById(R.id.tv_eventDetails);
        tvOpen = findViewById(R.id.tv_open);
        tvClose = findViewById(R.id.tv_close);
        tvAddress = findViewById(R.id.tv_avrg_rating_title);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_emails);
        tvWeb = findViewById(R.id.tv_ingeneral);


        tvDiscBody = findViewById(R.id.discussion_body);
        //description section
        descriptionCard = (CardView) findViewById(R.id.card_description);
        tvDescriptionTitle = findViewById(R.id.descrip_title);
        tvDescriptionBody = findViewById(R.id.description_body);
        btnDiscussion = findViewById(R.id.discussion_btn);
        imgFavorite = findViewById(R.id.img_favorite);
        //card terminfo

        cardTermInfo=(CardView)findViewById(R.id.card_terminfo);
        tvCategoryTitle = findViewById(R.id.cat_title);
        terminfoRecycle = findViewById(R.id.parent_terminfo);
        terminfoRecycle.setLayoutManager(new GridLayoutManager(EventDetailsActivity.this, 2));
        termList = new ArrayList<>();
        termInfoAdapter = new ListTermInfoAdapter(termList, this);
        terminfoRecycle.setAdapter(termInfoAdapter);



        //recycler
        discussionRecyler = (RecyclerView) findViewById(R.id.recyclelist);
        discussionRecyler.setLayoutManager(new LinearLayoutManager(this));
        discussionsList = new ArrayList<>();
        adapter = new DiscussionAdapter(discussionsList, this);
        discussionRecyler.setAdapter(adapter);

        //inflater = LayoutInflater.from(context);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarTitle.setText(getIntent().getStringExtra("post_title"));
                    isShow = true;
                } else if (isShow) {
                    toolbarTitle.setText("");
                    isShow = false;
                }
            }
        });
        if (!TextUtils.isEmpty(getIntent().getStringExtra("post_title"))){
            tvpostTitle.setText(getIntent().getStringExtra("post_title"));
        }else {
            tvpostTitle.setText("");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("cover_image"))){
            Glide.with(context)
                    .load(getIntent().getStringExtra("cover_image"))
                    .thumbnail(0.5f)
                    .into(imgCover);
        }else {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(imgCover);
        }
    }

    @Override
    public void onClicked(DiscussionModel model, int position) {
        new SendLike(context).userEventLikeDislike(model.getEvent_id());
        DiscussionModel discussionModel = discussionsList.get(position);
        boolean isLike = model.isMy_like();
        int total = Integer.parseInt(model.getTotal_likes());
        if (isLike) {
            discussionModel.setMy_like(false);
            discussionModel.setTotal_likes(String.valueOf(total - 1));
        } else {
            discussionModel.setTotal_likes(String.valueOf(total + 1));
            discussionModel.setMy_like(true);
        }
        discussionsList.set(position, discussionModel);
        //now update adapter
        adapter.updateRecords(discussionsList);

    }

    @Override
    public void commentsClick(DiscussionModel model, int position) {
        String eventID=model.getEvent_id();
        String userID=model.getUserId();
        String displayName=model.getDisplay_name();
        String commentDesc=model.getComment_desc();
        String comentDate=model.getComment_date();
        String totalLike=model.getTotal_likes();
        String countComment=model.getCount_comments();
        boolean myLike=model.isMy_like();
        DiscussionModel discussionModel = discussionsList.get(position);
        Intent commentPage=new Intent(EventDetailsActivity.this,CommentsActivity.class);
        commentPage.putExtra("from_page","from_event_page");
        commentPage.putExtra("reviewID",eventID);
        commentPage.putExtra("userID",userID);
        commentPage.putExtra("authorName",displayName);
        commentPage.putExtra("reviewDate",comentDate);
        commentPage.putExtra("reviewBody",commentDesc);
        commentPage.putExtra("like",totalLike);
        commentPage.putExtra("comment",countComment);
        startActivity(commentPage);

    }

    public void phoneCall(View view) {
        startCalling();
    }

    private void startCalling() {
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (checkPermission()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber.trim()));
                startActivity(callIntent);
                Log.d(TAG, "makePhoneCall: " + phoneNumber.trim());
                /*try {
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
                }*/
            } else {
                requestPermission();
                Log.d(TAG, "requestPermission: ");
            }
        } else {
            Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show();
        }
    }

    //permission
    private boolean checkPermission() {
        int external_read_write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (external_read_write == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //runtime permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(EventDetailsActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_PHONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "phone permission is not granted",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "All permissions granted successfully",
                                Toast.LENGTH_LONG).show();
                    }

                }
        }
    }

    public void sendMail(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example.yahoo.com"});
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email_address.trim().toLowerCase()});
            intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email client installed on your device.", Toast.LENGTH_SHORT).show();
        }

    }
    public void websiteView(View view) {
        ((MyApplication) getApplicationContext()).goToUrl(webAddress);
    }

    public void menuOption(View view) {
        showBottomDialog();
    }

    private void showBottomDialog() {
        //if (bottomSheetDialog==null){

        bottomSheetDialog = new BottomSheetDialog(EventDetailsActivity.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.event_bottomsheet,
                null);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        tvCallUs = view.findViewById(R.id.lblCallUs);
        tvShare = view.findViewById(R.id.lblShare);
        tvCancel = view.findViewById(R.id.lblCancel);
        tvCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalling();
                bottomSheetDialog.dismiss();
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSocials();
                bottomSheetDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }
    private void shareSocials() {
        if (!TextUtils.isEmpty(webAddress)) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
            String app_url = webAddress;
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else {
            Toast.makeText(context, "Link not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void menuClick(DiscussionModel model, int position) {
        showMenuDialog(model,position);

    }

    private void showMenuDialog(DiscussionModel model, int position) {
        AppCompatTextView tvComments,tvLike,tvCancel;
        bottomSheetDialog = new BottomSheetDialog(EventDetailsActivity.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_menu_dialog,
                null);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        tvComments = view.findViewById(R.id.comments_area);
        tvLike = view.findViewById(R.id.like_area);
        tvCancel = view.findViewById(R.id.cancel_area);
        tvComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventID=model.getEvent_id();
                String userID=model.getUserId();
                String displayName=model.getDisplay_name();
                String commentDesc=model.getComment_desc();
                String comentDate=model.getComment_date();
                String totalLike=model.getTotal_likes();
                String countComment=model.getCount_comments();
                boolean myLike=model.isMy_like();
                DiscussionModel discussionModel = discussionsList.get(position);
                Intent commentPage=new Intent(EventDetailsActivity.this,CommentsActivity.class);
                commentPage.putExtra("from_page","from_event_page");
                commentPage.putExtra("reviewID",eventID);
                commentPage.putExtra("userID",userID);
                commentPage.putExtra("authorName",displayName);
                commentPage.putExtra("reviewDate",comentDate);
                commentPage.putExtra("reviewBody",commentDesc);
                commentPage.putExtra("like",totalLike);
                commentPage.putExtra("comment",countComment);
                startActivity(commentPage);

                bottomSheetDialog.dismiss();
            }
        });
        tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendLike(context).userEventLikeDislike(model.getEvent_id());
                DiscussionModel discussionModel = discussionsList.get(position);
                boolean isLike = model.isMy_like();
                int total = Integer.parseInt(model.getTotal_likes());
                if (isLike) {
                    discussionModel.setMy_like(false);
                    discussionModel.setTotal_likes(String.valueOf(total - 1));
                } else {
                    discussionModel.setTotal_likes(String.valueOf(total + 1));
                    discussionModel.setMy_like(true);
                }
                discussionsList.set(position, discussionModel);
                //now update adapter
                adapter.updateRecords(discussionsList);
                bottomSheetDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    public void backPageReturn(View view) {
        finish();
    }
    private void displayInMapFrag() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.event_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lattitude, logitude))
                .title("Marker"));
        LatLng pos = new LatLng(lattitude, lattitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                openMapApplicationDialog();
            }
        });


    }
    private android.app.AlertDialog openMapApplicationDialog() {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.setTitle("External URL");
        alertDialog.setMessage("Do you want to open map in your app ?");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String geoUri = "http://maps.google.com/maps?q=loc:" + lattitude + "," + logitude + " (" + "" + ")";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        context.startActivity(intent);
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

        return alertDialog;
    }
}