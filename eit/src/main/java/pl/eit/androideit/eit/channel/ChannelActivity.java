package pl.eit.androideit.eit.channel;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.helpers.ChannelsHelper;
import pl.eit.androideit.eit.service.model.Channel;
import uk.co.ribot.easyadapter.EasyAdapter;

public class ChannelActivity extends ActionBarActivity {

    private static final int PARALLAX_SIZE = 30;

    List<Channel> mListItems;
    private CharSequence mTitle;
    private CharSequence mCurrentTitle;

    @InjectView(R.id.channel_list)
    ListView mListView;
    @InjectView(R.id.sliding_pane)
    SlidingPaneLayout mPanes;

    private Channel mCurrentChannel;
    private EasyAdapter<Channel> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_sliding_panel);
        ButterKnife.inject(this);

        mTitle = mCurrentTitle = getTitle();

        mPanes.setParallaxDistance(PARALLAX_SIZE);
        mPanes.setShadowResource(R.drawable.sliding_pane_shadow);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.channels_title));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListItems = ChannelsHelper.getChannels(this);
        mAdapter = new EasyAdapter<Channel>(this, ChannelViewHolder.class, mListItems);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mPanes.isOpen()) {
                    closePane();
                } else {
                    openPane();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPane() {
        mPanes.openPane();
        getSupportActionBar().setTitle(mTitle);
    }

    private void closePane() {
        mPanes.closePane();
        getSupportActionBar().setTitle(mCurrentTitle);
    }

    public Channel getCurrentChannel() {
        return mCurrentChannel;
    }

    @OnItemClick(R.id.channel_list)
    public void selectItem(int position) {
        mCurrentChannel = mListItems.get(position);
        mCurrentTitle = mCurrentChannel.channelName;

        closePane();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.channel_container, ChannelFragment.newInstance())
                .commit();
    }

}
