package com.romo.tonder.visits.FCMService;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.NotificationActivity;
import com.romo.tonder.visits.activities.NotificationListActivity;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.NotificationListings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class MyFirebaseMessagingService_new extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        System.out.println("MyFirebaseMessagingService_new token:: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            System.out.println(remoteMessage.getData());
            Map<String, String> dataMap = remoteMessage.getData();
            String imageAsString = dataMap.get("notificationImage");
            String id = dataMap.get("notificationId");
            String data = dataMap.get("data");
            String body = dataMap.get("notificationBody");
            String title = dataMap.get("notificationText");
            String promoCode = dataMap.get("promoCode");
            String redirectLink = dataMap.get("redirectLink");
            displayNotification(id, title, body, imageAsString, data, promoCode, redirectLink);

        }
    }


    private ArrayList<NotificationListings> getJsonData(RemoteMessage remoteMessage) {
        ArrayList<NotificationListings> notificationListings = new ArrayList<>();
        try {
            String data = remoteMessage.getData().get("data");
            JSONArray results = new JSONArray(data);
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
                notificationListings.add(notificationListings1);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return notificationListings;
    }

    private void displayNotification(String id, String title, String body, String imageUrl, String listingsData, String promoCode, String link) {
        Context context = getApplicationContext();

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.custom_notification_small_title, title);
        remoteViews.setTextViewText(R.id.custom_notification_small_description, body);

        RemoteViews remoteViewsBig = new RemoteViews(getPackageName(), R.layout.custom_notification_big);
        remoteViewsBig.setTextViewText(R.id.custom_notification_big_title, title);
        remoteViewsBig.setTextViewText(R.id.custom_notification_big_description, body);


        Bundle bundle = new Bundle();
        bundle.putString("notification_id", id);
        bundle.putString("notification_list_data", listingsData);
        bundle.putString("title", title);
        bundle.putString("body", body);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("promo_code", promoCode);
        bundle.putString("redirect_link", link);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        remoteViewsBig.setImageViewBitmap(R.id.image, resource);
                        remoteViews.setImageViewBitmap(R.id.image, resource);
                        notifyMessage(title, body, context, remoteViews, remoteViewsBig, bundle);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        notifyMessage(title, body, context, remoteViews, remoteViewsBig, bundle);

                    }
                });


    }

    private void notifyMessage(String title, String body, Context context, RemoteViews remoteViews, RemoteViews remoteViewsBig, Bundle bundle) {

        Intent intent = new Intent(this, NotificationActivity.class).putExtra("bundle", bundle);
        intent.putExtra(Common.NOTIFIED, Common.NOTIFIED_PREFERENCE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = null;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", importance);
            builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setCustomContentView(remoteViews);
            builder.setCustomBigContentView(remoteViewsBig);
            builder.setSound(tone);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(channel);

            builder.setChannelId("channel_id");
        }

        assert builder != null;
        manager.notify(1, builder.build());

    }
}


