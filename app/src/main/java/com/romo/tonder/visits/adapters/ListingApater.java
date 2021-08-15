package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
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
import com.romo.tonder.visits.activities.ListingDetailsActivity;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.ListingModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListingApater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int LISTING = 1;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;
    private static final String TAG = ListingApater.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ListingModel> listingList;
    private boolean isHorizontal;

    public ListingApater(ArrayList<ListingModel> listingList, boolean isHorizontal, Context context) {
        this.listingList = listingList;
        this.context = context;
        this.isHorizontal = isHorizontal;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return listingList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Common.HOME_LISTING) {
            return new ListingApater.HomeListingViewHolder(inflater.inflate(R.layout.item_home_listing_card, parent, false));
        }else if (viewType == Common.CATEGORY_LISTING) {
            return new ListingApater.HomeListingViewHolder(inflater.inflate(R.layout.item_listing_card, parent, false));
        } else  {
            if (isHorizontal) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_map_list_card, parent, false);
                int width = parent.getWidth();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) (width * 0.8);
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                view.setLayoutParams(params);
                return new ListingApater.ListingViewHolder(view);
            } else
                return new ListingApater.ListingViewHolder(inflater.inflate(R.layout.item_listing_card, parent, false));
        }
        //case NO_DATA: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_data_notification, parent, false));
        //case NO_INTERNET: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_internet_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListingModel listing_model = listingList.get(position);
        if (listing_model.getViewType() == Common.HOME_LISTING || listing_model.getViewType()==Common.CATEGORY_LISTING) {
            setupHomeListing(listing_model, (ListingApater.HomeListingViewHolder) holder);
        } else {
            setupListing(listing_model, (ListingApater.ListingViewHolder) holder);
        }
    }

    private void setupListing(final ListingModel listing_model, ListingViewHolder holder) {
        String postTitle = listing_model.getListPostTitle();
        String tagLine = listing_model.getListTagLine();
        String listAdd = listing_model.getListAdd();
        String coverIMG = listing_model.getListCoverImg();
        String logoIMG = listing_model.getListLogo();
        String listDist = listing_model.getListDistance();
        String listMode = listing_model.getListMode();
        String listAvg = listing_model.getListAverage();
        String listTotalScore = listing_model.getListTotleScore();
        String isOpen=listing_model.getIsOpen();
        //String listDist = "50010 KM";

        if (postTitle.equals("")){
            holder.tvTitle.setText("");
        }else {
            holder.tvTitle.setText(Html.fromHtml(postTitle));
        }
        if (tagLine.equals("")){
            holder.tvTagline.setText("");
        }else {
            holder.tvTagline.setText(tagLine);
        }
        if (listAdd.equals("")) {
            holder.tvAddress.setText("");
        } else {
            holder.tvAddress.setText(listAdd);
        }
        if (listDist.equals("")){
            holder.tvDistance.setVisibility(View.GONE);
            holder.tvDistance.setText("");
        }else {
            holder.tvDistance.setVisibility(View.VISIBLE);
            holder.tvDistance.setText(listDist);
        }
        if (listMode.equals("") && listAvg.equals("")){
            holder.tvModeRating.setText("");
        } else if (listAvg.equals("0")) {
            holder.tvModeRating.setText("");
        } else {
            holder.tvModeRating.setText(listAvg+" / "+ listMode);
        }
//        if (!isOpen.equals("")){
        if (!TextUtils.isEmpty(isOpen)){
            if (isOpen.equals("closed")){
                isOpen="";
                isOpen=context.getResources().getString(R.string.closed);
                holder.tvOpenClosed.setTextColor(context.getColor(R.color.red_main_theme));
            }else if (isOpen.equals("open_always")){
                isOpen="";
                isOpen=context.getResources().getString(R.string.open24);
                holder.tvOpenClosed.setTextColor(context.getColor(R.color.MediumSeaGreen));
            }else {
                isOpen="";
                isOpen=context.getResources().getString(R.string.open);
                holder.tvOpenClosed.setTextColor(context.getColor(R.color.MediumSeaGreen));
            }
            holder.tvOpenClosed.setText(isOpen);
        }
        if (coverIMG.equals("")) {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.cover_img);
        } else {
            Glide.with(context)
                    .load(coverIMG)
                    .placeholder(R.drawable.app_placeholder)
                    .error(R.drawable.app_placeholder)
                    .into(holder.cover_img);
        }
        if (logoIMG.equals("")) {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.logo_img);

        } else {
            Glide.with(context)
                    .load(logoIMG)
                    .placeholder(R.drawable.app_placeholder)
                    .error(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.logo_img);

        }
        holder.cover_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ListingDetails.class);
                Intent intent = new Intent(context, ListingDetailsActivity.class);
                intent.putExtra("id", listing_model.getListId());
                intent.putExtra("post_title", listing_model.getListPostTitle());
                intent.putExtra("tag_line", listing_model.getListTagLine());
                intent.putExtra("cover_image", listing_model.getListCoverImg());
                intent.putExtra("logo_image", listing_model.getListLogo());
                intent.putExtra("from_page", Common.FROM_LIST_PAGE);
                context.startActivity(intent);

            }
        });

    }

    private void setupHomeListing(final ListingModel listing_model, HomeListingViewHolder holder) {
        String postTitle = listing_model.getListPostTitle();
        String tagLine = listing_model.getListTagLine();
        String listAdd = listing_model.getListAdd();
        String coverIMG = listing_model.getListCoverImg();
        String logoIMG = listing_model.getListLogo();
        String listMode = listing_model.getListMode();
        String listAvg = listing_model.getListAverage();
        String isOpen=listing_model.getListHourMode();

        if (postTitle.equals("")){
            holder.tvTitle.setText("");
        }else {
            holder.tvTitle.setText(Html.fromHtml(postTitle));
        }
        if (tagLine.equals("")){
            holder.tvTagline.setText("");
        }else {
        holder.tvTagline.setText(tagLine);
        }
        if (TextUtils.isEmpty(listAdd)) {
            holder.tvAddress.setText("");
        } else {
            holder.tvAddress.setText(listAdd);
        }
        if (listMode.equals("") && listAvg.equals("")){
            holder.tvModeRating.setText("");
        } else if (listAvg.equals("0")) {
            holder.tvModeRating.setText("");
        } else {
            holder.tvModeRating.setText(listAvg+" / "+ listMode);
        }
        if (!TextUtils.isEmpty(isOpen)){
            if (isOpen.equals("no_hours_available")){
                isOpen="";
                isOpen=context.getResources().getString(R.string.closed);
                holder.tvOpenClosed.setTextColor(context.getColor(R.color.red_main_theme));
            }else if (isOpen.equals("always_open")){
                isOpen="";
                isOpen=context.getResources().getString(R.string.open24);
                holder.tvOpenClosed.setTextColor(context.getColor(R.color.MediumSeaGreen));
            }else {
                isOpen="";
                isOpen=context.getResources().getString(R.string.open);
                holder.tvOpenClosed.setTextColor(context.getColor(R.color.MediumSeaGreen));
            }
            holder.tvOpenClosed.setText(isOpen);
        }

        if (coverIMG.equals("")) {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.cover_img);
        } else {
            Glide.with(context)
                    .load(coverIMG)
                    .placeholder(R.drawable.app_placeholder)
                    .error(R.drawable.app_placeholder)
                    .into(holder.cover_img);
        }
        if (logoIMG.equals("")) {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.logo_img);

        } else {
            Glide.with(context)
                    .load(logoIMG)
                    .placeholder(R.drawable.app_placeholder)
                    .error(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.logo_img);

        }
        holder.cover_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListingDetailsActivity.class);
//                Intent intent = new Intent(context, ListingDetailsModified.class);
                intent.putExtra("id", listing_model.getListId());
                intent.putExtra("post_title", listing_model.getListPostTitle());
                intent.putExtra("tag_line", listing_model.getListTagLine());
                intent.putExtra("cover_image", listing_model.getListCoverImg());
                intent.putExtra("logo_image", listing_model.getListLogo());
                intent.putExtra("from_page", Common.FROM_LIST_PAGE);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    public class HomeListingViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvTagline, tvTitle, tvAddress, tvModeRating, tvOpenClosed;
        AppCompatImageView cover_img, wave_img,location_img;
        CircleImageView logo_img;

        public HomeListingViewHolder(View inflate) {
            super(inflate);
            cover_img = inflate.findViewById(R.id.listImg);
            location_img = inflate.findViewById(R.id.location_img);
            logo_img = inflate.findViewById(R.id.listLogo);
            tvTitle = inflate.findViewById(R.id.postTitle);
            tvTagline = inflate.findViewById(R.id.tagLine);
            tvAddress = inflate.findViewById(R.id.add);
            tvModeRating = inflate.findViewById(R.id.userModeRting);
            tvOpenClosed = inflate.findViewById(R.id.openClosed);

        }
    }
    public class ListingViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvTagline, tvTitle, tvAddress, tvModeRating, tvOpenClosed,tvDistance;
        AppCompatImageView cover_img, wave_img,location_Img;
        CircleImageView logo_img;

        public ListingViewHolder(View inflate) {
            super(inflate);
            cover_img = inflate.findViewById(R.id.listImg);
            location_Img = inflate.findViewById(R.id.locationImg);
            logo_img = inflate.findViewById(R.id.listLogo);
            tvTitle = inflate.findViewById(R.id.postTitle);
            tvTagline = inflate.findViewById(R.id.tagLine);
            tvAddress = inflate.findViewById(R.id.add);
            tvModeRating = inflate.findViewById(R.id.userModeRting);
            tvOpenClosed = inflate.findViewById(R.id.openClosed);
            tvDistance = inflate.findViewById(R.id.listDistance);
        }
    }
}
