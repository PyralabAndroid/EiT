package pl.eit.androideit.eit.chanel;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.eit.androideit.eit.R;

public class ChannelsActivity extends ListActivity {
	ArrayList<ChannelObject> listItems;
	CustomListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channels);
		
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setTitle("Channels");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Ładowanie subskrybowanych kanałów
		DB db = new DB(this);
		String[] subscriptions = {"0", "1", "2", "3", "4", "5"};  //TODO pobierać listę subskrybowanych kanałów z ustawień
		listItems = db.getChannels(subscriptions);
		mAdapter = new CustomListAdapter(this, R.layout.channels_activity_row, listItems);
		setListAdapter(mAdapter);
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.channels, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.channels_register_window) {
			Intent intent = new Intent(this, LogInOrSignUp.class);
			startActivity(intent);
			return true;
		} else {
			return false;
		}
	}
	
	class CustomListAdapter extends ArrayAdapter<ChannelObject>{
			
			Context context;
			ArrayList<ChannelObject> channelsArray;
			int rowLayout;
			
			CustomListAdapter(Context context, int rowLayout, ArrayList<ChannelObject> channelsArray){
				super(context, rowLayout, channelsArray);
				this.context = context;
				this.channelsArray = channelsArray;
				this.rowLayout = rowLayout;
			}
			
			private class ViewHolder{
				TextView channelName;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				View view = convertView;
				final ViewHolder holder;
				
				if(view == null){
					LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = inflater.inflate(rowLayout, null);
					
					holder = new ViewHolder();
					holder.channelName = (TextView)view.findViewById(R.id.channels_list_row_channel_name);
					
					view.setTag(holder);
				}
				else{
					holder = (ViewHolder)view.getTag();
				}
				
				final ChannelObject singleChannel = listItems.get(position);
				holder.channelName.setText(singleChannel.channelName);
				
				// Obsługa kliknięcia na kanał - ładuje wybrany kanał.
				getListView().setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						
						ChannelObject channel = listItems.get(position);
						Intent intent = new Intent(ChannelsActivity.this, SingleChannel.class);
						intent.putExtra("channelId", channel.channelId);
						intent.putExtra("channelName", channel.channelName);
						startActivity(intent);
					}
					
				});
				
				
				return view;
			}
	}
		
}
