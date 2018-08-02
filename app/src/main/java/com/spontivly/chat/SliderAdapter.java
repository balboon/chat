package com.spontivly.chat;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.onboard_asset_1,
            R.drawable.general_view,

    };

    @Override
    public int getCount() {
        return 0; //return slide_headings.length
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == (RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboard_layout1, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideDescription = view.findViewById(R.id.slide_description);



        return view;
    }
}
