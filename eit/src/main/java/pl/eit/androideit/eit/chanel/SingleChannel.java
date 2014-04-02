package pl.eit.androideit.eit.chanel;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pl.eit.androideit.eit.AlertDialogManager;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.content.SharedPrefs;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.GCMRegister;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.Message;

public class SingleChannel extends ListActivity {
	Context context;
	static String TAG = "GCM";
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
    public static final String PROPERTY_APP_VERSION = "appVersion";

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

        if(isOnline()){
            getMessagesForChannel();
        }
        else{
            fetchMessagesFromLocalDb();
        }
				

	}

	


    /** Pobiera wiadomości z lokalnej bazy danych i ustawia adapter **/
    public void fetchMessagesFromLocalDb(){
        DB db = new DB(this);
        if(listItems != null) {
            listItems.clear();
        }
        listItems = db.getMessagesForChannel(channelTimestamp);
        mAdapter = new CustomListAdapter(this, R.layout.channel_row, listItems);
        setListAdapter(mAdapter);
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
                getMessagesForChannel();
        }
        return super.onOptionsItemSelected(item);
    }

    /** Pobieranie wiadomości z serwera dla kanału **/
    public void getMessagesForChannel(){
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Odświeżanie listy wiadomości...");
        pDialog.setCancelable(true);
        pDialog.show();
        DB db = new DB(context);
        lastSync = db.getLastChannelSync(channelTimestamp);

        final JSONObject json = new JSONObject();
        try {
            json.put("channelTimestamp", channelTimestamp);
            json.put("lastSync", lastSync);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... voids) {
                ServerConnection server = new ServerConnection();
                String response = null;
                try {
                    response = server.post(
                            ServerConnection.SERVER_GET_MESSAGES,
                            json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                if(pDialog != null){
                    pDialog.dismiss();
                }

                AlertDialogManager alert = new AlertDialogManager();
                if(response != null){
                    if(response.equals("serverProblem")){
                        alert.showAlertDialog(SingleChannel.this, "Błąd",
                                "Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
                    }
                    else{
                        //alert.showAlertDialog(SingleChannel.this, "błąd", "z: " + response, false, null);
                        int success = 0;
                        String error = "";

                        JSONObject jsonResponse;
                        try {
                            jsonResponse = new JSONObject(response);
                            success = jsonResponse.getInt("success");
                            error = jsonResponse.getString("error");

                            // Jeśli udało się pobrać wiadomości z serwera
                            // zapisz je w bazie danych i wyświetl wszystkie
                            // wiadomości w liście.
                            if(success == 1){
                                JSONArray data = jsonResponse.getJSONArray("data");
                                if(data.length() > 0) {
                                    DB db = new DB(context);
                                    ArrayList<Message> newMessages = db.saveMessagesFromServer(data);
                                    //listItems.addAll(0, newMessages);
                                    //mAdapter.notifyDataSetChanged();
                                    fetchMessagesFromLocalDb();
                                }
/*                                else{
                                    Toast.makeText(context, "Lista wiadomości jest aktualna",
                                            Toast.LENGTH_SHORT).show();
                                }*/
                            }
                            else{
                                alert.showAlertDialog(SingleChannel.this, "Błąd",
                                        "Nie można wysłać wiadomości. Błąd: " + error, false, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.execute();
    }

    /** Wysyłanie wiadomości **/
	public void sendMessage(View view){
        // Tylko zalogowany user może wysyłać wiadomości
        if(userName != null && userName.length() > 0){
            boolean online = isOnline();
            // Czy jest połączenie z Internetem ?
            if(online){
                // Tekst wiadomości
                String message = messageET.getText().toString();
                messageET.setText("");
                // Timestamp wiadomości
                long messageTimestamp = System.currentTimeMillis();
                // Obiekt wiadomości
                final Message msgObj = new Message(message, channelTimestamp, messageTimestamp, userName);
                // Wysyłanie wiadomości na serwer
                sendMessageToServer(msgObj);
            }
            else{
                Toast.makeText(context, "Brak połączenia z Internetem", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(context, "Musisz być zalogowany w celu wysłania wiadomości",
                    Toast.LENGTH_LONG).show();
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
	
	/** Wysyła wiadomość do serwera, a po poprawnej odp. zapisuje ją w lokalnej bazie danych**/
	public void sendMessageToServer(final Message msgObj){
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Wysyłanie wiadomości...");
        pDialog.setCancelable(true);
        pDialog.show();
        // Asynchroniczne zapytanie do serwera
         new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                // JSON wiadomości wysyłany na serwer
                JSONObject json = new JSONObject();
                try {
                    json.put("message", msgObj.message);
                    json.put("messageTimestamp", msgObj.messageTimestamp);
                    json.put("userName", userName); // TODO nazwa usera ma byc
                    json.put("channelTimestamp", msgObj.channelTimestamp);
                    json.put("channelName", channelName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Zapytanie do serwera
                ServerConnection server = new ServerConnection();
                String response = null;
                try {
                    response = server.post(ServerConnection.SERVER_SEND_MESSAGE, json.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return response;
            }

             @Override
             protected void onPostExecute(String response) {
                 super.onPostExecute(response);

                 if(pDialog != null){
                     pDialog.dismiss();
                 }

                 // Sprawdzam odpowiedz z serwera
                 AlertDialogManager alert = new AlertDialogManager();
                 if(response != null){
                     if(response.equals("serverProblem")){
                         alert.showAlertDialog(SingleChannel.this, "Błąd",
                                 "Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
                     }
                     else{

                         //alert.showAlertDialog(SingleChannel.this, "błąd", "z: " + response, false, null);
                         int success = 0;
                         String error = "";
                         try {
                             JSONObject jsonResponse = new JSONObject(response);
                             success = jsonResponse.getInt("success");
                             error = jsonResponse.getString("error");
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                         if(success == 1){
                             //Dodawanie wiadomości do listy
                             listItems.add(msgObj);
                             mAdapter.notifyDataSetChanged();
                             // Wstawianie wiadomości do lokalnej bazy danych
                             DB db = new DB(context);
                             db.insertMessage(msgObj);
                         }
                         else{
                             alert.showAlertDialog(SingleChannel.this, "Błąd",
                                     "Nie można wysłać wiadomości. Błąd: " + error, false, null);
                         }
                     }
                 }
             }

         }.execute();
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
