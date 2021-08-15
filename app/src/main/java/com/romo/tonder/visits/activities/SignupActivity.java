package com.romo.tonder.visits.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;


public class SignupActivity extends BaseActivity {
    private Toolbar mtoolbar;
    private AppCompatEditText edt_username,edt_email,edt_pass;
    private Button btn_signup;
    private ImageButton check_trams,check_privacy;
    private static final String TAG = SignupActivity.class.getSimpleName();
    private boolean isTramsChacked=false;
    private String isAgreeTrams="";
    private boolean isPrivacyChecked=false;
    private String isAgreePrivacy="";
    private Dialog dialog;
    private Button btn_login;
    //validation
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String[] PERMS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private SharedPreferences appPrefs;
    Timer timer;
    Handler h = new Handler();
    GPSTracker gps;
    String result;
    private double lastLatitude = -1;
    private double lastLogitude = -1;
    private String deviceID = "";
    private String fcmTokenID ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        bindActivity();
        initListener();
        getLocationPermission();
       //check gps
        if (!checkgps()) {
            permission();
        } else {
            gps = new GPSTracker(SignupActivity.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                lastLatitude = gps.getLatitude();
                lastLogitude = gps.getLongitude();
            }

        }
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            fcmTokenID = task.getResult().getToken();
                            Log.d(TAG, "FIREBASE_TOKEN: "+fcmTokenID);
                            System.out.print(fcmTokenID);
                        }
                    }
                });




    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMS, READ_PHONE_STATE);

        }else {
            requestPermission();
        }

    }

    private static final int READ_PHONE_STATE = 300;
    private static final int REQUEST_FROM_LOCATION=301;

    private void bindActivity() {
        //TODO object declaration
        /*//toolbar
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.registration_page_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        edt_username=(AppCompatEditText) findViewById(R.id.edtUsername);
        edt_email=(AppCompatEditText)findViewById(R.id.edtEmail);
        edt_pass=(AppCompatEditText)findViewById(R.id.edtPass);
        btn_signup=(Button) findViewById(R.id.btnSignup);
        btn_login=(Button) findViewById(R.id.btnLogin);

        check_trams=(ImageButton) findViewById(R.id.checkTramsCons);
        check_privacy=(ImageButton) findViewById(R.id.checkPrivacy);
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();
        deviceID = getDeviceId(getApplicationContext());
    }

    private void initListener() {
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkpermission()) {
                    if (validation()) {
//                        Log.e(TAG, "onClick: latitude : " + lastLatitude + " Longitude : " + lastLogitude);
                        saveRegistrationDetails(fcmTokenID, String.valueOf(lastLatitude),
                                String.valueOf(lastLogitude));
                        /*if (!deviceID.isEmpty())

                        else {
                            deviceID = getDeviceId(getApplicationContext());
                            saveRegistrationDetails(firebase,
                                    String.valueOf(lastLatitude), String.valueOf(lastLogitude));
                        }*/
                    }
                } else {
//                    requestPermission();
                }
            }
        });
        check_trams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTramsChacked){
                    isTramsChacked=false;
                    check_trams.setImageResource(R.drawable.ic_uncheck_box_outline_blank_24px);
                }else {
                    isTramsChacked=true;
                    isAgreeTrams="Yes";
                    check_trams.setImageResource(R.drawable.ic_checked_24);
                }


            }
        });
        check_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPrivacyChecked){
                    isPrivacyChecked=false;
                    check_privacy.setImageResource(R.drawable.ic_uncheck_box_outline_blank_24px);
                }else {
                    isPrivacyChecked=true;
                    isAgreePrivacy="Yes";
                    check_privacy.setImageResource(R.drawable.ic_checked_24);
                }

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void saveRegistrationDetails(String firebaseToken, String lastLatitude, String logitude) {
        dialog.show();

        String url = Common.BASE_URL + "registration";

        AndroidNetworking.post(url)
                .addBodyParameter("emailAddress", edt_email.getText().toString().trim())
                .addBodyParameter("username", edt_username.getText().toString())
                .addBodyParameter("password",edt_pass.getText().toString().trim())
                .addBodyParameter("isAgreeToPrivacyPolicy",isAgreePrivacy)
                .addBodyParameter("isAgreeToTermsAndConditionals",isAgreeTrams)
                .addBodyParameter("deviceType", "Android")
                .addBodyParameter("deviceToken",firebaseToken)
                .addBodyParameter("latitude", lastLatitude)
                .addBodyParameter("longtitude", logitude)
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
                        Toast.makeText(SignupActivity.this,"Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());

                    }
                });
    }

    private void parseResponse(JSONObject response) {
        dialog.dismiss();
        try {
            if (response.getString("status").equals("Success")) {
                JSONObject object = response.getJSONObject("oData");
                String userID=object.getString("userId");
                String tokenKey=object.getString("tokenKey");
                //store data to prefre------
                SharedPreferences.Editor edit = appPrefs.edit();
                edit.putString("userID", userID);
                edit.putString("userName",edt_username.getText().toString());
                edit.putString("tokenKey", tokenKey);
                edit.putString("emailID",edt_email.getText().toString().trim());
                edit.putString("password",edt_pass.getText().toString());
                edit.putString("deviceID",deviceID);
                edit.putBoolean("isRegistration", Common.isRegistrer);
                edit.apply();
                goToWelcomeScreen();
                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();

            }else {
                String failureMsg = response.getString("oData");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void goToWelcomeScreen() {
        Intent next =new Intent(getApplicationContext(),WelcomeScreenActivy.class);
        next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(next);

    }

    public boolean checkgps() {
        LocationManager lm = (LocationManager) SignupActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }
    }
    public void permission() {
        if (!checkgps()) {
            // notify user
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(SignupActivity.this);
            dialog.setMessage("Location Settings is turned off!! Please turn it on.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("GO To Setup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });

            dialog.show();

        }
        //start a timer to disable the start button for 4 seconds
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    public void run() {
                        //btnSubmit.setEnabled(true);
                        //Common.enableButton(btnSubmit, true);
                        timer.cancel();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }

    private boolean validation() {
        Matcher matcher= Pattern.compile(emailPattern).matcher(edt_email.getText());
        if (edt_email.getText().toString().trim().isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.msg_email), Toast.LENGTH_SHORT).show();
            return false;
        }else if (!matcher.matches()){
            Toast.makeText(this, getResources().getString(R.string.msg_valid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (edt_username.getText().toString().trim().isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.msg_username), Toast.LENGTH_SHORT).show();
            return false;
        }else if (edt_pass.getText().toString().trim().isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.msg_enter_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isTramsChacked){
            Toast.makeText(this, getResources().getString(R.string.select_trams_and_condition), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isPrivacyChecked){
            Toast.makeText(this, getResources().getString(R.string.select_privecy_policy), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(SignupActivity.this,PERMS,READ_PHONE_STATE);
        ActivityCompat.requestPermissions(SignupActivity.this, PERMS, REQUEST_FROM_LOCATION);

    }
    /* ============= PERMISSION SETTINGS ================================== */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PHONE_STATE && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_service_enable),
                            Toast.LENGTH_SHORT).show();
                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_service_enable),
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                return;
            }

        }
    }

    @SuppressLint("MissingPermission")
    public String getDeviceId(Context context) {
        String deviceId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = mngr.getDeviceId();
        }
        return deviceId;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public boolean checkpermission() {
        int readPhonestate= ActivityCompat.checkSelfPermission(SignupActivity.this,
                Manifest.permission.READ_PHONE_STATE);
        int location = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if ( readPhonestate == PackageManager.PERMISSION_GRANTED
                && location == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }else {
            return false;
        }

    }
}
