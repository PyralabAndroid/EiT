package pl.eit.androideit.eit;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.schedule_fragment.BaseScheduleFragment;
import pl.eit.androideit.eit.service.Parser;
import pl.eit.androideit.eit.service.model.BaseSchedule;

public class ScheduleActivity extends FragmentActivity implements ActionBar.TabListener,
        ViewPager.OnPageChangeListener {

    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;

    public static boolean EVEN_WEEK;

    private Calendar mCalendar;
    private Parser mParser;
    private BaseSchedule mBaseSchedule;

    @InjectView(R.id.pager)
    ViewPager mPager;

    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.inject(this);

        mCalendar = Calendar.getInstance();
        EVEN_WEEK = mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0;

        mParser = new Parser(getBaseContext());
        mBaseSchedule = null;

        try {
            Reader reader = new InputStreamReader(getAssets().open("schedule.json"));
            mBaseSchedule = mParser.parseSchedule(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing schedule!");
        }

        mPagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager(), getFragments());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        for (int i=0 ;i <5; i++) {
            fragments.add(BaseScheduleFragment.newInstance(i+1));
        }
        return fragments;
    }

    public BaseSchedule getBaseSchedule(){
        return mBaseSchedule;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTabs();

        final int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (dayOfWeek >= 1 && dayOfWeek <= 5) {
            getActionBar().setSelectedNavigationItem(dayOfWeek);
        }
    }

    private void loadTabs() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.monday))
                .setTabListener(this).setTag(MONDAY));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.tuesday))
                .setTabListener(this).setTag(TUESDAY));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.wednesday))
                .setTabListener(this).setTag(WEDNESDAY));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.thursday))
                .setTabListener(this).setTag(THURSDAY));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.friday))
                .setTabListener(this).setTag(FRIDAY));
    }


    @Override
    protected void onPause() {
        super.onPause();
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.removeAllTabs();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
