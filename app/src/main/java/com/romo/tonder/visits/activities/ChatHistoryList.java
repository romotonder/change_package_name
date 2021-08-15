package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.ChatHistoryAdapter;
import com.romo.tonder.visits.models.UserChatListModel;

import java.util.ArrayList;

public class ChatHistoryList extends AppCompatActivity {
    private RecyclerView chatlist_recycle;
    private static final String TAG = ChatHistoryList.class.getSimpleName();
    private SharedPreferences appPrefs;
    private DatabaseReference mReferenceUser;
    private String userID;
    private ArrayList<UserChatListModel> chatHistoryLists;
    private ChatHistoryAdapter adapter;
    AppCompatImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_list);
        if (!getIntent().getStringExtra("user_id").equals("")) {
            userID = getIntent().getStringExtra("user_id");
            //testing
            //userID = "174";
            bindActivity();
            getChatUserList(userID);
        }
    }

    private void getChatUserList(String userID) {
        chatHistoryLists.clear();
        mReferenceUser = FirebaseDatabase.getInstance().getReference().child("messages").child("users");
        DatabaseReference userRef = mReferenceUser.getRef().child("___" + userID + "___");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatHistoryLists.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    UserChatListModel userChatList = shot.getValue(UserChatListModel.class);
                    assert userChatList != null;
                    if (userChatList.getUserID() != Integer.parseInt(userID)) {
//                        String user_displayname = userChatList.getDisplayName();
//                        String f_user = userChatList.getfUser();
//                        String user_message = userChatList.getMessage();
//                        String displayPicture = userChatList.getAvatar();
//                        boolean isnew = userChatList.isNew();
//                        long time = userChatList.getTimestamp();
//                        int user_id = userChatList.getUserID();
                        chatHistoryLists.add(userChatList);
                        if (chatHistoryLists.size() > 0)
                            adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void bindActivity() {
        btn_back = findViewById(R.id.btnBack);
        chatlist_recycle = findViewById(R.id.chatlist);
        chatlist_recycle.setLayoutManager(new LinearLayoutManager(this));
        chatHistoryLists = new ArrayList<>();
        adapter = new ChatHistoryAdapter(chatHistoryLists, ChatHistoryList.this);
        chatlist_recycle.setAdapter(adapter);
    }

    public void previousPage(View view) {
        finish();
    }
}