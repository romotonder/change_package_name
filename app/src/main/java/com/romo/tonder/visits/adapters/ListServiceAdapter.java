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
import com.romo.tonder.visits.models.ServiceModel;

import java.util.ArrayList;

public class ListServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ServiceModel> serviceList;
    private Context context;
    private LayoutInflater inflater;
    private static final int SERVICE = 1;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;

    public ListServiceAdapter(ArrayList<ServiceModel> serviceList, Context context) {
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
            case SERVICE:
                return new ListServiceAdapter.serviceViewHolder(inflater.inflate(R.layout.item_service,parent,false));


            //case NO_DATA: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_data_notification, parent, false));
            //case NO_INTERNET: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_internet_notification, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ServiceModel serviceModel=serviceList.get(position);
        if (serviceModel.getViewType() == SERVICE) {
            setupListing(serviceModel, (ListServiceAdapter.serviceViewHolder) holder);
        }
    }
    private void setupListing(ServiceModel serviceModel, serviceViewHolder holder) {
        if (!serviceModel.getServiceID().equals("")){

        }
        if (!serviceModel.getServiceName().equals("")){
            holder.service_name.setText(serviceModel.getServiceName());
        }
        if (!serviceModel.getServiceIcon().equals("")){
            Glide.with(context)
                    .load(serviceModel.getServiceIcon())
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
    public class serviceViewHolder extends RecyclerView.ViewHolder {
        TextView service_name;
        ImageView icon_img;

        public serviceViewHolder(View inflate) {
            super(inflate);
            icon_img=inflate.findViewById(R.id.icon);
            service_name=inflate.findViewById(R.id.name);
        }
    }
    public class termViewHolder extends RecyclerView.ViewHolder {
        TextView service_name;
        ImageView icon_img;

        public termViewHolder(View inflate) {
            super(inflate);
            icon_img=inflate.findViewById(R.id.icon);
            service_name=inflate.findViewById(R.id.name);
        }
    }
}
