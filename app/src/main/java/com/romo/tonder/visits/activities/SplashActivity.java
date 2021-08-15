package com.romo.tonder.visits.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.LocaleManager;


public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private SharedPreferences appPrefs;
    private String appsLanguage = "";
    private static int SPLASH_TIME_OUT = 3000;
    private ImageView background;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //background = findViewById(R.id.background);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);





        /*Glide.with(this)
                .load(R.drawable.backgrund_app)
                .thumbnail(0.5f)
                .into(background);*/

//        try {
//            Bundle bundle = getIntent().getBundleExtra("bundle");
//            if (bundle != null){
//                Toast.makeText(this, "from Notification", Toast.LENGTH_SHORT).show();
//            } else{
//                Toast.makeText(this, "not from Notification", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, " error Notification", Toast.LENGTH_SHORT).show();
//        }

        bindActivity();
        if (!appsLanguage.equals("")) {
            selectLaguage(appsLanguage);
        } else {
            selectLaguage(getResources().getString(R.string.danish_lang));
            //selectLaguage(getResources().getString(R.string.english_lang));
        }
        initListener();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = null;
                boolean isLogin = appPrefs.getBoolean("isLogin", false);

                if (getIntent().getExtras() != null) {
                    intent = new Intent(SplashActivity.this, NotificationListActivity.class);
                    intent.putExtra(Common.NOTIFICATION_LIST_TYPE, Common.NOTIFICATION_TYPE_BELL_ICON);
                    intent.putExtra(Common.NOTIFIED, "true");
                    startActivity(intent);
                    finish();
                } else {
                    if (appPrefs.getBoolean("isLogin", false) &&
                            appPrefs.getBoolean("isRegistrationSetup", false)) {
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                    } else if (appPrefs.getBoolean("isLogin", false)) {
                        intent = new Intent(SplashActivity.this, WelcomeScreenActivy.class);
                    } else {
                        intent = new Intent(SplashActivity.this, SigninActivity.class);
                        intent.putExtra("language", getResources().getString(R.string.danish_lang));
                        //intent.putExtra("language", getResources().getString(R.string.english_lang));
                    }
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        appsLanguage = appPrefs.getString("preferredLanguage",
                "");
        //TODO object declaration
    }

    private void initListener() {

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
        /*Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
    }
}
