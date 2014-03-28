package pl.eit.androideit.eit.chanel;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.content.SharedPrefs;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.Message;

public class SingleChannel extends ListActivity {
	Context context;
	static String TAG = "GCM";
	String channelName;
	long channelTimestamp;
	ArrayList<Message> listItems;
	CustomListAdapter mAdapter;
	/** Nazwa usera do wyswietlania obok tekstu wiadomosci **/
	String userName;
	EditText messageET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel);
		context = SingleChannel.this;
		
		Intent intent = getIntent();
		channelName = intent.getStringExtra("channelName");
		channelTimestamp = intent.getLongExtra("channelTimestamp", -1);
		
		// Pobiera nazwe usera
		SharedPrefs sp = new SharedPrefs(this);
		userName = sp.getUserName();
		
		// focusuje edittext
		messageET = (EditText)findViewById(R.id.channel_edittext);
		messageET.requestFocus();
				
		/*checkGCMRegId();*/
	}
	
	
	@Override
	// Pobieram wiadomości dla wybranego kanału i łąduję je do listy
	protected void onResume() {
		super.onResume();
		DB db = new DB(this);
		listItems = db.getMessagesForChannel(channelTimestamp);
        Log.d("timestamp", "z: " +String.valueOf(channelTimestamp));
        mAdapter = new CustomListAdapter(this, R.layout.channel_row, listItems);
		setListAdapter(mAdapter);
	}
	
	//TODO przenieść do głównego activity
/*	private void checkGCMRegId(){
		GCMRegister gcmReg = new GCMRegister();
		// Pobieram registration id z SharedPreferences
        String regid = gcmReg.getSavedGCMRegId(context);
        Log.d("reg id z preferencji", " " + regid);
		
                
		// Jeśli istnieje regId sprawdzam czy jest nadal ważne.
		if(regid != null && regid.length() > 0){
			SharedPreferences prefs = gcmReg.getGCMPreferences(this);
			// Sprawdzam czy wersja aplikacji nie zmieniłą się. Jeśli tak to konieczna jest aktualizacja regId.
			int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
			int currentVersion = gcmReg.getAppVersion(context);
			if (registeredVersion != currentVersion || gcmReg.isRegistrationExpired(this)) {
				Log.v(TAG, "Wersja aplikacji zmieniła się lub wygasła rejestracja.");
				// Rejestracja w GCM nowym regId + aktualizacja regId na serwerze.
				new GCMRegister(context, regid, true).execute(null, null, null);
			}
		}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Wysyłanie wiadomości **/
	public void sendMessage(View view){
		boolean online = isOnline();
		if(online){
			// Tekst wiadomości
			String message = messageET.getText().toString();
			messageET.setText("");
			// Timestamp wiadomości
			long messageTimestamp = System.currentTimeMillis();
			// Obiekt wiadomości
			Message msgObj = new Message(message, channelTimestamp, messageTimestamp, userName);
			// Wysyłanie wiadomości na serwer
			//sendMessageToServer(msgObj);
			//Dodawanie wiadomości do listy
			listItems.add(msgObj);
			mAdapter.notifyDataSetChanged();
			// Wstawianie wiadomości do lokalnej bazy danyc
			DB db = new DB(this);
			db.insertMessage(msgObj);	
		}
		else{
			Toast.makeText(context, "Brak połączenia z Internetem", Toast.LENGTH_SHORT).show();
		}
			
	}
	
	/** Sprawdza czy istnieje polaczenie z Internetem **/
	private boolean isOnline(){
		ConnectivityManager cManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cManager != null){
			NetworkInfo[] info = cManager.getAllNetworkInfo();
			if(info != null){
				for(NetworkInfo element : info){
					if(element.getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/** Wysyła wiadomość do serwera **/
	public void sendMessageToServer(Message msgObj){
		JSONObject json = new JSONObject();
		try {
			json.put("message", msgObj.message);
			json.put("messageTimestamp", msgObj.messageTimestamp);
			json.put("userName", msgObj.userName);
			json.put("channelTimestamp", msgObj.channelTimestamp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ServerConnection server = new ServerConnection();
		String response;
		try {
			response = server.post(ServerConnection.SERVER_SEND_MESSAGE, json.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
