package pl.eit.androideit.eit.schedule_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import pl.eit.androideit.eit.ScheduleActivity;

public class BaseScheduleFragment extends Fragment {

    private int mScheduleId;

    public BaseScheduleFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleId = savedInstanceState.getInt(ScheduleActivity.EXTRA_SCHEDULE_KEY, 1);
    }
}
