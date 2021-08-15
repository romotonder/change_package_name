//package com.romo.tonder.visit.helpers;
//
//import android.app.Activity;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.work.Data;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.androidnetworking.AndroidNetworking;
//import com.androidnetworking.common.Priority;
//import com.androidnetworking.error.ANError;
//import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
//import com.romo.tonder.visit.MyApplication;
//import com.romo.tonder.visit.R;
//import com.romo.tonder.visit.activities.HomeActivity;
//import com.romo.tonder.visit.activities.SignupActivity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import okhttp3.Response;
//
//public class MyWorker extends Worker {
//    private static final String TAG = MyWorker.class.getSimpleName();
//    public static final String KEY_OUTPUT_TASK = "output_task";
//    private String msg = "";
//    private NotificationManager manager;
//    private Context context;
//
//    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        try {
//            callPushNotification(context);
//            Log.e(TAG, "Work Call:------- ");
//            Data getDataFromHome = getInputData();
//            String dataFromHome = getDataFromHome.getString(HomeActivity.KEY_INPUT_TASK);
//            //displayNotification(dataFromHome);
//        } catch (Exception e) {
//            Result.retry();
//        }
//
//        return Result.success();
//    }
//
//    private void displayNotification(String dataFromHome) {
//        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(getApplicationContext(), MyApplication.CHANNEL_ONE_ID)
//                .setSmallIcon(R.drawable.app_placeholder)
//                .setContentTitle("Static Notification")
//                .setContentText(dataFromHome)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .build();
//
//        manager.notify(1, notification);
//
//    }
//
//    private Data createOutputData() {
//        Data senDataAfterWorkfinished = new Data.Builder()
//                .putString(KEY_OUTPUT_TASK, "PushNotification API Call Success")
//                .build();
//        return senDataAfterWorkfinished;
//    }
//
////    private void callPushNotification() {
////        String url = Common.BASE_URL + "send-push-notification";
////        String userID = ((MyApplication) getApplicationContext()).getAppPrefs().getString("userID", "");
////        String dateTime = Common.GetCurrentDateTime();
////        System.out.print(dateTime);
////
////        AndroidNetworking.post(url)
////                .addBodyParameter("userId", userID)
////                .addBodyParameter("curTime", dateTime)
////                .addBodyParameter("notificationType", "Greetings Notification")
////                .addBodyParameter("lat", "55.056595")
////                .addBodyParameter("lag", "8.824121")
////                .setPriority(Priority.HIGH)
////                .build()
////                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
////                    @Override
////                    public void onResponse(Response okHttpResponse, JSONObject response) {
////                        parseResponse(response);
////                    }
////
////                    @Override
////                    public void onError(ANError anError) {
////                        Log.e(TAG, "onError: " + anError.getErrorDetail());
////
////                    }
////                });
////
////    }
//
//
//    private void callPushNotification(Context context) {
//        String url = Common.BASE_URL + "send-push-notification";
////        String userID = ((MyApplication) activity.getApplicationContext()).getAppPrefs().getString("userID", "");
//        String userID = ((MyApplication) context.getApplicationContext()).getAppPrefs().getString("userID", "");
//        String dateTime = Common.GetCurrentDateTime();
//        System.out.print(dateTime);
//
//        AndroidNetworking.post(url)
//                .addBodyParameter("userId", userID)
//                .addBodyParameter("curTime", dateTime)
//                .addBodyParameter("notificationType", "Greetings Notification")
//                .addBodyParameter("lat", "55.056595")
//                .addBodyParameter("lag", "8.824121")
//
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(Response okHttpResponse, JSONObject response) {
////                        parseResponse(response);
//                        try {
////                            if (response.getString("status").equals("Success")) {
//                            if (response.getString("success").equals("1")) {
//                                String msg = response.getString("results");
////                                parseResponse(response);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//
//                    }
//                });
//
//    }
//
//    private void parseResponse(JSONObject response) {
//        try {
//            if (response.getString("success").equals("1")) {
//                //msg = response.getString("oData");
//                //String oData=response.getString("oData");
//                String multicast_id = response.getString("multicast_id");
//                System.out.print(multicast_id);
//
//
//            } else {
//                //String failureMsg = response.getString("oData");
//                //Log.e(TAG, "failour: " + failureMsg);
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//}
