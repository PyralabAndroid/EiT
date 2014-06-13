package pl.eit.androideit.eit.channel;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.channel.dummy.DummyContent;
import pl.eit.androideit.eit.helpers.ChannelsHelper;
import pl.eit.androideit.eit.service.model.Channel;
import uk.co.ribot.easyadapter.EasyAdapter;

public class ChannelsListFragment extends Fragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.channels_list_list)
    ListView mListView;

    private EasyAdapter<Channel> mAdapter;
    private OnChannelSetListener mOnChannelSetListener;
    List<Channel> mChannelsList;

    public ChannelsListFragment() {
    }

    public interface OnChannelSetListener {
        public void onChannelSet(Channel channel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.channels_list_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mChannelsList = ChannelsHelper.getChannels(getActivity().getBaseContext());
        mAdapter = new EasyAdapter<Channel>(getActivity(), ChannelViewHolder.class, mChannelsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mOnChannelSetListener = (OnChannelSetListener)getActivity();
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnChannelSetListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mOnChannelSetListener.onChannelSet(mChannelsList.get(position));
    }


}
