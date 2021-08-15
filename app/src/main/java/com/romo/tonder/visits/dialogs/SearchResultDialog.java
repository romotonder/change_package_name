package com.romo.tonder.visits.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.EventAdapter;
import com.romo.tonder.visits.adapters.ListingApater;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.EventModel;
import com.romo.tonder.visits.models.ListingModel;

import java.util.ArrayList;

public class SearchResultDialog extends Dialog {

    private Context context;
    private RecyclerView resultListRecycler;
    private ImageView gobackImageView;
    private ArrayList<ListingModel> listingModelArrayList;
    private ArrayList<EventModel> eventModelArrayList;
    private String globalSearchtype;


    public SearchResultDialog(@NonNull Context context, ArrayList<ListingModel> listingModels, ArrayList<EventModel> eventModels, String globalSearchtype) {
        super(context, R.style.DialogFragmentTheme);
        this.context = context;
        this.listingModelArrayList = listingModels;
        this.eventModelArrayList = eventModels;
        this.globalSearchtype = globalSearchtype;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_search_result_layout);
        resultListRecycler = findViewById(R.id.result_list_recycler);
        resultListRecycler.setLayoutManager(new GridLayoutManager(context, 2));

        gobackImageView = findViewById(R.id.go_back_image_view);
        if (globalSearchtype.equals(Common.SEARCH_TYPE_LISTING)) {
            ListingApater adapter = new ListingApater(listingModelArrayList, false, context);
            resultListRecycler.setAdapter(adapter);
        } else {
            EventAdapter adapter = new EventAdapter(eventModelArrayList, context);
            resultListRecycler.setAdapter(adapter);
        }

        gobackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
