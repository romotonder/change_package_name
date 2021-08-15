package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.ChatActivity;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.UserChatListModel;

import java.util.ArrayList;

import static com.romo.tonder.visits.R.color.red_main_theme;
import static com.romo.tonder.visits.R.color.subtitle_text_color;

public class ChatHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;
    private static final String TAG = ChatHistoryAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<UserChatListModel> chatLists;

    public ChatHistoryAdapter(ArrayList<UserChatListModel> chatlist, Context context) {
        this.chatLists = chatlist;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHistoryAdapter.ChatlistViewHolder(inflater.inflate(R.layout.item_chats_lists,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserChatListModel userChatListModel = chatLists.get(position);
        setupChatList(userChatListModel, (ChatHistoryAdapter.ChatlistViewHolder) holder);

    }

    private void setupChatList(UserChatListModel userChatListModel, ChatlistViewHolder holder) {
        String user_name = userChatListModel.getDisplayName();
        String message = userChatListModel.getMessage();
        String displayPicture = userChatListModel.getAvatar();
        int receiverID = userChatListModel.getUserID();
        holder.username.setText(user_name);
        if (userChatListModel.isNew()){
            holder.message.setTextColor(ContextCompat.getColor(context, red_main_theme));

        }else {
            holder.message.setTextColor(ContextCompat.getColor(context, subtitle_text_color));
        }
        holder.message.setText(message);
        if (userChatListModel.getTimestamp() != 0) {
            holder.time.setText(Common.getDate(userChatListModel.getTimestamp()));
        }
        if (displayPicture != null) {
            Glide.with(context).load(displayPicture).into(holder.displayPicture);
        } else {
            Glide.with(context).load(R.drawable.app_placeholder).into(holder.displayPicture);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatpage = new Intent(context, ChatActivity.class);
                chatpage.putExtra("author_userId", String.valueOf(userChatListModel.getUserID()));
                chatpage.putExtra("author_name", userChatListModel.getDisplayName());
                chatpage.putExtra("time_stamp", userChatListModel.getTimestamp());
                chatpage.putExtra("is_new_message", userChatListModel.isNew());
                context.startActivity(chatpage);

            }
        });
    }


    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    class ChatlistViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView username, message, time;
        AppCompatImageView displayPicture;

        public ChatlistViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            displayPicture = itemView.findViewById(R.id.display_user);
        }
    }
}
