package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.interfaces.CommentsListenerInterface;
import com.romo.tonder.visits.models.CommentsModels;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = DiscussionAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CommentsModels> commentslist;
    private final CommentsListenerInterface interfacediscussion;

    public void updateRecords(ArrayList<CommentsModels> discussionsList) {
        this.commentslist = discussionsList;

        notifyDataSetChanged();
    }

    public CommentsAdapter(ArrayList<CommentsModels> cList, Context context) {
        this.commentslist = cList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        interfacediscussion= (CommentsListenerInterface) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            return new CommentsAdapter.ItemViewHolderList(inflater.inflate(R.layout.item_comments, parent, false));
        }else {
            return new CommentsAdapter.ItemViewHolderEvent(inflater.inflate(R.layout.item_comments, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentsModels model = commentslist.get(position);
        if (model.getViewType()==1){
            setupItemsList(model,(CommentsAdapter.ItemViewHolderList)holder);
        }else {
            setupItemsEvents(model,(CommentsAdapter.ItemViewHolderEvent)holder);
        }


    }

    private void setupItemsEvents(CommentsModels model, ItemViewHolderEvent holder) {
        String displayname=model.getDisplayName();
        String usercomments=model.getCommentDescription();
        String commentsdates=model.getCommentDate();
        if (!TextUtils.isEmpty(displayname)){
            holder.user_name.setText(displayname);
        }
        if (!TextUtils.isEmpty(usercomments)){
            holder.user_comments.setText(usercomments);
        }
        if (!TextUtils.isEmpty(commentsdates)){
            holder.user_date.setText(commentsdates);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacediscussion.onClicked(model,holder.getAdapterPosition());
            }
        });


    }

    private void setupItemsList(CommentsModels model, ItemViewHolderList holder) {
        String displayname=model.getDisplayName();
        String usercomments=model.getCommentDescription();
        String commentsdates=model.getCommentDate();
        if (!TextUtils.isEmpty(displayname)){
            holder.user_name.setText(displayname);
        }
        if (!TextUtils.isEmpty(usercomments)){
            holder.user_comments.setText(usercomments);
        }
        if (!TextUtils.isEmpty(commentsdates)){
            holder.user_date.setText(commentsdates);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacediscussion.onClicked(model,holder.getAdapterPosition());
            }
        });


    }
    @Override
    public int getItemViewType(int position) {
        return commentslist.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return commentslist.size();
    }

    private class ItemViewHolderList extends RecyclerView.ViewHolder {
        AppCompatTextView user_name,user_comments,user_date;
        CircleImageView user_dp;

        public ItemViewHolderList(View view_dis) {
            super(view_dis);
            user_name=view_dis.findViewById(R.id.user_name);
            user_comments=view_dis.findViewById(R.id.user_comments);
            user_date=view_dis.findViewById(R.id.user_date);
            user_dp=view_dis.findViewById(R.id.user_dp);
        }
    }
    private class ItemViewHolderEvent extends RecyclerView.ViewHolder {
        AppCompatTextView user_name,user_comments,user_date;
        CircleImageView user_dp;

        public ItemViewHolderEvent(View view_dis) {
            super(view_dis);
            user_name=view_dis.findViewById(R.id.user_name);
            user_comments=view_dis.findViewById(R.id.user_comments);
            user_date=view_dis.findViewById(R.id.user_date);
            user_dp=view_dis.findViewById(R.id.user_dp);
        }
    }
}
