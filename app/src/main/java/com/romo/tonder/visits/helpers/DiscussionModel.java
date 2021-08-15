package com.romo.tonder.visits.helpers;

public class DiscussionModel {
    //event
    private String event_id;
    private String comment_desc;
    private String display_name;
    private String comment_date;
    //General
    private String total_likes;
    private String count_comments;
    private boolean my_like;
    //listing
    private String reviewId;
    private String authorName;
    private String userId;
    private String reviewDate;
    private String reviewtitle;
    private String reviewDesc;
    private String overallRatingText;
    private String overallReviewRate;
    private String overallReviewRateFrom;


    private int viewType;

    /*public DiscussionModel(String event_id, String comment_desc, String display_name, String comment_date, String total_likes, String count_comments, boolean my_like) {
        //this.viewType=viewType;
        this.event_id = event_id;
        this.comment_desc = comment_desc;
        this.display_name = display_name;
        this.comment_date = comment_date;
        this.total_likes = total_likes;
        this.count_comments = count_comments;
        this.my_like = my_like;
    }*/


    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getComment_desc() {
        return comment_desc;
    }

    public void setComment_desc(String comment_desc) {
        this.comment_desc = comment_desc;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(String total_likes) {
        this.total_likes = total_likes;
    }

    public String getCount_comments() {
        return count_comments;
    }

    public void setCount_comments(String count_comments) {
        this.count_comments = count_comments;
    }

    public boolean isMy_like() {
        return my_like;
    }

    public void setMy_like(boolean my_like) {
        this.my_like = my_like;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewtitle() {
        return reviewtitle;
    }

    public void setReviewtitle(String reviewtitle) {
        this.reviewtitle = reviewtitle;
    }

    public String getReviewDesc() {
        return reviewDesc;
    }

    public void setReviewDesc(String reviewDesc) {
        this.reviewDesc = reviewDesc;
    }

    public String getOverallRatingText() {
        return overallRatingText;
    }

    public void setOverallRatingText(String overallRatingText) {
        this.overallRatingText = overallRatingText;
    }

    public String getOverallReviewRate() {
        return overallReviewRate;
    }

    public void setOverallReviewRate(String overallReviewRate) {
        this.overallReviewRate = overallReviewRate;
    }

    public String getOverallReviewRateFrom() {
        return overallReviewRateFrom;
    }

    public void setOverallReviewRateFrom(String overallReviewRateFrom) {
        this.overallReviewRateFrom = overallReviewRateFrom;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
