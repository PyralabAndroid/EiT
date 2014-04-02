package pl.eit.androideit.eit;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.eit.androideit.eit.ogloszenia.AdapterListView;
import pl.eit.androideit.eit.ogloszenia.GsonArr;
import pl.eit.androideit.eit.ogloszenia.JsonFields;
import pl.eit.androideit.eit.ogloszenia.ServerAsyncTask;


public class NewsActivity extends ActionBarActivity {

    public String url;
    public ServerAsyncTask obiekt;
    //@InjectView(R.id.tester)
    TextView tester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //ButterKnife.inject(this);
        url = "http://iamorganized.cba.pl/android/data.json";
        //new ServerAsyncTask().execute(url);
        accessWebService();

    }

    public void accessWebService() {
        obiekt = new ServerAsyncTask(this);
        obiekt.execute(url);

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

        ArrayList<JsonFields> items = new ArrayList<JsonFields>();

        int i=0;
        for(JsonFields x : ob.products_tab.products)
        {
            items.add(x);
        }
        AdapterListView adapter = new AdapterListView(this, items);


        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}
