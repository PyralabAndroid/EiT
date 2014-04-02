package pl.eit.androideit.eit.chanel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.GCMRegister;
import pl.eit.androideit.eit.service.model.Chanel;
import static pl.eit.androideit.eit.service.GCMRegister.PROPERTY_APP_VERSION;

public class ChannelsActivity extends FragmentActivity implements OnItemClickListener {

    ArrayList<Chanel> listItems;
    ChanelListAdapter mAdapter;
    Context context;

    @InjectView(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        ButterKnife.inject(this);
        context = ChannelsActivity.this;

        checkGCMRegId();

        mListView.setOnItemClickListener(this);

        final DB db = new DB(this);
        listItems = db.getChannels();
        mAdapter = new ChanelListAdapter(this, R.layout.channels_activity_row, listItems);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Chanel channel = listItems.get(position);
        Intent intent = new Intent(ChannelsActivity.this, SingleChannel.class);
        intent.putExtra("channelTimestamp", channel.channelTimestamp);
        intent.putExtra("channelName", channel.channelName);
        startActivity(intent);
    }

    //TODO przenieść do głównego activity
    private void checkGCMRegId(){
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
                Log.v("GCM", "Wersja aplikacji zmieniła się lub wygasła rejestracja.");
                // Rejestracja w GCM nowym regId + aktualizacja regId na serwerze.
                new GCMRegister(context, regid, true).execute(null, null, null);
            }
        }
    }
}
