package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.NotificationActivity;
import com.romo.tonder.visits.models.NotificationListings;
import com.romo.tonder.visits.models.Notifications;

import java.util.ArrayList;

public class NotificationListingAdapter extends RecyclerView.Adapter<NotificationListingAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<NotificationListings> listings;
    private final ArrayList<Notifications> notificationsArrayList;

    public NotificationListingAdapter(Context context, ArrayList<NotificationListings> listings, ArrayList<Notifications> notificationsArrayList) {
        this.context = context;
        this.listings = listings;
        this.notificationsArrayList = notificationsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_listing_adaptor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = "";
        String title = "";
        String description = "";
        String notificationId = "";
        String data = "";
        String promoCode = "";
        String redirectLink = "";
        int viewStatus = 0;
        if (listings != null) {
            imageUrl = listings.get(position).getCoverImg();
            title = listings.get(position).getPostTitle();
            description = listings.get(position).getTagLine();
            notificationId = listings.get(position).getID();
        } else if (notificationsArrayList != null) {
            imageUrl = notificationsArrayList.get(position).getNotificationImage();
            title = notificationsArrayList.get(position).getNotificationTitle();
            description = notificationsArrayList.get(position).getNotificationDetails();
            notificationId = notificationsArrayList.get(position).getNotificationId();
            viewStatus = Integer.parseInt(notificationsArrayList.get(position).getNotificationStatus());
            data = notificationsArrayList.get(position).getData();
            promoCode = notificationsArrayList.get(position).getPromoCode();
            redirectLink = notificationsArrayList.get(position).getRedirectLink();
            if (viewStatus == 0) {
                holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.NotificationListCard));
            } else if (viewStatus == 1) {
                holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.app_background));
            }
        }

        if (!TextUtils.isEmpty(title))
            holder.title.setText(title);
        else
            holder.title.setText("Unavailable");
        if (!TextUtils.isEmpty(description))
            holder.description.setText(description);
        else holder.description.setText("Unavailable");

        Glide.with(context)
                .load(imageUrl)
                .thumbnail(0.5f)
                .into(holder.imageView);


        String finalTitle = title;
        String finalDescription = description;
        String finalImageUrl = imageUrl;
        String finalNotificationId = notificationId;
        String finalData = data;
        String promo = promoCode;
        String redirLink = redirectLink;
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("notification_id", finalNotificationId);
                bundle.putString("notification_list_data", finalData);
                bundle.putString("title", finalTitle);
                bundle.putString("body", finalDescription);
                bundle.putString("imageUrl", finalImageUrl);
                bundle.putString("promo_code", promo);
                bundle.putString("redirect_link", redirLink);


                Intent intent = new Intent(context, NotificationActivity.class).putExtra("bundle", bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listings != null && listings.size() > 0)
            return listings.size();
        else if (notificationsArrayList != null && notificationsArrayList.size() > 0)
            return notificationsArrayList.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView;
        AppCompatTextView title, description;
        CardView parentLayout;
        ConstraintLayout rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notification_image_view);
            title = itemView.findViewById(R.id.title_text_view);
            description = itemView.findViewById(R.id.description_text_view);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }
}
