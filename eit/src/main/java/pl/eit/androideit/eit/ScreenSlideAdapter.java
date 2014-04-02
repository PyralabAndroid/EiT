package pl.eit.androideit.eit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import pl.eit.androideit.eit.schedule_fragment.BaseScheduleFragment;

public class ScreenSlideAdapter extends FragmentStatePagerAdapter {

    private List<BaseScheduleFragment> mFragments;

    public ScreenSlideAdapter(FragmentManager fm, List<BaseScheduleFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}