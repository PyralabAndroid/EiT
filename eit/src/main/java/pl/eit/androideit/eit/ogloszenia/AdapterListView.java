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

    private final ArrayList<JsonFields> items_array;
    private final Context context;

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

        TextView title = (TextView) rowView.findViewById(R.id.label);
        TextView text = (TextView) rowView.findViewById(R.id.value);

        title.setText(items_array.get(position).title);
        text.setText(items_array.get(position).text);

        return  rowView;
    }
}
