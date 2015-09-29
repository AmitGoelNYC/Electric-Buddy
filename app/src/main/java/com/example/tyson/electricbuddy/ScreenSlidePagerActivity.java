package com.example.tyson.electricbuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class ScreenSlidePagerActivity extends AppCompatActivity {

    private ViewPager mPager;
    private ArrayList<Stations> exampleListItem;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        exampleListItem = getIntent().getExtras().getParcelableArrayList("theObject");
        position = getIntent().getExtras().getInt("position");

        if (exampleListItem.get(position).operatorTitle != null)
            Log.d("Operator Title", exampleListItem.get(position).operatorTitle);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        mPager.setCurrentItem(position);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.material_blue_500));
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            args.putParcelable(ScreenSlidePageFragment.ARG_OBJECT,exampleListItem.get(i));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return exampleListItem.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return exampleListItem.get(position).stationName;
        }
    }
}