package pl.eit.androideit.eit.ogloszenia;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Robert on 2014-04-08.
 */
public class GetRSSAsyncTask extends AsyncTask<String, String, String> {

    private String mUrl;
    public XMLParseRSS mXMLPullParseObject;
    private InputStream mInputStream;
    public String mStream;
    public Context mContext;
    private OnAsynTaskSucessListener mListener;
    private ProgressDialog mDialog;


    public void setOnSuccessListener(OnAsynTaskSucessListener listsner) {
        mListener = listsner;
    }
    public GetRSSAsyncTask(String url, Context context ) {
        super();
        this.mUrl = url;
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL   url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10 * 1000);
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();


            int status = connection.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
            else {
                Log.d("Post successfull", "code: " + status);
                mInputStream = connection.getInputStream();
                mStream = getStringFromInputStream(mInputStream);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
         catch (IOException e) {
             throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mDialog.setProgress(Integer.parseInt(values[0]));
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Loading...");
            mDialog.setIndeterminate(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setCancelable(true);
            mDialog.show();
            mDialog.setMax(100);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDialog.dismiss();
        mXMLPullParseObject = new XMLParseRSS();
        try {
            ArrayList<Item> mItemArrayList = mXMLPullParseObject.parse(mStream);
            mListener.onSuccess(mItemArrayList);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException
    {
        byte[] bytes = new byte[1000];
        StringBuilder sb = new StringBuilder();
        int numRead = 0;
        while ((numRead = is.read(bytes)) >= 0){
            sb.append(new String(bytes, 0, numRead));
        }
        return sb.toString();
    }
}
