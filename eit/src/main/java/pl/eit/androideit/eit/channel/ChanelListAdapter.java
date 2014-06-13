package pl.eit.androideit.eit.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.async_task.ToogleSubscriptonAsyncTask;
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.service.model.Channel;

public class ChanelListAdapter extends ArrayAdapter<Channel> {

    Context mContext;
    List<Channel> mChanelArray;
    int mRowLayout;
    private AppPreferences mAppPreferences;

    public ChanelListAdapter(Context context, List<Channel> channels) {
        super(context, R.layout.channels_activity_row);
        mContext = context;
        mChanelArray = channels;
        mAppPreferences = new AppPreferences(context);
    }

    private class ViewHolder {
        TextView channelName;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mRowLayout, null);

            holder = new ViewHolder();
            holder.channelName = (TextView) view.findViewById(R.id.channels_list_row_channel_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Channel singleChannel = mChanelArray.get(position);
        holder.channelName.setText(singleChannel.channelName);


        return view;
    }
}

