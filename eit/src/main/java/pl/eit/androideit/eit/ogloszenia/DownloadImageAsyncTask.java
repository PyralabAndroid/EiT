package pl.eit.androideit.eit.ogloszenia;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import pl.eit.androideit.eit.NewsActivity;

/**
 * Created by Robert on 2014-04-07.
 */
public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

        ImageView bmImage;
        private ProgressDialog dialog;
        public Context news_object;

        public DownloadImageAsyncTask(ImageView bmImage, Context news_object) {
            this.bmImage = bmImage;
            this.news_object = news_object;
        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

       /* dialog = new ProgressDialog(news_object);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.show();*/
    }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
           // dialog.dismiss();
        }
}
