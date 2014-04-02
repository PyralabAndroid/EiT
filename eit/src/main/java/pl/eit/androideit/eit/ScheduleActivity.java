package pl.eit.androideit.eit;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
        ViewPager.OnPageChangeListener, CustomDismissDialogListener {

    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;

    private Calendar mCalendar;
    private Parser mParser;
    private BaseSchedule mBaseSchedule;

    @InjectView(R.id.pager)
    ViewPager mPager;

    private PagerAdapter mPagerAdapter;
    private ActionBar mActionBar;
    private ArrayList<BaseScheduleFragment> mFagments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.inject(this);

        mCalendar = Calendar.getInstance();

        mActionBar = getActionBar();
        mActionBar.setTitle("Plan zajęć");
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

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

    private List<BaseScheduleFragment> getFragments() {
        mFagments = new ArrayList<BaseScheduleFragment>();
        for (int i=0 ;i <5; i++) {
            mFagments.add(BaseScheduleFragment.newInstance(i+1));
        }
        return mFagments;
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
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.addTab(mActionBar.newTab().setText(getString(R.string.monday))
                .setTabListener(this).setTag(MONDAY));
        mActionBar.addTab(mActionBar.newTab().setText(getString(R.string.tuesday))
                .setTabListener(this).setTag(TUESDAY));
        mActionBar.addTab(mActionBar.newTab().setText(getString(R.string.wednesday))
                .setTabListener(this).setTag(WEDNESDAY));
        mActionBar.addTab(mActionBar.newTab().setText(getString(R.string.thursday))
                .setTabListener(this).setTag(THURSDAY));
        mActionBar.addTab(mActionBar.newTab().setText(getString(R.string.friday))
                .setTabListener(this).setTag(FRIDAY));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case R.id.action_change_group:
                 changeGroup();
                return true;
            case R.id.action_change_week:
                changeWeek();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeWeek() {
        // TODO:
    }

    private void changeGroup() {
        GroupDialog groupDialog = new GroupDialog(this);
        groupDialog.setOnDismissDialogListener(this);
        groupDialog.showDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActionBar.removeAllTabs();
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onDialogDismiss() {
        for (BaseScheduleFragment fragment : mFagments) {
            fragment.updateFragment();
        }
        mPagerAdapter.notifyDataSetChanged();
    }
}
