package com.romo.tonder.visits.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.PrefrenceAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.LocaleManager;
import com.romo.tonder.visits.helpers.ProfileData;
import com.romo.tonder.visits.interfaces.PreferOnClickListener;
import com.romo.tonder.visits.models.InterestModels;
import com.romo.tonder.visits.models.Pmodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class ViewProfile extends BaseActivity implements PreferOnClickListener {
    private static final String TAG = ViewProfile.class.getSimpleName();
    private ProfileData profileData;
    private List<Pmodel> plisting;
    private LinearLayout preferenceArea;
    private LayoutInflater inflater;
    private Context context;
    private Intent intent;
    private AppCompatTextView txtDisplayName,txtEmails,txtLanguage,txtAddress,txtPhone;

    private Dialog dialog;
    private SharedPreferences appPrefs;
    private ArrayList<InterestModels>preferList;
    private RecyclerView preferenceRecycler;
    private PrefrenceAdapter preAdapter;
    private AppCompatImageView display_picture,cover_picture;
    private CardView cardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewprofilee);
        context = this;
        bindActivity();
        profileData=new ProfileData();
        plisting=new ArrayList<Pmodel>();
        preferenceArea=(LinearLayout)findViewById(R.id.parentInterest);
        inflater = LayoutInflater.from(context);
        if (!appPrefs.getString("preferredLanguage", "").equals("")) {
            selectLaguage(appPrefs.getString("preferredLanguage", ""));
        } else {
            selectLaguage(getResources().getString(R.string.danish_lang));
        }
        getProfileData(appPrefs.getString("userID",""));
        //getProfileData();
    }

    private void getProfileData(String userID) {
        dialog.show();
        String url = Common.BASE_URL + "get-profile";
        AndroidNetworking.post(url)
                .addBodyParameter("userId", userID)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseProfile(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });


    }
    private void parseResponseProfile(JSONObject response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject=null;
            JSONObject jBasicInfo=null;
            JSONObject jfollowContact=null;
            JSONArray interestArray=null;
            if (response.getString("status").equals("Success")) {
                jsonObject=response.getJSONObject("oData");
                if (jsonObject!=null){
                    jBasicInfo=jsonObject.getJSONObject("basicInfo");
                    jfollowContact=jsonObject.getJSONObject("followAndContact");
                    if (!jBasicInfo.getString("firstName").equals("")){
                        profileData.firstName=jBasicInfo.getString("firstName");
                    }
                    if (!jBasicInfo.getString("lastName").equals("")){
                        profileData.lastName=jBasicInfo.getString("lastName");
                    }
                    if (!jBasicInfo.getString("displayName").equals("")){
                        profileData.displayName=jBasicInfo.getString("displayName");
                        txtDisplayName.setText(jBasicInfo.getString("displayName"));

                    }
                    if (!jBasicInfo.getString("userEmail").equals("")){
                        profileData.userEmail=jBasicInfo.getString("userEmail");
                        txtEmails.setText(getString(R.string.email_id)+" : "+jBasicInfo.getString("userEmail"));
                    }
                    if (!jBasicInfo.getString("avtarImage").equals("")){
                        profileData.avtarImage=jBasicInfo.getString("avtarImage");
                        Glide.with(this)
                                .load(profileData.avtarImage)
                                .placeholder(R.drawable.app_placeholder)
                                .error(R.drawable.app_placeholder)
                                .into(display_picture);
                    }
                    if (!jBasicInfo.getString("coverImage").equals("")){
                        profileData.coverImage=jBasicInfo.getString("coverImage");
                        Glide.with(this)
                                .load(profileData.coverImage)
                                .placeholder(R.drawable.app_placeholder)
                                .error(R.drawable.app_placeholder)
                                .into(cover_picture);
                    }
                    if (!jBasicInfo.getString("introduction").equals("")){
                        profileData.introduction=jBasicInfo.getString("introduction");
                    }
                    if (!jBasicInfo.getString("prefferedLanguage").equals("")){
                        profileData.prefferedLanguage=jBasicInfo.getString("prefferedLanguage");
                        txtLanguage.setText(getString(R.string.language)+" : "+jBasicInfo.getString("prefferedLanguage"));
                    }
                    if (!jfollowContact.getString("address").equals("")){
                        txtAddress.setVisibility(View.VISIBLE);
                        profileData.useraddress=jfollowContact.getString("address");
                        txtAddress.setText(getString(R.string.address)+" : "+jfollowContact.getString("address"));
                    }else {
                        txtAddress.setVisibility(View.GONE);
                        txtAddress.setText("");
                    }
                    if (!jfollowContact.getString("phone").equals("")){
                        txtPhone.setVisibility(View.VISIBLE);
                        profileData.phone=jfollowContact.getString("phone");
                        txtPhone.setText(getString(R.string.Phone)+" : "+jfollowContact.getString("phone"));
                    }else {
                        txtPhone.setVisibility(View.GONE);
                        txtPhone.setText("");
                    }
                    interestArray=jsonObject.getJSONArray("areaOfInterest");
                    if (interestArray!=null){
                        JSONObject jObj=null;
                        preferList.clear();
                        for (int i = 0; i < interestArray.length(); i++) {
                            jObj = interestArray.getJSONObject(i);
                            String preferID = jObj.getString("term_id");
                            String preferName = jObj.getString("name");
                            String preferSelect = jObj.getString("selected");
                            int viewType=1;

                            InterestModels bean=new InterestModels(preferSelect,preferName,preferID,viewType);
                            preferList.add(bean);
                            if (preferList.size()>0){
                                preAdapter=new PrefrenceAdapter(preferList,this,this);
                                preferenceRecycler.setAdapter(preAdapter);
                            }
                        }
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

    private void bindActivity() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();
        txtDisplayName=(AppCompatTextView)findViewById(R.id.display_name);
        txtEmails=(AppCompatTextView)findViewById(R.id.emails);
        txtLanguage=(AppCompatTextView)findViewById(R.id.language);
        txtAddress=(AppCompatTextView)findViewById(R.id.address);
        txtPhone=(AppCompatTextView)findViewById(R.id.phone);
        display_picture=(AppCompatImageView)findViewById(R.id.displayImg);
        cover_picture=(AppCompatImageView)findViewById(R.id.coverImg);
        cardInfo=(CardView)findViewById(R.id.cardInfo);

        //adapter settings
        preferenceRecycler=(RecyclerView)findViewById(R.id.preferenceArea);
        preferenceRecycler.setLayoutManager(new LinearLayoutManager(this));
        preferList=new ArrayList<>();
        preAdapter=new PrefrenceAdapter(preferList,this,this);
        preferenceRecycler.setAdapter(preAdapter);
    }
    public void editProfile(View view) {
        intent = new Intent(context, EditProfileActivity.class);
        startActivityForResult(intent,101);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    public void modifyProfile(View view) {
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                getProfileData(appPrefs.getString("userID",""));
            }
        }
    }

    @Override
    public void onClicked(InterestModels models, int position) {

    }
}