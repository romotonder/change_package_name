package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.NotificationListingAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.NotificationListings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Response;

public class NotificationActivity extends BaseActivity {
    public static final String TAG = NotificationActivity.class.getSimpleName();
    private String datas = "",promoCode="",redirectLink="";
    private Toolbar toolbar;
    private AppCompatImageView notificationImg;
    private AppCompatTextView descriptionTextView;
    private AppCompatTextView titleTextView,toolbarTextView;
    private AppCompatTextView promotext;
    private AppCompatImageView copyImageView;
    private RecyclerView recyclerView;
    private AppCompatTextView tvNotify;
    private String img_url = "https://www.app.romo-tonder.dk/wp-content/uploads/2019/09/51786720_2228604877195848_725330104103731200_n.jpg";
    private String text = "In a professional context it often happens that private or corporate clients corder a publication to be made and presented with the actual content still not being ready. Think of a news blog that's filled with content hourly on the day of going live. However, reviewers tend to be distracted by comprehensible content, say, a random text copied from a newspaper or the internet.";
    private LinearLayout promocodeLayout;
    private Button btnRedirectLink;


    private ArrayList<NotificationListings> listingsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbar);
        titleTextView = findViewById(R.id.notification_title);
        toolbarTextView = findViewById(R.id.toolbar_text_view);
        descriptionTextView = findViewById(R.id.notification_description);
        notificationImg = findViewById(R.id.notification_image_view);
        recyclerView = findViewById(R.id.list_recycler);
        promotext = findViewById(R.id.promo_text);
        copyImageView = findViewById(R.id.copy_promo_image_view);
        promocodeLayout = findViewById(R.id.linear_layout);
        btnRedirectLink = findViewById(R.id.link_button);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_back_arrow_24_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        copyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("KJAGJD12K".toString(), text);
                clipboard.setPrimaryClip(clip);
            }
        });

        listingsArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getBundleExtra("bundle");
        ArrayList<NotificationListings> listings = new ArrayList<>();
        if (bundle != null) {
            String id = bundle.getString("notification_id");
            String title = bundle.getString("title");
            String body = bundle.getString("body");
            String imageUrl = bundle.getString("imageUrl");
            String notificationListData = bundle.getString("notification_list_data");
            promoCode = bundle.getString("promo_code");
            redirectLink = bundle.getString("redirect_link");


            JSONArray results = null;
            try {
                if ((!TextUtils.isEmpty(notificationListData)))
                    results = new JSONArray(notificationListData);
                if (results != null)
                    for (int i = 0; i < results.length(); i++) {
                        NotificationListings notificationListings1 = new NotificationListings();
                        JSONObject object = results.getJSONObject(i);
                        notificationListings1.setID(object.getString("ID"));
                        notificationListings1.setCoverImg(object.getString("coverImg"));
                        notificationListings1.setLink(object.getString("link"));
                        notificationListings1.setLogo(object.getString("logo"));
                        notificationListings1.setPostTitle(object.getString("postTitle"));
                        notificationListings1.setTagLine(object.getString("tagLine"));
                        notificationListings1.setType(object.getString("type"));
                        listingsArrayList.add(notificationListings1);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            titleTextView.setText(title);
            toolbarTextView.setText(title);
            descriptionTextView.setText(body);

            Glide.with(this)
                    .load(imageUrl)
                    .thumbnail(0.5f)
                    .into(notificationImg);


            if (!TextUtils.isEmpty(id)) {
                readNotification(id);
            }

            if (!promoCode.isEmpty()){
                promocodeLayout.setVisibility(View.VISIBLE);
                promotext.setText(promoCode);
            }else {
                promocodeLayout.setVisibility(View.GONE);
            }
            if (!redirectLink.isEmpty()){
                btnRedirectLink.setVisibility(View.VISIBLE);
            }else {
                btnRedirectLink.setVisibility(View.GONE);
            }
        }
        if (listingsArrayList != null && listingsArrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            NotificationListingAdapter notificationListingAdapter = new NotificationListingAdapter(this,
                    listingsArrayList, null);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(notificationListingAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
        }

    }


    private void readNotification(String id) {
        String url = Common.BASE_URL + "read-notification";
        String userID = ((MyApplication) getApplicationContext()).getAppPrefs().getString("userID", "");
        AndroidNetworking.post(url)
                .addBodyParameter("userId", userID)
                .addBodyParameter("notificationId", id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getIntent().getStringExtra(Common.NOTIFIED) != null) {
            if (Objects.equals(getIntent().getStringExtra(Common.NOTIFIED), Common.NOTIFIED_PREFERENCE))
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

    public void goLinkPage(View view) {
        try {
            String url = redirectLink;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.e("indranil", String.valueOf(e));
        }
    }
}