package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
//import com.google.android.libraries.places.api.Places;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.BusynessHourApapter;
import com.romo.tonder.visits.adapters.DiscussionAdapter;
import com.romo.tonder.visits.adapters.GallaryListAdapter;
import com.romo.tonder.visits.adapters.GalleryAdapter;
import com.romo.tonder.visits.adapters.ListServiceAdapter;
import com.romo.tonder.visits.adapters.ListTermInfoAdapter;
import com.romo.tonder.visits.adapters.ListVideoAdapter;
import com.romo.tonder.visits.adapters.ProductAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.DiscussionModel;
import com.romo.tonder.visits.helpers.ListingDetailsData;
import com.romo.tonder.visits.helpers.SendLike;
import com.romo.tonder.visits.helpers.sendFavourite;
import com.romo.tonder.visits.interfaces.DiscussionClickInterfaces;
import com.romo.tonder.visits.models.BusynessHourModel;
import com.romo.tonder.visits.models.GallaryModel;
import com.romo.tonder.visits.models.ListingModel;
import com.romo.tonder.visits.models.ProductModel;
import com.romo.tonder.visits.models.ServiceModel;
import com.romo.tonder.visits.models.TermInfoModel;
import com.romo.tonder.visits.models.UserChatListModel;
import com.romo.tonder.visits.models.VideoModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;


public class ListingDetailsActivity extends BaseActivity implements DiscussionClickInterfaces, OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private Context context;
    private double lat = 0, lng = 0;
    private String listID = "";
    private static final String TAG = ListingDetailsActivity.class.getSimpleName();
    private ImageView btnBack;

    private Dialog dialog;
    private SharedPreferences appPrefs;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imgCover;
    private CircleImageView imgLogoimage;
    private Toolbar toolbar;
    private AppCompatImageView menusOption, faceBook;

    private AppCompatTextView tvpostTitle, toolbarTitle, tvtagLine, tvAvrgRatingTitle, tvLocation, tvQuality, tvPhone, tvAdd, tvEmail, tvWebsite,
            tvInGeneral, tvSrvice, tvTimingTitle, tvStatTitle, tvStatView, tvStatReview, tvStatFav, tvTerminfoTitle,
            tvServiceTitle, tvInfoTitle, tvGallaryTitle, tvGallaryViewall, tvDescTitle, tvDescBody, tvDescViewall, tvViewTitle,
            tvProductTitle, tvVideoTitle, reviewRatingHeading, tvUserAvg, tvUserMood, tvUserQuality, tvLocationValue, tvQualityValue, tvInGeneralValue, tvServiceValue;
    private Button btnBookNow, btnYourReview;
    //private LinearLayout parentProduct;
    private LayoutInflater inflater;
    private CardView cardAverageRating, cardTiming, cardDesc, cardProduct,
            cardGallary, cardVideo, cardInfo, cardService, cardTermInfo, cardStatDetails, cardViewAllGallary, cardYourReview,
            cardReviewRating;
    private RecyclerView serviceRecycle, terminfoRecycle;
    private ArrayList<ServiceModel> serviceList;
    private ArrayList<TermInfoModel> termList;

    private ListServiceAdapter serviceAdapter;
    private ListTermInfoAdapter termInfoAdapter;
    private static final int SERVICE = 1;
    private static final int TERM_INFO = 2;
    private ArrayList<GallaryModel> galleryList;
    private GalleryAdapter galleryAdapter;
    private RecyclerView galleryRecycle;
    private ArrayList arrayTestList;

    private RecyclerView viewAllRecycler;
    private RecyclerView parentTimingLayout;
    private ArrayList<GallaryModel> gallary_model_list;
    private GallaryListAdapter gallerylist_adapter;


    //String
    ListingDetailsData listingDetailsData = new ListingDetailsData();

    //discussion
    private ArrayList<DiscussionModel> discussionsList;
    private DiscussionAdapter adapter;
    private RecyclerView discussionRecyler;

    private static final int REQUEST_PAGE = 10;

    //busynesshr
    private ArrayList<BusynessHourModel> busynessHrLists;
    private BusynessHourApapter busynessHourApapter;
    private AppCompatTextView tvOpenStatus;
    //Bottom section
    AppCompatTextView bottomHome, bottomDesc, bottomGallary, bottomProduct, bottomService, bottomReviewBtn;
    //Image Section
    AppCompatImageView doMessage, doShare, doFavourite;
    private ImageButton doPhone;
    private AppCompatTextView lblPhone, lblMessage, lblShare;
    private String[] permissions = {
            Manifest.permission.CALL_PHONE
    };
    private RecyclerView videpRecycler;
    private ArrayList<VideoModels> videoModelsArrayList;
    private ListVideoAdapter videoAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private AppCompatTextView tvMessage, tvCallUs, tvShare, tvReview, tvCancel;

    //product
    private RecyclerView parentProduct;
    private ArrayList<ProductModel> productList;
    private ProductAdapter productAdapter;

    private String key = "";
    private boolean isChatHistoryAvailable = true;
    private long authar_timestamp = 0;
    private String author_avatar = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_details_layout);
        if (getIntent().getStringExtra("from_page").equals(Common.FROM_LIST_PAGE)) {
            if (!getIntent().getStringExtra("id").equals("")) {
                context = this;
                bindActivity();
                listID = getIntent().getStringExtra("id");
                Log.d(TAG, "ID: " + listID);
                getDetails(listID, appPrefs.getString("preferredLanguage", ""),
                        appPrefs.getString("userID", ""));
                //getDetails(listID, "English", appPrefs.getString("userID", ""));
                clickEvent();
            } else {
                Toast.makeText(context, "No details page available", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showData() {
        if (!listingDetailsData.openingHourButtonColor.equals("")) {
            if (Common.isNowOpen) {
                tvOpenStatus.setText("Open");
                tvOpenStatus.setTextColor(getResources().getColor(R.color.MediumSeaGreen));
                tvOpenStatus.setBackground(getResources().getDrawable(R.drawable.box_shape_open));
            } else {
                tvOpenStatus.setText("Close");
                tvOpenStatus.setTextColor(getResources().getColor(R.color.red_main_theme));
                tvOpenStatus.setBackground(getResources().getDrawable(R.drawable.box_shape_closed));
            }
        }
    }

    private void clickEvent() {
        btnYourReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveReviews();
//                Intent next = new Intent(ListingDetails2.this, YourReviewActivity.class);
//                next.putExtra("post_id", getIntent().getStringExtra("id"));
//                //next.putExtra("post_id", "19");
//                startActivityForResult(next, REQUEST_PAGE);
            }
        });
        menusOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
                //Toast.makeText(context, "Work in progress ", Toast.LENGTH_SHORT).show();
            }
        });
        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalling();
            }
        });
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example.yahoo.com"});
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{listingDetailsData.email});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback");
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "There are no email client installed on your device.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showBottomDialog() {
        //if (bottomSheetDialog==null){

        bottomSheetDialog = new BottomSheetDialog(ListingDetailsActivity.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bootom_sheet_dialog,
                null);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        tvMessage = view.findViewById(R.id.lblMeaasage);
        tvCallUs = view.findViewById(R.id.lblCallUs);
        tvShare = view.findViewById(R.id.lblShare);
        tvReview = view.findViewById(R.id.lblReview);
        tvCancel = view.findViewById(R.id.lblCancel);
        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMessage();
                bottomSheetDialog.dismiss();
            }
        });
        tvCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalling();
                bottomSheetDialog.dismiss();
            }
        });
        tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveReviews();
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
        if (!listingDetailsData.website.equals("")) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
            String app_url = listingDetailsData.website;
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else {
            Toast.makeText(context, "Link not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void giveReviews() {
        Intent next = new Intent(ListingDetailsActivity.this, YourReviewActivity.class);
        next.putExtra("post_id", getIntent().getStringExtra("id"));
        //next.putExtra("post_id", "19");
        startActivityForResult(next, REQUEST_PAGE);
    }

    private void startCalling() {
        if (!listingDetailsData.phone.trim().equals("")) {
            if (checkPermission()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + listingDetailsData.phone.trim()));
                startActivity(callIntent);
                Log.d(TAG, "makePhoneCall: " + listingDetailsData.phone.trim());
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

    private void startMessage() {
        Toast.makeText(context, "Work in Progress", Toast.LENGTH_SHORT).show();
    }

    private void getDetails(String listID, String preferredLanguage, String userID) {
        dialog.show();
        String url = Common.BASE_URL + "listing-details";
        AndroidNetworking.post(url)
                //.addBodyParameter("listingId", "5323")
                .addBodyParameter("listingId", listID)
                .addBodyParameter("language", preferredLanguage)
                // .addBodyParameter("userId", "318")
                .addBodyParameter("userId", userID)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseListResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseListResponse(JSONObject response) {
        dialog.dismiss();
        try {
            JSONObject data = null;
            JSONObject results = null;
            if (response.getString("status").equals("Success")) {
                data = response.getJSONObject("data");
                results = data.getJSONObject("results");
                String id = results.getString("ID");
                String user_id = results.getString("userId");
                String post_title = results.getString("postTitle");
                String tag_line = results.getString("tagLine");
                String home_title = results.getString("homeTitle");
                if (!id.equals("")) {
                    listingDetailsData.listingId = id;
                }
                if (!user_id.equals("")) {
                    listingDetailsData.listingUserId = user_id;
                }
                if (!post_title.equals("")) {
                    listingDetailsData.listingPostTitle = post_title;
                } else {
                    listingDetailsData.listingPostTitle = "";
                }
                if (!tag_line.equals("")) {
                    listingDetailsData.listingTagLine = tag_line;
                    tvtagLine.setText("");
                    tvtagLine.setText(Html.fromHtml(tag_line));
                } else {
                    listingDetailsData.listingTagLine = "";
                }
                if (!home_title.equals("")) {
                    bottomHome.setVisibility(View.VISIBLE);
                    bottomHome.setText(home_title);
                    bottomHome.setTextColor(getResources().getColor(R.color.red_main_theme));
                    bottomHome.setClickable(false);
                } else {
                    bottomHome.setVisibility(View.GONE);
                    bottomHome.setClickable(true);
                }
                if (!results.isNull("descriptions")) {
                    final JSONObject jdescription = results.getJSONObject("descriptions");
                    final String descrip_title = jdescription.getString("desTitle");
                    final String descrip_body = jdescription.getString("desDesctription");
                    String str = descrip_body.replaceAll("\\<.*?\\>", "");
                    listingDetailsData.descriptionTitle = descrip_title;
                    listingDetailsData.descriptionBody = descrip_body;
                    if (!jdescription.getString("desTitle").equals("")
                            && !jdescription.getString("desDesctription").equals("")) {
                        cardDesc.setVisibility(View.VISIBLE);
                        bottomDesc.setVisibility(View.VISIBLE);
                        bottomDesc.setText(listingDetailsData.descriptionTitle);
                        showDescriptionCard(listingDetailsData.descriptionTitle, listingDetailsData.descriptionBody);
                    } else {
                        cardDesc.setVisibility(View.GONE);
                    }
                }
                String logo = results.getString("logo");
                String post_url = results.getString("postUrl");
                String coverImg = results.getString("coverImg");
                String time_zone = results.getString("timezone");
                String phoneNumber = results.getString("phone");
                String email = results.getString("email");
                String website = results.getString("website");
                listingDetailsData.phone = phoneNumber;
                listingDetailsData.email = email;
                listingDetailsData.website = website;
                //listingDetailsData.website = "http://WWW.VIDAA.DK";
                if (!listingDetailsData.phone.equals("")) {
                    lblPhone.setVisibility(View.VISIBLE);
                    doPhone.setVisibility(View.VISIBLE);
                } else {
                    lblPhone.setVisibility(View.GONE);
                    doPhone.setVisibility(View.GONE);
                }
                if (!listingDetailsData.website.equals("")) {
                    lblShare.setVisibility(View.VISIBLE);
                    doShare.setVisibility(View.VISIBLE);
                } else {
                    lblShare.setVisibility(View.GONE);
                    doShare.setVisibility(View.GONE);
                }

                //map title
                if (!results.isNull("address")) {
                    JSONObject jaddress = results.getJSONObject("address");
                    String map_title = jaddress.getString("mapTitle");
                    String address = jaddress.getString("address");
                    String latitude = jaddress.getString("lat");
                    String lag = jaddress.getString("lag");
                    try {
                        lat = Double.parseDouble(latitude);
                        lng = Double.parseDouble(lag);
                    } catch (Exception e) {
                        Log.e(TAG, String.valueOf(e));
                    }
                    String google_map_url = jaddress.getString("googleMapUrl");
                    listingDetailsData.mapTitle = map_title;
                    listingDetailsData.mapAddress = address;
                    listingDetailsData.mapLat = latitude;
                    listingDetailsData.mapLong = lag;
                    listingDetailsData.googleMapUrl = google_map_url;
                    if (!listingDetailsData.mapTitle.equals("")) {
                        cardInfo.setVisibility(View.VISIBLE);
                        listingDetailsData.isCardInfo = true;
                        showInfoCard(listingDetailsData.mapTitle, listingDetailsData.mapAddress, listingDetailsData.mapLat, listingDetailsData.mapLong, listingDetailsData.googleMapUrl,
                                listingDetailsData.email, listingDetailsData.phone, listingDetailsData.website);
                    } else {
                        cardInfo.setVisibility(View.GONE);
                        listingDetailsData.isCardInfo = true;
                    }
                }
                //feature img
                if (!results.isNull("featuredImg")) {
                    JSONObject jfeatured_img = results.getJSONObject("featuredImg");
                    String large_img = jfeatured_img.getString("large");
                    String medium_img = jfeatured_img.getString("medium");
                    String thumbnail_img = jfeatured_img.getString("thumbnail");
                }
                if (!results.getString("hourMode").equals("")) {
                    String hour_mode = results.getString("hourMode");
                    if (hour_mode.equals("no_hours_available")) {
                        cardTiming.setVisibility(View.GONE);
                        listingDetailsData.isCardTiming = false;
                    } else if (hour_mode.equals("open_for_selected_hours")) {
                        cardTiming.setVisibility(View.VISIBLE);
                        listingDetailsData.isCardTiming = true;
                        tvTimingTitle.setText("");
                        tvTimingTitle.setText(getResources().getString(R.string.listing_details_timing));
                    } else {
                        cardTiming.setVisibility(View.GONE);
                        listingDetailsData.isCardTiming = false;
                    }
                }
                if (!results.isNull("businessHours")) {
                    JSONArray jbusinesshr = null;
                    JSONObject jobject = null;
                    String hour_button_color = "";
                    parentTimingLayout.setVisibility(View.VISIBLE);
                    hour_button_color = results.getString("hourButtonColor");
                    if (!hour_button_color.equals(""))
                        listingDetailsData.openingHourButtonColor = hour_button_color;
                    jbusinesshr = results.getJSONArray("businessHours");
                    String firstOpenHour = "", firstCloseHour = "", secondOpenHour = "", secondCloseHour = "";
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    for (int i = 0; i < jbusinesshr.length(); i++) {
                        jobject = jbusinesshr.getJSONObject(i);
                        String day_id = jobject.getString("ID");
                        String objectID = jobject.getString("objectID");
                        String dayOfWeek = jobject.getString("dayOfWeek");
                        String isOpen = jobject.getString("isOpen");
                        if (!TextUtils.isEmpty(jobject.getString("firstOpenHour"))
                                && !TextUtils.isEmpty(jobject.getString("firstCloseHour"))) {
                            firstOpenHour = jobject.getString("firstOpenHour");
                            firstCloseHour = jobject.getString("firstCloseHour");
                        }
                        /*if (jobject.getString("firstOpenHour") != null
                                && jobject.getString("firstCloseHour") != null) {
                            firstOpenHour = jobject.getString("firstOpenHour");
                            firstCloseHour = jobject.getString("firstCloseHour");

                        }*/
                        if (jobject.getString("secondOpenHour") != null && !jobject.getString("secondOpenHour").equals("")) {
                            secondOpenHour = jobject.getString("secondOpenHour");
                        }
                        if (jobject.getString("secondCloseHour") != null && !jobject.getString("secondCloseHour").equals("")) {
                            secondCloseHour = jobject.getString("secondCloseHour");
                        }
                        //String firstCloseHourUTC = jobject.getString("firstCloseHourUTC");
                        //String secondOpenHourUTC = jobject.getString("secondOpenHourUTC");
                        //String secondCloseHourUTC = jobject.getString("secondCloseHourUTC");
                        BusynessHourModel bean = new BusynessHourModel();
                        bean.setId(day_id);
                        bean.setObjectID(objectID);
                        bean.setDayOfWeek(dayOfWeek);
                        bean.setIsOpen(isOpen);
                        bean.setFirstOpenHour(firstOpenHour);
                        bean.setFirstCloseHour(firstCloseHour);
                        bean.setViewType(1);
                        busynessHrLists.add(bean);
                        busynessHourApapter.notifyDataSetChanged();
                        String today = "";
                        today = Common.dayOfWeek(day);
                        if (!dayOfWeek.equals("")) {
                            if (dayOfWeek.equalsIgnoreCase(today)) {
                                if ((!firstOpenHour.equals("") && !firstOpenHour.equals("null"))
                                        && (!firstCloseHour.equals("") && !firstCloseHour.equals("null"))) {
                                    Common.isNowOpen = Common.compareTime(firstOpenHour, firstCloseHour);
                                } else {
                                    Common.isNowOpen = false;
                                }

                            }
                        }
                        showData();
                        /*if (busynessHrLists.size() == 0) {

                        } else {
                            busynessHourApapter = new BusynessHourApapter(busynessHrLists, this);
                            parentTimingLayout.setAdapter(busynessHourApapter);
                            busynessHourApapter.notifyDataSetChanged();
                        }*/
                    }
                } else {
                    parentTimingLayout.setVisibility(View.GONE);
                }
                String price_range = results.getString("priceRange");
                String claim_status = results.getString("claimStatus");
                String publish_date = results.getString("publishDate");
                String current_date = results.getString("currentDate");
                String people_interested = results.getString("peopleInterested");
                String byUser = results.getString("byUser");

                //gallery card
                if (!results.isNull("gallery")) {
                    String gallery_tittle = "";
                    JSONObject jgallery = results.getJSONObject("gallery");
                    gallery_tittle = jgallery.getString("galTitle");
                    listingDetailsData.gallaryTitle = gallery_tittle;
                    if (!jgallery.isNull("galList")) {
                        if (!listingDetailsData.gallaryTitle.equals("")) {
                            cardGallary.setVisibility(View.VISIBLE);
                            bottomGallary.setVisibility(View.VISIBLE);
                            tvGallaryTitle.setText(listingDetailsData.gallaryTitle);
                            bottomGallary.setText(listingDetailsData.gallaryTitle);
                            listingDetailsData.isCardGallary = true;
                            JSONArray jgallerList = jgallery.getJSONArray("galList");
                            //for (int i = 0; i < arrayTestList.size(); i++) {
                            for (int i = 0; i < jgallerList.length(); i++) {
                                //String galler_img = (String) arrayTestList.get(i);
                                String galler_img = jgallerList.getString(i);
                                GallaryModel gallaryModel = new GallaryModel();
                                gallaryModel.setImgUrl(galler_img);
                                gallaryModel.setViewType(4);
                                galleryList.add(gallaryModel);
                                listingDetailsData.gallaryModelArrayList.add(gallaryModel);
                                if (galleryList.size() == 0) {

                                } else {
                                    galleryAdapter.notifyDataSetChanged();
                                }

                            }
                            tvGallaryViewall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openGallary();
                                    showGalleryCard(listingDetailsData.gallaryModelArrayList, listingDetailsData.gallaryTitle);
                                }
                            });

                        } else {
                            cardGallary.setVisibility(View.GONE);
                            bottomGallary.setVisibility(View.GONE);
                            bottomGallary.setText("");
                            listingDetailsData.isCardGallary = false;
                        }
                    }
                }
                //user review details
                String review_title = "", mode = "", quality = "", average = "";
                if (!results.isNull("userReviewDetail")) {
                    JSONObject jreviewDetails = results.getJSONObject("userReviewDetail");
                    review_title = jreviewDetails.getString("reviewTitle");
                    mode = jreviewDetails.getString("mode");
                    average = jreviewDetails.getString("average");
                    String totalScore = jreviewDetails.getString("totalScore");
                    quality = jreviewDetails.getString("quality");
                    listingDetailsData.yourReviewTitle = review_title;
                    if (!review_title.equals("")) {
                        //btnYourReview.setVisibility(View.VISIBLE);
                        bottomReviewBtn.setVisibility(View.VISIBLE);
                        //btnYourReview.setText(listingDetailsData.yourReviewTitle);
                        bottomReviewBtn.setText(listingDetailsData.yourReviewTitle);
                        listingDetailsData.isYourReview = true;

                    } else {
                        //btnYourReview.setVisibility(View.GONE);
                        bottomReviewBtn.setVisibility(View.GONE);
                        //btnYourReview.setText("");
                        listingDetailsData.isYourReview = false;
                    }
                    if (!mode.equals(""))
                        listingDetailsData.mode = mode;
                    if (!quality.equals(""))
                        listingDetailsData.quality = quality;
                    if (!average.equals(""))
                        listingDetailsData.average = average;
                }
                //avrg review category
                if (!results.isNull("averageReviewCategory")) {
                    cardAverageRating.setVisibility(View.VISIBLE);
                    listingDetailsData.isCardAverageRating = true;
                    JSONObject javrgReviewCategory = results.getJSONObject("averageReviewCategory");
                    String ac_title = javrgReviewCategory.getString("acTitle");
                    JSONObject jacLocetion = javrgReviewCategory.getJSONObject("acLocation");
                    JSONObject jacQuality = javrgReviewCategory.getJSONObject("acQuality");
                    JSONObject jacGeneral = javrgReviewCategory.getJSONObject("acGeneral");
                    JSONObject jacService = javrgReviewCategory.getJSONObject("acService");
                    String aclTitle = jacLocetion.getString("aclTitle");
                    String aclValue = jacLocetion.getString("aclValue");
                    String acqTitle = jacQuality.getString("acqTitle");
                    String acqValue = jacQuality.getString("acqValue");
                    String acgTitle = jacGeneral.getString("acgTitle");
                    String acgValue = jacGeneral.getString("acgValue");
                    String acsTitle = jacService.getString("acsTitle");
                    String acsValue = jacService.getString("acsValue");
                    listingDetailsData.acTitile = ac_title;
                    listingDetailsData.aclTitile = aclTitle;
                    listingDetailsData.aclValue = aclValue;
                    listingDetailsData.acqTitile = acqTitle;
                    listingDetailsData.acqValue = acqValue;
                    listingDetailsData.acgTitile = acgTitle;
                    listingDetailsData.acgValue = acgValue;
                    listingDetailsData.acsTitile = acsTitle;
                    listingDetailsData.acsValue = acsValue;
                    tvUserQuality.setText(listingDetailsData.quality);
                    tvUserMood.setText(" / " + listingDetailsData.mode);
                    tvUserAvg.setText(listingDetailsData.average);


                    showAverageReviewCard(listingDetailsData.acTitile, listingDetailsData.aclTitile, listingDetailsData.aclValue,
                            listingDetailsData.acqTitile, listingDetailsData.acqValue, listingDetailsData.acgTitile, listingDetailsData.acgValue,
                            listingDetailsData.acsTitile, listingDetailsData.acsValue);


                } else {
                    cardAverageRating.setVisibility(View.GONE);
                    listingDetailsData.isCardAverageRating = false;
                }
                //author info
                if (!results.isNull("authorInfo")) {
                    JSONObject jauthorInfo = results.getJSONObject("authorInfo");
                    listingDetailsData.authorID = jauthorInfo.getString("userId");
                    listingDetailsData.authorDisplayName = jauthorInfo.getString("displayName");
                }
                //author favoriteInfo
                if (!results.isNull("favoriteInfo")) {
                    JSONObject jfavoriteInfo = results.getJSONObject("favoriteInfo");
                    String total_fav = jfavoriteInfo.getString("totalFavorites");
                    String test = jfavoriteInfo.getString("test");
                }
                //productInfo
                if (!results.isNull("productInfo")) {
                    JSONObject jproductInfo = results.getJSONObject("productInfo");
                    String productTitle = jproductInfo.getString("productTitle");
                    listingDetailsData.productTitle = productTitle;
                    if (!jproductInfo.isNull("productLists")) {
                        if (!listingDetailsData.productTitle.equals("")) {
                            cardProduct.setVisibility(View.VISIBLE);
                            listingDetailsData.isCardProduct = true;
                            bottomProduct.setVisibility(View.VISIBLE);
                            tvProductTitle.setText(listingDetailsData.productTitle);
                            bottomProduct.setText(listingDetailsData.productTitle);
                            JSONArray jproductList = jproductInfo.getJSONArray("productLists");
                            productList.clear();
                            for (int i = 0; i < jproductList.length(); i++) {
                                JSONObject jProducts = jproductList.getJSONObject(i);
                                String product_name = jProducts.getString("productName");
                                String productCat = jProducts.getString("productCat");
                                String productCur = jProducts.getString("productCur");
                                String productPrice = jProducts.getString("productPrice");
                                String productUrl = jProducts.getString("productUrl");
                                String productImg = jProducts.getString("productImg");
                                System.out.print(productImg);
                                ProductModel bean = new ProductModel();
                                bean.setProductName(jProducts.getString("productName"));
                                bean.setProductImg(jProducts.getString("productImg"));
                                bean.setProductCur(jProducts.getString("productCur"));
                                bean.setProductPrice(jProducts.getString("productPrice"));
                                bean.setProductCat(jProducts.getString("productCat"));
                                bean.setViewType(1);
                                productList.add(bean);
                                if (productList.size() > 0) {
                                    productAdapter.notifyDataSetChanged();
                                }


                                /*listingDetailsData.productName = product_name;
                                listingDetailsData.productCat = productCat;
                                listingDetailsData.productCur = productCur;
                                listingDetailsData.productPrice = productPrice;
                                listingDetailsData.productUrl = productUrl;
                                listingDetailsData.productImg = productImg;
                                showProductCard(listingDetailsData.productName, listingDetailsData.productCat, listingDetailsData.productCur,
                                        listingDetailsData.productPrice, listingDetailsData.productUrl, listingDetailsData.productImg);*/
                            }
                        } else {
                            cardProduct.setVisibility(View.GONE);
                            listingDetailsData.isCardProduct = false;
                            bottomProduct.setVisibility(View.GONE);
                            tvProductTitle.setText("");
                            bottomProduct.setText("");
                        }
                    }
                }
                //restaurantInfo
                if (!results.isNull("restaurantInfo")) {
                    JSONObject jrestaurantInfo = results.getJSONObject("restaurantInfo");
                    String restaurant_title = jrestaurantInfo.getString("restaurantTitle");
                    String restaurnat_menu_desc = jrestaurantInfo.getString("restaurnatMenuDesc");
                    String restaurnat_menu_title = jrestaurantInfo.getString("restaurnatMenuTitle");
                    if (jrestaurantInfo.isNull("restaurnatMenuList")) {
                        JSONArray jMenuList = jrestaurantInfo.getJSONArray("restaurnatMenuList");
                        for (int i = 0; i < jMenuList.length(); i++) {
                            JSONObject jmenus = jMenuList.getJSONObject(i);
                            String menu_title = jmenus.getString("menuTitle");
                            String menu_desc = jmenus.getString("menuDesc");
                            String menu_price = jmenus.getString("menuPrice");

                        }
                    }
                }
                //social network info
                if (!results.isNull("socialNetworkInfo")) {
                    JSONObject jSocialNetwork = results.getJSONObject("socialNetworkInfo");
                    String social_title = jSocialNetwork.getString("socialTitle");
                    if (!jSocialNetwork.isNull("socialNetworkList")) {
                        JSONObject jSocialList = jSocialNetwork.getJSONObject("socialNetworkList");
                        String facebook = jSocialList.getString("facebook");
                        String twitter = jSocialList.getString("twitter");
                        String google_plus = jSocialList.getString("google-plus");
                        String tumblr = jSocialList.getString("tumblr");
                        String vk = jSocialList.getString("vk");
                        String youtube = jSocialList.getString("youtube");
                        String instagram = jSocialList.getString("instagram");
                        String pinterest = jSocialList.getString("pinterest");
                        String medium = jSocialList.getString("medium");
                        String wikipedia = jSocialList.getString("wikipedia");
                        String linkedin = jSocialList.getString("linkedin");
                        String skype = jSocialList.getString("skype");
                        String bloglovin = jSocialList.getString("bloglovin");
                        String whatsapp = jSocialList.getString("whatsapp");
                        //String line = jSocialList.getString("line");
                        //String spotify = jSocialList.getString("spotify");
                        if (!TextUtils.isEmpty(facebook)) {
                            faceBook.setVisibility(View.VISIBLE);
                        } else {
                            faceBook.setVisibility(View.GONE);
                        }
                    }
                }
                //videoInfo
                if (!results.isNull("videoInfo")) {
                    JSONObject jvideoInfo = results.getJSONObject("videoInfo");
                    String video_title = jvideoInfo.getString("videoTitle");
                    if (!jvideoInfo.isNull("videoList")) {
                        listingDetailsData.videoTitle = video_title;
                        if (!listingDetailsData.videoTitle.equals("")) {
                            cardVideo.setVisibility(View.VISIBLE);
                            listingDetailsData.isVideoCard = true;
                            tvVideoTitle.setText(listingDetailsData.videoTitle);
                        }
                        JSONArray jvideoList = jvideoInfo.getJSONArray("videoList");
                        for (int i = 0; i < jvideoList.length(); i++) {
                            JSONObject jmenus = jvideoList.getJSONObject(i);
                            String thumnail_video_img = jmenus.getString("thumbnailImage");
                            String video_url = jmenus.getString("videoUrl");
                            VideoModels videoModels = new VideoModels();
                            videoModels.setVideoThumnail(thumnail_video_img);
                            videoModels.setVideoUrl(video_url);
                            videoModels.setViewType(Common.LIST_VIDEO);
                            videoModelsArrayList.add(videoModels);
                            if (videoModelsArrayList.size() == 0) {
                                //Log.e(TAG, "parseResponse: size 0");
                                //noData();
                                //no_data.setVisibility(View.VISIBLE);
                                //swipeRefreshLayout.setRefreshing(false);
                                //Glide.with(this).load(R.drawable.no_data).into(no_data);

                            } else {
                                videoAdapter.notifyDataSetChanged();
                                //no_data.setVisibility(View.GONE);
                                //swipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    } else {
                        cardVideo.setVisibility(View.GONE);
                        listingDetailsData.isVideoCard = false;

                    }
                }
                //review rating info
                if (!results.isNull("reviewRatingInfo")) {
                    JSONObject jreviewRatingInfo = results.getJSONObject("reviewRatingInfo");
                    String review_heading = jreviewRatingInfo.getString("reviewHeading");
                    listingDetailsData.reviewHeading = review_heading;
                    if (!jreviewRatingInfo.isNull("reviewRatingDetails")) {
                        if (!listingDetailsData.reviewHeading.equals("")) {
                            cardReviewRating.setVisibility(View.VISIBLE);
                            listingDetailsData.isCardReviewRating = true;
                            reviewRatingHeading.setText(listingDetailsData.reviewHeading);
                            discussionRecyler.setVisibility(View.VISIBLE);
                            listingDetailsData.isReviewRatingDetails = true;
                            JSONArray reviewRatingDetails = null;
                            reviewRatingDetails = jreviewRatingInfo.getJSONArray("reviewRatingDetails");
                            for (int i = 0; i < reviewRatingDetails.length(); i++) {
                                JSONObject jreviews = reviewRatingDetails.getJSONObject(i);
                                String reviewId = jreviews.getString("reviewId");
                                String authorName = jreviews.getString("authorName");
                                String userId = jreviews.getString("userId");
                                String reviewDate = jreviews.getString("reviewDate");
                                String reviewTitle = jreviews.getString("reviewTitle");
                                String reviewDesc = jreviews.getString("reviewDesc");
                                String overallRatingText = jreviews.getString("overallRatingText");
                                String overallReviewRateFrom = jreviews.getString("overallReviewRateFrom");
                                String overallReviewRate = jreviews.getString("overallReviewRate");
                                String totalLikes = jreviews.getString("totalLikes");
                                String countComment = jreviews.getString("countComment");
                                boolean myLike = jreviews.getBoolean("myLike");
                                DiscussionModel bean = new DiscussionModel();
                                bean.setReviewId(reviewId);
                                bean.setAuthorName(authorName);
                                bean.setUserId(userId);
                                bean.setReviewDate(reviewDate);
                                bean.setReviewtitle(reviewTitle);
                                bean.setReviewDesc(reviewDesc);
                                bean.setOverallReviewRate(overallReviewRate);
                                bean.setOverallReviewRateFrom(overallReviewRateFrom);
                                bean.setOverallRatingText(overallRatingText);
                                bean.setTotal_likes(totalLikes);
                                bean.setCount_comments(countComment);
                                bean.setMy_like(myLike);
                                bean.setViewType(Common.LISTING_DISCUSION);
                                discussionsList.add(bean);
                                if (discussionsList.size() == 0) {
                                    //Log.e(TAG, "parseResponse: size 0");
                                    //noData();
                                    //no_data.setVisibility(View.VISIBLE);
                                    //swipeRefreshLayout.setRefreshing(false);
                                    //Glide.with(this).load(R.drawable.no_data).into(no_data);

                                } else {
                                    adapter.notifyDataSetChanged();
                                    //no_data.setVisibility(View.GONE);
                                    //swipeRefreshLayout.setRefreshing(false);

                                }
                            }
                        } else {
                            cardReviewRating.setVisibility(View.GONE);
                            discussionRecyler.setVisibility(View.GONE);
                            listingDetailsData.isCardReviewRating = true;
                            listingDetailsData.isReviewRatingDetails = true;
                            reviewRatingHeading.setText("");
                        }
                    }
                }
                //serveice object
                if (!results.isNull("serviceObject")) {
                    JSONObject jserviceObject = results.getJSONObject("serviceObject");
                    String catTitle = jserviceObject.getString("catTitle");
                    listingDetailsData.serviceTitle = catTitle;
                    if (!jserviceObject.isNull("catList")) {
                        if (!listingDetailsData.serviceTitle.equals("")) {
                            cardService.setVisibility(View.VISIBLE);
                            bottomService.setVisibility(View.VISIBLE);
                            listingDetailsData.isCardService = true;
                            tvServiceTitle.setText(listingDetailsData.serviceTitle);
                            bottomService.setText(listingDetailsData.serviceTitle);
                            JSONArray jCatList = jserviceObject.getJSONArray("catList");
                            for (int i = 0; i < jCatList.length(); i++) {
                                ServiceModel serviceModel = new ServiceModel();
                                JSONObject jreviews = jCatList.getJSONObject(i);
                                String serviceID = jreviews.getString("serviceId");
                                String serviceName = jreviews.getString("serviceName");
                                String serviceIcon = jreviews.getString("serviceIcon");
                                if (!jreviews.getString("serviceId").equals("")) {
                                    serviceModel.setServiceID(jreviews.getString("serviceId"));
                                }
                                if (!jreviews.getString("serviceName").equals("")) {
                                    serviceModel.setServiceName(jreviews.getString("serviceName"));
                                }
                                if (!jreviews.getString("serviceIcon").equals("")) {
                                    serviceModel.setServiceIcon(jreviews.getString("serviceIcon"));
                                }
                                serviceModel.setViewType(SERVICE);
                                serviceList.add(serviceModel);
                                listingDetailsData.serviceModelArrayList.add(serviceModel);
                                if (serviceList.size() == 0) {
//                                serviceList.clear();
//                                ServiceModel serviceModel1=new ServiceModel();
//                                serviceAdapter.notifyDataSetChanged();

                                } else {
                                    serviceAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            cardService.setVisibility(View.GONE);
                            bottomService.setVisibility(View.GONE);
                            listingDetailsData.isCardService = false;
                            tvServiceTitle.setText("");
                            bottomService.setText("");
                        }
                    }
                }
                //term info
                if (!results.isNull("termInfo")) {
                    JSONObject jtermInfoObject = results.getJSONObject("termInfo");
                    String termInfocatTitle = jtermInfoObject.getString("catTitle");
                    listingDetailsData.termInfoTitle = termInfocatTitle;

                    if (!jtermInfoObject.isNull("catList")) {
                        if (!listingDetailsData.termInfoTitle.equals("")) {
                            cardTermInfo.setVisibility(View.VISIBLE);
                            listingDetailsData.isCardTermInfo = true;
                            tvTerminfoTitle.setText(listingDetailsData.termInfoTitle);
                            JSONArray jtermInfoList = jtermInfoObject.getJSONArray("catList");
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
                }
                //stat
                if (!results.isNull("statDetails")) {
                    //stat details
                    cardStatDetails.setVisibility(View.VISIBLE);
                    listingDetailsData.isCardStatDetails = true;
                    JSONObject jstatDetails = results.getJSONObject("statDetails");
                    String statTitle = jstatDetails.getString("statTitle");
                    String stat_total_views = jstatDetails.getString("totalViews");
                    String stat_total_reviews = jstatDetails.getString("totalReviews");
                    String stat_total_favorite = jstatDetails.getString("totalFavorite");
                    listingDetailsData.statTitle = statTitle;
                    listingDetailsData.statTotalView = stat_total_views;
                    listingDetailsData.statTotalReview = stat_total_reviews;
                    listingDetailsData.statTotalFavourite = stat_total_favorite;
                    showReviewCard(listingDetailsData.statTitle, listingDetailsData.statTotalView, listingDetailsData.statTotalReview, listingDetailsData.statTotalFavourite);
                } else {
                    cardStatDetails.setVisibility(View.GONE);
                    listingDetailsData.isCardStatDetails = false;
                }
                if (!results.isNull("myFavourite")) {
                    boolean my_fav = results.getBoolean("myFavourite");
                    if (my_fav) {
                        listingDetailsData.myFav = true;
                        doFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    } else {
                        listingDetailsData.myFav = false;
                        doFavourite.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    }
                }
                //button section
                if (!results.isNull("oButtonInfo")) {
                    JSONObject jButton = results.getJSONObject("oButtonInfo");
                    String buttonTitle = jButton.getString("buttonTitle");
                    String buttonLink = jButton.getString("buttonLink");
                    listingDetailsData.booknowTitle = buttonTitle;
                    listingDetailsData.booknowLink = buttonLink.toLowerCase();

                    if (!listingDetailsData.booknowTitle.equals("")) {
                        btnBookNow.setVisibility(View.VISIBLE);
                        btnBookNow.setText(listingDetailsData.booknowTitle);
                        listingDetailsData.isbooknow = true;

                    } else {
                        btnBookNow.setVisibility(View.GONE);
                        listingDetailsData.isbooknow = false;
                    }
                }
                Toast.makeText(context, "Complete details", Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showReviewCard(String statTitle, String statTotalView, String statTotalReview, String statTotalFavourite) {
        if (!statTitle.equals("")) {
            tvStatTitle.setText(statTitle);
        }
        if (!statTotalView.equals("")) {
            tvStatView.setText(statTotalView);
        }
        if (!statTotalReview.equals("")) {
            tvStatReview.setText(statTotalReview);
        }
        if (!statTotalFavourite.equals("")) {
            tvStatFav.setText(statTotalFavourite);
        }

    }

    private void showAverageReviewCard(String acTitile, String aclTitile, String aclValue, String acqTitile, String acqValue, String acgTitile, String acgValue, String acsTitile, String acsValue) {
        if (!acTitile.equals("")) {
            tvAvrgRatingTitle.setText(acTitile);
        }
        if (!aclTitile.equals("") && !aclValue.equals("")) {
            tvLocation.setText(aclTitile);
            tvLocationValue.setText(aclValue);
        }
        if (!acqTitile.equals("") && !acqValue.equals("")) {
            tvQuality.setText(acqTitile);
            tvQualityValue.setText(acqValue);
        }
        if (!acgTitile.equals("") && !acgValue.equals("")) {
            tvInGeneral.setText(acgTitile);
            tvInGeneralValue.setText(acgValue);
        }
        if (!acsTitile.equals("") && !acsValue.equals("")) {
            tvSrvice.setText(acsTitile);
            tvServiceValue.setText(acsValue);
        }
    }

    private void showProductCard(String productName, String productCat, String productCur,
                                 String productPrice, String productUrl, String productImg) {
        View view = inflater.inflate(R.layout.item_product, parentProduct,
                false);
        ImageView img = view.findViewById(R.id.productImg);
        TextView txt_productName = view.findViewById(R.id.tv_productname);
        TextView txt_productPrice = view.findViewById(R.id.tv_productPrice);
        TextView txt_productCat = view.findViewById(R.id.tv_productCat);

        if (!productImg.equals("")) {
            Glide.with(context)
                    .load(productImg)
                    .thumbnail(0.5f)
                    .into(img);
        }
        if (!productName.equals("")) {
            txt_productName.setText(productName);
        }
        if (!productPrice.equals("")
                && !productCur.equals("")) {
            txt_productPrice.setText(productCur + productPrice);
        }
        if (!productCat.equals("")) {
            txt_productCat.setText(productCat);
        }
        parentProduct.addView(view);

    }

    private void showInfoCard(String mapTitle, String mapAddress, String mapLat, String mapLong, String googleMapUrl,
                              String email, String phone, String website) {
        if (!mapTitle.equals(""))
            tvInfoTitle.setText(mapTitle);
        if (!mapAddress.equals(""))
            tvAdd.setText(mapAddress);
        if (!email.equals("")) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(email);
        } else {
            tvEmail.setVisibility(View.GONE);
            tvEmail.setText("");
        }
        if (!website.equals("")) {
            tvWebsite.setVisibility(View.VISIBLE);
            tvWebsite.setText(website);
        } else {
            tvWebsite.setVisibility(View.GONE);
            tvWebsite.setText("");
        }
        if (!phone.equals("")) {
            tvPhone.setVisibility(View.VISIBLE);
            tvPhone.setText(phone);

        } else {
            tvPhone.setVisibility(View.GONE);
            tvPhone.setText("");
        }
        displayInMapFrag();

    }

    private void displayInMapFrag() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void showDescriptionCard(String descriptionTitle, String descriptionBody) {
        tvDescTitle.setText(Html.fromHtml(descriptionTitle));
        tvDescBody.setText(Html.fromHtml(descriptionBody));
    }

    //gallary view all
    private void showGalleryCard(ArrayList<GallaryModel> gallery_List, String gallery_tittle) {
        viewAllRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        gallary_model_list = new ArrayList<>();
        for (int i = 0; i < gallery_List.size(); i++) {
            GallaryModel gallaryModel = gallery_List.get(i);
            gallaryModel.setImgUrl(gallaryModel.getImgUrl());
            gallaryModel.setViewType(Common.ListingDetailsPageViewallImages);
            gallary_model_list.add(gallaryModel);
        }
        gallerylist_adapter = new GallaryListAdapter(gallary_model_list, this);
        viewAllRecycler.setAdapter(gallerylist_adapter);
        tvViewTitle.setText(gallery_tittle);
    }

    //open all card
    private void openHomeCard() {
        if (listingDetailsData.isCardDesc)
            if (listingDetailsData.isCardDesc)
                cardDesc.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardAverageRating)
            cardAverageRating.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardInfo)
            cardInfo.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardProduct)
            cardProduct.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardTiming)
            cardTiming.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardGallary)
            cardGallary.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardService)
            cardService.setVisibility(View.VISIBLE);
        if (listingDetailsData.isbooknow)
            btnBookNow.setVisibility(View.VISIBLE);
        //if (listingDetailsData.isYourReview)
        btnYourReview.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardReviewRating)
            cardReviewRating.setVisibility(View.VISIBLE);
        if (listingDetailsData.isReviewRatingDetails)
            discussionRecyler.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardTermInfo)
            cardTermInfo.setVisibility(View.VISIBLE);
        if (listingDetailsData.isCardStatDetails)
            cardStatDetails.setVisibility(View.VISIBLE);
        if (listingDetailsData.isVideoCard)
            cardVideo.setVisibility(View.VISIBLE);

        cardViewAllGallary.setVisibility(View.GONE);
        viewAllRecycler.setVisibility(View.GONE);
        //bottom section
        bottomHome.setClickable(false);
        bottomHome.setTextColor(getResources().getColor(R.color.red_main_theme));
        bottomDesc.setClickable(true);
        bottomDesc.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomGallary.setClickable(true);
        bottomGallary.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomProduct.setClickable(true);
        bottomProduct.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomService.setClickable(true);
        bottomService.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomReviewBtn.setClickable(true);
        bottomReviewBtn.setTextColor(getResources().getColor(R.color.title_text_color));
    }

    //open desc card
    private void openDecriptionCard() {
        cardDesc.setVisibility(View.VISIBLE);
        discussionRecyler.setVisibility(View.GONE);
        cardReviewRating.setVisibility(View.GONE);
        btnBookNow.setVisibility(View.GONE);
        btnYourReview.setVisibility(View.GONE);
        cardAverageRating.setVisibility(View.GONE);
        cardInfo.setVisibility(View.GONE);
        cardTiming.setVisibility(View.GONE);
        cardProduct.setVisibility(View.GONE);
        cardGallary.setVisibility(View.GONE);
        cardVideo.setVisibility(View.GONE);
        cardTermInfo.setVisibility(View.GONE);
        cardService.setVisibility(View.GONE);
        cardStatDetails.setVisibility(View.GONE);
        cardViewAllGallary.setVisibility(View.GONE);
        viewAllRecycler.setVisibility(View.GONE);
        //tvDescTitle.setText(descrip_title);
        //tvDescBody.setText(descripBody);
        //bottom nav section
        bottomHome.setClickable(true);
        bottomHome.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomGallary.setClickable(true);
        bottomGallary.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomProduct.setClickable(true);
        bottomProduct.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomDesc.setClickable(false);
        bottomDesc.setTextColor(getResources().getColor(R.color.red_main_theme));
        bottomService.setClickable(true);
        bottomService.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomReviewBtn.setClickable(true);
        bottomReviewBtn.setTextColor(getResources().getColor(R.color.title_text_color));
    }

    private void openGallary() {
        cardViewAllGallary.setVisibility(View.VISIBLE);
        viewAllRecycler.setVisibility(View.VISIBLE);
        discussionRecyler.setVisibility(View.GONE);
        cardReviewRating.setVisibility(View.GONE);
        btnBookNow.setVisibility(View.GONE);
        btnYourReview.setVisibility(View.GONE);
        cardAverageRating.setVisibility(View.GONE);
        cardInfo.setVisibility(View.GONE);
        cardTiming.setVisibility(View.GONE);
        cardDesc.setVisibility(View.GONE);
        cardProduct.setVisibility(View.GONE);
        cardGallary.setVisibility(View.GONE);
        cardVideo.setVisibility(View.GONE);
        cardTermInfo.setVisibility(View.GONE);
        cardService.setVisibility(View.GONE);
        cardStatDetails.setVisibility(View.GONE);
        //botttom section
        bottomGallary.setClickable(false);
        bottomGallary.setTextColor(getResources().getColor(R.color.red_main_theme));
        bottomProduct.setClickable(true);
        bottomProduct.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomDesc.setClickable(true);
        bottomDesc.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomHome.setClickable(true);
        bottomHome.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomService.setClickable(true);
        bottomService.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomReviewBtn.setClickable(true);
        bottomReviewBtn.setTextColor(getResources().getColor(R.color.title_text_color));
    }

    //open product
    private void openProduct() {
        cardProduct.setVisibility(View.VISIBLE);
        cardDesc.setVisibility(View.GONE);
        discussionRecyler.setVisibility(View.GONE);
        cardReviewRating.setVisibility(View.GONE);
        btnBookNow.setVisibility(View.GONE);
        btnYourReview.setVisibility(View.GONE);
        cardAverageRating.setVisibility(View.GONE);
        cardInfo.setVisibility(View.GONE);
        cardTiming.setVisibility(View.GONE);
        cardGallary.setVisibility(View.GONE);
        cardVideo.setVisibility(View.GONE);
        cardTermInfo.setVisibility(View.GONE);
        cardService.setVisibility(View.GONE);
        cardStatDetails.setVisibility(View.GONE);
        cardViewAllGallary.setVisibility(View.GONE);
        viewAllRecycler.setVisibility(View.GONE);

        //bottom nav section
        bottomHome.setClickable(true);
        bottomHome.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomGallary.setClickable(true);
        bottomGallary.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomProduct.setClickable(false);
        bottomProduct.setTextColor(getResources().getColor(R.color.red_main_theme));
        bottomDesc.setClickable(true);
        bottomDesc.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomService.setClickable(true);
        bottomService.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomReviewBtn.setClickable(true);
        bottomReviewBtn.setTextColor(getResources().getColor(R.color.title_text_color));

    }

    //open services
    private void openServicesCard() {
        cardService.setVisibility(View.VISIBLE);
        cardDesc.setVisibility(View.GONE);
        discussionRecyler.setVisibility(View.GONE);
        cardReviewRating.setVisibility(View.GONE);
        btnBookNow.setVisibility(View.GONE);
        btnYourReview.setVisibility(View.GONE);
        cardAverageRating.setVisibility(View.GONE);
        cardInfo.setVisibility(View.GONE);
        cardTiming.setVisibility(View.GONE);
        cardProduct.setVisibility(View.GONE);
        cardGallary.setVisibility(View.GONE);
        cardVideo.setVisibility(View.GONE);
        cardTermInfo.setVisibility(View.GONE);
        cardStatDetails.setVisibility(View.GONE);
        cardViewAllGallary.setVisibility(View.GONE);
        viewAllRecycler.setVisibility(View.GONE);
        //bottom nav section
        bottomHome.setClickable(true);
        bottomHome.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomGallary.setClickable(true);
        bottomGallary.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomProduct.setClickable(true);
        bottomProduct.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomDesc.setClickable(true);
        bottomDesc.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomService.setClickable(false);
        bottomService.setTextColor(getResources().getColor(R.color.red_main_theme));
        bottomReviewBtn.setClickable(true);
        bottomReviewBtn.setTextColor(getResources().getColor(R.color.title_text_color));

    }

    //open Reviews
    private void openReviewButton() {
        btnYourReview.setVisibility(View.VISIBLE);
        cardService.setVisibility(View.GONE);
        cardDesc.setVisibility(View.GONE);
        discussionRecyler.setVisibility(View.GONE);
        cardReviewRating.setVisibility(View.GONE);
        btnBookNow.setVisibility(View.GONE);
        cardAverageRating.setVisibility(View.GONE);
        cardInfo.setVisibility(View.GONE);
        cardTiming.setVisibility(View.GONE);
        cardProduct.setVisibility(View.GONE);
        cardGallary.setVisibility(View.GONE);
        cardVideo.setVisibility(View.GONE);
        cardTermInfo.setVisibility(View.GONE);
        cardStatDetails.setVisibility(View.GONE);
        cardViewAllGallary.setVisibility(View.GONE);
        viewAllRecycler.setVisibility(View.GONE);
        //bottom nav section
        bottomHome.setClickable(true);
        bottomHome.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomGallary.setClickable(true);
        bottomGallary.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomProduct.setClickable(true);
        bottomProduct.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomDesc.setClickable(true);
        bottomDesc.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomService.setClickable(true);
        bottomService.setTextColor(getResources().getColor(R.color.title_text_color));
        bottomReviewBtn.setClickable(false);
        bottomReviewBtn.setTextColor(getResources().getColor(R.color.red_main_theme));

    }


    private void showNoData() {
        //listingList.clear();
        ListingModel bean = new ListingModel();
        //bean.setViewType(NO_DATA);
        //listingList.add(bean);
        //adapter.notifyDataSetChanged();
    }


    private void bindActivity() {
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
        imgLogoimage = findViewById(R.id.im_logoimage);
        setSupportActionBar(toolbar);
        menusOption = findViewById(R.id.menu);
        //address/map
        cardInfo = findViewById(R.id.card_info);
        tvInfoTitle = findViewById(R.id.tv_info_title);
        tvAdd = findViewById(R.id.tv_add);
        tvEmail = findViewById(R.id.tv_email);
        tvWebsite = findViewById(R.id.tv_website);
        tvPhone = findViewById(R.id.tv_phone);
        faceBook = findViewById(R.id.fb);
        //textview
        tvpostTitle = findViewById(R.id.tv_title);
        tvtagLine = findViewById(R.id.tv_tagline);
        toolbarTitle = findViewById(R.id.txt_toolbarTitle);
        btnBookNow = (Button) findViewById(R.id.btn_booknow);
        inflater = LayoutInflater.from(context);

        //card rating
        cardAverageRating = findViewById(R.id.card_rating);
        tvAvrgRatingTitle = findViewById(R.id.tv_avrg_rating_title);
        tvTimingTitle = findViewById(R.id.tv_avrg_timing_title);
        tvLocation = findViewById(R.id.tv_location);
        tvSrvice = findViewById(R.id.tv_service);
        tvQuality = findViewById(R.id.tv_emails);
        tvInGeneral = findViewById(R.id.tv_ingeneral);
        tvLocationValue = findViewById(R.id.tv_location_value);
        tvQualityValue = findViewById(R.id.tv_quality_value);
        tvInGeneralValue = findViewById(R.id.tv_ingeneral_value);
        tvServiceValue = findViewById(R.id.tv_service_value);
        tvUserAvg = findViewById(R.id.tv_user_avg);
        tvUserMood = findViewById(R.id.tv_user_mood);
        tvUserQuality = findViewById(R.id.tv_user_quality);

        //card busynesshour
        cardTiming = findViewById(R.id.card_timing);
        tvOpenStatus = findViewById(R.id.tv_open);
        parentTimingLayout = (RecyclerView) findViewById(R.id.parent_timing_layout);
        busynessHrLists = new ArrayList<>();
        parentTimingLayout.setLayoutManager(new LinearLayoutManager(ListingDetailsActivity.this));
        busynessHourApapter = new BusynessHourApapter(busynessHrLists, this);
        parentTimingLayout.setAdapter(busynessHourApapter);

        /*busynessHourApapter = new BusynessHourApapter(busynessHrLists, this);
        parentTimingLayout.setAdapter(busynessHourApapter);*/

        //card desc
        cardDesc = findViewById(R.id.card_desc);
        tvDescViewall = findViewById(R.id.tv_desc_viewall);
        tvDescTitle = findViewById(R.id.tv_desc_title);
        tvDescBody = findViewById(R.id.tv_desc_body);
        //card Gallary
        cardGallary = findViewById(R.id.card_gallery);
        galleryRecycle = findViewById(R.id.parent_gallery);
        galleryRecycle.setLayoutManager(new GridLayoutManager(this, 3));
        galleryList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(galleryList, this);
        galleryRecycle.setAdapter(galleryAdapter);
        tvGallaryViewall = findViewById(R.id.tv_gallary_viewall);
        tvGallaryTitle = findViewById(R.id.tv_gallary_title);

        //card Video
        cardVideo = findViewById(R.id.card_video);
        tvVideoTitle = findViewById(R.id.tv_video_title);
        videpRecycler = findViewById(R.id.parent_video);
        videoModelsArrayList = new ArrayList<>();
        videpRecycler.setLayoutManager(new LinearLayoutManager(ListingDetailsActivity.this));
        videoAdapter = new ListVideoAdapter(videoModelsArrayList, this);
        videpRecycler.setAdapter(videoAdapter);


        //Card product
        cardProduct = findViewById(R.id.card_product);
        tvProductTitle = findViewById(R.id.tv_product_title);


        //parentProduct = (LinearLayout) findViewById(R.id.parent_product);
        parentProduct = (RecyclerView) findViewById(R.id.parent_product);
        parentProduct.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        parentProduct.setAdapter(productAdapter);

        //card service
        cardService = findViewById(R.id.card_service);
        serviceRecycle = findViewById(R.id.parent_service);
        serviceRecycle.setLayoutManager(new GridLayoutManager(ListingDetailsActivity.this, 2));
        serviceList = new ArrayList<>();
        serviceAdapter = new ListServiceAdapter(serviceList, this);
        serviceRecycle.setAdapter(serviceAdapter);
        tvServiceTitle = findViewById(R.id.tv_service_title);

        //card terminfo
        cardTermInfo = findViewById(R.id.card_terminfo);
        terminfoRecycle = findViewById(R.id.parent_terminfo);
        terminfoRecycle.setLayoutManager(new GridLayoutManager(ListingDetailsActivity.this, 2));
        termList = new ArrayList<>();
        termInfoAdapter = new ListTermInfoAdapter(termList, this);
        terminfoRecycle.setAdapter(termInfoAdapter);
        tvTerminfoTitle = findViewById(R.id.tv_terminfo_title);


        //card stat
        cardStatDetails = findViewById(R.id.card_statDetails);
        tvStatTitle = findViewById(R.id.tv_stat_title);
        tvStatView = findViewById(R.id.tv_stat_view);
        tvStatReview = findViewById(R.id.tv_stat_review);
        tvStatFav = findViewById(R.id.tv_stat_fav);
        //reviewRating
        cardReviewRating = (CardView) findViewById(R.id.card_ReviewRating);
        reviewRatingHeading = findViewById(R.id.tv_review_title);
        discussionRecyler = (RecyclerView) findViewById(R.id.parent_review_list);
        discussionRecyler.setLayoutManager(new LinearLayoutManager(this));
        discussionsList = new ArrayList<>();
        adapter = new DiscussionAdapter(discussionsList, this);
        discussionRecyler.setAdapter(adapter);

        //your review button
        btnYourReview = findViewById(R.id.btn_your_review);

        //viewAll
        cardViewAllGallary = findViewById(R.id.card_viewAll);
        viewAllRecycler = findViewById(R.id.viewAllRecycler);
        tvViewTitle = findViewById(R.id.tv_view_title);

        //botton navigation
        bottomHome = findViewById(R.id.bottomHome);
        bottomDesc = findViewById(R.id.bottomDesc);
        bottomGallary = findViewById(R.id.bottomGallary);
        bottomProduct = findViewById(R.id.bottomProduct);
        bottomService = findViewById(R.id.bottomService);
        bottomReviewBtn = findViewById(R.id.bottomReview);

        //Images
        doPhone = findViewById(R.id.im_phone);
        lblPhone = findViewById(R.id.lbl_phone);
        doMessage = findViewById(R.id.im_message);
        lblMessage = findViewById(R.id.lbl_message);

        doShare = findViewById(R.id.im_share);
        lblShare = findViewById(R.id.lbl_share);
        doFavourite = findViewById(R.id.im_favourite);

        //video card


        arrayTestList = new ArrayList();
        arrayTestList.add("https://www.romo-tonder.dk/wp-content/uploads/2019/09/51786720_2228604877195848_725330104103731200_n.jpg");
        arrayTestList.add("https://www.romo-tonder.dk/wp-content/uploads/2019/09/51710778_2228604827195853_3770552201475260416_n.jpg");
        arrayTestList.add("https://www.romo-tonder.dk/wp-content/uploads/2019/09/66294671_2471549892901344_2556139894904717312_n.jpg");
        arrayTestList.add("https://www.romo-tonder.dk/wp-content/uploads/2019/09/51786720_2228604877195848_725330104103731200_n.jpg");
        arrayTestList.add("https://www.romo-tonder.dk/wp-content/uploads/2019/09/51710778_2228604827195853_3770552201475260416_n.jpg");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarTitle.setText(Html.fromHtml(getIntent().getStringExtra("post_title")));
                    isShow = true;
                } else if (isShow) {
                    toolbarTitle.setText("");
                    isShow = false;
                }
            }
        });
        if (!getIntent().getStringExtra("post_title").equals("")) {
            tvpostTitle.setText(Html.fromHtml(getIntent().getStringExtra("post_title")));
        }
        if (!getIntent().getStringExtra("tag_line").equals("")) {
            tvtagLine.setText(Html.fromHtml(getIntent().getStringExtra("tag_line")));
        } else {
            tvtagLine.setText("");
        }
        if (!getIntent().getStringExtra("cover_image").equals("")) {
            Glide.with(context)
                    .load(getIntent().getStringExtra("cover_image"))
                    .thumbnail(0.5f)
                    .into(imgCover);
        } else {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(imgCover);
        }
        if (!getIntent().getStringExtra("logo_image").equals("")) {
            Glide.with(context)
                    .load(getIntent().getStringExtra("logo_image"))
                    .thumbnail(0.5f)
                    .into(imgLogoimage);
        } else {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(imgLogoimage);
        }
    }

    public void Home(View view) {
        openHomeCard();
    }

    public void Description(View view) {
        openDecriptionCard();
        showDescriptionCard(listingDetailsData.descriptionTitle, listingDetailsData.descriptionBody);
    }

    public void products(View view) {
        //open products card if available
        openProduct();
        //showProductCard(listingDetailsData.productName, listingDetailsData.productCat, listingDetailsData.productCur,
        //       listingDetailsData.productPrice, listingDetailsData.productUrl, listingDetailsData.productImg);

    }

    public void gallaryClick(View view) {
        openGallary();
        showGalleryCard(galleryList, listingDetailsData.gallaryTitle);
    }

    public void serviceClick(View view) {
        openServicesCard();
    }

    public void reviewClick(View view) {
        openReviewButton();
    }


   /* @Override
    public void onClicked(DiscussionModel model, int position) {
        new SendLike(context).userListReviewLikeDislike(model.getReviewId());
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

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PAGE) {
                bindActivity();
                String postId = data.getStringExtra("list_id");
                getDetails(postId, appPrefs.getString("preferredLanguage", ""),
                        appPrefs.getString("userID", ""));
                System.out.print(postId + appPrefs.getString("preferredLanguage", "") + appPrefs.getString("userID", ""));
            }
        }
    }

    // Image button section
    public void makePhoneCall(View view) {
        startCalling();
    }

    public void makeMessage(View view) {
        getChatUserList(((MyApplication) getApplicationContext()).getAppPrefs().getString("userID", ""));
    }

    private void getChatUserList(String userID) {
        ArrayList chatId = new ArrayList();
        DatabaseReference mReferenceUser = FirebaseDatabase.getInstance().getReference().child("messages").child("users");
        DatabaseReference userRef = mReferenceUser.getRef().child("___" + userID + "___");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatId.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    UserChatListModel userChatList = shot.getValue(UserChatListModel.class);
                    assert userChatList != null;
                    //chatId.add(userChatList.getUserID());
                    if (userChatList.getUserID() == Integer.parseInt(listingDetailsData.authorID)) {
                        //if (userChatList.getUserID() == Integer.parseInt("2")) {
                        isChatHistoryAvailable = true;
                        authar_timestamp = userChatList.getTimestamp();
                        author_avatar = userChatList.getAvatar();
                        break;
                    } else {
                        isChatHistoryAvailable = false;
                    }
                }
                /*for (int index=0;index<chatId.size();index++){
                    int id=0;
                    id=chatId.indexOf(index);
                    if (id == Integer.parseInt(listingDetailsData.authorID)){
                        isChatHistoryAvailable=true;
                        authar_timestamp=userChatList.getTimestamp();
                        author_avatar=userChatList.getAvatar();

                    }else {
                        isChatHistoryAvailable=false;
                    }
                }*/
                gotoChatpage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());

            }
        });
    }

    private void gotoChatpage() {
        Intent chat = new Intent(ListingDetailsActivity.this, ChatActivity.class);
        if (isChatHistoryAvailable) {
            chat.putExtra("author_userId", listingDetailsData.authorID);
            chat.putExtra("author_name", listingDetailsData.authorDisplayName);
            chat.putExtra("author_picture", author_avatar);
            chat.putExtra("time_stamp", authar_timestamp);
        } else {
            chat.putExtra("author_userId", listingDetailsData.authorID);
            chat.putExtra("author_name", listingDetailsData.authorDisplayName);
            chat.putExtra("author_picture", "");
            chat.putExtra("time_stamp", "");
        }
        startActivity(chat);
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
        ActivityCompat.requestPermissions(ListingDetailsActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_PHONE);
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


    public void makeShare(View view) {
        shareSocials();
        /*if (!listingDetailsData.website.equals("")) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
            String app_url = listingDetailsData.website;
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }else {
            Toast.makeText(context, "Link not available", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void makeFavourite(View view) {
        new sendFavourite(context).listingFavourite(listingDetailsData.listingId);
        if (listingDetailsData.myFav) {
            listingDetailsData.myFav = false;
            doFavourite.setImageResource(R.drawable.ic_outline_favorite_border_24);
        } else {
            listingDetailsData.myFav = true;
            doFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    }

    public void viewDescriptionBody(View view) {
        tvDescBody.setMaxLines(50);
        openDecriptionCard();
        showDescriptionCard(listingDetailsData.descriptionTitle, listingDetailsData.descriptionBody);

    }

    public void goToWebsite(View view) {
        try {
//            ((MyApplication) getApplicationContext()).goToUrl(listingDetailsData.website);
            String url = listingDetailsData.website;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.e("indranil", String.valueOf(e));
        }

    }

    public void gotoBookNow(View view) {
//        ((MyApplication) getApplicationContext()).goToUrl(listingDetailsData.booknowLink.toLowerCase());
        //((MyApplication) getApplicationContext()).goToUrl("https://www.romo-tonder.dk/vare/entre-til-jump-a-lot-incl-lille-slushice-eller-varm-drik/");

        try {
            String url =listingDetailsData.booknowLink.toLowerCase();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.e("indranil", String.valueOf(e));
        }
    }

    @Override
    public void onClicked(DiscussionModel model, int position) {
        new SendLike(context).userListReviewLikeDislike(model.getReviewId());
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
        String reviewID = model.getReviewId();
        String userID = model.getUserId();
        String authorname = model.getAuthorName();
        String reviewDate = model.getReviewDate();
        String reviewTitle = model.getReviewtitle();
        String reviewBody = model.getReviewDesc();
        String ratingText = model.getOverallRatingText();
        String rate = model.getOverallReviewRate();
        String rateFrom = model.getOverallReviewRateFrom();
        String like = model.getTotal_likes();
        String comment = model.getCount_comments();
        DiscussionModel discussionModel = discussionsList.get(position);
        Intent commentPage = new Intent(ListingDetailsActivity.this, CommentsActivity.class);
        commentPage.putExtra("from_page", "from_listing_page");
        commentPage.putExtra("reviewID", reviewID);
        commentPage.putExtra("userID", userID);
        commentPage.putExtra("authorName", authorname);
        commentPage.putExtra("reviewDate", reviewDate);
        commentPage.putExtra("reviewTitle", reviewTitle);
        commentPage.putExtra("reviewBody", reviewBody);
        commentPage.putExtra("ratingText", ratingText);
        commentPage.putExtra("rate", rate);
        commentPage.putExtra("rateFrom", rateFrom);
        commentPage.putExtra("like", like);
        commentPage.putExtra("comment", comment);
        startActivity(commentPage);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Marker"));
        LatLng pos = new LatLng(lat, lng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                openMapApplicationDialog();
            }
        });

    }

    private AlertDialog openMapApplicationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("External URL");
        alertDialog.setMessage("Do you want to open map in your app ?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + "" + ")";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        context.startActivity(intent);
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

        return alertDialog;
    }

    @Override
    public void menuClick(DiscussionModel model, int position) {
        showDialogBottom(model, position);
    }

    private void showDialogBottom(DiscussionModel model, int position) {
        AppCompatTextView tvComments, tvLike, tvCancel;
        bottomSheetDialog = new BottomSheetDialog(ListingDetailsActivity.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_menu_dialog,
                null);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        tvComments = view.findViewById(R.id.comments_area);
        tvLike = view.findViewById(R.id.like_area);
        tvCancel = view.findViewById(R.id.cancel_area);
        tvComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentPage = new Intent(ListingDetailsActivity.this, CommentsActivity.class);
                commentPage.putExtra("from_page", "from_listing_page");
                commentPage.putExtra("reviewID", model.getReviewId());
                commentPage.putExtra("userID", model.getUserId());
                commentPage.putExtra("authorName", model.getAuthorName());
                commentPage.putExtra("reviewDate", model.getReviewDate());
                commentPage.putExtra("reviewTitle", model.getReviewtitle());
                commentPage.putExtra("reviewBody", model.getReviewDesc());
                commentPage.putExtra("ratingText", model.getOverallRatingText());
                commentPage.putExtra("rate", model.getOverallReviewRate());
                commentPage.putExtra("rateFrom", model.getOverallReviewRateFrom());
                commentPage.putExtra("like", model.getTotal_likes());
                commentPage.putExtra("comment", model.getCount_comments());
                startActivity(commentPage);

                bottomSheetDialog.dismiss();
            }
        });
        tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendLike(context).userListReviewLikeDislike(model.getReviewId());
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

    public void backpage(View view) {
        finish();
    }
}