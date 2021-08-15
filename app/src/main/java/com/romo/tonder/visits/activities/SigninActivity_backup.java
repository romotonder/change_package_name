//package com.romo.tonder.visit.activities;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.telephony.TelephonyManager;
//import android.util.Base64;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.androidnetworking.AndroidNetworking;
//import com.androidnetworking.common.Priority;
//import com.androidnetworking.error.ANError;
//import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
//import com.google.android.gms.common.SignInButton;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FacebookAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
//import com.romo.tonder.visit.MyApplication;
//import com.romo.tonder.visit.R;
//import com.romo.tonder.visit.helpers.Common;
////import com.romo.tonder.visit.helpers.GPSTracker;
//import com.romo.tonder.visit.helpers.LocaleManager;
//import com.romo.tonder.visit.models.User;
//
////import net.crowdconnected.androidcolocator.CoLocator;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import okhttp3.Response;
//
//public class SigninActivity_backup extends BaseActivity {
//    private static final String TAG = SigninActivity_backup.class.getSimpleName();
//    private static final int RC_SIGN_IN = 23;
//    private Context context;
//    private ImageView background;
//    private Button btn_login;
//    private Dialog dialog;
//    private EditText edt_username, edt_password;
//    private SharedPreferences appPrefs;
//    private GoogleSignInClient mGoogleSignInClient;
//    private User user;
//    private Button btn_googleLogin, btn_fbLogin, btn_registration;
//    private String deviceID = "";
////    private static final String[] PERMS = {
////            Manifest.permission.READ_PHONE_STATE,
////            Manifest.permission.ACCESS_FINE_LOCATION
////    };
//
//    private static final String EMAIL = "email";
//    private double txtSize;
//    private TextView tvTitle, tvBody, tvForgotPwd;
//    private LinearLayout ll_increaseDecreaseArea;
//    private ImageButton increase;
//    private ImageButton decrease;
//    private TextView tvCountTextSize;
//    private int count = 0;
//    private ImageButton btn_eye;
//    private boolean INVISIBLE = false;
//    Timer timer;
//    Handler h = new Handler();
////    GPSTracker gps;
//    String result;
////    private double lastLatitude = -1;
////    private double lastLogitude = -1;
//    double lastLatitude = 55.056595, lastLogitude = 8.824121;
//
//    private String appsLanguage = "";
//    SignInButton signInButton;
//
//    //facebook
//    private CallbackManager mCallbackManager;
//    private FirebaseAuth mAuth;
//
//    private String fcmTokenID="";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signin);
//        context = this;
//        bindActivity();
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            requestPermissions(PERMS, READ_PHONE_STATE);
////        } else {
////            requestPermission();
////        }
//        if (getIntent().hasExtra("language")) {
//            appsLanguage = getIntent().getStringExtra("language");
//            selectLaguage(appsLanguage);
//        } else {
//            selectLaguage(getResources().getString(R.string.danish_lang));
//        }
//        initListener();
//        //facebookHash();
//        CoLocator.start(this.getApplication(), Common.CO_LOCATOR_KEY);
//        if (!checkgps()) {
//            permission();
//        } else {
////            gps = new GPSTracker(SigninActivity_backup.this);
////            // check if GPS enabled
////            if (gps.canGetLocation()) {
////                lastLatitude = gps.getLatitude();
////                lastLogitude = gps.getLongitude();
////            }
//
//        }
///*        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.server_client_id))
//                .requestEmail()
//                .build();
//        // Build a GoogleSignInClient with the options specified by gso.
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/
//
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (task.isSuccessful()) {
//                            fcmTokenID = task.getResult().getToken();
//                            Log.d(TAG, "FIREBASE_TOKEN: "+fcmTokenID);
//                            System.out.print(fcmTokenID);
//                        }
//                    }
//                });
//    }
//
//    private void facebookHash() {
//        // Add code to print out the key hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.romo.tonder.visit",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e("KeyHash:", e.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("KeyHash:", e.toString());
//        }
//    }
//
//    private static final int READ_PHONE_STATE = 300;
//    private static final int REQUEST_FROM_LOCATION = 301;
//
//    private void bindActivity() {
//        background = findViewById(R.id.background);
//        /*Glide.with(context)
//                .load(R.drawable.backgrund_app)
//                .thumbnail(0.5f)
//                .into(background);*/
//
//        btn_login = findViewById(R.id.btnLogin);
//        edt_username = findViewById(R.id.username);
//        edt_password = findViewById(R.id.password);
//
//        btn_registration = findViewById(R.id.btnRegistration);
//
//        dialog = new Dialog(this);
//        dialog.setContentView(R.layout.item_loading);
//        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(false);
//        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
//
//        user = new User();
//        btn_fbLogin = findViewById(R.id.btnFB);
//
//        tvBody = findViewById(R.id.body);
//        tvTitle = findViewById(R.id.title);
//        tvForgotPwd = findViewById(R.id.tvForgetPassword);
//        btn_eye = findViewById(R.id.btnEye);
//        ll_increaseDecreaseArea = findViewById(R.id.increase_decrease_area);
//        increase = findViewById(R.id.increase);
//        decrease = findViewById(R.id.decrease);
//        tvCountTextSize = findViewById(R.id.textSize);
//        deviceID = getDeviceId(getApplicationContext());
//
//        btn_googleLogin = findViewById(R.id.btnGoogleLogin);
//        mAuth = FirebaseAuth.getInstance();
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        mCallbackManager=CallbackManager.Factory.create();
//
//
//    }
//
//    private void initListener() {
//        //TODO event onclick
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                throw new RuntimeException("app crashed..!!");
//                if (checkValidation())
//                    userLogin();
//
//                //goToHomePage("");
//            }
//        });
//        btn_googleLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (checkpermission()) {
//                    signInGoogle();
//                } else {
////                    requestPermission();
//                }
//
//            }
//        });
//
//        btn_registration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
//            }
//        });
//
//
//        btn_fbLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (checkpermission()) {
//                    /*if (!checkgps()) {
//                        permission();
//                    } else {
//                        gps = new GPSTracker(SigninActivity.this);
//                        // check if GPS enabled
//                        if (gps.canGetLocation()) {
//                            lastLatitude = gps.getLatitude();
//                            lastLogitude = gps.getLongitude();
//                        }*/
//                    LoginManager.getInstance().logInWithReadPermissions(SigninActivity_backup.this,
//                            Arrays.asList("public_profile","email"));
//                    LoginManager.getInstance().registerCallback(mCallbackManager,
//                            new FacebookCallback<LoginResult>() {
//                                @Override
//                                public void onSuccess(LoginResult loginResult) {
//                                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                                    handleFacebookAccessToken(loginResult.getAccessToken());
//                                }
//
//                                @Override
//                                public void onCancel() {
//                                    Log.d(TAG, "facebook:onCancel");
//                                    // ...
//                                }
//
//                                @Override
//                                public void onError(FacebookException error) {
//                                    Log.d(TAG, "facebook:onError", error);
//                                    // ...
//                                }
//                            });
//
//                        /*AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//                        callbackManager = CallbackManager.Factory.create();
//
//                        LoginManager.getInstance()
//                                .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                                    @Override
//                                    public void onSuccess(LoginResult loginResult) {
//                                        getUserDataFB(loginResult.getAccessToken());
//                                    }
//
//                                    @Override
//                                    public void onCancel() {
//                                        Log.e(TAG, "onCancel: ");
//                                    }
//
//                                    @Override
//                                    public void onError(FacebookException error) {
//                                        Log.e(TAG, "onError: " + error.getMessage());
//                                    }
//                                });
//                        LoginManager.getInstance().logInWithReadPermissions(SigninActivity.this,
//                                Arrays.asList("public_profile", "email", "user_birthday", "user_location"));*/
//
//                 //   }
//
//                } else {
////                    requestPermission();
//                }
//
//
//            }
//        });
//        increase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                increase.setClickable(true);
//                increase.setEnabled(true);
//                int c = count;
//                if (c >= 0 && c < 2) {
//                    c = c + 1;
//                    tvCountTextSize.setText(String.valueOf(c));
//                    count = c;
//                    sizeIncrese(count);
//                    System.out.print(count);
//                } else {
//                    increase.setClickable(false);
//                    increase.setEnabled(false);
//                }
//            }
//        });
//        decrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int c = count;
//                if (c >= 0 && c != 0) {
//                    c = c - 1;
//                    tvCountTextSize.setText(String.valueOf(c));
//                    count = c;
//                    sizeIncrese(count);
//                    System.out.print(count);
//                    increase.setClickable(true);
//                    increase.setEnabled(true);
//
//                } else {
//                    ll_increaseDecreaseArea.setVisibility(View.GONE);
//                    System.out.print(count);
//                    INVISIBLE = false;
//
//                }
//            }
//        });
//    }
//    //facebook
//    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d("RREE", "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("TokenSuccess", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            if (!user.getDisplayName().isEmpty()){
//                                loginWithFB(user);
//                            }else {
//                                loginWithFB(null);
//                            }
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("TokenFailed", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(SigninActivity_backup.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            loginWithFB(null);
//                        }
//                    }
//                });
//    }
//    /*private void getUserDataFB(AccessToken accessToken) {
//        Log.e(TAG, "getUserDataFB: " + accessToken.getToken());
//        GraphRequest request = GraphRequest.newMeRequest(accessToken,
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.e("LoginActivity", response.toString());
//                        // Application code
//                        try {
//                            Log.e(TAG, "onCompleted: profile http://graph.facebook.com/" +
//                                    object.getString("id") + "/picture?type=large");
//
//                            user.setDisplayName(object.getString("name"));
//                            user.setUserEmail(object.getString("email"));
//                            user.setUserID(object.getString("id"));
//                            user.setSocialPlatform("Facebook");
//                            if (object.has("birthday"))
//                                user.setBirthDay(object.getString("birthday"));
//                            Uri photo = Uri.parse("http://graph.facebook.com/" +
//                                    object.getString("id") + "/picture?type=large");
//                            user.setPhotoURI(photo);
//
//                            if (!deviceID.isEmpty())
//                                loginWithFB(deviceID);
//                            else {
//                                deviceID = getDeviceId(getApplicationContext());
//                                loginWithFB(deviceID);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e(TAG, "onCompleted: " + e.getMessage());
//                        }
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,email");
//        request.setParameters(parameters);
//        request.executeAsync();
//    }*/
//
//    private void loginWithFB(final FirebaseUser user) {
//        dialog.show();
//        String url = Common.BASE_URL + "facebook-login";
//        AndroidNetworking.post(url)
//                .addBodyParameter("emailAddress", user.getEmail())
//                .addBodyParameter("socialLoginId", user.getDisplayName())
//                .addBodyParameter("deviceType", "Facebook")
//                .addBodyParameter("deviceToken", deviceID)
//                .addBodyParameter("latitude", String.valueOf(lastLatitude))
//                .addBodyParameter("longtitude", String.valueOf(lastLogitude))
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(Response okHttpResponse, JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        parseResponseforFBLogin(response,user);
//                    }
//                    @Override
//                    public void onError(ANError anError) {
//                        dialog.dismiss();
//                        Toast.makeText(SigninActivity_backup.this,"Please try again later", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//                    }
//                });
//
//    }
//
//    private void parseResponseforFBLogin(JSONObject response,FirebaseUser user) {
//        dialog.dismiss();
//        try {
//            if (response.getString("status").equals("Login success")) {
//                JSONObject object = response.getJSONObject("oData");
//                String requiredSetupScreen = object.getString("requiredSetupScreen");
//                String pre_lang = object.getString("language");
//
//
//                //store data to prefre------
//                SharedPreferences.Editor edit = appPrefs.edit();
//                edit.putString("userID", object.getString("id"));
//                edit.putString("emailID", object.getString("emailId"));
//                edit.putString("userName",user.getDisplayName());
//                edit.putString("preferredLanguage", pre_lang);
//                edit.putString("tokenKey", object.getString("tokenKey"));
//                edit.putString("deviceID", deviceID);
//                edit.putBoolean("isLogin", Common.isLogedIn);
//                selectLaguage(pre_lang);
//                if (requiredSetupScreen.equals("Yes")) {
//                    goToWelcomeScree();
//                } else {
//                    edit.putBoolean("isRegistration", Common.isRegistrer);
//                    edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
//                    goToHomePage(pre_lang);
//                }
//                edit.apply();
//                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
//            } else {
//                String failureMsg = response.getString("msg");
//                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    private void signInGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (mCallbackManager != null){
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
//        //super.onActivityResult(requestCode, resultCode, data);
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            String idToken = account.getIdToken();
//            // Signed in successfully, show authenticated UI.
//            updateUI(account);
//            Log.e(TAG, "handleSignInResult: account " + account);
//
//        } catch (ApiException e) {
//            Toast.makeText(context, GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode())
//                            +"Failed Code ="+ e.getStatusCode() , Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
//            Log.e(TAG, "signInResult:failed msg=" +
//                    GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
//            updateUI(null);
//        }
//    }
//
//    private void updateUI(GoogleSignInAccount account) {
//        if (account != null) {
//            String tokenID=account.getIdToken();
//            String displayName=account.getDisplayName();
//            String personEmail = account.getEmail();
//            String personId = account.getId();
//            if (!deviceID.isEmpty())
//                loginwithGoogle(tokenID,displayName,personEmail,personId);
//            else {
//                deviceID = getDeviceId(getApplicationContext());
//                loginwithGoogle(tokenID,displayName,personEmail,personId);
//            }
//        }
//
//    }
//
//    @SuppressLint("MissingPermission")
//    public String getDeviceId(Context context) {
//        String deviceId = "";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            deviceId = Settings.Secure.getString(
//                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        } else {
//            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            deviceId = mngr.getDeviceId();
//        }
//        return deviceId;
//    }
//
//    private boolean checkValidation() {
//        if (edt_username.getText().toString().trim().isEmpty()) {
//            Toast.makeText(this, getResources().getString(R.string.enter_username), Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (edt_password.getText().toString().trim().isEmpty()) {
//            Toast.makeText(this, getResources().getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    /*login with google credential*/
//    private void loginwithGoogle(String tokenID, String displayName, String personEmail, String deviceID) {
//        dialog.show();
//        String url = Common.BASE_URL + "google-login";
//        AndroidNetworking.post(url)
//                .addBodyParameter("emailAddress", personEmail)
//                .addBodyParameter("deviceType", "Google")
//                .addBodyParameter("deviceToken", deviceID)
//                .addBodyParameter("latitude", String.valueOf(lastLatitude))
//                .addBodyParameter("longtitude", String.valueOf(lastLogitude))
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(Response okHttpResponse, JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        googleResponse(response);
//                    }
//                    @Override
//                    public void onError(ANError anError) {
//                        dialog.dismiss();
//                        Toast.makeText(SigninActivity_backup.this,"Please try again later", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//                    }
//                });
//    }
//
//    private void googleResponse(JSONObject response) {
//        dialog.dismiss();
//        try {
//            if (response.getString("status").equals("Login success")) {
//                JSONObject object = response.getJSONObject("oData");
//                String requireScreen=object.getString("requiredSetupScreen");
//                String pre_language=object.getString("language");
//                //store data to prefre------
//                SharedPreferences.Editor edit = appPrefs.edit();
//                edit.putString("userID", object.getString("id"));
//                edit.putString("emailID", object.getString("emailId"));
//                edit.putString("userName", user.getDisplayName());
//                edit.putString("preferredLanguage", pre_language);
//                edit.putString("tokenKey", object.getString("tokenKey"));
//                edit.putString("deviceID", deviceID);
//                edit.putBoolean("isLogin", Common.isLogedIn);
//                selectLaguage(object.getString("language"));
//                if (requireScreen.equals("Yes")) {
//                   goToWelcomeScree();
//                } else {
//                    edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
//                    edit.putBoolean("isRegistration", Common.isRegistrer);
//                    goToHomePage(pre_language);
//                }
//                edit.apply();
//                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
//            } else {
//                String failureMsg = response.getString("msg");
//                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /*normal login*/
//    private void userLogin() {
//        dialog.show();
//        String url = Common.BASE_URL + "login";
//        AndroidNetworking.post(url)
//                .addBodyParameter("username", edt_username.getText().toString().trim())
//                .addBodyParameter("password", edt_password.getText().toString().trim())
//                .addBodyParameter("deviceType", "Android")
//                .addBodyParameter("deviceToken", fcmTokenID)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(Response okHttpResponse, JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        parseResponse(response);
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        if (!SigninActivity_backup.this.isFinishing() && dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//                    }
//                });
//    }
//
//    private void parseResponse(JSONObject response) {
//        if (!SigninActivity_backup.this.isFinishing() && dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//        try {
//            if (response.getString("status").equals("loggedIn")) {
//                String tokenKey = response.getString("token");
//                String deviceToken = response.getString("deviceToken");
//                //String userFirebaseID= response.getString("userFirebaseId");
//                //String adminFirebaseID= response.getString("adminFirebaseId");
//                JSONObject object = response.getJSONObject("oUserInfo");
//                String requiredSetupScreen = object.getString("requiredSetupScreen");
//                SharedPreferences.Editor edit = appPrefs.edit();
//                edit.putString("userID", object.getString("userID"));
//                edit.putString("userName", object.getString("userName"));
//                edit.putString("preferredLanguage", object.getString("prefferedLanguage"));
//                edit.putString("tokenKey", tokenKey);
//                edit.putString("deviceID",deviceToken);
//                //edit.putString("userFirebaseId",userFirebaseID);
//                //edit.putString("adminFirebaseId",adminFirebaseID);
//                edit.putString("password",edt_password.getText().toString().trim());
//                edit.putBoolean("isLogin", Common.isLogedIn);
//                if (requiredSetupScreen.equals("Yes")) {
//                    goToWelcomeScree();
//                } else {
//                    edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
//                    edit.putBoolean("isRegistration", Common.isRegistrer);
//                    goToHomePage(object.getString("prefferedLanguage"));
//                }
//                selectLaguage(object.getString("prefferedLanguage"));
//                edit.apply();
//                Toast.makeText(this, response.getString("status"), Toast.LENGTH_SHORT).show();
//            } else {
//                String failureMsg = response.getString("msg");
//                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void goToHomePage(String language) {
//        Intent nextHomePage = new Intent(getApplicationContext(), HomeActivity.class);
//        nextHomePage.putExtra("language", language);
//        startActivity(nextHomePage);
//        finish();
//    }
//
//    private void goToWelcomeScree() {
//        Intent nextWelcomeScreen = new Intent(getApplicationContext(), WelcomeScreenActivy.class);
//        startActivity(nextWelcomeScreen);
//        finish();
//    }
//
//    public boolean checkgps() {
//        LocationManager lm = (LocationManager) SigninActivity_backup.this.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    public void permission() {
//        if (!checkgps()) {
//            // notify user
//            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(SigninActivity_backup.this);
//            dialog.setMessage("Location Settings is turned off!! Please turn it on.");
//            dialog.setCancelable(false);
//            dialog.setPositiveButton("GO To Setup", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(myIntent);
//                    //get gps
//                }
//            });
//
//            dialog.show();
//
//        }
//        //start a timer to disable the start button for 4 seconds
//        timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                h.post(new Runnable() {
//                    public void run() {
//                        //btnSubmit.setEnabled(true);
//                        //Common.enableButton(btnSubmit, true);
//                        timer.cancel();
//                    }
//                });
//            }
//        };
//        timer.schedule(timerTask, 0, 2000);
//    }
//
////    private void requestPermission() {
////        ActivityCompat.requestPermissions(SigninActivity_backup.this, PERMS, READ_PHONE_STATE);
////        ActivityCompat.requestPermissions(SigninActivity_backup.this, PERMS, REQUEST_FROM_LOCATION);
////
////    }
//
//    /* ============= PERMISSION SETTINGS ================================== */
////    @Override
////    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
////        if (requestCode == READ_PHONE_STATE && grantResults.length > 0) {
////            if (grantResults[0] != PackageManager.PERMISSION_GRANTED
////                    || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
////
////                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_service_enable),
////                            Toast.LENGTH_SHORT).show();
////                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_service_enable),
////                            Toast.LENGTH_SHORT).show();
////                }
////
////            } else {
////                return;
////            }
////
////        }
////    }
//
//    public boolean checkpermission() {
////        int readPhonestate = ActivityCompat.checkSelfPermission(SigninActivity_backup.this,
////                Manifest.permission.READ_PHONE_STATE);
////        int location = ActivityCompat.checkSelfPermission(getApplicationContext(),
////                Manifest.permission.ACCESS_FINE_LOCATION);
////
////        if (readPhonestate == PackageManager.PERMISSION_GRANTED
////                && location == PackageManager.PERMISSION_GRANTED) {
////            return true;
////        } else {
//            return false;
////        }
//
//    }
//
//    public void sizeIncrese(int count) {
//        int c = count;
//        if (c == Common.ZERO) {
//            tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbody0));
//            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.logintitle0));
//            edt_username.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginedittxt0));
//            edt_password.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginedittxt0));
//            btn_login.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton0));
//            btn_registration.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton0));
//            tvForgotPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginrgistration0));
//            btn_googleLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton0));
//            btn_fbLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton0));
//        } else if (c == Common.ONE) {
//            tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbody1));
//            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.logintitle1));
//            edt_username.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginedittxt1));
//            edt_password.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginedittxt1));
//            btn_login.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton1));
//            btn_registration.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton1));
//            tvForgotPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginrgistration1));
//            btn_googleLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton1));
//            btn_fbLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton1));
//        } else if (c == Common.TWO) {
//            tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbody2));
//            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.logintitle2));
//            edt_username.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginedittxt2));
//            edt_password.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginedittxt2));
//            btn_login.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton2));
//            btn_registration.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton2));
//            tvForgotPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginrgistration2));
//            btn_googleLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton2));
//            btn_fbLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.loginbutton2));
//        }
//
//        /*txtSize = 00.0;
//        txtSize = txtSize + 1;
//        Double val = Double.valueOf((Float) tvBody.getTextSize());
//        val = val + txtSize;
//        System.out.print("old Size" + txtSize);
//        System.out.print("current Size" + val);
//        tvBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                getResources().getDimension(R.dimen.loginbody1));
//        System.out.print("new Size" + txtSize);*/
//
//    }
//
//    public void visibleDisible(View view) {
//        if (INVISIBLE) {
//            ll_increaseDecreaseArea.setVisibility(View.GONE);
//            INVISIBLE = false;
//        } else {
//            INVISIBLE = true;
//            ll_increaseDecreaseArea.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /*apps laguage section*/
//    private void selectLaguage(String appsLanguage) {
//        if (appsLanguage.equals(getResources().getString(R.string.english_lang))) {
//            setNewLocale(this, LocaleManager.ENGLISH);
//        }
//        if (appsLanguage.equals(getResources().getString(R.string.danish_lang))) {
//            setNewLocale(this, LocaleManager.DANISH);
//        }
//        if (appsLanguage.equals(getResources().getString(R.string.german_lang))) {
//            setNewLocale(this, LocaleManager.GERMAN);
//        }
//    }
//
//    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
//        LocaleManager.setNewLocale(this, language);
//        /*Intent intent = mContext.getIntent();
//        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
//    }
//}
