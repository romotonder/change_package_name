package com.romo.tonder.visits.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.romo.tonder.visits.R;

public class SettingsActivity extends BaseActivity {
    private Intent intent;
    private LinearLayout editProfileArea,notificationArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bindActivity();
    }

    private void bindActivity() {
        notificationArea=(LinearLayout)findViewById(R.id.notificationArea);
        editProfileArea=(LinearLayout)findViewById(R.id.editProfileArea);


    }

    public void editProfile(View view) {
        intent=new Intent(this,EditProfileActivity.class);
        startActivity(intent);
    }

    public void notificationSettings(View view) {
        intent=new Intent(this,NotificationSettingsActivity.class);
        startActivity(intent);

    }

    public void back(View view) {
        finish();
    }
}