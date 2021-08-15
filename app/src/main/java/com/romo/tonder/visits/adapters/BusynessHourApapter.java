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
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.BusynessHourModel;

import java.util.ArrayList;
import java.util.Calendar;

public class BusynessHourApapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<BusynessHourModel> busynessHourLists;
    private Context context;
    private LayoutInflater inflater;
    private static final int SERVICE = 1;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;
    public BusynessHourApapter(ArrayList<BusynessHourModel> busynessHourLists, Context context) {
        this.busynessHourLists = busynessHourLists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getItemViewType(int position) {
        return busynessHourLists.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            return new BusynessHourApapter.ItemViewHolder(inflater.inflate(R.layout.item_business_hour, parent, false));
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BusynessHourModel busynessHourModel=busynessHourLists.get(position);
        setupView(busynessHourModel,(BusynessHourApapter.ItemViewHolder)holder);

    }

    private void setupView(BusynessHourModel busynessHourModel, ItemViewHolder holder) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today="";
        today= Common.dayOfWeek(day);
        String currentDay=busynessHourModel.getDayOfWeek();
        String open_time=busynessHourModel.getFirstOpenHour();
        String close_time=busynessHourModel.getFirstCloseHour();

        if (!currentDay.equals("")){
            if (currentDay.equalsIgnoreCase(today)) {
                holder.day.setTextColor(context.getColor(R.color.red_main_theme));
                holder.openingTime.setTextColor(context.getColor(R.color.red_main_theme));
            } else {
                holder.day.setTextColor(context.getColor(R.color.title_text_color));
                holder.openingTime.setTextColor(context.getColor(R.color.title_text_color));
            }
            holder.day.setText(currentDay);
        }
        if ((!TextUtils.isEmpty(open_time) && !open_time.equals("null"))
                && (!TextUtils.isEmpty(close_time) && !close_time.equals("null"))){
            holder.openingTime.setText(open_time + "-" + close_time);
        }
    }



    @Override
    public int getItemCount() {
        return busynessHourLists.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView day,openingTime;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            day=itemView.findViewById(R.id.tv_day);
            openingTime=itemView.findViewById(R.id.tv_timing);
        }
    }
}
