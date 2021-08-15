package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.activities.VideoPlayer;
import com.romo.tonder.visits.helpers.Common;
import com.romo.tonder.visits.models.VideoModels;

import java.util.ArrayList;

public class ListVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<VideoModels> videoModelsArrayList;

    public ListVideoAdapter(ArrayList<VideoModels> videoModelsArrayList, Context mContex) {
        this.videoModelsArrayList = videoModelsArrayList;
        this.mContext = mContex;
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getItemViewType(int position) {
        return videoModelsArrayList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Common.LIST_VIDEO) {
            return new ListVideoAdapter.VideoViewHolder(inflater.inflate(R.layout.item_video, parent, false));
        } else {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoModels videoModels = videoModelsArrayList.get(position);
        setupItemVideo(videoModels, (VideoViewHolder) holder);

    }

    private void setupItemVideo(VideoModels videoModels, VideoViewHolder holder) {
        String videoThumbnail=videoModels.getVideoThumnail();
        String videoUrl=videoModels.getVideoUrl();
        if (!videoThumbnail.equals("") && !videoUrl.equals("")) {
            Glide.with(mContext)
                    .load(videoModels.getVideoThumnail())
                    .thumbnail(0.5f)
                    .into(holder.videoThumbnail);
        }
        holder.videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent video=new Intent(mContext, VideoPlayer.class);
                video.putExtra("url",videoUrl);
                mContext.startActivity(video);

            }
        });


    }

    @Override
    public int getItemCount() {
        return videoModelsArrayList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView videoThumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
