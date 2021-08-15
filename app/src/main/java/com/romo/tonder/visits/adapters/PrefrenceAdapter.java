package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.romo.tonder.visits.R;
import com.romo.tonder.visits.interfaces.PreferOnClickListener;
import com.romo.tonder.visits.models.InterestModels;

import java.util.ArrayList;

public class PrefrenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<InterestModels> prelisting;
    private LayoutInflater inflater;
    private PreferOnClickListener onClickListener;

    public void updateRecords(ArrayList<InterestModels> prelisting) {
        this.prelisting = prelisting;

        notifyDataSetChanged();
    }


    public PrefrenceAdapter(ArrayList<InterestModels> prelisting,PreferOnClickListener onClickListener,Context context) {
        this.prelisting = prelisting;
        this.onClickListener = onClickListener;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getItemViewType(int position) {
        return prelisting.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new PrefrenceAdapter.PreferViewHlder(inflater.inflate(R.layout.item_prefrencelist,
                    parent, false));

        }else {
            return new PrefrenceAdapter.PreferViewHlder(inflater.inflate(R.layout.item_prefrencelist,
                    parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        InterestModels models=prelisting.get(position);
        if (models.getViewType()==1){
            setupItemsView(models,(PrefrenceAdapter.PreferViewHlder)holder);
        }else {
            setupItems(models,(PrefrenceAdapter.PreferViewHlder)holder);
        }


    }

    private void setupItemsView(InterestModels models, PreferViewHlder holder) {
        String text =models.getPname();
        String isSelection=models.getPselected();
        if (isSelection.equals("Yes")){
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked_24, 0);
            holder.textView.setText(text);
        }else {
            holder.textView.setText("");
            holder.textView.setVisibility(View.GONE);
        }
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClicked(models,holder.getAdapterPosition());
                /*if (models.getPselected().equals("Yes")){
                    models.setPselected("No");
                    holder.textView.setTextColor(context.getColor(R.color.grey_font));
                    if (getSelectedItemList().size()==0){
                        onClickListener.onClicked(models,false);

                    }
                }else {
                    models.setPselected("Yes");
                    holder.textView.setTextColor(context.getColor(R.color.red_main_theme));
                    onClickListener.onClicked(models,true);
                }*/
            }
        });
    }

    private void setupItems(InterestModels models, PreferViewHlder holder) {
        String text =models.getPname();
        String isSelection=models.getPselected();
        if (isSelection.equals("Yes")){
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked_24, 0);
            holder.textView.setText(text);
        }else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_uncheck_box_outline_blank_24px, 0);
            holder.textView.setText(text);
        }
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClicked(models,holder.getAdapterPosition());
                /*if (models.getPselected().equals("Yes")){
                    models.setPselected("No");
                    holder.textView.setTextColor(context.getColor(R.color.grey_font));
                    if (getSelectedItemList().size()==0){
                        onClickListener.onClicked(models,false);

                    }
                }else {
                    models.setPselected("Yes");
                    holder.textView.setTextColor(context.getColor(R.color.red_main_theme));
                    onClickListener.onClicked(models,true);
                }*/
            }
        });
    }

    public ArrayList<InterestModels> getSelectedItemList(){

        ArrayList<InterestModels> list=new ArrayList<>();
        for (InterestModels interestModels: prelisting){
            if (interestModels.getPselected().equals("Yes")){
                list.add(interestModels);
            }
        }
        return list;

    }

    @Override
    public int getItemCount() {
        return prelisting.size();
    }
    public class PreferViewHlder extends RecyclerView.ViewHolder {
        AppCompatTextView textView;
        public PreferViewHlder(View inflate) {
            super(inflate);
            textView = inflate.findViewById(R.id.tvPreference);

        }
    }
}
