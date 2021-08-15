package com.romo.tonder.visits.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.CustomAdapter;
import com.romo.tonder.visits.adapters.InterestAdapter;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfileActivity2 extends BaseActivity implements PreferOnClickListener {
    private static final String TAG = EditProfileActivity2.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY_Avtar = 200;
    private static final int REQUEST_GALLERY_Cover = 201;
    private Dialog dialog;
    private SharedPreferences appPrefs;
    private TextInputEditText first_name, last_name,dis_name,email_add,position,intro,address,contact_phone,
            userwebsite,language;
    private LinearLayout save_area;
    private ProfileData profileData;
    private List<Pmodel> plisting;
    private ArrayList<InterestModels>preferList;
    private ListView preferenceArea;
    private CustomAdapter adapter;
    private InterestAdapter iadapter;
    private List<InterestModels> interestList;
    private Spinner language_dropdown;

    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //Manifest.permission.CAMERA
    };
    private String file_path_avtar = null;
    private String file_path_cover = null;
    private RecyclerView preferenceRecycler;
    private PrefrenceAdapter preAdapter;
    private String finalPreferList="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        binding();
        File dir = new File(Common.ImgCompressedFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        getProfileData(appPrefs.getString("userID",""));


        ArrayAdapter<String> editiondataAdapter = new ArrayAdapter<String>(this, R.layout.dropdown,
                getResources().getStringArray(R.array.language_choice));
        editiondataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_dropdown.setAdapter(editiondataAdapter);
        language_dropdown.setPrompt(getResources().getString(R.string.select_lang));
        language_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profileData.prefferedLanguage="";
                profileData.prefferedLanguage = parent.getItemAtPosition(position).toString();
                /*TextView tvdrop;
                tvdrop = view.findViewById(R.id.tvdrop);
                // First item is disable and it is used for hint
                *//*if (position == 0) {

                    tvdrop.setTextColor(getResources().getColor(R.color.Gray));
                    Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
                }*//*
                if (position >= 0) {
                    // Notify the selected item text
                    tvdrop.setTextColor(getResources().getColor(R.color.grey_font));
                    //changeLanguage(selectedLanguage);
                    Toast.makeText(getApplicationContext(), "Selected : "
                            + profileData.prefferedLanguage, Toast.LENGTH_SHORT).show();
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Nothing Selected",
                        Toast.LENGTH_SHORT).show();

            }
        });

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
                    }
                    if (!jBasicInfo.getString("userEmail").equals("")){
                        profileData.userEmail=jBasicInfo.getString("userEmail");
                    }
                    if (!jBasicInfo.getString("avtarImage").equals("")){
                        profileData.avtarImage=jBasicInfo.getString("avtarImage");
                    }
                    if (!jBasicInfo.getString("coverImage").equals("")){
                        profileData.coverImage=jBasicInfo.getString("coverImage");
                    }
                    if (!jBasicInfo.getString("introduction").equals("")){
                        profileData.introduction=jBasicInfo.getString("introduction");
                    }
                    if (!jBasicInfo.getString("prefferedLanguage").equals("")){
                        profileData.prefferedLanguage=jBasicInfo.getString("prefferedLanguage");
                        //profileData.prefferedLanguage="English";
                    }
                    if (!jfollowContact.getString("address").equals("")){
                        profileData.useraddress=jfollowContact.getString("address");
                    }
                    if (!jfollowContact.getString("phone").equals("")){
                        profileData.phone=jfollowContact.getString("phone");
                    }
                    if (!jfollowContact.getString("website").equals("")){
                        profileData.website=jfollowContact.getString("website");
                    }
                    interestArray=jsonObject.getJSONArray("areaOfInterest");
                    if (interestArray!=null){
                        JSONObject jObj=null;
                        for (int i = 0; i < interestArray.length(); i++) {
                            jObj = interestArray.getJSONObject(i);
                            String preferID = jObj.getString("term_id");
                            String preferName = jObj.getString("name");
                            String preferSelect = jObj.getString("selected");
                            int viewType=2;

                            InterestModels bean=new InterestModels(preferSelect,preferName,preferID,viewType);
                            preferList.add(bean);
                            if (preferList.size()>0){
                                preAdapter=new PrefrenceAdapter(preferList,this,this);
                                preferenceRecycler.setAdapter(preAdapter);
                            }
                            /*Pmodel pmodel = new Pmodel(preferSelect, preferName, preferID);
                            plisting.add(pmodel);
                            adapter=new CustomAdapter(this, plisting);
                            preferenceArea.setAdapter(adapter);
                            preferenceArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });*/
                        }

                    }

                }
                populateProfile(profileData.firstName,profileData.lastName,profileData.displayName, profileData.useraddress,profileData.phone,
                        profileData.website,profileData.prefferedLanguage,profileData.userposition,profileData.introduction);
                //Toast.makeText(this, response.getString("status")+"sauvik", Toast.LENGTH_SHORT).show();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void binding() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();
        first_name=(TextInputEditText)findViewById(R.id.editFname);
        last_name=(TextInputEditText)findViewById(R.id.editLname);
        dis_name=(TextInputEditText)findViewById(R.id.editDname);
        email_add=(TextInputEditText)findViewById(R.id.editEmail);
        position=(TextInputEditText)findViewById(R.id.editPosition);
        intro=(TextInputEditText)findViewById(R.id.editIntro);
        address=(TextInputEditText)findViewById(R.id.editAddress);
        contact_phone=(TextInputEditText)findViewById(R.id.editPhone);
        userwebsite=(TextInputEditText)findViewById(R.id.editWebsite);
        //save
        save_area=(LinearLayout)findViewById(R.id.saveArea);
        //dropdown
        language_dropdown = findViewById(R.id.spnLanguage);
        profileData=new ProfileData();

        preferenceRecycler=(RecyclerView)findViewById(R.id.preferenceArea);
        preferenceRecycler.setLayoutManager(new LinearLayoutManager(this));
        preferList=new ArrayList<>();
        preAdapter=new PrefrenceAdapter(preferList,this,this);
        preferenceRecycler.setAdapter(preAdapter);
    }

    public void updateProfile(View view) {
        ArrayList<InterestModels> list=preAdapter.getSelectedItemList();
        finalPreferList=getPreferList(list);
        /*if (file_path_avtar !=null && file_path_cover!=null){
            //upload both file
            if (uploadBothFile(file_path_avtar, file_path_avtar)){
                showMessage();
            }
        } else*/
        if (file_path_avtar!=null) {
            //upload avtar file
            if (uploadAvtarFile(file_path_avtar)){
                showMessage();
            }
        }else if (file_path_cover!=null){
            //upload cover file
            if (uploadCoverFile(file_path_cover)){
                showMessage();
            }
        }else {
            //without file
            profileUpdate("174");
        }

    }

    private void showMessage() {
        Toast.makeText(this, R.string.profile_update_success, Toast.LENGTH_SHORT).show();
    }

    private String getPreferList(ArrayList<InterestModels> list){
        ArrayList<String>items = null;
        String totalPreferLit="";
        items=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            InterestModels model = list.get(i);
            if (model.getPselected().equals("Yes")) {
                items.add(model.getP_id());
            }
        }
        if (items.size() >= 1) {
            totalPreferLit = TextUtils.join(",", items);

        } else {
            totalPreferLit="";
            return totalPreferLit;
        }
        return totalPreferLit;
    }



    private void profileUpdate(String s) {

        dialog.show();
        String url = Common.BASE_URL + "update-profile";
        AndroidNetworking.post(url)
                .addBodyParameter("userId", appPrefs.getString("userID",""))
                .addBodyParameter("firstName", first_name.getText().toString())
                .addBodyParameter("lastName", last_name.getText().toString())
                .addBodyParameter("displayName", dis_name.getText().toString())
                .addBodyParameter("address", address.getText().toString())
                .addBodyParameter("phone", contact_phone.getText().toString())
                .addBodyParameter("website", userwebsite.getText().toString())
                .addBodyParameter("position", position.getText().toString())
                .addBodyParameter("introduction", intro.getText().toString())
                .addBodyParameter("preference", finalPreferList)
                .addBodyParameter("prefferedLanguage", profileData.prefferedLanguage)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponseUpadte(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }
    private void parseResponseUpadte(JSONObject response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject=null;
            JSONObject jBasicInfo=null;
            JSONObject jfollowContact=null;
            JSONArray interestArray=null;
            if (response.getString("status").equals("Success")) {
                jsonObject=response.getJSONObject("oData");
                SharedPreferences.Editor edit = appPrefs.edit();
                edit.putString("preferredLanguage", profileData.prefferedLanguage);
                edit.apply();
                selectLaguage(profileData.prefferedLanguage);
                Toast.makeText(this, R.string.profile_update_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, new Intent());
                finish();
            } else {
                String failureMsg = response.getString("msg");
                Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void populateProfile(String firstName, String lastName, String displayName, String useraddress,  String phone, String website,
                                 String language,String userposition, String introduction) {
        first_name.setText(firstName);
        last_name.setText(lastName);
        dis_name.setText(displayName);
        address.setText(useraddress);
        contact_phone.setText(phone);
        userwebsite.setText(website);
        int dropdownID=0;
        if (language.equals("Danish")){
            dropdownID=Common.LANG_DANISH;
        }else if (language.equals("English")) {
            dropdownID=Common.LANG_ENGLISH;
        }else {
            dropdownID=Common.LANG_GERMAN;
        }
        language_dropdown.setSelection(dropdownID-1);
        position.setText(userposition);
        intro.setText(introduction);

    }

    public void back(View view) {
        finish();
    }

    public void selectLanguage(View view) {
        language_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profileData.prefferedLanguage="";
                profileData.prefferedLanguage = parent.getItemAtPosition(position).toString();
                TextView tvdrop;
                tvdrop = view.findViewById(R.id.tvdrop);
                // First item is disable and it is used for hint
                /*if (position == 0) {

                    tvdrop.setTextColor(getResources().getColor(R.color.Gray));
                    Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
                }*/
                if (position >= 0) {
                    // Notify the selected item text
                    tvdrop.setTextColor(getResources().getColor(R.color.title_text_color));
                    //changeLanguage(selectedLanguage);
                    Toast.makeText(getApplicationContext(), "Selected : "
                            + profileData.prefferedLanguage, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Nothing Selected",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void attachAvrtar(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                filePickerAvtar();
            } else {
                requestPermission();
            }
        }

    }
    public void attachCoverImg(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                filePickerCover();
            } else {
                requestPermission();
            }
        }

    }

    private void filePickerAvtar() {
        Intent opengallery = new Intent(Intent.ACTION_PICK);
        opengallery.setType("image/*");
        startActivityForResult(opengallery, REQUEST_GALLERY_Avtar);
    }
    private void filePickerCover() {
        Intent opengallery = new Intent(Intent.ACTION_PICK);
        opengallery.setType("image/*");
        startActivityForResult(opengallery, REQUEST_GALLERY_Cover);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_CANCELED && data != null && data.getData() != null) {
            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_Avtar) {
                String filePath =getRealPathFromUri(data.getData(), EditProfileActivity2.this);
                Log.d("File Path : ", " " + filePath);
                this.file_path_avtar = filePath;
                //tvFileName.setText(file.getName());
            }else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_Cover) {
                String filePath = getRealPathFromUri(data.getData(), EditProfileActivity2.this);
                Log.d("File Path : ", " " + filePath);
                this.file_path_cover = filePath;

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getRealPathFromUri(Uri uri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = activity.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private boolean uploadBothFile(String avtarpath,String coverpath) {
        dialog.show();
        File avtarfile = new File(avtarpath);
        File fCompressed_avtar = new Compressor.Builder(EditProfileActivity2.this).setMaxWidth(Common.maxwidth).setMaxHeight(Common.maxheight).setQuality(Common.quality).setCompressFormat(Bitmap.CompressFormat.JPEG).setDestinationDirectoryPath(Common.ImgCompressedFilePath).build().compressToFile(avtarfile);
        String fCompressedd_avtarfile = avtarfile.getName();
        System.out.print(fCompressedd_avtarfile);

        File coverfile = new File(coverpath);
        File fCompressed_cover = new Compressor.Builder(EditProfileActivity2.this).setMaxWidth(Common.maxwidth).setMaxHeight(Common.maxheight).setQuality(Common.quality).setCompressFormat(Bitmap.CompressFormat.JPEG).setDestinationDirectoryPath(Common.ImgCompressedFilePath).build().compressToFile(coverfile);
        String fCompressedd_coverfile = avtarfile.getName();
        System.out.print(fCompressedd_coverfile);
        try {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("avtarImage", fCompressed_avtar.getName(), RequestBody.create(MediaType.parse("image/*"), fCompressed_avtar))
                    .addFormDataPart("coverImage", fCompressed_cover.getName(), RequestBody.create(MediaType.parse("image/*"), fCompressed_cover))
                    .addFormDataPart("userId", appPrefs.getString("userID",""))
                    .addFormDataPart("firstName", first_name.getText().toString())
                    .addFormDataPart("lastName", last_name.getText().toString())
                    .addFormDataPart("displayName", dis_name.getText().toString())
                    .addFormDataPart("address", address.getText().toString())
                    .addFormDataPart("phone", contact_phone.getText().toString())
                    .addFormDataPart("website", userwebsite.getText().toString())
                    .addFormDataPart("position", position.getText().toString())
                    .addFormDataPart("introduction", intro.getText().toString())
                    .addFormDataPart("preference", finalPreferList)
                    .addFormDataPart("prefferedLanguage", profileData.prefferedLanguage)
                    .build();

            Request request = new Request.Builder()
                    .url(Common.BASE_URL + "update-profile")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.MINUTES);
            builder.readTimeout(30, TimeUnit.MINUTES);
            builder.writeTimeout(30, TimeUnit.MINUTES);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onFailure: " + e.getMessage());
                    Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
                    dialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        response.body().string();
                        System.out.print("");
                        setResult(RESULT_OK, new Intent());
                        dialog.dismiss();
                        finish();
                    } else {
                        System.out.print("");
                        dialog.dismiss();
                    }
                    Log.e(TAG, "onResponse: " + call);
                    Log.e(TAG, "onResponse: " + response.body());
                    Log.e(TAG, "onResponse: " + response.message());
                }

            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    private boolean uploadAvtarFile(String file_path) {
        dialog.show();
        File file = new File(file_path);
        File fCompressed = new Compressor.Builder(EditProfileActivity2.this).setMaxWidth(Common.maxwidth)
                .setMaxHeight(Common.maxheight).setQuality(Common.quality).setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(Common.ImgCompressedFilePath).build().compressToFile(file);
        String fCompressedd_avtarfile = file.getName();
        System.out.print(fCompressedd_avtarfile);
        try {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("avtarImage", fCompressed.getName(), RequestBody.create(MediaType.parse("image/*"), fCompressed))
                    //.addFormDataPart("avtarImage", file.getName(), RequestBody.create(MediaType.parse("image/*"), file_path))
                    .addFormDataPart("userId", appPrefs.getString("userID",""))
                    //.addFormDataPart("userId", "174")
                    .addFormDataPart("firstName", first_name.getText().toString())
                    .addFormDataPart("lastName", last_name.getText().toString())
                    .addFormDataPart("displayName", dis_name.getText().toString())
                    .addFormDataPart("address", address.getText().toString())
                    .addFormDataPart("phone", contact_phone.getText().toString())
                    .addFormDataPart("website", userwebsite.getText().toString())
                    .addFormDataPart("position", position.getText().toString())
                    .addFormDataPart("introduction", intro.getText().toString())
                    .addFormDataPart("preference", finalPreferList)
                    .addFormDataPart("prefferedLanguage", profileData.prefferedLanguage)
                    .build();

            Request request = new Request.Builder()
                    .url(Common.BASE_URL + "update-profile")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.MINUTES);
            builder.readTimeout(30, TimeUnit.MINUTES);
            builder.writeTimeout(30, TimeUnit.MINUTES);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onFailure: " + e.getMessage());
                    Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
                    //Toast.makeText(getApplicationContext(), "Netork error launch complain", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        response.body().string();
                        SharedPreferences.Editor edit = appPrefs.edit();
                        edit.putString("preferredLanguage", profileData.prefferedLanguage);
                        edit.apply();
                        selectLaguage(profileData.prefferedLanguage);
                        System.out.print("");
                        //Toast.makeText(getApplicationContext(), "Successfully launch your complain", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, new Intent());
                        finish();//go home
                    } else {
                        System.out.print("");
                        dialog.dismiss();
                    }
                    Log.e(TAG, "onResponse: " + call);
                    Log.e(TAG, "onResponse: " + response.body());
                    Log.e(TAG, "onResponse: " + response.message());
                }

            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    private boolean uploadCoverFile(String file_path) {
        dialog.show();
        File file = new File(file_path);
        File fCompressed = new Compressor.Builder(EditProfileActivity2.this).setMaxWidth(Common.maxwidth).setMaxHeight(Common.maxheight).setQuality(Common.quality).setCompressFormat(Bitmap.CompressFormat.JPEG).setDestinationDirectoryPath(Common.ImgCompressedFilePath).build().compressToFile(file);
        String fCompressedd_avtarfile = file.getName();
        System.out.print(fCompressedd_avtarfile);
        try {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("coverImage", fCompressed.getName(), RequestBody.create(MediaType.parse("image/*"), fCompressed))
                    .addFormDataPart("userId", appPrefs.getString("userID",""))
                    //.addFormDataPart("userId", "174")
                    .addFormDataPart("firstName", first_name.getText().toString())
                    .addFormDataPart("lastName", last_name.getText().toString())
                    .addFormDataPart("displayName", dis_name.getText().toString())
                    .addFormDataPart("address", address.getText().toString())
                    .addFormDataPart("phone", contact_phone.getText().toString())
                    .addFormDataPart("website", userwebsite.getText().toString())
                    .addFormDataPart("position", position.getText().toString())
                    .addFormDataPart("introduction", intro.getText().toString())
                    .addFormDataPart("preference", finalPreferList)
                    .addFormDataPart("prefferedLanguage", profileData.prefferedLanguage)
                    .build();

            Request request = new Request.Builder()
                    .url(Common.BASE_URL + "update-profile")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.MINUTES);
            builder.readTimeout(30, TimeUnit.MINUTES);
            builder.writeTimeout(30, TimeUnit.MINUTES);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onFailure: " + e.getMessage());
                    dialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        response.body().string();
                        SharedPreferences.Editor edit = appPrefs.edit();
                        edit.putString("preferredLanguage", profileData.prefferedLanguage);
                        edit.apply();
                        selectLaguage(profileData.prefferedLanguage);
                        setResult(RESULT_OK, new Intent());
                        dialog.dismiss();
                        finish();
                    } else {
                        System.out.print("");
                        dialog.dismiss();
                    }
                    Log.e(TAG, "onResponse: " + call);
                    Log.e(TAG, "onResponse: " + response.body());
                    Log.e(TAG, "onResponse: " + response.message());
                }

            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }




    //-----------Run time Permission-----------
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity2.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE+Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(EditProfileActivity2.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        }
        else {
            //ActivityCompat.requestPermissions(LaunchComplain.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(EditProfileActivity2.this,
                    permissions, PERMISSION_REQUEST_CODE);

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(EditProfileActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                +ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EditProfileActivity2.this,
                            "Permission granted successfully.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity2.this,
                            "You need to give read write permission to upload files.",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    public void onClicked(InterestModels models, int position) {
        InterestModels premodel = preferList.get(position);
        String isSelection=models.getPselected();
        if (isSelection.equals("Yes")){
            premodel.setPselected("No");
        }else {
            premodel.setPselected("Yes");
        }
        preferList.set(position, premodel);
        //now update adapter
        preAdapter.updateRecords(preferList);
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
