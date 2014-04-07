package pl.eit.androideit.eit.channel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.Channel;
import pl.eit.androideit.eit.service.model.Message;
import uk.co.ribot.easyadapter.EasyAdapter;

public class ChannelFragment extends Fragment {

    @InjectView(R.id.channel_list_view)
    ListView mChannelListView;
    @InjectView(R.id.message_edit_text)
    EditText mMessageEditText;
    @InjectView(R.id.message_send_button)
    ImageButton mMessageSendButton;

    private AppPreferences mAppPrefrences;
    private EasyAdapter<Message> mAdapter;
    private List<Message> mList;
    private long lastSync;

    private Channel mChannel;
    private String mUserName;

    public static Fragment newInstance() {
        return new ChannelFragment();
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

        mChannel = ((ChannelActivity) getActivity()).getCurrentChannel();

        DB db = new DB(getActivity());
        mList = db.getMessagesForChannel(mChannel.channelTimestamp);
        Collections.sort(mList, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return lhs.messageTimestamp < rhs.messageTimestamp ? 1 : -1;
            }
        });

        mAdapter = new EasyAdapter<Message>(getActivity().getBaseContext(),
                MessageViewHolder.class, mList);
        mChannelListView.setAdapter(mAdapter);

        refresh();
    }

    @OnClick(R.id.message_send_button)
    public void sendMessage() {
        final String message = mMessageEditText.getText().toString();
        if (Strings.isNullOrEmpty(message)) {
            return;
        } else {
            sendMessageToServer(new Message(message, mChannel.channelTimestamp,
                    System.currentTimeMillis(), "pRabel"));
            mMessageEditText.setText("");
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
                    // TODO Auto-generated catch block
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

                // Sprawdzam odpowiedz z serwera
                AlertDialogManager alert = new AlertDialogManager();
                if (response != null) {
                    if (response.equals("serverProblem")) {
//                        alert.showAlertDialog(SingleChannelActivity.this, "Błąd",
//                                "Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
                    } else {

                        //alert.showAlertDialog(SingleChannel.this, "błąd", "z: " + response, false, null);
                        int success = 0;
                        String error = "";
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            success = jsonResponse.getInt("success");
                            error = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            throw new RuntimeException(e.getMessage());
                        }

                        if (success == 1) {
                            //Dodawanie wiadomości do listy
                            mList.add(msgObj);
                            mAdapter.notifyDataSetChanged();
                            // Wstawianie wiadomości do lokalnej bazy danych
                            DB db = new DB(getActivity());
                            db.insertMessage(msgObj);
                            refresh();
                        } else {
//                            alert.showAlertDialog(SingleChannelActivity.this, "Błąd",
//                                    "Nie można wysłać wiadomości. Błąd: " + error, false, null);
                        }
                    }
                }
            }

        }.execute();
    }
}
