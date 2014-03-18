package pl.eit.androideit.eit;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;

import butterknife.ButterKnife;
import pl.eit.androideit.eit.schedule_fragment.BaseScheduleFragment;
import pl.eit.androideit.eit.service.Parser;
import pl.eit.androideit.eit.service.model.BaseSchedule;

import static com.google.common.base.Preconditions.checkNotNull;

public class ScheduleActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;

    public static boolean EVEN_WEEK;

    private Calendar mCalendar;
    private Parser mParser;
    private BaseSchedule mBaseSchedule;

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
    }

    public BaseSchedule getBaseSchedule(){
        return mBaseSchedule;
    }


    private void activate(Fragment fragment) {
        checkNotNull(fragment);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.conteiner, fragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTabs();

        final int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
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
        activate(BaseScheduleFragment.newInstance(id));
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

    @Override
    protected void onStart() {
        super.onStart();
    }
}
