package com.romo.tonder.visits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.ChatAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.ChatModel;
import com.romo.tonder.visits.models.UserChatListModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends BaseActivity {
    private String sender_id, receiver_id;
    private RecyclerView chatRecycler;
    private static final String TAG = ChatActivity.class.getSimpleName();
    private SharedPreferences appPrefs;
    private FirebaseUser mUser;
    private ChatAdapter adapter;
    private ArrayList<ChatModel> chatList;

    private AppCompatEditText inputChat;
    private AppCompatImageView btnSendChat;
    private AppCompatTextView receiverName, dateTime;

    private DatabaseReference dbReference;
    private DatabaseReference receiver_sender_node;
    private String textMessage = "";
    private boolean isChatAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getIntent().hasExtra("author_userId")) {
            bindActivity();
            sender_id = appPrefs.getString("userID", "");
            receiver_id = getIntent().getStringExtra("author_userId");
            getMessage();
        } else {
            finish();
        }
    }

    private void bindActivity() {
        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        chatRecycler = (RecyclerView) findViewById(R.id.chat_recycle);
        chatList = new ArrayList<>();
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));

        inputChat = findViewById(R.id.edit_chat);
        btnSendChat = findViewById(R.id.btn_send_chat);
        receiverName = findViewById(R.id.receiver_name);
        dateTime = findViewById(R.id.date_time);

        adapter = new ChatAdapter(chatList, ChatActivity.this);
        chatRecycler.setAdapter(adapter);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("author_name"))) {
            receiverName.setText(getIntent().getStringExtra("author_name"));
        } else {
            receiverName.setText("");
        }
        long datetime = getIntent().getLongExtra("time_stamp", 0);
        String date_time = Common.getDate(datetime);
        if (!TextUtils.isEmpty(date_time)) {
            dateTime.setText(date_time);
        } else {
            dateTime.setText("31/10/2020");
        }

    }

    private void getMessage() {
        chatList.clear();
        dbReference = FirebaseDatabase.getInstance().getReference().child("messages").child("chats");
        DatabaseReference comRef = dbReference.getRef().child("___" + receiver_id + "___" + sender_id + "___");   //_1_174_
        DatabaseReference comReferenceList = comRef.getRef().child("lists");  //__1__112_
        String mReferenceUserKey = dbReference.getKey();
        String com_ref = comRef.getKey();
        String com_reflist = comReferenceList.getKey();
        System.out.print(mReferenceUserKey);
        System.out.print(comReferenceList);
        comReferenceList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    ChatModel chat = shot.getValue(ChatModel.class);
                    if (chat != null) {
                        int receiverId = 0;
                        receiverId = Common.GetInt(receiver_id);
                        if (chat.getUserID() == receiverId) {
                            //receiver
                            chat.setViewType(Common.LEFT);
                        } else {
                            //sender
                            chat.setViewType(Common.RIGHT);
                        }
                        chatList.add(chat);
                        if (chatList.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());

            }
        });
        comRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendMessage(View view) {
        if (!TextUtils.isEmpty(inputChat.getText().toString())) {
            textMessage = inputChat.getText().toString();
            insertChatTable();
            newInsertUserTable();
            inputChat.setText("");
        } else {
            Toast.makeText(this, "Fill the text field", Toast.LENGTH_SHORT).show();
        }
    }

    //==========user table insert=============
    private void newInsertUserTable() {
        dbReference = FirebaseDatabase.getInstance().getReference().child("messages").child("users");
        receiver_sender_node = dbReference.getRef().child("___" + appPrefs.getString("userID", "") + "___");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String keyname = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        UserChatListModel userChatList = ds.getValue(UserChatListModel.class);
                        if (userChatList.getUserID() == Integer.parseInt(receiver_id)) {
                            isChatAvailable = true;
                            keyname = ds.getKey();
                            break;
                        } else {
                            isChatAvailable = false;
                        }
                    }
                }
                if (isChatAvailable) {
                    DatabaseReference node = receiver_sender_node.getRef().child(keyname); // -ML6HEibJbmKIyvCTGTx
                    node.child("message").setValue(textMessage);
                    node.child("active").setValue(false);
                    node.child("new").setValue(false);
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("displayName", getIntent().getStringExtra("author_name"));
                    map.put("firebaseID", appPrefs.getString("adminFirebaseID", ""));
                    map.put("message", textMessage);
                    map.put("timestamp", Common.GetTime());
                    map.put("userID", Common.GetInt(receiver_id));
                    map.put("active", false);
                    map.put("new", false);
                    receiver_sender_node.push().setValue(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };

        receiver_sender_node.addListenerForSingleValueEvent(valueEventListener);

    }

    //============insert chat table======================
    private void insertChatTable() {
        dbReference = FirebaseDatabase.getInstance().getReference().child("messages").child("chats");
        HashMap<String, Object> map = new HashMap<>();
        map.put("displayName", appPrefs.getString("userName", ""));
        map.put("firebaseID", appPrefs.getString("userFirebaseId", "")); //need to change
        map.put("message", textMessage);
        map.put("timestamp", Common.GetTime());
        map.put("userID", Common.GetInt(appPrefs.getString("userID", "")));

        receiver_sender_node = dbReference.getRef().child("___" + receiver_id + "___" + sender_id + "___");  //_1_174_
        DatabaseReference list_node = receiver_sender_node.getRef().child("lists");  //__1__112_
        list_node.push().setValue(map);
    }

    public void backBtn(View view) {
        finish();
    }

    //===========update user table====================== //not required
    private void updateUserTable() {

        dbReference = FirebaseDatabase.getInstance().getReference().child("messages").child("users");
        DatabaseReference userChild = dbReference.getRef().child("___" + appPrefs.getString("userID", "") + "___"); //___466___
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        UserChatListModel userChatList = ds.getValue(UserChatListModel.class);
                        if (userChatList.getUserID() == Integer.parseInt(receiver_id)) {
                            //if (userChatList.getUserID() == Integer.parseInt("1")) {
                            String keyname = "";
                            keyname = ds.getKey();

                            UserChatListModel userChatListModel = new UserChatListModel();
                            userChatListModel.setActive(false);
                            userChatListModel.setAvatar(userChatList.getAvatar());
                            userChatListModel.setDisplayName(userChatList.getDisplayName());
                            userChatListModel.setfUser(userChatList.getfUser());
                            //userChatListModel.setMessage(inputChat.getText().toString());
                            userChatListModel.setMessage(textMessage);
                            userChatListModel.setNew(false);
                            userChatListModel.setTimestamp(Common.GetTime());
                            userChatListModel.setUserID(userChatList.getUserID());

                            DatabaseReference node = userChild.getRef().child(keyname); // -ML6HEibJbmKIyvCTGTx
                            node.setValue(userChatListModel);
                            isChatAvailable = true;
                            break;

                        } else {
                            isChatAvailable = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };

        userChild.addListenerForSingleValueEvent(valueEventListener);
    }


}