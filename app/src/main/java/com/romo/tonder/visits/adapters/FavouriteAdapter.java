package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.FavouriteModels;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int LISTING = 1;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;
    private static final String TAG = FavouriteAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<FavouriteModels> listingList;
    public FavouriteAdapter(ArrayList<FavouriteModels> listingList,Context context) {
        this.listingList = listingList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getItemViewType(int position) {
        return listingList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ItemsViewHolder(inflater.inflate(R.layout.item_favourite, parent, false));
        }else {
        return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FavouriteModels model = listingList.get(position);
        if (model.getViewType() == 1) {
            setupItems(model,(FavouriteAdapter.ItemsViewHolder)holder);
        }

    }

    private void setupItems(FavouriteModels model, ItemsViewHolder holder) {
        String title=model.getListTitle();
        String tagline=model.getListTagline();
        String coverimage=model.getCoverImage();
        if (!TextUtils.isEmpty(title)){
            holder.tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(tagline)){
            holder.tvTagline.setText(tagline);
        }
        if (!TextUtils.isEmpty(coverimage)){
            Glide.with(context)
                    .load(coverimage)
                    .placeholder(R.drawable.app_placeholder)
                    .error(R.drawable.app_placeholder)
                    .into(holder.cover_img);
        }else {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .into(holder.cover_img);
        }

    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvTagline, tvTitle;
        AppCompatImageView cover_img;

        public ItemsViewHolder(View inflate) {
            super(inflate);
            cover_img = inflate.findViewById(R.id.coverimage);
            tvTitle = inflate.findViewById(R.id.title);
            tvTagline = inflate.findViewById(R.id.tagline);
        }
    }

}
