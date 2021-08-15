package com.romo.tonder.visits.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.romo.tonder.visits.R;
import com.romo.tonder.visits.adapters.ImageSliderAdapter;
import com.romo.tonder.visits.models.GallaryModel;
import com.romo.tonder.visits.models.ImageModel;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ArrayList<String> imagelist;
    private SliderView sliderView;
    private List<ImageModel> imageModelList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        sliderView=findViewById(R.id.imageSlider);

        ArrayList<GallaryModel> imags =  (ArrayList<GallaryModel>)getIntent().getSerializableExtra("image_list");
        for (int i=0;i<imags.size();i++){
            /*GallaryModel gallaryModel=imags.get(i);

            gallaryModel.setImgUrl(gallaryModel.getImgUrl());
            gallaryModel.setViewType(Common.GALLARY_PAGE);
            gallaryModel.add(gallaryModel);*/
        }


        imagelist=new ArrayList<>();
        for (int i=0;i<imags.size();i++){
            GallaryModel gallaryModel=imags.get(i);
            imagelist.add(gallaryModel.getImgUrl());
        }
        imageModelList=new ArrayList<>();
        for (int i=0;i<imagelist.size();i++){
            ImageModel imageModel=new ImageModel();
            imageModel.setImgUrl(imagelist.get(i));
            imageModelList.add(imageModel);

        }
        sliderView.setSliderAdapter(new ImageSliderAdapter(this,imageModelList));
    }

    public void gotoPreviousPage(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}