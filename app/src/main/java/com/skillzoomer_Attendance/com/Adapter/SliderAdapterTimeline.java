package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skillzoomer_Attendance.com.Model.SliderDataTimeline;
import com.skillzoomer_Attendance.com.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapterTimeline extends SliderViewAdapter<SliderAdapterTimeline.SliderAdapterTimelineViewHolder> {

    private Context context;
    private final ArrayList<SliderDataTimeline> mSliderItems1;

    public SliderAdapterTimeline(Context context, ArrayList<SliderDataTimeline> mSliderItems) {
        this.context = context;
        this.mSliderItems1 = mSliderItems;
    }

    @Override
    public SliderAdapterTimeline.SliderAdapterTimelineViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_slider_timeline, null);
        return new SliderAdapterTimeline.SliderAdapterTimelineViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterTimeline.SliderAdapterTimelineViewHolder viewHolder, int position) {
        final SliderDataTimeline sliderItem = mSliderItems1.get(position);
        Log.e("PositionSlider",""+position);
        if(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("Language","hi").equals("en")){
            Glide.with(viewHolder.itemView)
                    .load(sliderItem.getUrl())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        }else{
            Glide.with(viewHolder.itemView)
                    .load(sliderItem.getUrlHindi())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        }


    }

    @Override
    public int getCount() {
        return mSliderItems1.size();
    }

    public class SliderAdapterTimelineViewHolder extends ViewHolder {
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterTimelineViewHolder(View itemView) {
            super(itemView);
            imageViewBackground=itemView.findViewById(R.id.imageViewBackground);
            this.itemView = itemView;
        }
    }
}
