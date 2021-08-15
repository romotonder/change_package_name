package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.helpers.DiscussionModel;
import com.romo.tonder.visits.interfaces.DiscussionClickInterfaces;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = DiscussionAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<DiscussionModel> discussionsList;
    private final DiscussionClickInterfaces interfacediscussion;
    private final DiscussionClickInterfaces interfacecomments;
    private final DiscussionClickInterfaces interfacemenu;

    public void updateRecords(ArrayList<DiscussionModel> discussionsList) {
        this.discussionsList = discussionsList;

        notifyDataSetChanged();
    }

    public DiscussionAdapter(ArrayList<DiscussionModel> discussionsList, Context context) {
        this.discussionsList = discussionsList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        interfacediscussion = (DiscussionClickInterfaces) context;
        interfacecomments = (DiscussionClickInterfaces) context;
        interfacemenu = (DiscussionClickInterfaces) context;
    }

    @Override
    public int getItemViewType(int position) {
        return discussionsList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Common.LISTING_DISCUSION) {
            return new DiscussionAdapter.ListingDiscussionViewHolder(inflater.inflate(R.layout.item_lists_discussion, parent, false));
        } else {
            return new DiscussionAdapter.EventDiscussionViewHolder(inflater.inflate(R.layout.item_event_discussion, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DiscussionModel model = discussionsList.get(position);
        if (model.getViewType() == Common.LISTING_DISCUSION)
            setupListingDis(model, (ListingDiscussionViewHolder) holder);
        else
            setupEventDis(model, (DiscussionAdapter.EventDiscussionViewHolder) holder);

    }

    private void setupEventDis(final DiscussionModel model, final EventDiscussionViewHolder holder) {
        if (!model.getDisplay_name().equals("")) {
            holder.username.setText(model.getDisplay_name());
        }
        if (!model.getComment_date().equals("")) {
            holder.postdate.setText(model.getComment_date());
        }
        if (!model.getComment_desc().equals("")) {
            holder.usercomment.setText(model.getComment_desc());
        }
        if (!model.getCount_comments().equals("")) {
            holder.commentscount.setText(model.getCount_comments() + context.getString(R.string.comments));
        }
        if (!model.getTotal_likes().equals("")) {
            holder.likecount.setText(model.getTotal_likes() + context.getString(R.string.like));
        }
        if (model.isMy_like()) {
            holder.like.setImageDrawable(context.getDrawable(R.drawable.ic_filled_thumb_up_24));
        } else {
            holder.like.setImageDrawable(context.getDrawable(R.drawable.ic_outline_thumb_up_24));
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacediscussion.onClicked(model, holder.getAdapterPosition());
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacecomments.commentsClick(model, holder.getAdapterPosition());

            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacemenu.menuClick(model, holder.getAdapterPosition());

            }
        });
    }

    private void setupListingDis(final DiscussionModel model, final ListingDiscussionViewHolder holder) {
        if (!model.getAuthorName().equals("")) {
            holder.authername.setText(model.getAuthorName());
        }
        if (!model.getReviewDate().equals("")) {
            holder.reviewdate.setText(model.getReviewDate());
        }
        if (!model.getOverallReviewRate().equals("")) {
            holder.overallReviewRate.setText(model.getOverallReviewRate());
        }
        if (!model.getOverallReviewRateFrom().equals("")) {
            holder.overallReviewRateFrom.setText("/ " + model.getOverallReviewRateFrom());
        }
        if (!model.getOverallRatingText().equals("")) {
            holder.reviewRateText.setText(model.getOverallRatingText());
        }
        if (!model.getReviewtitle().equals("")) {
            holder.reviewtitle.setText(model.getReviewtitle());
        }
        if (!model.getReviewDesc().equals("")) {
            holder.reviewbody.setText(model.getReviewDesc());
        }
        if (!model.getCount_comments().equals("")) {
            holder.commentscount.setText(model.getCount_comments() + context.getString(R.string.comments));
        }
        if (!model.getTotal_likes().equals("")) {
            holder.likecount.setText(model.getTotal_likes() + context.getString(R.string.like));
        }
        if (model.isMy_like()) {
            holder.like.setImageDrawable(context.getDrawable(R.drawable.ic_filled_thumb_up_24));
        } else {
            holder.like.setImageDrawable(context.getDrawable(R.drawable.ic_outline_thumb_up_24));
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacediscussion.onClicked(model, holder.getAdapterPosition());
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacecomments.commentsClick(model, holder.getAdapterPosition());

            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacemenu.menuClick(model, holder.getAdapterPosition());

            }
        });

    }

    @Override
    public int getItemCount() {
        return discussionsList.size();
    }

    private class EventDiscussionViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView username, postdate, usercomment, likecount, commentscount;
        AppCompatImageView like, comment,menu;
        CircleImageView imgDP;

        public EventDiscussionViewHolder(View view_dis) {
            super(view_dis);
            imgDP=(CircleImageView)view_dis.findViewById(R.id.img_dp);
            username = view_dis.findViewById(R.id.txt_username);
            postdate = view_dis.findViewById(R.id.txt_date);
            usercomment = view_dis.findViewById(R.id.txt_comment);
            likecount = view_dis.findViewById(R.id.txt_likecount);
            commentscount = view_dis.findViewById(R.id.txt_commentscount);
            like = view_dis.findViewById(R.id.btn_like);
            comment = view_dis.findViewById(R.id.btn_comment);
            menu = view_dis.findViewById(R.id.imgmenu);
        }
    }

    private class ListingDiscussionViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView authername, reviewdate, reviewtitle, reviewbody, likecount, commentscount;
        AppCompatTextView overallReviewRate, overallReviewRateFrom, reviewRateText;
        AppCompatImageView like, comment,menu;
        CircleImageView img_Menu;

        public ListingDiscussionViewHolder(View view_dis) {
            super(view_dis);
            img_Menu = (CircleImageView) view_dis.findViewById(R.id.img_menu_list);
            authername = view_dis.findViewById(R.id.txt_autherrname);
            reviewdate = view_dis.findViewById(R.id.txt_review_date);
            reviewtitle = view_dis.findViewById(R.id.txt_review_title);
            overallReviewRate = view_dis.findViewById(R.id.txt_overall_rate);
            overallReviewRateFrom = view_dis.findViewById(R.id.txt_overall_rate_from);
            reviewRateText = view_dis.findViewById(R.id.review_rate_text);
            reviewbody = view_dis.findViewById(R.id.txt_review_body);
            likecount = view_dis.findViewById(R.id.txt_likecount);
            commentscount = view_dis.findViewById(R.id.txt_commentscount);
            like = view_dis.findViewById(R.id.btn_like);
            comment = view_dis.findViewById(R.id.btn_comment);
            menu = view_dis.findViewById(R.id.img_menu);
        }
    }
}
