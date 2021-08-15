package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.ChatModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatModel> chatList;
    private Context context;
    private LayoutInflater inflater;

    public ChatAdapter(ArrayList<ChatModel> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new textItemHolder(inflater.inflate(R.layout.chat_details_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel model = chatList.get(position);
        setupLeftText((textItemHolder) holder, model, position);
    }


    private void setupLeftText(textItemHolder holder, ChatModel model, int position) {
        if (model.getViewType() == Common.LEFT) {
            holder.leftArea.setVisibility(View.VISIBLE);
            holder.rightArea.setVisibility(View.GONE);
//            holder.leftTextMessage.setVisibility(View.VISIBLE);
//            holder.rightTextMessage.setVisibility(View.GONE);
//            holder.rightdate.setVisibility(View.GONE);
//            holder.leftdate.setVisibility(View.VISIBLE);
//            holder.leftTextMessage.setText(model.getMessage()+"  \n  "+ Common.getDate(model.getTimestamp()));
            holder.leftTextMessage.setText(model.getMessage().trim());
            holder.leftdate.setText(Common.getDate(model.getTimestamp()));
        } else {
            holder.leftArea.setVisibility(View.GONE);
            holder.rightArea.setVisibility(View.VISIBLE);
//            holder.leftTextMessage.setVisibility(View.GONE);
//            holder.rightTextMessage.setVisibility(View.VISIBLE);
//            holder.rightdate.setVisibility(View.VISIBLE);
//            holder.leftdate.setVisibility(View.GONE);
//            holder.rightTextMessage.setText(model.getMessage()+"  \n  "+ Common.getDate(model.getTimestamp()));
            holder.rightTextMessage.setText(model.getMessage().trim());
            holder.rightdate.setText(Common.getDate(model.getTimestamp()));

        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    private class textItemHolder extends RecyclerView.ViewHolder {
        AppCompatTextView leftTextMessage,leftdate;
        AppCompatTextView rightTextMessage,rightdate;
        LinearLayout rightArea,leftArea;


        public textItemHolder(View view) {
            super(view);
            leftTextMessage = view.findViewById(R.id.leftMsg);
            leftdate = view.findViewById(R.id.leftdate);
            rightdate = view.findViewById(R.id.rightdate);
            rightTextMessage = view.findViewById(R.id.rightMsg);
            rightArea=view.findViewById(R.id.right_area);
            leftArea=view.findViewById(R.id.left_area);
        }
    }
}
