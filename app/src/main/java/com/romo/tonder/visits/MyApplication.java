package com.romo.tonder.visits;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import com.androidnetworking.AndroidNetworking;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.romo.tonder.visits.activities.SigninActivity;
import com.romo.tonder.visits.activities.WebViewActivity;
import com.romo.tonder.visits.helpers.LocaleManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MyApplication extends Application {
    private SharedPreferences appPrefs;
    public static final int DATA = 1;
    public static final int LOADING = 0;
    public static final int NO_DATA = -1;
    public static final int NO_INTERNET = -2;
    public static final String CHANNEL_ONE_ID = "channel_one_id";

    @Override
    public void onCreate() {
        super.onCreate();
        appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        AndroidNetworking.initialize(getApplicationContext());


/*        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("415158262655-0aaf1snqp66pne6q25k4n6vnqmlr8rr5.apps.googleusercontent.com") // Required for Analytics.
                .setProjectId("romo-tonder-1563873199277") // Required for Firebase Installations.
                .setApiKey("AIzaSyARWMNbXH7WX_sZywNP4YRcVx57nSPGlk8") // Required for Auth.
                .build();
        FirebaseApp.initializeApp(this, options, "Romo Tonder");*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channelOne = new NotificationChannel(CHANNEL_ONE_ID,
                    "Channel One",
                    NotificationManager.IMPORTANCE_HIGH);

            channelOne.setDescription("This is channel one for video notifications");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(channelOne);

            manager.createNotificationChannels(channels);
        }


        new Instabug.Builder(this, "897cf24eea5fe44cf1b58f0ea770916d")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT)
                .build();


    }

    public SharedPreferences getAppPrefs() {
        return appPrefs;
    }

    public void setAppPrefs(SharedPreferences appPrefs) {
        this.appPrefs = appPrefs;
    }

    public void logout() {
        appPrefs.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void updateBearer(Response response) {
        String authorization = response.header("Authorization", null);
        if (authorization != null) {
            appPrefs.edit().putString("authorization", authorization).apply();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    public void goToUrl(String url) {
        Intent i = new Intent(this, WebViewActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("url", url);
        startActivity(i);
    }
}
