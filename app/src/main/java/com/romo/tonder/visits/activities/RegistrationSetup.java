package com.romo.tonder.visits.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.LocaleManager;

import java.util.Calendar;

public class RegistrationSetup extends BaseActivity
        implements DatePickerDialog.OnDateSetListener {
    private Toolbar mtoolbar;
    private Dialog dialog;
    private DatePickerDialog datePickerDialog;
    private AppCompatTextView txt_arrivalDate;
    private AppCompatTextView txt_arrivalDateTitle;
    private View view_arriavalTime;
    private AppCompatEditText edt_destination;
    private String arrivalDate = "";
    private String selectedLanguage = "";
    private String selectedJourney = "";
    private MaterialButton btn_next;

    private Spinner language_dropdown;
    private Spinner whereToGo_dropdown;
    Context mContext;
    private SharedPreferences appPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_setup);
        bindActivity();
        if (!appPrefs.getString("preferredLanguage", "").equals("")) {
            changeLanguage(appPrefs.getString("preferredLanguage", ""));
        } else {
            changeLanguage(getResources().getString(R.string.danish_lang));
        }
        initListener();

    }

    private void initListener() {
        txt_arrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_arrivalDate.setClickable(false);
                showDatePickerDialog();
            }
        });
        ArrayAdapter<String> editiondataAdapter = new ArrayAdapter<String>(this, R.layout.dropdown,
                getResources().getStringArray(R.array.language_choice));
        editiondataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_dropdown.setAdapter(editiondataAdapter);
        language_dropdown.setPrompt(getResources().getString(R.string.select_lang));
        language_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = parent.getItemAtPosition(position).toString();
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
                            + selectedLanguage, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Nothing Selected",
                        Toast.LENGTH_SHORT).show();

            }
        });


        ArrayAdapter<String> journeyAdapter = new ArrayAdapter<String>(this, R.layout.dropdown,
                getResources().getStringArray(R.array.wheretogo_choice));
        journeyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whereToGo_dropdown.setAdapter(journeyAdapter);
        whereToGo_dropdown.setPrompt(getResources().getString(R.string.wher_to_go));
        whereToGo_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedJourney = parent.getItemAtPosition(position).toString();
                TextView tvdrop;
                tvdrop = view.findViewById(R.id.tvdrop);
                // First item is disable and it is used for hint
                if (position == 0) {

                    tvdrop.setTextColor(getResources().getColor(R.color.Gray));
                    Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
                }
                if (position > 0) {
                    // Notify the selected item text
                    tvdrop.setTextColor(getResources().getColor(R.color.title_text_color));
                    if (selectedJourney.equals(getResources().getString(R.string.guest))) {
                        txt_arrivalDate.setVisibility(View.GONE);
                        txt_arrivalDate.setText("");
                        arrivalDate = "";
                        txt_arrivalDateTitle.setVisibility(View.GONE);
                        view_arriavalTime.setVisibility(View.GONE);
                    } else {
                        txt_arrivalDate.setVisibility(View.VISIBLE);
                        txt_arrivalDateTitle.setVisibility(View.VISIBLE);
                        view_arriavalTime.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getApplicationContext(), "Selected : " +
                            selectedJourney, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();

            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    Intent nextPage = new Intent(getApplicationContext(), InterestInterest.class);
                    if (!selectedJourney.equals(getResources().getString(R.string.guest))) {
                        nextPage.putExtra("ARRIVAL_DATE", arrivalDate);
                    }
                    nextPage.putExtra("WHERE_TO_GO", selectedJourney);
                    nextPage.putExtra("CHOOSE_LANG", selectedLanguage);
                    startActivity(nextPage);
                }

            }
        });
    }

    private void changeLanguage(String selectedLanguage) {
        if (selectedLanguage.equals(getResources().getString(R.string.english_lang))) {
            setNewLocale(this, LocaleManager.ENGLISH);
        }
        if (selectedLanguage.equals(getResources().getString(R.string.danish_lang))) {
            setNewLocale(this, LocaleManager.DANISH);
        }
        if (selectedLanguage.equals(getResources().getString(R.string.german_lang))) {
            setNewLocale(this, LocaleManager.GERMAN);
        }
    }

    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        /*Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
    }

    private boolean checkValidation() {
        if (arrivalDate.isEmpty() && !selectedJourney.equals(getResources().getString(R.string.guest))) {
            Toast.makeText(this, getResources().getString(R.string.arrival_time_hint), Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectedJourney.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.where_you_going_hint), Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (selectedLanguage.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.select_lang_hint), Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectedLanguage.equals(getResources().getString(R.string.choose))) {
            Toast.makeText(this, getResources().getString(R.string.select_lang_hint), Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }

    private void showDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(this,
                (DatePickerDialog.OnDateSetListener) this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        Calendar cMax = Calendar.getInstance();
        cMax.set(2030, 11, 10);//Year,Mounth -1,Day
        /*Calendar cMin = Calendar.getInstance();
        cMin.set(2020,06,21);*/

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(cMax.getTimeInMillis());
        txt_arrivalDate.setEnabled(true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        txt_arrivalDate.setText(date);
        arrivalDate = date;
    }

    private void bindActivity() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();

        txt_arrivalDate = findViewById(R.id.txtArrivalTime);
        txt_arrivalDateTitle = findViewById(R.id.txtArrivalTimeTitle);
        view_arriavalTime = findViewById(R.id.viewArrivalTime);
        //edt_destination=findViewById(R.id.edtDestination);
        language_dropdown = findViewById(R.id.spnLanguage);
        whereToGo_dropdown = findViewById(R.id.spnWhereToGo);
        btn_next = findViewById(R.id.btnNext);
    }

}
