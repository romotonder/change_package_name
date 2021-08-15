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
import com.romo.tonder.visits.activities.GalleryActivity;
import com.romo.tonder.visits.models.GallaryModel;

import java.util.ArrayList;

import static com.romo.tonder.visits.R.*;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<GallaryModel> galleryList;
    Context context;
    LayoutInflater inflater;
    private final int limit = 3;

    public GalleryAdapter(ArrayList<GallaryModel> galleryList, Context context) {
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
        if (viewType == 4) {
            return new GalleryAdapter.GallaryViewHolder(inflater.inflate(layout.item_gallery, parent, false));
        } else {
            return new GalleryAdapter.GallaryViewHolder(inflater.inflate(layout.item_gallery, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GallaryModel gallaryModel = galleryList.get(position);
        if (gallaryModel.getViewType() == 4) {
            if (position < limit - 1) {
                setupItems(gallaryModel, (GalleryAdapter.GallaryViewHolder) holder);
            } else if (position == limit - 1) {
                int rest_img = galleryList.size() - limit;
                lastItemSetup(gallaryModel, (GalleryAdapter.GallaryViewHolder) holder, rest_img);
            }
        }
    }

    private void lastItemSetup(GallaryModel gallaryModel, final GallaryViewHolder holder, int restImg) {
        holder.number.setVisibility(View.VISIBLE);
        holder.number.setText("+" + String.valueOf(restImg));
        holder.last_img.setVisibility(View.VISIBLE);
        holder.gallary_img.setVisibility(View.GONE);
        if (!gallaryModel.getImgUrl().equals("")) {
            Glide.with(context)
                    .load(gallaryModel.getImgUrl())
                    .thumbnail(0.5f)
                    .into(holder.last_img);
        } else {
            Glide.with(context)
                    .load(drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.last_img);
        }
        holder.last_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("image_list", galleryList);
                context.startActivity(intent);
            }
        });

    }

    private void setupItems(GallaryModel gallaryModel, GallaryViewHolder holder) {
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
                    .load(drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.gallary_img);
        }
    }

    @Override
    public int getItemCount() {
        if (galleryList.size() > limit) {
            return limit;
        } else {
            return galleryList.size();
        }
    }

    public class GallaryViewHolder extends RecyclerView.ViewHolder {
        ImageView gallary_img,last_img;
        TextView number;

        public GallaryViewHolder(View inflate) {
            super(inflate);
            gallary_img = inflate.findViewById(id.gallary_img);
            last_img = inflate.findViewById(id.last_img);
            number = inflate.findViewById(id.tv_number);
        }
    }
}
