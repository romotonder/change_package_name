package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.CategoryOrRegionFilterListModel;
import com.romo.tonder.visits.models.FilterChoiceEventBusModel;
import com.romo.tonder.visits.models.FilterChoiceGlobalSearchEventBusModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FilterationListAdapter extends RecyclerView.Adapter<FilterationListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryOrRegionFilterListModel> categoryOrRegionFilterListModels;
    private String filterType;
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private String selectedItemId = "";
    private boolean globalSearch;

    public FilterationListAdapter(ArrayList<CategoryOrRegionFilterListModel> categoryOrRegionFilterListModels, Context context, String filterType, BottomSheetDialogFragment bottomSheetDialogFragment, String selectedItemId, boolean globalSearch) {
        this.categoryOrRegionFilterListModels = categoryOrRegionFilterListModels;
        this.context = context;
        this.filterType = filterType;
        this.bottomSheetDialogFragment = bottomSheetDialogFragment;
        this.selectedItemId = selectedItemId;
        this.globalSearch = globalSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = categoryOrRegionFilterListModels.get(position).getTermName();
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        } else {
            holder.title.setText("unavailable");
        }

        if (categoryOrRegionFilterListModels.get(position).getTermId().equals(selectedItemId)) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            holder.checkBox.setImageResource(0);
            holder.checkBox.setVisibility(View.GONE);
        }
        setUpListners(holder, position);
    }

    private void setUpListners(ViewHolder holder, int position) {
        holder.itemParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setImageResource(R.drawable.ic_baseline_check_24);
                if (!globalSearch) {
                    FilterChoiceEventBusModel filterChoiceEventBusModel = new FilterChoiceEventBusModel(position, categoryOrRegionFilterListModels.get(position).getTermId(), categoryOrRegionFilterListModels.get(position).getTermName(), filterType);
                    EventBus.getDefault().post(filterChoiceEventBusModel);
                } else {
                    FilterChoiceGlobalSearchEventBusModel filterChoiceGlobalSearchEventBusModel = new FilterChoiceGlobalSearchEventBusModel(position, categoryOrRegionFilterListModels.get(position).getTermId(), categoryOrRegionFilterListModels.get(position).getTermName(), filterType);
                    EventBus.getDefault().post(filterChoiceGlobalSearchEventBusModel);
                }
                bottomSheetDialogFragment.onDestroyView();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryOrRegionFilterListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView checkBox;
        LinearLayout itemParentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_text_view);
            checkBox = itemView.findViewById(R.id.check_view);
            itemParentLayout = itemView.findViewById(R.id.item_parent_layout);

        }
    }
}
