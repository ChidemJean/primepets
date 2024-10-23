package com.monacoprime.primepets.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ViewGroup;

import com.monacoprime.primepets.R;
import com.monacoprime.primepets.fragments.HomeFragment;
import com.monacoprime.primepets.fragments.MapFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jean on 09/02/2017.
 */
public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles = {"Prime Pets"};
    private int[] icons = {};
    private int[] iconsSelected = {};
    private String[] textos = {"NOVIDADES", "MAPA"};
    private int heightIcon;
    Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

    public TabsAdapter(FragmentManager fm, Context c) {
        super(fm);
        double scale = c.getResources().getDisplayMetrics().density;
        heightIcon = (int)(24 * scale + 0.5f);
        this.mContext = c;
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new MapFragment();
                break;
        }
        Bundle params = new Bundle();
        params.putInt("position", position);
        fragment.setArguments(params);
        mPageReferenceMap.put(position, fragment);

        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
//        mPageReferenceMap.remove(position);
    }
    public Fragment getFragment(int position){

        return mPageReferenceMap.get(position);
    }
    @Override
    public int getCount() {
        return textos.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
//        Drawable d = mContext.getResources().getDrawable(icons[position]);
//        d.setBounds(0, 0, heightIcon, heightIcon);
//        ImageSpan is = new ImageSpan(d);
//        SpannableString sp = new SpannableString(" ");
//        sp.setSpan(is, 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return textos[position];
    }
    public CharSequence getPageTitleString(int position) {
        return titles[position];
    }
    public CharSequence getPageTitleSelected(int position) {
        Drawable d = mContext.getResources().getDrawable(iconsSelected[position]);
        d.setBounds(0, 0, heightIcon, heightIcon);
        ImageSpan is = new ImageSpan(d);
        SpannableString sp = new SpannableString(" ");
        sp.setSpan(is, 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }
}