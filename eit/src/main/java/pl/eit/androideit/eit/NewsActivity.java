package pl.eit.androideit.eit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.ogloszenia.ExpandableListAdapter;
import pl.eit.androideit.eit.ogloszenia.JsonFields;
import pl.eit.androideit.eit.ogloszenia.ServerAsyncTask;


public class NewsActivity extends ActionBarActivity {

    public String url;
    public ServerAsyncTask async_task_object;
    private ListView mListView;

    ExpandableListAdapter expandable_list_adapter;

    //@InjectView(R.id.exp_list_view)
    ListView exp_list_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);
        url = "http://iamorganized.cba.pl/android/data_parse.json";
        accessWebService();
        mListView = (ListView) findViewById(R.id.exp_list_view);
    }

    public void accessWebService() {
        async_task_object = new ServerAsyncTask(this);
        async_task_object.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void przypisz(ServerAsyncTask ob) {

        List<JsonFields> items = new ArrayList<JsonFields>();

        for(JsonFields field : ob.products_tab.products){
            items.add(field);
        }

        expandable_list_adapter = new ExpandableListAdapter(this, items);

        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(expandable_list_adapter);
        alphaInAnimationAdapter.setAbsListView(getListView());
        alphaInAnimationAdapter.setInitialDelayMillis(500);
        getListView().setAdapter(alphaInAnimationAdapter);
    }

    public ListView getListView() {
        return mListView;
    }
}
