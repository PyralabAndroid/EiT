package pl.eit.androideit.eit.schedule_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.ScheduleActivity;
import pl.eit.androideit.eit.service.ScheduleFinder;

public class BaseScheduleFragment extends Fragment {

    public static final String EXTRA_SCHEDULE_KEY = "extra_schedule_key";

    private int mScheduleId;

    @InjectView(R.id.base_schedule_list_view)
    ListView mListView;

    private ScheduleListAdapter mScheduleListAdapter;
    private ScheduleFinder mScheduleFinder;

    public static BaseScheduleFragment newInstance(int id) {
        BaseScheduleFragment baseScheduleFragment = new BaseScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SCHEDULE_KEY, id);
        baseScheduleFragment.setArguments(bundle);
        return baseScheduleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        List<ScheduleItem> list = mScheduleFinder.getScheduleList();
        mScheduleListAdapter = new ScheduleListAdapter(getActivity().getBaseContext(), list);
        mListView.setAdapter(mScheduleListAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleId = getArguments().getInt(EXTRA_SCHEDULE_KEY, 1);
        mScheduleFinder = new ScheduleFinder(getActivity().getBaseContext(),
                ((ScheduleActivity)getActivity()).getBaseSchedule(),
                mScheduleId);
    }


}
