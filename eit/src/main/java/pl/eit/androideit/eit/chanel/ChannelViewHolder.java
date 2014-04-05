package pl.eit.androideit.eit.chanel;

import android.view.View;
import android.widget.TextView;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.service.model.Channel;
import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.channels_activity_row)
public class ChannelViewHolder extends ItemViewHolder<Channel> {

    @ViewId(R.id.channels_list_row_channel_name)
    TextView mChannelName;

    public ChannelViewHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Channel chanel, PositionInfo positionInfo) {
        mChannelName.setText(chanel.channelName);
    }
}

