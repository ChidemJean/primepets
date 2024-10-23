package com.monacoprime.primepets.slider;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monacoprime.primepets.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jean on 30/05/2017.
 */

public class Slider {

    private ViewPager viewPagerBanner;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layoutsBanner;
    private ViewPagerBannerAdapter viewPagerAdapterBanner;
    private int[] imagesBanner;
    private int duration;
    private Activity context;
    private Timer timer;
    private int page = 0;

    public Slider(Activity context, int[] images, int duration){

        this.context = context;
        this.duration = duration;
        this.imagesBanner = images;

        viewPagerBanner = (ViewPager) context.findViewById(R.id.view_pager_banner);
        dotsLayout = (LinearLayout) context.findViewById(R.id.layout_dots_banner);

        addBottomDots(0);

        viewPagerAdapterBanner = new ViewPagerBannerAdapter();
        viewPagerBanner.setAdapter(viewPagerAdapterBanner);
        viewPagerBanner.addOnPageChangeListener(viewPagerPageChangeListener);

        initPageSwitcher(duration);

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[imagesBanner.length];

        int colorsActive = R.color.dot_dark_banner;
        int colorsInactive = R.color.dot_light_banner;

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(context.getResources().getColor(colorsInactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(context.getResources().getColor(colorsActive));
    }

    public void initPageSwitcher(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
    }

    class RemindTask extends TimerTask {
        @Override
        public void run() {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    if (page >= imagesBanner.length) {
                        page = 0;
                    } else {
                        page++;
                    }
                    viewPagerBanner.setCurrentItem(page);
                }
            });
        }
    }
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            page = position;
            addBottomDots(position);
        }
        @Override public void onPageScrolled(int arg0, float arg1, int arg2) {}
        @Override public void onPageScrollStateChanged(int arg0) {}
    };

    public class ViewPagerBannerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public ViewPagerBannerAdapter() { }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_banner_layout, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item_banner);
            iv.setImageDrawable(context.getResources().getDrawable(imagesBanner[position]));

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return imagesBanner.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
