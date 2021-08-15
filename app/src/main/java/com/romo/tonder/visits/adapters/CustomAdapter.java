package com.romo.tonder.visits.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.Pmodel;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
  Activity activity;
  List<Pmodel> pList;
  //List<InterestModels> interestModelsList;
  LayoutInflater inflater;

  public CustomAdapter(Activity activity) {
    this.activity = activity;
  }

  public CustomAdapter(Activity activity, List<Pmodel> pList) {
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
    return position;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;

    if (viewHolder == null) {

      convertView = inflater.inflate(R.layout.item_prefrencelist, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvPreference);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    Pmodel model = pList.get(position);
    if (model.getPselected().equals("Yes")) {
      model.setPselected("Yes");
      viewHolder.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked_24, 0);
    } else {
      viewHolder.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_uncheck_box_outline_blank_24px, 0);

    }
    viewHolder.tvUserName.setText(model.getPname());
    return convertView;
  }

  public void updateRecords(List<Pmodel> ppmodel) {
    this.pList = ppmodel;

    notifyDataSetChanged();
  }

  public class ViewHolder {
    TextView tvUserName;

  }
}
