package pl.eit.androideit.eit.channel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.eit.androideit.eit.AlertDialogManager;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.async_task.ToogleSubscriptonAsyncTask;
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.Channel;
import pl.eit.androideit.eit.service.model.Message;
import uk.co.ribot.easyadapter.EasyAdapter;

public class ChannelFragment extends Fragment implements ToogleSubscriptonAsyncTask.onSubscriptionFinishedListener {

    @InjectView(R.id.channel_list_view)
    ListView mChannelListView;
    @InjectView(R.id.message_edit_text)
    EditText mMessageEditText;
    @InjectView(R.id.message_send_button)
    ImageButton mMessageSendButton;
    @InjectView(R.id.not_selected_channel_tv)
    TextView notSelectedChannelTV;

    private AppPreferences mAppPrefrences;
    private EasyAdapter<Message> mAdapter;
    private List<Message> mList;
    private long lastSync;
    ProgressDialog pDialog;

    private Channel mChannel;
    private String mUserName;

    public static Fragment newInstance() {
        return new ChannelFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.channel_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_refresh_messages :
                refresh();
                return true;
            case R.id.menu_subscribe_channel :
                subscribeChannel();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.single_channel_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mAppPrefrences = new AppPreferences(getActivity());
        mUserName = mAppPrefrences.getUserName();

        mChannel = ((ChannelActivity)getActivity()).getCurrentChannel();
        if(mChannel != null){
            notSelectedChannelTV.setVisibility(View.GONE);

            DB db = new DB(getActivity());
            mList = db.getMessagesForChannel(mChannel.channelTimestamp);
/*            Collections.sort(mList, new Comparator<Message>() {
                @Override
                public int compare(Message lhs, Message rhs) {
                    return lhs.messageTimestamp < rhs.messageTimestamp ? 1 : -1;
                }
            });*/


            mAdapter = new EasyAdapter<Message>(getActivity().getBaseContext(),
                    MessageViewHolder.class, mList);
            mChannelListView.setAdapter(mAdapter);

            if(ServerConnection.isOnline(getActivity())){
                refresh();
            }

        }
        else{
            notSelectedChannelTV.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.message_send_button)
    public void sendMessage() {
        if(!mAppPrefrences.isLoggedIn()){
            Toast.makeText(getActivity(),
                    "Musisz być zalogowany, aby wysłać wiadomość",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(ServerConnection.isOnline(getActivity().getBaseContext())){
            final String message = mMessageEditText.getText().toString();
            if (Strings.isNullOrEmpty(message)) {
                return;
            } else {
                sendMessageToServer(new Message(message, mChannel.channelTimestamp,
                        System.currentTimeMillis(), mUserName));
                mMessageEditText.setText("");
            }
        }
        else{
            Toast.makeText(getActivity().getBaseContext(),
                    "Brak połączenia z Internetem",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void refresh() {
        getMessagesForChannel();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Pobieranie wiadomości dla kanału *
     */
    public void getMessagesForChannel() {
        boolean online = ServerConnection.isOnline(getActivity());
        if (online) {

            final ProgressDialog pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Odświeżanie listy wiadomości...");
            pDialog.setCancelable(true);
            pDialog.show();
            // Pobiera czas ostatniej synchronizacji wiadomości
            DB db = new DB(getActivity());
            lastSync = db.getLastChannelSync(mChannel.channelTimestamp);
            // JSON żądania POST
            final JSONObject json = new JSONObject();
            try {
                json.put("channelTimestamp", mChannel.channelTimestamp);
                json.put("lastSync", lastSync);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Pobieranie wiadomości z serwera
            new AsyncTask<Void, Void, String>() {

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

                    if (pDialog != null) {
                        pDialog.dismiss();
                    }

                    AlertDialogManager alert = new AlertDialogManager();
                    if (response != null) {
                        if (response.equals("serverProblem")) {
//                            alert.showAlertDialog(SingleChannelActivity.this, "Błąd",
//                                    "Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
                        } else {
                            //alert.showAlertDialog(SingleChannel.this, "błąd", "z: " + response, false, null);
                            int success = 0;
                            String error = "";

                            JSONObject jsonResponse;
                            try {
                                jsonResponse = new JSONObject(response);
                                success = jsonResponse.getInt("success");
                                error = jsonResponse.getString("error");

                                if (success == 1) {
                                    JSONArray data = jsonResponse.getJSONArray("data");
                                    if (data.length() > 0) {
                                        DB db = new DB(getActivity());
                                        ArrayList<Message> newMessages = db.saveMessagesFromServer(data);
                                        //listItems.addAll(0, newMessages);
                                        //mAdapter.notifyDataSetChanged();

                                        mList.clear();
                                        ArrayList<Message> list2 = db.getMessagesForChannel(mChannel.channelTimestamp);
                                        mList.addAll(list2);
                                        mAdapter.notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(getActivity(), "Lista wiadomości jest aktualna",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
//                                    alert.showAlertDialog(SingleChannelActivity.this, "Błąd",
//                                            "Nie można wysłać wiadomości. Błąd: " + error, false, null);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.execute();
        } else {
            Toast.makeText(getActivity(), "Brak połączenia z Internetem", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Wysyła wiadomość do serwera, a po poprawnej odp. zapisuje ją w lokalnej bazie danych*
     */
    public void sendMessageToServer(final Message msgObj) {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
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
                    json.put("userName", msgObj.userName); // TODO nazwa usera ma byc
                    json.put("channelTimestamp", msgObj.channelTimestamp);
                    json.put("channelName", mChannel.channelName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Zapytanie do serwera
                ServerConnection server = new ServerConnection();
                String response = null;
                try {
                    response = server.post(ServerConnection.SERVER_SEND_MESSAGE, json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                if (pDialog != null && !getActivity().isFinishing()) {
                    pDialog.dismiss();
                }

                // Sprawdzam odpowiedz z serwera
                AlertDialogManager alert = new AlertDialogManager();
                if (response != null) {
                    if (response.equals("serverProblem")) {
                        alert.showAlertDialog(getActivity(), "Błąd",
                                "Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
                    }
                    else {

                        //alert.showAlertDialog(SingleChannel.this, "błąd", "z: " + response, false, null);
                        int success = 0;
                        String error = "";
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            success = jsonResponse.getInt("success");
                            error = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            alert.showAlertDialog(getActivity(), "Błąd",
                                    "Nieznay błąd podczas prasowania: " + response, false, null);
                            return;
                        }

                        if (success == 1) {
                            //Dodawanie wiadomości do listy
                            mList.add(msgObj);
                            mAdapter.notifyDataSetChanged();
                            // Wstawianie wiadomości do lokalnej bazy danych
                            DB db = new DB(getActivity());
                            db.insertMessage(msgObj);
                           // refresh();
                        }
                        else {
                            alert.showAlertDialog(getActivity(), "Błąd",
                                    "Nie można wysłać wiadomości. Błąd: " + error, false, null);
                        }
                    }
                }
            }

        }.execute();
    }

    private void subscribeChannel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(mChannel.isSub == 0){
            builder.setMessage(getString(R.string.subscribe_info));
        }
        else{
            builder.setMessage(getString(R.string.unsubscribe_info));
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Trwa subskrybowanie kanału...");
                pDialog.show();
                new ToogleSubscriptonAsyncTask(getActivity(), mChannel, ChannelFragment.this).execute();
            }
        })
        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
        .show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.menu_subscribe_channel);
        if(mChannel.isSub == 1){
            menuItem.setIcon(getActivity().getResources().getDrawable(R.drawable.btn_radio_on_pressed));
        }
        else{
            menuItem.setIcon(getActivity().getResources().getDrawable(R.drawable.btn_radio_off_pressed));
        }

    }

    @Override
    public void onSubscriptionFinished(int subscription) {
        if(pDialog != null){
            pDialog.dismiss();
        }
        mChannel.isSub = subscription;
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }
}
