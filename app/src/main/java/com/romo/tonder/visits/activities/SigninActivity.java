package com.romo.tonder.visits.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.BuildConfig;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResponse;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
//import com.nabinbhandari.android.permissions.PermissionHandler;
//import com.nabinbhandari.android.permissions.Permissions;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.GPSTracker;
import com.romo.tonder.visits.helpers.LocaleManager;
import com.romo.tonder.visits.models.User;

//import net.crowdconnected.androidcolocator.CoLocator;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

import okhttp3.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
//import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class SigninActivity extends BaseActivity {
    private static final String TAG = SigninActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 23;
    private Context context;
    private ImageView background;
    private Button btn_login;
    private Dialog dialog;
    private EditText edt_username, edt_password;
    private SharedPreferences appPrefs;
    private GoogleSignInClient mGoogleSignInClient;
    private User user;
    private Button btn_googleLogin, btn_fbLogin, btn_registration;
    private String deviceID = "";


    private static final String EMAIL = "email";
    private double txtSize;
    private TextView tvTitle, tvBody, tvForgotPwd;
    private LinearLayout ll_increaseDecreaseArea;
    private ImageButton increase;
    private ImageButton decrease;
    private TextView tvCountTextSize;
    private int count = 0;
    private ImageButton btn_eye;
    private boolean INVISIBLE = false;
    Timer timer;
    Handler h = new Handler();
    GPSTracker gps;
    String result;
    private double lastLatitude = -1;
    private double lastLogitude = -1;
    private String appsLanguage = "";

    //facebook
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private String fcmTokenID="";


    private LoginButton loginButton;
//    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
    String social_name, social_email, social_image = "";
    String fbId = "", googleId = "", customerName, customerEmail;


    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        context = this;
        bindActivity();
        checkrunTimePermission();

        if (getIntent().hasExtra("language")) {
            appsLanguage = getIntent().getStringExtra("language");
            selectLaguage(appsLanguage);
        } else {
            selectLaguage(getResources().getString(R.string.danish_lang));
            //selectLaguage(getResources().getString(R.string.english_lang));

        }
        initListener();
        facebookHash();
//        CoLocator.start(this.getApplication(), Common.CO_LOCATOR_KEY);
        if (!checkgps()) {
           // permission();
        } else {
            gps = new GPSTracker(SigninActivity.this);
            gps.getLocation();
//             check if GPS enabled
            if (gps.canGetLocation()) {
                lastLatitude = gps.getLatitude();
                lastLogitude = gps.getLongitude();
            }

        }

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.clientId))
//                .requestEmail()
//                .build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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


        //todo:tanushree

        /*******************************Facebook************************************/
        mCallbackManager = CallbackManager.Factory.create();
        /*loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                FacebookLogIn();
            }
        });*/
    }

    private void checkrunTimePermission() {

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION,
                }, 108);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




//        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
//        Permissions.check(this, permissions, null, null, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                switchOnGPS();
//            }
//
//            @Override
//            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
//                super.onDenied(context, deniedPermissions);
//            }
//        });
//    }

//    private void switchOnGPS() {
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
//        builder.setAlwaysShow(true);
//        mLocationSettingsRequest = builder.build();
//
//        mSettingsClient = LocationServices.getSettingsClient(SigninActivity.this);
//
//        mSettingsClient
//                .checkLocationSettings(mLocationSettingsRequest)
//                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//                    @Override
//                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                        //Success Perform Task Here
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        int statusCode = ((ApiException) e).getStatusCode();
//                        switch (statusCode) {
//                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                try {
//                                    ResolvableApiException rae = (ResolvableApiException) e;
//                                    rae.startResolutionForResult(SigninActivity.this, REQUEST_CHECK_SETTINGS);
//                                } catch (IntentSender.SendIntentException sie) {
//                                    Log.e("GPS","Unable to execute request.");
//                                }
//                                break;
//                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                Log.e("GPS","Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
//                        }
//                    }
//                })
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//                        Log.e("GPS","checkLocationSettings -> onCanceled");
//                    }
//                });
//    }

    private void getUserProfile(AccessToken mAccessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                mAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.d("Graph Response", response.toString());

                        String myCustomizedResponse = response.getJSONObject().toString();

                        Log.d("fbresponce", response.getJSONObject().toString());

                        try {
                            JSONObject obj = new JSONObject(myCustomizedResponse);

                            String id = obj.getString("id");
                            String first_name = obj.getString("first_name");
                            String last_name = obj.getString("last_name");
                            String email = obj.getString("email");
                            String name = obj.getString("name");
                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                            System.out.println("image_url==>" + image_url);

                            if (image_url.equals("null")) {
                                social_image = "";

                            } else {
                                social_image = image_url;

                            }

                               /* try {
                                    URL profile_pic = new URL(
                                            "http://graph.facebook.com/" + id + "/picture?type=large");
                                    Log.i("profile_pic", profile_pic + "");
                                    social_image = object.getString(String.valueOf(profile_pic));

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }*/

                            social_name = name;
                            social_email = email;


                            Log.d("Id", id);
                            Log.d("FirstName", first_name);
                            Log.d("LastName", last_name);
                            Log.d("Email", email);

                            fbId = id;
                            customerName = first_name + " " + last_name;
                            customerEmail = email;
                            socialmedialoginFacebookApi();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void socialmedialoginFacebookApi() {

        dialog.show();
        String url = Common.BASE_URL + "facebook-login";
        AndroidNetworking.post(url)
                .addBodyParameter("emailAddress", social_email)
                .addBodyParameter("socialLoginId", social_name)
                .addBodyParameter("deviceType", "Android")
                .addBodyParameter("deviceToken", fcmTokenID)
                .addBodyParameter("latitude", String.valueOf(lastLatitude))
                .addBodyParameter("longtitude", String.valueOf(lastLogitude))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforFBLogin(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Toast.makeText(SigninActivity.this,"Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void FacebookLogIn() {
        Log.i("FacebookLogIn===","====>");
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //Toast.makeText(mContext, ""+loginResult, Toast.LENGTH_SHORT).show();
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);

                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);


            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(SigninActivity.this, "Facebook Login failed, Please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                //Toast.makeText(mContext, ""+exception, Toast.LENGTH_SHORT).show();
                System.out.println("exception===>" + exception);
                Toast.makeText(SigninActivity.this, "Error! Facebook Login failed, Please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void facebookHash() {
        // Add code to print out the key hash
        //todo:tanushree
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //Toast.makeText(mContext, ""+Base64.encodeToString(md.digest(), Base64.NO_WRAP), Toast.LENGTH_SHORT).show();

                Log.i("Base64", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }

       /* try {
            PackageInfo info = getPackageManager().getPackageInfo("com.romo.tonder.visit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                Log.i("Base64", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }*/
    }


    private void bindActivity() {
        loginButton = findViewById(R.id.login_button);


        background = findViewById(R.id.background);

        btn_login = findViewById(R.id.btnLogin);
        edt_username = findViewById(R.id.username);
        edt_password = findViewById(R.id.password);

        btn_registration = findViewById(R.id.btnRegistration);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();

        user = new User();
        btn_fbLogin = findViewById(R.id.btnFB);

        tvBody = findViewById(R.id.body);
        tvTitle = findViewById(R.id.title);
        tvForgotPwd = findViewById(R.id.tvForgetPassword);
        btn_eye = findViewById(R.id.btnEye);
        ll_increaseDecreaseArea = findViewById(R.id.increase_decrease_area);
        increase = findViewById(R.id.increase);
        decrease = findViewById(R.id.decrease);
        tvCountTextSize = findViewById(R.id.textSize);
        deviceID = getDeviceId(getApplicationContext());

        btn_googleLogin = findViewById(R.id.btnGoogleLogin);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        //todo:tanushree
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        mCallbackManager=CallbackManager.Factory.create();

        // Initialize Facebook Login button
    }


    private void initListener() {
        //TODO event onclick
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                throw new RuntimeException("app crashed..!!");
                if (checkValidation())
                    userLogin();

                //goToHomePage("");
            }
        });
        btn_googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkpermission()) {
                    signInGoogle();
                } else {
                    checkrunTimePermission();
                }

            }
        });

        btn_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        btn_fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.performClick();
                FacebookLogIn();


            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase.setClickable(true);
                increase.setEnabled(true);
                int c = count;
                if (c >= 0 && c < 2) {
                    c = c + 1;
                    tvCountTextSize.setText(String.valueOf(c));
                    count = c;
                    sizeIncrese(count);
                    System.out.print(count);
                } else {
                    increase.setClickable(false);
                    increase.setEnabled(false);
                }
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int c = count;
                if (c >= 0 && c != 0) {
                    c = c - 1;
                    tvCountTextSize.setText(String.valueOf(c));
                    count = c;
                    sizeIncrese(count);
                    System.out.print(count);
                    increase.setClickable(true);
                    increase.setEnabled(true);

                } else {
                    ll_increaseDecreaseArea.setVisibility(View.GONE);
                    System.out.print(count);
                    INVISIBLE = false;

                }
            }
        });
    }

    private boolean checkpermission() {

        int hasPermission = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else{
            return false;
        }
    }

    //facebook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("RREE", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (!user.getDisplayName().isEmpty()){
                                loginWithFB(user);
                            }else {
                                //  loginWithFB(null);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TokenFailed", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //loginWithFB(null);
                        }
                    }
                });
    }
    private void loginWithFB(final FirebaseUser user) {
        dialog.show();
        String url = Common.BASE_URL + "facebook-login";
        AndroidNetworking.post(url)
                .addBodyParameter("emailAddress", user.getEmail())
                .addBodyParameter("socialLoginId", user.getDisplayName())
                .addBodyParameter("deviceType", "Android")
                .addBodyParameter("deviceToken", fcmTokenID)
                .addBodyParameter("latitude", String.valueOf(lastLatitude))
                .addBodyParameter("longtitude", String.valueOf(lastLogitude))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseforFBLogin(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Toast.makeText(SigninActivity.this,"Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponseforFBLogin(JSONObject response) {
        dialog.dismiss();
        try {
            if (response.getString("status").equals("Login success")) {
                JSONObject object = response.getJSONObject("oData");
                String requiredSetupScreen = object.getString("requiredSetupScreen");
                String pre_lang = object.getString("language");


                //store data to prefre------
                SharedPreferences.Editor edit = appPrefs.edit();
                edit.putString("userID", object.getString("id"));
                edit.putString("emailID", object.getString("emailId"));
                edit.putString("userName",social_name);
                edit.putString("preferredLanguage", pre_lang);
                edit.putString("tokenKey", object.getString("tokenKey"));
                edit.putString("deviceID", deviceID);
                edit.putString("loginwith", "fb");
                edit.putBoolean("isLogin", Common.isLogedIn);
                selectLaguage(pre_lang);
                if (requiredSetupScreen.equals("Yes")) {
                    goToWelcomeScree();
                } else {
                    edit.putBoolean("isRegistration", Common.isRegistrer);
                    edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
                    goToHomePage(pre_lang);
                }
                edit.apply();
                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data); /*For  Facebook*/

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            // Signed in successfully, show authenticated UI.
            Log.i("getDisplayName ::","----"+account.getDisplayName());
            updateUI(account);
            Log.e(TAG, "handleSignInResult: account " + account);

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode())
//                    +"Failed Code ="+ e.getStatusCode() , Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
//            Log.e(TAG, "signInResult:failed msg=" +
//                    GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            String tokenID=account.getIdToken();
            String displayName=account.getDisplayName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            if (!deviceID.isEmpty())
                loginwithGoogle(tokenID,displayName,personEmail,personId);
            else {
                deviceID = getDeviceId(getApplicationContext());
                loginwithGoogle(tokenID,displayName,personEmail,personId);
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

    private boolean checkValidation() {
        if (edt_username.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enter_username), Toast.LENGTH_SHORT).show();
            return false;
        } else if (edt_password.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /*login with google credential*/
    private void loginwithGoogle(String tokenID, String displayName, String personEmail, String deviceID) {
        dialog.show();
        String url = Common.BASE_URL + "google-login";
        AndroidNetworking.post(url)
                .addBodyParameter("emailAddress", personEmail)
                .addBodyParameter("deviceType", "Android")
                .addBodyParameter("deviceToken", fcmTokenID)
                .addBodyParameter("latitude", String.valueOf(lastLatitude))
                .addBodyParameter("longtitude", String.valueOf(lastLogitude))
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        googleResponse(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                        Toast.makeText(SigninActivity.this,"Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void googleResponse(JSONObject response) {
        if (!SigninActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            if (response.getString("status").equals("Login success")) {
                JSONObject object = response.getJSONObject("oData");
                String requireScreen=object.getString("requiredSetupScreen");
                String pre_language=object.getString("language");
                //store data to prefre------
                SharedPreferences.Editor edit = appPrefs.edit();
                edit.putString("userID", object.getString("id"));
                edit.putString("emailID", object.getString("emailId"));
                edit.putString("userName", user.getDisplayName());
                edit.putString("preferredLanguage", pre_language);
                edit.putString("tokenKey", object.getString("tokenKey"));
                edit.putString("deviceID", deviceID);
                edit.putBoolean("isLogin", Common.isLogedIn);
                edit.putString("loginwith", "google");
                selectLaguage(object.getString("language"));
                if (requireScreen.equals("Yes")) {
                    goToWelcomeScree();
                } else {
                    edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
                    edit.putBoolean("isRegistration", Common.isRegistrer);
                    goToHomePage(pre_language);
                }
                edit.apply();
                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*normal login*/
    private void userLogin() {
        dialog.show();
        String url = Common.BASE_URL + "login";
        AndroidNetworking.post(url)
                .addBodyParameter("username", edt_username.getText().toString().trim())
                .addBodyParameter("password", edt_password.getText().toString().trim())
                .addBodyParameter("deviceType", "Android")
                .addBodyParameter("deviceToken", fcmTokenID)
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
                        if (!SigninActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void parseResponse(JSONObject response) {
        if (!SigninActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            if (response.getString("status").equals("loggedIn")) {
                String tokenKey = response.getString("token");
                String deviceToken = response.getString("deviceToken");
                //String userFirebaseID= response.getString("userFirebaseId");
                //String adminFirebaseID= response.getString("adminFirebaseId");
                JSONObject object = response.getJSONObject("oUserInfo");
                String requiredSetupScreen = object.getString("requiredSetupScreen");
                SharedPreferences.Editor edit = appPrefs.edit();
                edit.putString("userID", object.getString("userID"));
                edit.putString("userName", object.getString("userName"));
                edit.putString("preferredLanguage", object.getString("prefferedLanguage"));
                edit.putString("tokenKey", tokenKey);
                edit.putString("deviceID",deviceToken);
                //edit.putString("userFirebaseId",userFirebaseID);
                //edit.putString("adminFirebaseId",adminFirebaseID);
                edit.putString("password",edt_password.getText().toString().trim());
                edit.putBoolean("isLogin", Common.isLogedIn);
                if (requiredSetupScreen.equals("Yes")) {
                    goToWelcomeScree();
                } else {
                    edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
                    edit.putBoolean("isRegistration", Common.isRegistrer);
                    goToHomePage(object.getString("prefferedLanguage"));
                }
                selectLaguage(object.getString("prefferedLanguage"));
                edit.apply();
                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void goToHomePage(String language) {
        Intent nextHomePage = new Intent(getApplicationContext(), HomeActivity.class);
        nextHomePage.putExtra("language", language);
        startActivity(nextHomePage);
        finish();
    }

    private void goToWelcomeScree() {
        Intent nextWelcomeScreen = new Intent(getApplicationContext(), WelcomeScreenActivy.class);
        startActivity(nextWelcomeScreen);
        finish();
    }

    public boolean checkgps() {
        LocationManager lm = (LocationManager) SigninActivity.this.getSystemService(Context.LOCATION_SERVICE);
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


    public void sizeIncrese(int count) {
        int c = count;
        if (c == Common.ZERO) {
            tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbody0));
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.logintitle0));
            edt_username.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginedittxt0));
            edt_password.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginedittxt0));
            btn_login.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton0));
            btn_registration.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton0));
            tvForgotPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginrgistration0));
            btn_googleLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton0));
            btn_fbLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton0));
        } else if (c == Common.ONE) {
            tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbody1));
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.logintitle1));
            edt_username.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginedittxt1));
            edt_password.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginedittxt1));
            btn_login.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton1));
            btn_registration.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton1));
            tvForgotPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginrgistration1));
            btn_googleLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton1));
            btn_fbLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton1));
        } else if (c == Common.TWO) {
            tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbody2));
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.logintitle2));
            edt_username.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginedittxt2));
            edt_password.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginedittxt2));
            btn_login.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton2));
            btn_registration.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton2));
            tvForgotPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginrgistration2));
            btn_googleLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton2));
            btn_fbLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.loginbutton2));
        }

        /*txtSize = 00.0;
        txtSize = txtSize + 1;
        Double val = Double.valueOf((Float) tvBody.getTextSize());
        val = val + txtSize;
        System.out.print("old Size" + txtSize);
        System.out.print("current Size" + val);
        tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.loginbody1));
        System.out.print("new Size" + txtSize);*/

    }

    public void visibleDisible(View view) {
        if (INVISIBLE) {
            ll_increaseDecreaseArea.setVisibility(View.GONE);
            INVISIBLE = false;
        } else {
            INVISIBLE = true;
            ll_increaseDecreaseArea.setVisibility(View.VISIBLE);
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
        /*Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
    }
}
