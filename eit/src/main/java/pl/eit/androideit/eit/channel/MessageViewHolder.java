package pl.eit.androideit.eit.channel;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.helpers.TimeCalculateHelper;
import pl.eit.androideit.eit.service.model.Message;
import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.message_row)
public class MessageViewHolder extends ItemViewHolder<Message>{

    @ViewId(R.id.message_row_user_name)
    TextView mMessageRowUserName;
    @ViewId(R.id.message_row_message)
    TextView mMessageRowMessage;
    @ViewId(R.id.message_row_time)
    TextView mMessageRowTime;

    private Calendar mCalendar;
    private SimpleDateFormat mFormatter;

    public MessageViewHolder(View view) {
        super(view);

        mCalendar = Calendar.getInstance();
    }

    @Override
    public void onSetValues(Message message, PositionInfo positionInfo) {
        mMessageRowMessage.setText(message.message);
        mMessageRowUserName.setText(message.userName);
        mMessageRowTime.setText(TimeCalculateHelper.getTimeDifferenceString(message.messageTimestamp));
    }
}
