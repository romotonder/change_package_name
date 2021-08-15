package com.romo.tonder.visits.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.FilterationListAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.FilterChoiceEventBusModel;
import com.romo.tonder.visits.models.FilterChoiceGlobalSearchEventBusModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterationBottomShitFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterationBottomShitFrag extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Context context;
    private TextView cancelTextView;
    private TextView resetTextView;
    private EditText searchEditText;
    private RecyclerView listRecycler;
    private ArrayList<CategoryOrRegionFilterListModel> categoryOrRegionFilterListModels;
    private ArrayList<CategoryOrRegionFilterListModel> modifiedListModels;
    private FilterationListAdapter filterationListAdapter;
    private String filterType = "";
    private String selectedItemId = "";
    private boolean globalSearch = false;


    public FilterationBottomShitFrag() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public FilterationBottomShitFrag newInstance(ArrayList<CategoryOrRegionFilterListModel> arrayList, String filterType, String selectedItemId, boolean globalSearch) {
        categoryOrRegionFilterListModels = arrayList;
        this.filterType = filterType;
        this.selectedItemId = selectedItemId;
        this.globalSearch = globalSearch;
        FilterationBottomShitFrag fragment = new FilterationBottomShitFrag();
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterationBottomShitFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterationBottomShitFrag newInstance(String param1, String param2) {
        FilterationBottomShitFrag fragment = new FilterationBottomShitFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filteration_bottom_shit, container, false);
        cancelTextView = view.findViewById(R.id.cancel_text_view);
        resetTextView = view.findViewById(R.id.reset_text_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        listRecycler = view.findViewById(R.id.filter_list_recycler);
        modifiedListModels = new ArrayList<>();

        if (filterType.equals(Common.GLOBAL_SEARCH_TYPE)) {
            searchEditText.setVisibility(View.GONE);
            cancelTextView.setVisibility(View.GONE);
        }

        setUpListners();
        setUpAdaptersAndRecyclers(categoryOrRegionFilterListModels);
        return view;
    }

    private void setUpAdaptersAndRecyclers(ArrayList<CategoryOrRegionFilterListModel> listModels) {
        listRecycler.setLayoutManager(new LinearLayoutManager(context));
        if (TextUtils.isEmpty(selectedItemId))
            selectedItemId = "0";
        filterationListAdapter = new FilterationListAdapter(listModels, context, filterType, this, selectedItemId, globalSearch);
        listRecycler.setAdapter(filterationListAdapter);
    }

    private void setUpListners() {
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        resetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!globalSearch) {
                    FilterChoiceEventBusModel filterChoiceEventBusModel = new FilterChoiceEventBusModel(0, categoryOrRegionFilterListModels.get(0).getTermId(), categoryOrRegionFilterListModels.get(0).getTermName(), filterType);
                    EventBus.getDefault().post(filterChoiceEventBusModel);
                } else {
                    FilterChoiceGlobalSearchEventBusModel filterChoiceGlobalSearchEventBusModel = new FilterChoiceGlobalSearchEventBusModel(0, categoryOrRegionFilterListModels.get(0).getTermId(), categoryOrRegionFilterListModels.get(0).getTermName(), filterType);
                    EventBus.getDefault().post(filterChoiceGlobalSearchEventBusModel);
                }
                onDestroyView();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    modifySearchResult(s);
                } else {
                    setUpAdaptersAndRecyclers(categoryOrRegionFilterListModels);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void modifySearchResult(CharSequence s) {
        String search = s.toString();
        modifiedListModels = new ArrayList<>();
        for (CategoryOrRegionFilterListModel d : categoryOrRegionFilterListModels) {
            if (d.getTermName() != null && d.getTermName().toLowerCase().contains(search.toLowerCase()))
                modifiedListModels.add(d);
        }
        setUpAdaptersAndRecyclers(modifiedListModels);
    }
}