package pl.eit.androideit.eit.schedule_fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.ScheduleActivity;
import pl.eit.androideit.eit.service.ScheduleFinder;
import pl.eit.androideit.eit.service.model.BaseSchedule;
import uk.co.ribot.easyadapter.EasyAdapter;

public class BaseScheduleFragment extends Fragment {

    private static final String EXTRA_SCHEDULE_KEY = "extra_schedule_key";

    private int mScheduleId;

    @InjectView(R.id.base_schedule_list_view)
    ListView mListView;

    List<ScheduleItem> mList;

    private ScheduleFinder mScheduleFinder;
    private EasyAdapter<ScheduleItem> mAdapter;

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
        mList = mScheduleFinder.getScheduleList();
        mAdapter = new EasyAdapter<ScheduleItem>(getActivity().getBaseContext(),
                ScheduleViewHolder.class, mList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleId = getArguments().getInt(EXTRA_SCHEDULE_KEY, 1);
        mScheduleFinder = new ScheduleFinder(getActivity().getBaseContext(),
                ((ScheduleActivity)getActivity()).getBaseSchedule(),
                mScheduleId);
    }

    public void updateFragment(){
        if (mScheduleFinder != null) {
            BaseSchedule baseSchedule = ((ScheduleActivity) getActivity()).getBaseSchedule();
            if (baseSchedule != null) {
                mScheduleFinder.updateBaseSchedule(baseSchedule);
                mList = mScheduleFinder.getScheduleList();
                mAdapter.setItems(mList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
