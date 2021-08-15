package com.romo.tonder.visits.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.romo.tonder.visits.MyApplication;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.CommentsAdapter;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.interfaces.CommentsListenerInterface;
import com.romo.tonder.visits.models.CommentsModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class CommentsActivity extends AppCompatActivity implements CommentsListenerInterface {
    private RecyclerView commentsArea;
    private CircleImageView userDP;
    private AppCompatTextView userName, commentsDate, reviewRate, reviewTitle, reviewBody, likeCount, commentsCount;
    private ArrayList<CommentsModels> listComments;
    private CommentsAdapter cadapter;
    private AppCompatEditText commentInfoText;
    private AppCompatImageView btnSendListing, btnSendEvent;
    private SharedPreferences appPrefs;
    private Dialog dialog;
    private BottomSheetDialog bottomSheetDialog;
    private String commentsReviewRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initView();
        if (getIntent().hasExtra("from_page")) {
            if (getIntent().getStringExtra("from_page").equals("from_event_page")) {
                if (getIntent().hasExtra("reviewID")) {
                    populateEventComments();
                    eventCommentList(Common.EVENT);
                }
            } else if (getIntent().getStringExtra("from_page").equals("from_listing_page")) {
                if ((getIntent().hasExtra("reviewID")) && (getIntent().hasExtra("userID"))) {
                    populatedListingComments();
                    getCommentsList(Common.LISTING);

                }
            }
            clickEvent();
        }
    }

    private void eventCommentList(String event) {
        dialog.show();
        String url = Common.BASE_URL + "event-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("eventId", getIntent().getStringExtra("reviewID"))
                // .addBodyParameter("eventId", "23653")
                .addBodyParameter("userId", getIntent().getStringExtra("userID"))
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Select")
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponse(response, event);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void populateEventComments() {
        reviewRate.setVisibility(View.INVISIBLE);
        reviewTitle.setVisibility(View.GONE);
        btnSendListing.setVisibility(View.GONE);
        btnSendEvent.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("authorName")))
            userName.setText(getIntent().getStringExtra("authorName"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("reviewDate")))
            commentsDate.setText(getIntent().getStringExtra("reviewDate"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("reviewBody"))) {
            reviewBody.setText(getIntent().getStringExtra("reviewBody"));
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("like"))) {
            String likes = getIntent().getStringExtra("like") + " " + getResources().getString(R.string.like);
            likeCount.setText(likes);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("comment"))) {
            String comments = getIntent().getStringExtra("like") + " " + getResources().getString(R.string.comments);
            commentsCount.setText(comments);
        }


    }

    private void populatedListingComments() {
        reviewRate.setVisibility(View.VISIBLE);
        reviewTitle.setVisibility(View.VISIBLE);
        btnSendListing.setVisibility(View.VISIBLE);
        btnSendEvent.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("authorName")))
            userName.setText(getIntent().getStringExtra("authorName"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("reviewDate")))
            commentsDate.setText(getIntent().getStringExtra("reviewDate"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("rate"))) {
            commentsReviewRating = "";
            commentsReviewRating = getIntent().getStringExtra("rate");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("rateFrom"))) {
            commentsReviewRating = commentsReviewRating + " / " + getIntent().getStringExtra("rateFrom");
            reviewRate.setText(commentsReviewRating);
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("reviewTitle")))
            reviewTitle.setText(getIntent().getStringExtra("reviewTitle"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("reviewBody")))
            reviewBody.setText(getIntent().getStringExtra("reviewBody"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("like"))) {
            String likes = getIntent().getStringExtra("like") + " " + getResources().getString(R.string.like);
            likeCount.setText(likes);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("comment"))) {
            String comments = getIntent().getStringExtra("like") + " " + getResources().getString(R.string.comments);
            commentsCount.setText(comments);
        }
    }

    private void clickEvent() {
        btnSendListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    addComments(Common.LISTING);
                }
            }
        });
        btnSendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    addCommentsEvents(Common.EVENT);
                }
            }
        });
    }

    private void addCommentsEvents(String event) {
        dialog.show();
        String url = Common.BASE_URL + "event-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("eventId", getIntent().getStringExtra("reviewID"))
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", appPrefs.getString("userID", ""))
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Add")
                .addBodyParameter("commentInfo", commentInfoText.getText().toString())
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponse(response, event);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void addComments(String listing) {
        dialog.show();
        String url = Common.BASE_URL + "review-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("reviewId", getIntent().getStringExtra("reviewID"))
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", appPrefs.getString("userID", ""))
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Add")
                .addBodyParameter("commentInfo", commentInfoText.getText().toString())
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponse(response, listing);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void initView() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_loading);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        appPrefs = ((MyApplication) getApplicationContext()).getAppPrefs();
        commentsArea = (RecyclerView) findViewById(R.id.comments_area);
        commentsArea.setLayoutManager(new LinearLayoutManager(this));
        //user senction
        userDP = (CircleImageView) findViewById(R.id.user_dp);

        userName = (AppCompatTextView) findViewById(R.id.user_name);
        commentsDate = (AppCompatTextView) findViewById(R.id.user_date);
        reviewRate = (AppCompatTextView) findViewById(R.id.review_rate);
        reviewTitle = (AppCompatTextView) findViewById(R.id.txt_review_title);
        reviewBody = (AppCompatTextView) findViewById(R.id.txt_review_body);
        likeCount = (AppCompatTextView) findViewById(R.id.txt_likecount);
        commentsCount = (AppCompatTextView) findViewById(R.id.txt_commentscount);
        listComments = new ArrayList<>();
        cadapter = new CommentsAdapter(listComments, this);
        commentsArea.setAdapter(cadapter);

        //btn
        btnSendListing = (AppCompatImageView) findViewById(R.id.btn_send_listing);
        btnSendEvent = (AppCompatImageView) findViewById(R.id.btn_send_event);
        commentInfoText = (AppCompatEditText) findViewById(R.id.edit_comments_info);


    }

    private void getCommentsList(String listing) {
        dialog.show();
        String url = Common.BASE_URL + "review-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("reviewId", getIntent().getStringExtra("reviewID"))
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", getIntent().getStringExtra("userID"))
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Select")
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        parseResponse(response, listing);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void parseResponse(JSONObject response, String from) {
        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            JSONArray jsonArray = null;
            JSONObject jsonObject = null;
            if (response.getString("status").equals("Success")) {
                jsonArray = response.getJSONArray("commentList");
                if (jsonArray.length() > 0) {
                    listComments.clear();
                    commentInfoText.setText("");

                    for (int index = 0; index < jsonArray.length(); index++) {
                        jsonObject = jsonArray.getJSONObject(index);
                        CommentsModels bean = new CommentsModels();
                        if (!TextUtils.isEmpty(jsonObject.getString("displayName")))
                            bean.setDisplayName(jsonObject.getString("displayName"));
                        if (!TextUtils.isEmpty(jsonObject.getString("commentDesc")))
                            bean.setCommentDescription(jsonObject.getString("commentDesc"));
                        if (!TextUtils.isEmpty(jsonObject.getString("commentDate")))
                            bean.setCommentDate(jsonObject.getString("commentDate"));

                        bean.setUserID(jsonObject.getString("userId"));
                        bean.setCommentID(jsonObject.getString("commentId"));
                        if (from.equals("listing"))
                            bean.setViewType(1);
                        else
                            bean.setViewType(2);

                        listComments.add(bean);

                    }
                    if (listComments.size() > 0) {
                        cadapter.notifyDataSetChanged();
                    } else {
                        //nothing
                    }
                } else {
                    Toast.makeText(this, "No Comments", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidation() {
        if (!TextUtils.isEmpty(commentInfoText.getText().toString()))
            return true;

        return false;
    }


    @Override
    public void onClicked(CommentsModels model, int position) {
        String reviewID = model.getCommentID();
        String userID = model.getUserID();
        String commentID = model.getCommentID();
        String commentInfo = model.getCommentDescription();
        int viewType = model.getViewType();
        CommentsModels bean = listComments.get(position);
        showDialog(reviewID, userID, commentInfo, commentID, viewType);

    }

    private void showDialog(String reviewId, String userId, String commentDesc, String commentId, int viewType) {
        AppCompatTextView tvUpdate, tvDelete, tvCancel;
        bottomSheetDialog = new BottomSheetDialog(CommentsActivity.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_comments,
                null);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        tvUpdate = (AppCompatTextView) view.findViewById(R.id.update_area);
        tvDelete = (AppCompatTextView) view.findViewById(R.id.delete_area);
        tvCancel = (AppCompatTextView) view.findViewById(R.id.cancel_area);

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CommentsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.add_discussion, null);
                mBuilder.setView(view);
                final EditText comment = view.findViewById(R.id.add_comment);
                MaterialButton btnAdd = view.findViewById(R.id.btn_add);
                MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
                final AlertDialog dialog = mBuilder.create();
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!comment.getText().toString().equals("")) {
                            if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (viewType == 1) {
                                updateComments(reviewId, userId, comment.getText().toString(), commentId, viewType);
                            } else {
                                updateCommentsEvents(reviewId, userId, comment.getText().toString(), commentId, viewType);
                            }


                        } else {
                            Toast.makeText(CommentsActivity.this,
                                    "Please write comments", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewType == 1) {
                    deleteComments(reviewId, userId, commentId, viewType);
                } else {
                    deleteCommentsEvents(reviewId, userId, commentId, viewType);
                }

                bottomSheetDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void deleteCommentsEvents(String reviewId, String userId, String commentId, int viewType) {
        dialog.show();
        String url = Common.BASE_URL + "event-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("eventId", reviewId)
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", userId)
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Delete")
                .addBodyParameter("commentId", commentId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        deleteResponse(response, viewType);
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void deleteComments(String reviewId, String userId, String commentId, int viewType) {
        dialog.show();
        String url = Common.BASE_URL + "review-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("reviewId", reviewId)
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", userId)
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Delete")
                .addBodyParameter("commentId", commentId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        deleteResponse(response, viewType);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void deleteResponse(JSONObject response, int viewType) {
        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            JSONArray jsonArray = null;
            JSONObject jsonObject = null;
            if (response.getString("status").equals("Success")) {
                String message = response.getString("Data");
                new MaterialAlertDialogBuilder(CommentsActivity.this)
                        .setTitle("Deleted")
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (viewType == 1) {
                                    getCommentsList(Common.LISTING);
                                } else {
                                    eventCommentList(Common.EVENT);
                                }

                            }
                        })
                        .show();
                /*dialog = new Dialog(this);
                dialog.setContentView(R.layout.success_dialog);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
                dialog.setCancelable(false);
                Button btn_ok=dialog.findViewById(R.id.btnOk);
                AppCompatTextView succ_msg=dialog.findViewById(R.id.success_msg);
                succ_msg.setText("");
                succ_msg.setText(message);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCommentsList();
                        dialog.dismiss();

                    }
                });*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateCommentsEvents(String reviewId, String userId, String commentDesc, String commentId, int viewType) {
        dialog.show();
        String url = Common.BASE_URL + "event-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("eventId", reviewId)
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", userId)
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Update")
                .addBodyParameter("commentInfo", commentDesc)
                .addBodyParameter("commentId", commentId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        updateResponse(response, viewType);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void updateComments(String reviewId, String userId, String commentDesc, String commentId, int viewType) {
        dialog.show();
        String url = Common.BASE_URL + "review-comment";
        AndroidNetworking.post(url)
                .addBodyParameter("reviewId", reviewId)
                //.addBodyParameter("reviewId", "23352")
                .addBodyParameter("userId", userId)
                //.addBodyParameter("userId", "369")
                .addBodyParameter("type", "Update")
                .addBodyParameter("commentInfo", commentDesc)
                .addBodyParameter("commentId", commentId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        updateResponse(response, viewType);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void updateResponse(JSONObject response, int viewType) {
        if (!CommentsActivity.this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            if (response.getString("status").equals("Success")) {
                String message = response.getString("Data");
                new MaterialAlertDialogBuilder(CommentsActivity.this)
                        .setTitle("Updated")
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (viewType == 1) {
                                    getCommentsList(Common.LISTING);
                                } else {
                                    eventCommentList(Common.EVENT);
                                }

                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}