package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.EventDetailsActivity;
import com.romo.tonder.visits.models.EventModel;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int EVENT = 1;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;
    private static final String TAG = EventAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<EventModel> eventList;

    public EventAdapter(ArrayList<EventModel> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return eventList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case EVENT:
                return new EventAdapter.EventViewHolder(inflater.inflate(R.layout.item_events_card, parent, false));
            //case NO_DATA: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_data_notification, parent, false));
            //case NO_INTERNET: return new DefaultViewHolder(inflater.inflate(R.layout.item_no_internet_notification, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventModel event_model = eventList.get(position);
        if (event_model.getViewType() == EVENT) {
            setupListing(event_model, (EventAdapter.EventViewHolder) holder);
        }

    }
    private void setupListing(final EventModel eventModel, EventViewHolder holder) {

        if (!eventModel.getEventPostTitle().equals("")) {
            holder.tvTitle.setText(eventModel.getEventPostTitle());
        } else {
            holder.tvTitle.setVisibility(View.INVISIBLE);
        }
        if (!eventModel.getEventPublistDate().equals("")) {
            holder.tvDate.setText(eventModel.getEventPublistDate());
        } else {
            holder.tvDate.setVisibility(View.INVISIBLE);
        }
        if (!eventModel.getEventAdd().equals("")) {
            holder.tvAddress.setText(eventModel.getEventAdd());
        } else {
            holder.tvAddress.setVisibility(View.INVISIBLE);
        }
        if (!eventModel.getEventHostedBy().equals("")) {
            holder.tvHostedBy.setText("Hosted By :"+eventModel.getEventHostedBy());
        } else {
            holder.tvHostedBy.setVisibility(View.INVISIBLE);
        }
        if (!eventModel.getEventCoverImg().equals("")) {
            Glide.with(context)
                    .load(eventModel.getEventCoverImg())
                    .thumbnail(0.5f)
                    .into(holder.cover_img);
        } else {
            Glide.with(context)
                    .load(R.drawable.app_placeholder)
                    .thumbnail(0.5f)
                    .into(holder.cover_img);
        }
        holder.cover_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("event_id",eventModel.getEventId());
                intent.putExtra("post_title",eventModel.getEventPostTitle());
                intent.putExtra("tag_line",eventModel.getEventTagLine());
                intent.putExtra("cover_image",eventModel.getEventCoverImg());
                intent.putExtra("from_page", "event_page");
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDate, tvAddress,tvPeopleInterested,tvHostedBy, tvRating, tvOpenClosed;
        AppCompatImageView cover_img;

        public EventViewHolder(View inflate) {
            super(inflate);
            cover_img = inflate.findViewById(R.id.eChildImg);
            tvTitle = inflate.findViewById(R.id.esubTitle);
            tvDate = inflate.findViewById(R.id.edate);
            tvAddress = inflate.findViewById(R.id.eaddress);
            tvPeopleInterested = inflate.findViewById(R.id.epeopleInterested);
            tvHostedBy = inflate.findViewById(R.id.ehostedBy);
        }
    }
}
