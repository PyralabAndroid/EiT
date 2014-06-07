package pl.eit.androideit.eit.ogloszenia;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.squareup.picasso.Picasso;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.util.ArrayList;


import butterknife.ButterKnife;
import pl.eit.androideit.eit.R;

/**
 * Created by Robert on 2014-04-06.
 */
public class ExpandableListAdapter extends ExpandableListItemAdapter<Item> {


    Context context;
    ArrayList<Item> mItemArrayList;
    ImageView mImageFromUrl;

    public ExpandableListAdapter(Context context, final ArrayList<Item> items_list) {
        super(context, items_list);
        this.context = context;
        this.mItemArrayList = items_list;
    }


    @Override
    public View getTitleView(int position, View convertView, ViewGroup parent) {
        if (mItemArrayList != null) {
            if (convertView == null) {
                LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layout_inflater.inflate(R.layout.expandable_group_news, null);
            }
            TextView exp_title = (TextView) convertView.findViewById(R.id.exp_header_title);
            exp_title.setText(Html.fromHtml(mItemArrayList.get(position).title));
        }
        return convertView;
    }

    @Override
    public View getContentView(int position, View convertView, ViewGroup parent) {

        if (mItemArrayList != null) {
            if (convertView == null) {
                LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layout_inflater.inflate(R.layout.expandable_child_news, null);
            }

            TextView exp_text = (TextView) convertView.findViewById(R.id.exp_child_text);
            exp_text.setMovementMethod(LinkMovementMethod.getInstance());


            if(mItemArrayList.get(position).description.trim().length() < 10){
                exp_text.setVisibility(convertView.GONE);
            }
            else {
                exp_text.setText(Html.fromHtml(mItemArrayList.get(position).description));
            }

            TextView exp_links = (TextView) convertView.findViewById(R.id.exp_child_link);
            exp_links.setMovementMethod(LinkMovementMethod.getInstance());
            String   text = "<a href=" + mItemArrayList.get(position).link + ">" + mItemArrayList.get(position).link + "</a><br>" ;
            exp_links.setText(Html.fromHtml(text));

            String url_link = parseString(mItemArrayList.get(position).description);
            mImageFromUrl = (ImageView) convertView.findViewById(R.id.images_from_url);
            if(url_link != null){
                new DownloadImageAsyncTask(mImageFromUrl, context).execute(url_link);
                //Picasso.with(context).load(url_link).into(mImageFromUrl);
            }
                else mImageFromUrl.setVisibility(convertView.GONE);

            }
            return convertView;
    }
//            String wp = String.valueOf(Html.fromHtml(mItemArrayList.get(position).description));
//            wp = wp.replaceAll("\\n\\n.{1,2}\\n\\n", "\n\n").replaceAll("\\n\\n$","");
    /**
     *
     * @param xml_find_ulr_image
     * string w którym będzie wyszukiwany tag img src w celu wyciagniecia adresu linku
     * @return
     * zwraca adres linku
     */
    public String parseString(String xml_find_ulr_image){
        String s;
        Document doc = Jsoup.parse(xml_find_ulr_image);
        Element link = doc.select("img").first();

        if(link != null) {
            s = link.attr("src").toString();
        }
        else s = null;

        return s;
    }

}


