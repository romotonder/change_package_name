package com.romo.tonder.visits.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.LocaleManager;

public class WelcomeScreenActivy extends BaseActivity {
    MaterialButton btn_letsGo;
    private SharedPreferences appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        appPrefs=((MyApplication) getApplicationContext()).getAppPrefs();
        btn_letsGo=findViewById(R.id.btnLetsGo);
        if (!appPrefs.getString("preferredLanguage","").equals("")){
            selectLaguage(appPrefs.getString("preferredLanguage",""));
        }else {
            selectLaguage(getResources().getString(R.string.danish_lang));
        }
        initListener();

    }

    private void selectLaguage(String appsLanguage) {
        if (appsLanguage.equals(getResources().getString(R.string.english_lang))){
            setNewLocale(this, LocaleManager.ENGLISH);

        }
        if (appsLanguage.equals(getResources().getString(R.string.danish_lang))){
            setNewLocale(this,LocaleManager.DANISH);
        }
        if (appsLanguage.equals(getResources().getString(R.string.german_lang))){
            setNewLocale(this,LocaleManager.GERMAN);
        }
    }
    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
    }

    private void initListener() {
        btn_letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next=new Intent(getApplicationContext(),RegistrationSetup.class);
                next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(next);
            }
        });

    }
}
