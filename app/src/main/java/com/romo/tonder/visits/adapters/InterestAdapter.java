package com.romo.tonder.visits.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.InterestModels;

import java.util.List;

public class  InterestAdapter extends BaseAdapter {
    Activity activity;
    List<InterestModels> pList;
    //List<InterestModels> interestModelsList;
    LayoutInflater inflater;

    public InterestAdapter(Activity activity) {
        this.activity = activity;
    }

    public InterestAdapter(Activity activity, List<InterestModels> pList) {
        this.activity = activity;
        this.pList = pList;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return pList.size();
    }

    @Override
    public Object getItem(int position) {
        return pList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InterestAdapter.ViewHolder viewHolder = null;
        /*if (convertView==null){
            convertView = inflater.
                    inflate(R.layout.item_prefrencelist, parent, false);
        }*/
        if (viewHolder == null) {

            convertView = inflater.inflate(R.layout.item_prefrencelist, parent, false);
            viewHolder = new InterestAdapter.ViewHolder();
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvPreference);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InterestAdapter.ViewHolder) convertView.getTag();
        }
        InterestModels currentmodel = (InterestModels) getItem(position);
        // get the TextView for item name and item description
//        TextView tvUserName = (TextView)
//                convertView.findViewById(R.id.tvPreference);
        if (currentmodel.getPselected().equals("Yes")) {
            currentmodel.setPselected("Yes");
            viewHolder.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked_24, 0);
        } else {
            viewHolder.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_uncheck_box_outline_blank_24px, 0);

        }
        viewHolder.tvUserName.setText(currentmodel.getPname());
        return convertView;
    }

    public void updateRecords(List<InterestModels> ppmodel) {
        this.pList = ppmodel;

        //notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView tvUserName;

    }
}
