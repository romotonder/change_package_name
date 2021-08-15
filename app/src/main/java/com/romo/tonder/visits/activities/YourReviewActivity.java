package com.romo.tonder.visits.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.Common;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class YourReviewActivity extends AppCompatActivity {
    private AppCompatSeekBar general, service, quality, location;
    private AppCompatTextView txtGeneralProgress, txtServiceProgress, txtQualityProgress, txtLocationProgress;
    private String generalValue = "", serviceValue = "", qualityValue="", locationValue = "";
    private int max = 5;
    private int min = 2;
    private AppCompatTextView btnSbmit;
    private TextInputEditText edtReviewTitle, edtReview;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_review);
        setContentView(R.layout.activity_your_review);
        bindActivity();

        /*general*/
        general.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int stat=0;
                stat=progress;
                txtGeneralProgress.setText(String.valueOf(stat));
                System.out.print(String.valueOf(stat));
                System.out.print(String.valueOf(stat));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generalValue = String.valueOf(seekBar.getProgress());
                System.out.print(String.valueOf(generalValue));
            }
        });
        //service
        service.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtServiceProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                serviceValue = String.valueOf(seekBar.getProgress());
            }
        });
        //quality
        quality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtQualityProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                qualityValue = String.valueOf(seekBar.getProgress());
            }
        });
        //location
        location.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtLocationProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                locationValue = String.valueOf(seekBar.getProgress());
            }
        });

    }

    private void bindActivity() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        //general
        general = findViewById(R.id.general);
        txtGeneralProgress = findViewById(R.id.tvGeneralProgress);
        //Service
        service = findViewById(R.id.service);
        txtServiceProgress = findViewById(R.id.tvServiceProgress);
        //Quality
        quality = findViewById(R.id.quality);
        txtQualityProgress = findViewById(R.id.tvQualityProgress);
        //Location
        location = findViewById(R.id.location);
        txtLocationProgress = findViewById(R.id.tvLocationProgress);
        //edit text
        edtReviewTitle = findViewById(R.id.edtReviewTitle);
        edtReview = findViewById(R.id.edtReview);

    }

    public void sendYourReview(View view) {
        if (checkValidation()) {
            if (generalValue.equals(""))
                generalValue=String.valueOf(min);
            if (serviceValue.equals(""))
                serviceValue=String.valueOf(min);
            if (qualityValue.equals(""))
                qualityValue=String.valueOf(min);
            if (locationValue.equals(""))
                locationValue=String.valueOf(min);
            submitReview();

        }

    }

    private void submitReview() {
        dialog.show();
        SharedPreferences appPrefs;
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        String url = Common.BASE_URL + "review";
        AndroidNetworking.post(url)
                .addBodyParameter("type", "Add")
                .addBodyParameter("postId", getIntent().getStringExtra("post_id"))
                .addBodyParameter("userId", appPrefs.getString("userID", ""))
                .addBodyParameter("reviewTitle", edtReviewTitle.getText().toString())
                .addBodyParameter("yourReview", edtReview.getText().toString())
                .addBodyParameter("general", generalValue)
                .addBodyParameter("service", serviceValue)
                .addBodyParameter("quality", qualityValue)
                .addBodyParameter("location", locationValue)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseReviewlike(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponseReviewlike(JSONObject response) {
        dialog.dismiss();
        try {
            if (response.getString("status").equals("Success")) {
                String data = response.getString("Data");
                Intent previous=new Intent();
                previous.putExtra("list_id",getIntent().getStringExtra("post_id"));
                setResult(RESULT_OK,previous);
                finish();
            }else{
                Toast.makeText(this, "Review not submit successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidation() {
        if (edtReviewTitle.getText().toString().equals(""))
            return false;
        else if (edtReview.getText().toString().equals(""))
            return false;
        else
            return true;
    }
}