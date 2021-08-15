package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.ImageModel;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ImageSliderAdapter  extends SliderViewAdapter<SliderViewHolder> {
    Context mContex;
    List<ImageModel> imageModelList;

    public ImageSliderAdapter(Context mContex, List<ImageModel> imageModelList) {
        this.mContex = mContex;
        this.imageModelList = imageModelList;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_images,parent,false);

        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderViewHolder viewHolder, int position) {
        ImageModel imageModel=imageModelList.get(position);
        setupImage(imageModel,viewHolder);

    }

    private void setupImage(ImageModel imageModel, SliderViewHolder viewHolder) {
        Glide.with(mContex)
                .load(imageModel.getImgUrl())
                .placeholder(R.drawable.app_placeholder)
                .error(R.drawable.app_placeholder)
                .into(viewHolder.itemImageSlider);
    }

    @Override
    public int getCount() {
        return imageModelList.size();
    }
}
class SliderViewHolder extends SliderViewAdapter.ViewHolder{
    ImageView itemImageSlider;

    public SliderViewHolder(View itemView) {
        super(itemView);
        itemImageSlider=itemView.findViewById(R.id.iv_auto_image_slider);
    }
}


