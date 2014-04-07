package pl.eit.androideit.eit.channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.model.Message;

public class SingleChannelActivity extends ActionBarActivity {

	String channelName;
	long channelTimestamp;
    /** Elementy listy **/
	ArrayList<Message> listItems;
	CustomListAdapter mAdapter;
	/** Nazwa usera do wyswietlania obok tekstu wiadomosci **/
	String userName;
    /** Czas ostatniego pobierania wiadomości dla kanału **/
    long lastSync;
	EditText messageET;

    private AppPreferences mAppPrefrences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel);

		mAppPrefrences = new AppPreferences(this);

		Intent intent = getIntent();
		channelName = intent.getStringExtra("channelName");
		channelTimestamp = intent.getLongExtra("channelTimestamp", -1);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(channelName);
        ab.setDisplayShowHomeEnabled(false);

		userName = mAppPrefrences.getUserName();

        fetchMessagesFromLocalDb();
	}
	

	// Pobieram wiadomości dla wybranego kanału i łąduję je do listy
	private void fetchMessagesFromLocalDb(){
		DB db = new DB(this);
		listItems = db.getMessagesForChannel(channelTimestamp);
        Log.d("timestamp", "z: " +String.valueOf(channelTimestamp));
        mAdapter = new CustomListAdapter(this, R.layout.channel_row, listItems);
        ListView lV = (ListView)findViewById(android.R.id.list);
        lV.setAdapter(mAdapter);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.single_channel_refresh:
//                getMessagesForChannel();
        }
        return super.onOptionsItemSelected(item);
    }


	
	class CustomListAdapter extends ArrayAdapter<Message>{
		
		Context context;
		ArrayList<Message> items;
		int rowLayout;
		
		CustomListAdapter(Context context, int rowLayout, ArrayList<Message> items){
			super(context, rowLayout, items);
			this.context = context;
			this.items = items;
			this.rowLayout = rowLayout;
		}
		
		private class ViewHolder{
			TextView message;
			TextView date;
			TextView userName;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View view = convertView;
			final ViewHolder holder;
			
			if(view == null){
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(rowLayout, null);
				
				holder = new ViewHolder();
				holder.message = (TextView)view.findViewById(R.id.channel_row_message);
				holder.date = (TextView)view.findViewById(R.id.channel_row_date);
				holder.userName = (TextView)view.findViewById(R.id.channel_row_user);
				
				view.setTag(holder);
			}
			else{
				holder = (ViewHolder)view.getTag();
			}
			
			Message rowItem = items.get(position);
			holder.message.setText(rowItem.message);
			holder.userName.setText(rowItem.userName);
			
			long date = rowItem.messageTimestamp;
			if(date > 0){
				String messageDate = DateFormat.format("dd MMM hh:mm:ss", date).toString();
				holder.date.setText(messageDate);
			}
			
			return view;
		}
		
	}

}
