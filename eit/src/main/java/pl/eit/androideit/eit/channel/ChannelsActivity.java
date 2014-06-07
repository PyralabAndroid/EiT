package pl.eit.androideit.eit.channel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.helpers.ChannelsHelper;
import pl.eit.androideit.eit.service.model.Channel;

public class ChannelsActivity extends ActionBarActivity {

    List<Channel> mListItems;

    @InjectView(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.channels_title));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListItems = ChannelsHelper.getChannels(this);
        mListView.setAdapter(new ChanelListAdapter(this, mListItems));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Channel channel = mListItems.get(position);
        Intent intent = new Intent(this, SingleChannelActivity.class);
        intent.putExtra("channelTimestamp", channel.channelTimestamp);
        intent.putExtra("channelName", channel.channelName);
        startActivity(intent);
    }


}
