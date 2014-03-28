package pl.eit.androideit.eit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import pl.eit.androideit.eit.chanel.SingleChannel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.chanel.ChanelListAdapter;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.model.Chanel;

public class ChannelsActivity extends FragmentActivity implements OnItemClickListener {

    ArrayList<Chanel> listItems;
    ChanelListAdapter mAdapter;

    @InjectView(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        ButterKnife.inject(this);

        final DB db = new DB(this);
        String[] subscriptions = {"0", "1", "2", "3", "4", "5"};  //TODO pobierać listę subskrybowanych kanałów z ustawień
        listItems = db.getChannels(subscriptions);
        mAdapter = new ChanelListAdapter(this, R.layout.channels_activity_row, listItems);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Chanel channel = listItems.get(position);
        Intent intent = new Intent(ChannelsActivity.this, SingleChannel.class);
        intent.putExtra("channelTimestamp", channel.channelTimestamp);
        intent.putExtra("channelName", channel.channelName);
        startActivity(intent);
    }
}
