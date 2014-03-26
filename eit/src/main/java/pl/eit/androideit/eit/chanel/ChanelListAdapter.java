package pl.eit.androideit.eit.chanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.service.model.Chanel;

public class ChanelListAdapter extends ArrayAdapter<Chanel> {

    Context mContext;
    ArrayList<Chanel> mChanelArray;
    int rowLayout;

    public ChanelListAdapter(Context context, int rowLayout, ArrayList<Chanel> channelsArray) {
        super(context, rowLayout, channelsArray);
        this.mContext = context;
        this.mChanelArray = channelsArray;
        this.rowLayout = rowLayout;
    }

    private class ViewHolder {
        TextView channelName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(rowLayout, null);

            holder = new ViewHolder();
            holder.channelName = (TextView) view.findViewById(R.id.channels_list_row_channel_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Chanel singleChannel = mChanelArray.get(position);
        holder.channelName.setText(singleChannel.channelName);
        return view;
    }
}

