package pl.eit.androideit.eit.ogloszenia;

import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Robert on 2014-04-02.
 */
public class Przetwarzanie {
String jsontext;

public Przetwarzanie(String s)
{
    this.jsontext = s;
    this.przetwarzanie();

}

    public void przetwarzanie()
    {

        Gson gson = new GsonBuilder().create();

        GsonArr products_tab = gson.fromJson(this.jsontext, GsonArr.class);

        String x = products_tab.products[1].appendix1;



    }



}
