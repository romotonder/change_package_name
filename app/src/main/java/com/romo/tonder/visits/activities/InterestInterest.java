package com.romo.tonder.visits.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.google.android.material.button.MaterialButton;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.CustomAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.LocaleManager;
import com.romo.tonder.visits.models.Pmodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class InterestInterest extends BaseActivity {
  private static final String TAG = InterestInterest.class.getSimpleName();
  private List<Pmodel> plisting = new ArrayList<>();
  private ListView preferenceArea;
  private CustomAdapter adapter;

  private Dialog dialog;
  private MaterialButton btn_exit, btn_back;
  private String userID = "";
  private SharedPreferences appPrefs;
  private ArrayList<String> sendInterestList;
  private String prferLanguage = "";
  ArrayList<String> items;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_interest_interest);
    bindActivity();
    if (!appPrefs.getString("preferredLanguage", "").equals("")) {
      prferLanguage = appPrefs.getString("preferredLanguage", "");
    } else {
      prferLanguage = getResources().getString(R.string.danish_lang);
    }
    selectLaguage(prferLanguage);
    initListener();
    getPreferList(appPrefs.getString("userID", ""), prferLanguage);
    //getPreferList("174", prferLanguage);
  }


  private void initListener() {
    btn_exit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        items = new ArrayList<>();
        for (int i = 0; i < plisting.size(); i++) {
          Pmodel pmodel = plisting.get(i);
          if (pmodel.getPselected().equals("Yes")) {
            items.add(pmodel.getP_id());
          }
        }
        if (items.size() >= 1) {
          String joinedInterestList = TextUtils.join(",", items);
          saveInterestSetup(joinedInterestList);
        } else {
          Toast.makeText(InterestInterest.this, "Select Atleas one", Toast.LENGTH_SHORT).show();
        }
                /*List<Pmodel> plistings = new ArrayList<>();
                plistings.addAll(plisting);
                int size=plistings.size();
                System.out.print(size);
                int s=items.size();
                System.out.print(s);*/
      }
    });
    btn_back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }


  private void saveInterestSetup(String interestlist) {
    dialog.show();
    if (!appPrefs.getString("userID", "").isEmpty()) {
      userID = appPrefs.getString("userID", "");
    }
    String url = Common.BASE_URL + "registration-setup";
    AndroidNetworking.post(url)
            .addBodyParameter("userid", userID)
            .addBodyParameter("timearrival", getIntent().getStringExtra("ARRIVAL_DATE"))
            .addBodyParameter("wheregoing", getIntent().getStringExtra("WHERE_TO_GO"))
            .addBodyParameter("prefferedlanguage", getIntent().getStringExtra("CHOOSE_LANG"))
            .addBodyParameter("interestedlist", interestlist)
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
                Toast.makeText(InterestInterest.this,
                        getResources().getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + anError.getErrorDetail());

              }
            });
  }

  private void parseResponse(JSONObject response) {
    dialog.dismiss();
    try {
      if (response.getString("status").equals("Success")) {
        String succesMsg = response.getString("oData");
        goToHomePage();
        Toast.makeText(this, succesMsg, Toast.LENGTH_SHORT).show();
        //store data to prefre------
        SharedPreferences.Editor edit = appPrefs.edit();
        edit.putString("preferredLanguage", getIntent().getStringExtra("CHOOSE_LANG"));
        edit.putBoolean("isRegistrationSetup", Common.isRegistrationSetup);
        edit.apply();
        selectLaguage(getIntent().getStringExtra("CHOOSE_LANG"));

      } else {
        String failureMsg = response.getString("oData");
        Toast.makeText(this, failureMsg, Toast.LENGTH_LONG).show();
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void goToHomePage() {
    Intent nextPage = new Intent(getApplicationContext(), HomeActivity.class);
    nextPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(nextPage);
  }

  private void bindActivity() {
    dialog = new Dialog(this);
    dialog.setContentView(R.layout.item_loading);
    dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    dialog.setCancelable(false);
    btn_exit = findViewById(R.id.btnExit);
    btn_back = findViewById(R.id.btnBack);
    appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
    preferenceArea = findViewById(R.id.preferenceArea);
    preferenceArea.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    items = new ArrayList<>();
  }

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

  @Override
  public void onBackPressed() {
    //super.onBackPressed();
  }

  private void getPreferList(String userID, String prferLanguage) {
    dialog.show();
    String url = Common.BASE_URL + "user-preference-list";
    AndroidNetworking.post(url)
            .addBodyParameter("userId", userID)
            .addBodyParameter("language", prferLanguage)
            .setPriority(Priority.HIGH)
            .build()
            .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
              @Override
              public void onResponse(Response okHttpResponse, JSONObject response) {
                Log.e(TAG, "onResponse: " + response);
                parsePrefrenceResponse(response);
              }

              @Override
              public void onError(ANError anError) {
                dialog.dismiss();
                Log.e(TAG, "onError: " + anError.getErrorDetail());
              }
            });

  }

  private void parsePrefrenceResponse(JSONObject response) {
    dialog.dismiss();
    try {
      JSONArray jsonArray = null;
      JSONObject jsonObject = null;
      if (response.getString("status").equals("Success")) {
        jsonArray = response.getJSONArray("oData");

        for (int i = 0; i < jsonArray.length(); i++) {
          jsonObject = jsonArray.getJSONObject(i);
          String preferID = jsonObject.getString("term_id");
          String preferName = jsonObject.getString("name");
          String preferSelect = jsonObject.getString("selected");
          Pmodel pmodel = new Pmodel(preferSelect, preferName, preferID);
          plisting.add(pmodel);
          adapter = new CustomAdapter(this, plisting);

          preferenceArea.setAdapter(adapter);
          preferenceArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              Pmodel model = plisting.get(i);
              if (model.getPselected().equals("Yes")) {
                model.setPselected("No");
              } else {
                model.setPselected("Yes");
              }
              plisting.set(i, model);
              //now update adapter
              adapter.updateRecords(plisting);

            }
          });
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }


  }

  public void getitem(View view) {
    List<Pmodel> plistings = new ArrayList<>();
    plistings.addAll(plisting);
    int size = plistings.size();
    System.out.print(size);
  }
}
