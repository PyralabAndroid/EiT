package pl.eit.androideit.eit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.Calendar;

import butterknife.ButterKnife;
import pl.eit.androideit.eit.schedule_fragment.BaseScheduleFragment;

import static com.google.common.base.Preconditions.checkNotNull;

public class ScheduleActivity extends Activity implements ActionBar.TabListener {

    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;
    public static final String EXTRA_SCHEDULE_KEY = "extra_schedule_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.inject(this);
        Calendar calendar = Calendar.getInstance();
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) -1;
        getActionBar().setSelectedNavigationItem(dayOfWeek);
    }

    private void activate(Fragment fragment) {
        checkNotNull(fragment);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.conteiner, fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTabs();
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        final Integer id = (Integer) tab.getTag();
        if (id >= 1 && id <= 5) {
            activateFragemtnWithId(id);
        } else {
            throw new RuntimeException("no tab with id " + id);

        }
    }

    private void activateFragemtnWithId(Integer id) {
        checkNotNull(id);

        BaseScheduleFragment baseScheduleFragment = new BaseScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SCHEDULE_KEY, id);
        baseScheduleFragment.setArguments(bundle);
        activate(baseScheduleFragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.removeAllTabs();
    }
}
