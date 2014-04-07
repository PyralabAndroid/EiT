package pl.eit.androideit.eit.ogloszenia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


import pl.eit.androideit.eit.R;

/**
 * Created by Robert on 2014-04-02.
 */


public class AdapterListView extends ArrayAdapter<JsonFields>{
    public AdapterListView(Context context, int resource) {
        super(context, resource);
    }

    /*private final ArrayList<JsonFields> items_array;
    private final Context context;

    String ciag_linkow;

    public AdapterListView(Context context, ArrayList<JsonFields> items) {

        super(context, R.layout.ogloszenia_layout, items);
        this.context = context;
        this.items_array = items;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = (View) inflater.inflate(R.layout.ogloszenia_layout, parent, false);



        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView text = (TextView) rowView.findViewById(R.id.text);
        TextView linki = (TextView) rowView.findViewById(R.id.links);


        title.setText(items_array.get(position).title);
        text.setText(items_array.get(position).text);
        if(items_array.get(position).appendix != null) {
            ciag_linkow = pobierzLinki(items_array, position);
            linki.setText(ciag_linkow);
        }

        return  rowView;
    }

    public String pobierzLinki(ArrayList<JsonFields> json_link_array, int position)
    {

        String cos_tam = "";

        ArrayList<String> lista_linkow = new ArrayList<String>();

        if(json_link_array.get(position).appendix.link1 != null)
            lista_linkow.add(json_link_array.get(position).appendix.link1);

        if(json_link_array.get(position).appendix.link2 != null)
            lista_linkow.add(json_link_array.get(position).appendix.link2);

        if(json_link_array.get(position).appendix.link3 != null)
            lista_linkow.add(json_link_array.get(position).appendix.link3);

        if(json_link_array.get(position).appendix.link4 != null)
            lista_linkow.add(json_link_array.get(position).appendix.link4);


        for(String linki : lista_linkow)
        {
            if(lista_linkow == null) {
                lista_linkow.remove(lista_linkow);
                return cos_tam;
            }

            cos_tam = cos_tam + linki +"\n";
        }
        return  cos_tam;
    }*/
}
