package com.romo.tonder.visits.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.CustomAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.LocaleManager;
import com.romo.tonder.visits.helpers.ProfileData;
import com.romo.tonder.visits.models.Pmodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

public class UserAccount extends BaseActivity {
    private static final String TAG = UserAccount.class.getSimpleName();
    private static final String user_activity = "from_user";
    private Button user_logout;
    private Button delete_user_account;
    private Dialog dialog;
    private SharedPreferences appPrefs;
    private Intent nextPage = null;
    AlertDialog.Builder builder;
    private LinearLayout delete_area, logout_area;
    private CircleImageView display_picture;
    private AppCompatImageView cover_picture;
    private LinearLayout profileArea, settingsArea, favorotesArea;
    private Intent intent;
    private ProfileData profileData = new ProfileData();
    private List<Pmodel> plisting = new ArrayList<>();
    private CustomAdapter adapter;
    private AppCompatTextView textViewDisplayName;
    private static final int REQUEST_PAGE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        bindActivity();
        clickEvent();
        if (!appPrefs.getString("preferredLanguage", "").equals("")) {
            selectLaguage(appPrefs.getString("preferredLanguage", ""));
        } else {
            selectLaguage(getResources().getString(R.string.danish_lang));
        }
        getProfileData(appPrefs.getString("userID", ""));
    }

    private void getProfileData(String s) {
        dialog.show();
        String url = Common.BASE_URL + "get-profile";
        AndroidNetworking.post(url)
                .addBodyParameter("userId", s)
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
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponse(JSONObject response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = null;
            JSONObject jBasicInfo = null;
            JSONObject jfollowContact = null;
            JSONArray interestArray = null;
            if (response.getString("status").equals("Success")) {
                jsonObject = response.getJSONObject("oData");
                if (jsonObject != null) {
                    jBasicInfo = jsonObject.getJSONObject("basicInfo");
                    jfollowContact = jsonObject.getJSONObject("followAndContact");
                    if (!jBasicInfo.getString("firstName").equals("")) {
                        profileData.firstName = jBasicInfo.getString("firstName");
                    }
                    if (!jBasicInfo.getString("lastName").equals("")) {
                        profileData.lastName = jBasicInfo.getString("lastName");
                    }
                    if (!jBasicInfo.getString("displayName").equals("")) {
                        profileData.displayName = jBasicInfo.getString("displayName");
                        textViewDisplayName.setText(profileData.displayName);
                    }
                    if (!jBasicInfo.getString("userEmail").equals("")) {
                        profileData.userEmail = jBasicInfo.getString("userEmail");
                    }
                    if (!jBasicInfo.getString("avtarImage").equals("")) {
                        profileData.avtarImage = jBasicInfo.getString("avtarImage");
                        Glide.with(this)
                                .load(profileData.avtarImage)
                                .placeholder(R.drawable.app_placeholder)
                                .error(R.drawable.app_placeholder)
                                .into(display_picture);
                    }
                    if (!jBasicInfo.getString("coverImage").equals("")) {
                        profileData.coverImage = jBasicInfo.getString("coverImage");
                        Glide.with(this)
                                .load(profileData.coverImage)
                                .placeholder(R.drawable.app_placeholder)
                                .error(R.drawable.app_placeholder)
                                .into(cover_picture);
                    }
                    if (!jBasicInfo.getString("introduction").equals("")) {
                        profileData.introduction = jBasicInfo.getString("introduction");
                    }
                    if (!jBasicInfo.getString("prefferedLanguage").equals("")) {
                        profileData.prefferedLanguage = jBasicInfo.getString("prefferedLanguage");
                    }
                    if (!jfollowContact.getString("address").equals("")) {
                        profileData.useraddress = jfollowContact.getString("address");
                    }
                    if (!jfollowContact.getString("phone").equals("")) {
                        profileData.phone = jfollowContact.getString("phone");
                    }
                    if (!jfollowContact.getString("website").equals("")) {
                        profileData.website = jfollowContact.getString("website");
                    }
                    interestArray = jsonObject.getJSONArray("areaOfInterest");
                    if (interestArray != null) {
                        JSONObject jObj = null;
                        plisting.clear();
                        for (int i = 0; i < interestArray.length(); i++) {
                            jObj = interestArray.getJSONObject(i);
                            String preferID = jObj.getString("term_id");
                            String preferName = jObj.getString("name");
                            String preferSelect = jObj.getString("selected");
                            Pmodel pmodel = new Pmodel(preferSelect, preferName, preferID);
                            plisting.add(pmodel);

                        }

                    }

                }
                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void clickEvent() {
        delete_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount();
            }
        });
        logout_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();

            }
        });
    }

    private void bindActivity() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        delete_area = (LinearLayout) findViewById(R.id.deleteArea);
        logout_area = (LinearLayout) findViewById(R.id.logoutArea);
        display_picture = (CircleImageView) findViewById(R.id.displayImg);
        cover_picture = (AppCompatImageView) findViewById(R.id.coverImg);
        profileArea = (LinearLayout) findViewById(R.id.profileArea);
        settingsArea = (LinearLayout) findViewById(R.id.settingsArea);
        favorotesArea = (LinearLayout) findViewById(R.id.favorotesArea);
        //text view
        textViewDisplayName = (AppCompatTextView) findViewById(R.id.displayName);
    }

    public void deleteUserAccount() {
        new MaterialAlertDialogBuilder(UserAccount.this)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFromServer();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void deleteFromServer() {
        dialog.show();
        String url = Common.BASE_URL + "account-delete";
        AndroidNetworking.post(url)
                .addBodyParameter("userId", appPrefs.getString("userID", ""))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        deleteResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Toast.makeText(UserAccount.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void deleteResponse(JSONObject response) {
        dialog.dismiss();
        try {
            if (response.getString("status").equals("Success")) {
                String msg = response.getString("oUserInfo");
                new MaterialAlertDialogBuilder(UserAccount.this)
                        .setTitle(R.string.delete_success_dialog_title)
                        .setMessage(R.string.delete_success_dialog_message)
                        .setCancelable(false)
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(appPrefs.getString("loginwith","").equals("google")){
                                    GoogleSignInOptions gso = new GoogleSignInOptions.
                                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                            build();

                                    GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(UserAccount.this,gso);
                                    googleSignInClient.signOut();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (resetSharedPreference()) {
                                                    dialog.dismiss();

                                                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(UserAccount.this, "Try again", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 3000);
                                    resetSharedPreference();

                                }else  if(appPrefs.getString("loginwith","").equals("fb")){
                                    LoginManager.getInstance().logOut();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (resetSharedPreference()) {
                                                    dialog.dismiss();

                                                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(UserAccount.this, "Try again", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 3000);
                                    resetSharedPreference();

                                }else {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (resetSharedPreference()) {
                                                    dialog.dismiss();

                                                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(UserAccount.this, "Try again", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 3000);
                                    resetSharedPreference();
                                }





                            }
                        })
                        .show();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void userLogout() {

        try {
            new MaterialAlertDialogBuilder(UserAccount.this)
                    .setTitle(R.string.logout_dialog_title)
                    .setMessage(R.string.logout_dialog_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.show();


                            if(appPrefs.getString("loginwith","").equals("google")){
                                Log.i("==>","isLogingoogle==> ");
                                GoogleSignInOptions gso = new GoogleSignInOptions.
                                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                        build();

                                GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(UserAccount.this,gso);
                                googleSignInClient.signOut();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (resetSharedPreference()) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(UserAccount.this, "Try again", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 3000);
                                resetSharedPreference();

                            }else if (appPrefs.getString("loginwith","").equals("fb")){
                                LoginManager.getInstance().logOut();
                                Log.i("==>","fb==> ");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (resetSharedPreference()) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(UserAccount.this, "Try again", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 3000);
                                resetSharedPreference();


                            }else {
                                Log.i("==>","else==> ");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (resetSharedPreference()) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(UserAccount.this, "Try again", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 3000);
                                resetSharedPreference();
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*reset preference */
    private boolean resetSharedPreference() {



        //store data to prefre------
        SharedPreferences.Editor edit = appPrefs.edit();
        edit.putString("userID", "");
        edit.putString("userName", "");
        edit.putString("preferredLanguage", "");
        edit.putString("tokenKey", "");
        edit.putBoolean("isLogin", false);
        edit.putBoolean("isRegistrationSetup", false);
        edit.putBoolean("isRegistration", false);
        edit.putString("emailID", "");
        edit.putString("password", "");
        edit.putString("deviceID", "");

        edit.apply();
        return true;
    }

    /*private void logoutFromApps() {
        dialog.show();
        String url = Common.BASE_URL + "logout";
        AndroidNetworking.post(url)
                .addBodyParameter("userId", appPrefs.getString("userID", ""))
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
                        dialog.dismiss();
                        Toast.makeText(UserAccount.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }*/
    public void home(View view) {
        if (getIntent().hasExtra(Common.FROM_HOME_ACTIVITY)) {
            if (getIntent().getStringExtra("from_home").equals(Common.FROM_HOME_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), HomeActivity.class);
                nextPage.putExtra(Common.FROM_USER_ACTIVITY, user_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), HomeActivity.class);
            nextPage.putExtra(Common.FROM_USER_ACTIVITY, user_activity);
            startActivity(nextPage);
        }
    }

    public void events(View view) {
        if (getIntent().hasExtra(Common.FROM_EVENT_ACTIVITY)) {
            if (getIntent().getStringExtra("from_event").equals(Common.FROM_EVENT_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), EventActivity.class);
                nextPage.putExtra(Common.FROM_USER_ACTIVITY, user_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), EventActivity.class);
            nextPage.putExtra(Common.FROM_USER_ACTIVITY, user_activity);
            startActivity(nextPage);
        }
    }

    public void listing(View view) {
        if (getIntent().hasExtra(Common.FROM_LISTING_ACTIVITY)) {
            if (getIntent().getStringExtra("from_listing").equals(Common.FROM_LISTING_ACTIVITY)) {
                finish();
            } else {
                nextPage = new Intent(getApplicationContext(), ListingActivity.class);
                nextPage.putExtra(Common.FROM_USER_ACTIVITY, user_activity);
                startActivity(nextPage);
            }
        } else {
            nextPage = new Intent(getApplicationContext(), ListingActivity.class);
            nextPage.putExtra(Common.FROM_USER_ACTIVITY, user_activity);
            startActivity(nextPage);
        }
    }

    public void openNotificationListing(View view) {
        Intent intent = new Intent(this, NotificationListActivity.class);
        intent.putExtra(Common.NOTIFICATION_LIST_TYPE, Common.NOTIFICATION_TYPE_LIST);
        startActivity(intent);
    }

    public void profileAccount(View view) {

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

    public void viewProfileInfo(View view) {
        intent = new Intent(this, ViewProfile.class);
        intent.putExtra("f_name", profileData.firstName);
        intent.putExtra("l_name", profileData.lastName);
        intent.putExtra("d_name", profileData.displayName);
        intent.putExtra("address", profileData.useraddress);
        intent.putExtra("phone_no", profileData.phone);
        intent.putExtra("website", profileData.website);
        intent.putExtra("lang", profileData.prefferedLanguage);
        intent.putExtra("pos", profileData.userposition);
        intent.putExtra("intro", profileData.introduction);
        intent.putExtra("list", (Serializable) plisting);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_PAGE);
    }

    public void viewSettings(View view) {
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PAGE) {
                getProfileData(appPrefs.getString("userID", ""));
            }
        }
    }

    public void favouritePage(View view) {
        intent = new Intent(this, FavuritesActivity.class);
        startActivityForResult(intent, REQUEST_PAGE);

    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void chatHistory(View view) {
        Intent chatlistpage = new Intent(this, ChatHistoryList.class);
        chatlistpage.putExtra("user_id", appPrefs.getString("userID", ""));
        startActivity(chatlistpage);
    }
}
