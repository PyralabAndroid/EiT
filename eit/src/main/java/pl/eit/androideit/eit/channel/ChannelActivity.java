package pl.eit.androideit.eit.channel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.helpers.ChannelsHelper;
import pl.eit.androideit.eit.service.model.Channel;
import uk.co.ribot.easyadapter.EasyAdapter;

public class ChannelActivity extends ActionBarActivity implements SlidingPaneLayout.PanelSlideListener, ChannelsListFragment.OnChannelSetListener{

    private static final int PARALLAX_SIZE = 30;

    List<Channel> mListItems;
    private CharSequence mTitle;
    private CharSequence mCurrentTitle;

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
        mPanes.setPanelSlideListener(this);
        mPanes.openPane();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.channels_title));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.channel_container, ChannelFragment.newInstance())
                .commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    public Channel getCurrentChannel() {
        return mCurrentChannel;
    }


    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelOpened(View panel) {
        getSupportActionBar().setTitle(getString(R.string.channels_title));
        getSupportFragmentManager()
                .findFragmentById(R.id.channel_container)
                .setHasOptionsMenu(false);
    }

    @Override
    public void onPanelClosed(View panel) {
        if(mCurrentChannel != null){
            getSupportActionBar().setTitle(mCurrentTitle);
            getSupportFragmentManager()
                    .findFragmentById(R.id.channel_container)
                    .setHasOptionsMenu(true);
        }
    }

    @Override
    public void onChannelSet(Channel channel) {
        mCurrentChannel = channel;
        mCurrentTitle = mCurrentChannel.channelName;

        mPanes.closePane();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.channel_container, ChannelFragment.newInstance())
                .commit();
    }

}
