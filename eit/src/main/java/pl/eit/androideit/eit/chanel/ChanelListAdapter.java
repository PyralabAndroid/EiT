package pl.eit.androideit.eit.chanel;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pl.eit.androideit.eit.AlertDialogManager;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.Chanel;

public class ChanelListAdapter extends ArrayAdapter<Chanel> {

    Context mContext;
    ArrayList<Chanel> mChanelArray;
    int mRowLayout;
    private AppPreferences mAppPrefrences;

    public ChanelListAdapter(Context context, int rowLayout, ArrayList<Chanel> channelsArray) {
        super(context, rowLayout, channelsArray);
        mContext = context;
        mChanelArray = channelsArray;
        mRowLayout = rowLayout;
        mAppPrefrences = new AppPreferences(context);
    }

    private class ViewHolder {
        TextView channelName;
        Button subBtn;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mRowLayout, null);

            holder = new ViewHolder();
            holder.channelName = (TextView) view.findViewById(R.id.channels_list_row_channel_name);
            holder.subBtn = (Button) view.findViewById(R.id.channels_list_row_subBtn);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Chanel singleChannel = mChanelArray.get(position);
        holder.channelName.setText(singleChannel.channelName);

        // Jeśli jest subowany ustaw tło zielone
        if (singleChannel.isSub == 1) {
            holder.subBtn.setBackgroundColor(Color.parseColor("#2aea4a"));
            holder.subBtn.refreshDrawableState();
        }
        // Jeśli nie jest subowany ustaw odpowiednie tło
        else if (singleChannel.isSub == 0) {
            holder.subBtn.setBackgroundResource(android.R.drawable.btn_default);
            holder.subBtn.refreshDrawableState();
        }

        holder.subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView list = (ListView) parent;
                // Pobiera pozycję na liście dla wybranego elementu
                final int position = list.getPositionForView((LinearLayout) view.getParent());

                Chanel channel = mChanelArray.get(position);
                ProgressDialog pDialog = new ProgressDialog(mContext);
                //pDialog.setTitle("Trwa subskrybowanie...");
                // pDialog.show();
                toggleSubscription(channel, view, pDialog);

            }


        });
        return view;
    }


    private String toggleSubscription(final Chanel channel, final View view, final ProgressDialog pDialog) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                ServerConnection server = new ServerConnection();
                final String userName = mAppPrefrences.getUserName();
                JSONObject json = new JSONObject();
                String result = null;

                try {
                    json.put("channelTimestamp", channel.channelTimestamp);
                    json.put("isSub", channel.isSub);
                    json.put("userName", userName);
                    result = server.post(ServerConnection.SERVER_SET_SUB, json.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                AlertDialogManager alert = new AlertDialogManager();
                //if(pDialog != null){
                //   pDialog.dismiss();
                //}

                if (result.equals("serverProblem")) {
                    alert.showAlertDialog(mContext, "Błąd połączenia",
                            "Nie można połączyć się z serwerem",
                            false, null);
                } else {
                    JSONObject response;
                    int success = 0;
                    String error = "";
                    try {
                        response = new JSONObject(result);
                        success = response.getInt("success");
                        error = response.getString("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Jeśli subskrybowanie po stronie serwera powiodło się,
                    // zapisz subskrybcje w lokalnej bazie
                    if (success == 1) {
                        DB db = new DB(mContext);

                        int isSubbed = channel.isSub;
                        db.toggleChannelSub(channel.channelTimestamp, isSubbed);
                        // Jeśli kanał ma być subowany, zmień kolor na zielony
                        if (isSubbed == 0) {
                            channel.isSub = 1;
                            view.setBackgroundColor(Color.parseColor("#2aea4a"));
                            view.refreshDrawableState();
                        } else if (isSubbed == 1) {
                            channel.isSub = 0;
                            view.setBackgroundResource(android.R.drawable.btn_default);
                            view.refreshDrawableState();
                        }
                    } else {
                        alert.showAlertDialog(mContext, "Błąd",
                                "Nie można subskrybuować kanału. Spróbuj ponownie. Błąd: " + error,
                                false, null);
                    }
                }
            }
        }.execute();

        return null;
    }
}

