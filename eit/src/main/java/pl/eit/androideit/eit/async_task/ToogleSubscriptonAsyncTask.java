package pl.eit.androideit.eit.async_task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.content.AppConst;
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.Channel;

public class ToogleSubscriptonAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String SERVER_PROBLEM = "serverProblem";

    private Context mContext;
    private Channel mChannel;
    private AppPreferences mAppPreferences;
    private onSubscriptionFinishedListener mDelegate;

    public interface onSubscriptionFinishedListener{
        void onSubscriptionFinished(int subscription);
    }

    public ToogleSubscriptonAsyncTask(Context context, Channel channel, onSubscriptionFinishedListener delegate) {
        mDelegate = delegate;
        mContext = context;
        mChannel = channel;
        mAppPreferences = new AppPreferences(context);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = null;
        try {
            ServerConnection server = new ServerConnection();
            JSONObject json = new JSONObject();
            json.put(AppConst.CHANNEL_TIME_STAMP, mChannel.channelTimestamp);
            json.put(AppConst.CHANNEL_IS_SUBSCRIBED, mChannel.isSub);
            json.put(AppConst.CHANNEL_USER_NAME, mAppPreferences.getUserName());
            result = server.post(ServerConnection.SERVER_SET_SUB, json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result.equals(SERVER_PROBLEM)) {
            Toast.makeText(mContext, mContext.getString(R.string.channel_server_error),
                    Toast.LENGTH_LONG).show();
        } else {
            JSONObject response;
            int success;
            try {
                response = new JSONObject(result);
                success = response.getInt("success");
            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }
            if (success == 1) {
                DB db = new DB(mContext);
                int isSubbed = mChannel.isSub;
                db.toggleChannelSub(mChannel.channelTimestamp, isSubbed);
                mDelegate.onSubscriptionFinished(isSubbed == 0 ? 1 : 0);
            } else {
                Toast.makeText(mContext, R.string.channel_subscribing_error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
