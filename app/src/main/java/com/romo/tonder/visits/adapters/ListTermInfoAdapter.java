package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.TermInfoModel;

import java.util.ArrayList;

public class ListTermInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<TermInfoModel> serviceList;
    private Context context;
    private LayoutInflater inflater;
    private static final int TERM_INFO = 2;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;

    public ListTermInfoAdapter(ArrayList<TermInfoModel> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getItemViewType(int position) {

        return serviceList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TERM_INFO:
                return new ListTermInfoAdapter.TermViewHolder(inflater.inflate(R.layout.item_service,parent,false));
                //case NO_DATA: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_data_notification, parent, false));
            //case NO_INTERNET: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_internet_notification, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TermInfoModel serviceModel=serviceList.get(position);
        if (serviceModel.getViewType() == TERM_INFO) {
            setupListing(serviceModel, (ListTermInfoAdapter.TermViewHolder) holder);
        }
    }
    private void setupListing(TermInfoModel serviceModel, TermViewHolder holder) {
        if (!serviceModel.getTermID().equals("")){

        }
        if (!serviceModel.getTermName().equals("")){
            holder.service_name.setText(serviceModel.getTermName());
        }
        if (!serviceModel.getTermImage().equals("")){
            Glide.with(context)
                    .load(serviceModel.getTermImage())
                    .placeholder(R.drawable.favorite)
                    .into(holder.icon_img);
        }else {
            Glide.with(context)
                    .load(R.drawable.favorite)
                    .into(holder.icon_img);
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class TermViewHolder extends RecyclerView.ViewHolder {
        TextView service_name;
        ImageView icon_img;

        public TermViewHolder(View inflate) {
            super(inflate);
            icon_img=inflate.findViewById(R.id.icon);
            service_name=inflate.findViewById(R.id.name);
        }
    }

}
