package pl.eit.androideit.eit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

import java.util.ArrayList;
import java.util.logging.Handler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.ogloszenia.ExpandableListAdapter;
import pl.eit.androideit.eit.ogloszenia.GetRSSAsyncTask;
import pl.eit.androideit.eit.ogloszenia.Item;
import pl.eit.androideit.eit.ogloszenia.OnAsynTaskSucessListener;


public class NewsActivity extends ActionBarActivity implements OnAsynTaskSucessListener{

    public String url;
    public GetRSSAsyncTask mGetRSSAsyncTask;
    public ExpandableListAdapter expandable_list_adapter;

    @InjectView(R.id.exp_list_view)
    ListView mListView;

    private ArrayList<Item> mItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);

        isOnline();

        //mListView = (ListView) findViewById(R.id.exp_list_view);

    }

    public void accessWebService() {

        url = "http://et.put.poznan.pl/index.php/pl/ogoszenia?format=feed&type=rss";
        mGetRSSAsyncTask = new GetRSSAsyncTask(url, this);
        mGetRSSAsyncTask.setOnSuccessListener(this);
        mGetRSSAsyncTask.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAnimation() {
        //expandable_list_adapter = new ExpandableListAdapter(this, mNews);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(expandable_list_adapter);
        alphaInAnimationAdapter.setAbsListView(getListView());
        alphaInAnimationAdapter.setInitialDelayMillis(500);
        getListView().setAdapter(alphaInAnimationAdapter);
    }

     public ListView getListView() {
        return mListView;
    }

    @Override
    public void onSuccess(ArrayList<Item> item_list) {
        mItemArrayList = item_list;

        expandable_list_adapter = new ExpandableListAdapter(this, mItemArrayList);
        setAnimation();
        mListView.setAdapter(expandable_list_adapter);
        //expandable_list_adapter.notifyDataSetInvalidated();
    }

    public void isOnline() {
        Boolean online;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            online = true;
        }
        else online = false;

        if(!online){
            Toast.makeText(getApplicationContext(), "Konieczne jest połączenie z siecią w celu pobrania ogłoszeń.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }
        else accessWebService();
    }
}
