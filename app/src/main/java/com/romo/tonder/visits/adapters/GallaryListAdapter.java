package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.GalleryActivity;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.GallaryModel;

import java.util.ArrayList;

public class GallaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<GallaryModel> galleryList;
    Context context;
    LayoutInflater inflater;

    public GallaryListAdapter(ArrayList<GallaryModel> galleryList, Context context) {
        this.galleryList = galleryList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return galleryList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType== Common.ListingDetailsPageViewallImages){
            return new GallaryListAdapter.GallaryListViewHolder(inflater.inflate(R.layout.item_gallery, parent, false));
        }else {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GallaryModel gallaryModel=galleryList.get(position);
        if (gallaryModel.getViewType() == Common.ListingDetailsPageViewallImages) {
            setupGallary(gallaryModel,(GallaryListAdapter.GallaryListViewHolder) holder);
        }
    }

    private void setupGallary(GallaryModel gallaryModel, GallaryListViewHolder holder) {
        holder.gallary_img.setVisibility(View.VISIBLE);
        holder.last_img.setVisibility(View.GONE);
        holder.number.setVisibility(View.GONE);
        if (!gallaryModel.getImgUrl().equals("")) {
            Glide.with(context)
                    .load(gallaryModel.getImgUrl())
                    .thumbnail(0.5f)
                    .into(holder.gallary_img);
        } else {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.gallary_img);
        }
        holder.gallary_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("image_list", galleryList);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class GallaryListViewHolder extends RecyclerView.ViewHolder {

        ImageView gallary_img,last_img;
        TextView number;

        public GallaryListViewHolder(View inflate) {
            super(inflate);
            gallary_img = inflate.findViewById(R.id.gallary_img);
            last_img = inflate.findViewById(R.id.last_img);
            number = inflate.findViewById(R.id.tv_number);

        }
    }
}
