package pl.eit.androideit.eit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ScheduleActivity extends Activity implements ActionBar.TabListener{

    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        switch(id) {
            case MONDAY:
                break;
            case TUESDAY:
                break;
            case WEDNESDAY:
                break;
            case THURSDAY:
                break;
            case FRIDAY:
                break;
            default:
                throw new RuntimeException("no tab with id " + id);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
