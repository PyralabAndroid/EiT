package pl.eit.androideit.eit.schedule_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.ScheduleActivity;

public class BaseScheduleFragment extends Fragment {

    private int mScheduleId;

    @InjectView(R.id.base_fragment_tv)
    TextView mMainTextView;

    public BaseScheduleFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mMainTextView.setText(String.valueOf(mScheduleId));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleId = savedInstanceState.getInt(ScheduleActivity.EXTRA_SCHEDULE_KEY, 1);
    }
}
