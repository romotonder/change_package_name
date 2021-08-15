package com.romo.tonder.visits.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.EditProfileActivity;
import com.romo.tonder.visits.adapters.CustomAdapter;
import com.romo.tonder.visits.models.Pmodel;

import java.io.Serializable;
import java.util.List;

public class ViewProfileDialog extends Dialog {
    public static final String TAG = "GlobalSearchDialog";
    private Context context;
    private AppCompatImageView edit_profile;
    private  List<Pmodel> preferencelist;
    private AppCompatTextView textEmails,textLanguage;
    private String fname="",lname="",dname="",coverImage="",avtarImg="",email="",address="",phone="",
            website="",language="",userPosition,useIntroduction;
    private LinearLayout preferenceArea;
    private LayoutInflater inflater;
    private CustomAdapter adapter;
    private Intent intent;



    public ViewProfileDialog(@NonNull Context context, List<Pmodel> plisting, String firstName, String lastName, String displayName,
                             String coverImage, String avtarImage, String useraddress, String phone, String website, String prefferedLanguage,String pos, String intro) {
        super(context, R.style.DialogFragmentTheme);
        this.context = context;
        this.preferencelist = plisting;
        this.fname=firstName;
        this.lname=lastName;
        this.dname=displayName;
        this.coverImage=coverImage;
        this.avtarImg=avtarImage;
        this.address=useraddress;
        this.phone=phone;
        this.website=website;
        this.language=prefferedLanguage;
        this.userPosition=pos;
        this.useIntroduction=intro;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);
        bindView();
        clickListener();
    }

    private void clickListener() {
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, EditProfileActivity.class);
                intent.putExtra("f_name", fname);
                intent.putExtra("l_name", lname);
                intent.putExtra("d_name", dname);
                intent.putExtra("address", address);
                intent.putExtra("phone_no", phone);
                intent.putExtra("website", website);
                intent.putExtra("lang", language);
                intent.putExtra("pos", userPosition);
                intent.putExtra("intro", useIntroduction);
                intent.putExtra("list", (Serializable) preferencelist);

                context.startActivity(intent);
                dismiss();

            }
        });
    }

    private void bindView() {
        edit_profile=(AppCompatImageView)findViewById(R.id.editProfile);
        preferenceArea=(LinearLayout)findViewById(R.id.parentInterest);
        inflater = LayoutInflater.from(context);

        for (int index=0;index<preferencelist.size();index++){
            Pmodel pmodel=preferencelist.get(index);
            String name="";String isSelected="";
            name=pmodel.getPname();
            isSelected=pmodel.getPselected();
            View view = inflater.inflate(R.layout.interestlist, preferenceArea,
                    false);
            AppCompatTextView textName=view.findViewById(R.id.tvPreference);
            if (isSelected.equals("Yes")){
                textName.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_checked_24, 0);
            }else {
                textName.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_uncheck_box_outline_blank_24px, 0);
            }
            textName.setText(name);
            preferenceArea.addView(view);
        }


    }

}
