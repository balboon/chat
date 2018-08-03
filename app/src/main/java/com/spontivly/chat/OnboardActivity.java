package com.spontivly.chat;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class OnboardActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        viewPager = findViewById(R.id.slideViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
    }
}
