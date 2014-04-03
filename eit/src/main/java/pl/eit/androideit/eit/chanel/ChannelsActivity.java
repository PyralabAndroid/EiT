package pl.eit.androideit.eit.chanel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.model.Chanel;


public class ChannelsActivity extends ActionBarActivity {

    ArrayList<Chanel> mListItems;

    @InjectView(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        ButterKnife.inject(this);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Twoje rozmowy");
        ab.setDisplayShowHomeEnabled(false);

        final DB db = new DB(this);
        mListItems = db.getChannels();
        mListView.setAdapter(new ChanelListAdapter(this, R.layout.channels_activity_row, mListItems));
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Chanel channel = mListItems.get(position);
        Intent intent = new Intent(this, SingleChannel.class);
        intent.putExtra("channelTimestamp", channel.channelTimestamp);
        intent.putExtra("channelName", channel.channelName);
        startActivity(intent);
    }

}
