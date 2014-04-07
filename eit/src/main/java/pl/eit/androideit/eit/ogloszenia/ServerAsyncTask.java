package pl.eit.androideit.eit.ogloszenia;

/**
 * Created by Robert on 2014-04-01.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStream;

import pl.eit.androideit.eit.NewsActivity;


public class ServerAsyncTask extends AsyncTask<String, Void, String> {


    public NewsActivity news_obiekt;
    public Gson gson;
    public GsonArr products_tab;
    private ProgressDialog dialog;


    public ServerAsyncTask(NewsActivity obiekt)
    {
        this.news_obiekt = obiekt;
        this.gson = new GsonBuilder().create();
    }

    @Override
    protected String doInBackground(String... params) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(params[0]);
        try {

            HttpResponse getResponse = client.execute(getRequest);

            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity getResponseEntity = getResponse.getEntity();
            return getStringFromInputStream(getResponseEntity.getContent());
        } catch (IOException e) {
            getRequest.abort();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(news_obiekt);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onPostExecute(String s)
    {
        super.onPostExecute(s);
        dialog.dismiss();
        if (s!= null){
            products_tab = gson.fromJson(s, GsonArr.class);
            news_obiekt.przypisz(this);
        }
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException 
    {
        byte[] bytes = new byte[1000];

        StringBuilder sb = new StringBuilder();

        int numRead = 0;
        
        while ((numRead = is.read(bytes)) >= 0)
        {
            sb.append(new String(bytes, 0, numRead));
        }

        return sb.toString();
    }
}
