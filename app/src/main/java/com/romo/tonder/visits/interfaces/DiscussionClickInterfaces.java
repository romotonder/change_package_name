package com.romo.tonder.visits.interfaces;

import com.romo.tonder.visits.helpers.DiscussionModel;

public interface DiscussionClickInterfaces {
    public void onClicked(DiscussionModel model,int position);
    public void commentsClick(DiscussionModel model,int position);
    public void menuClick(DiscussionModel model,int position);
}
