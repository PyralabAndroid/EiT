package pl.eit.androideit.eit.schedule_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.eit.androideit.eit.R;

import static com.google.common.base.Preconditions.checkNotNull;


public class ScheduleListAdapter extends ArrayAdapter<ScheduleItem> {

    private Context mContext;
    private List<ScheduleItem> mItems;

    public ScheduleListAdapter(Context context, List<ScheduleItem> items) {
        super(context, R.layout.base_schedule_list_row);
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.base_schedule_list_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) convertView.findViewById(R.id.base_schedule_row_name);
            viewHolder.mPlace = (TextView) convertView.findViewById(R.id.base_schedule_row_place);
            viewHolder.mTime = (TextView) convertView.findViewById(R.id.base_schedule_row_time);
            viewHolder.mType = (TextView) convertView.findViewById(R.id.base_schedule_row_type);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final ScheduleItem scheduleItem = mItems.get(position);
        checkNotNull(scheduleItem);
        holder.mName.setText(scheduleItem.mName);
        holder.mPlace.setText(scheduleItem.mPlace);
        holder.mTime.setText(scheduleItem.mTime);
        holder.mType.setText(scheduleItem.mType);

        return convertView;
    }

    public static class ViewHolder {
        TextView mName;
        TextView mPlace;
        TextView mTime;
        TextView mType;
    }
}
