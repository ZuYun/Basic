package com.baselib.adapter.frgmt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class TabAdapter extends FragmentPagerAdapter {
    private String[] mTabtiles;
    private BaseFrgmtFractory mFrmtFractory;

    public TabAdapter(FragmentManager fm, String[] tabtiles, BaseFrgmtFractory frmtFractory){
        super(fm);
        mTabtiles = tabtiles;
        mFrmtFractory = frmtFractory;
    }

    @Override
    public Fragment getItem(int position){
        return mFrmtFractory.createFragment(position);
    }

    @Override
    public int getCount(){
        return mTabtiles.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return mTabtiles[position];
    }
}
