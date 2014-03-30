package pl.eit.androideit.eit.schedule_fragment;

import android.view.View;
import android.widget.TextView;

import pl.eit.androideit.eit.R;
import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.base_schedule_list_row)
public class ScheduleViewHolder extends ItemViewHolder<ScheduleItem> {

    @ViewId(R.id.base_schedule_row_name)
    TextView mName;
    @ViewId(R.id.base_schedule_row_place)
    TextView mPlace;
    @ViewId(R.id.base_schedule_row_time)
    TextView mTime;
    @ViewId(R.id.base_schedule_row_type)
    TextView mType;

    public ScheduleViewHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(ScheduleItem scheduleItem, PositionInfo positionInfo) {
        mName.setText(scheduleItem.mName);
        mPlace.setText(scheduleItem.mPlace);
        mTime.setText(scheduleItem.mTime);
        mType.setText(scheduleItem.mType);
    }
}
