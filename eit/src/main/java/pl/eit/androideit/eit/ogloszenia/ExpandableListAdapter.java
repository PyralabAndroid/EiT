package pl.eit.androideit.eit.ogloszenia;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pl.eit.androideit.eit.R;

/**
 * Created by Robert on 2014-04-06.
 */
public class ExpandableListAdapter extends ExpandableListItemAdapter<JsonFields> {


    List<JsonFields> json_field_list;
    Context context;

    public ExpandableListAdapter(Context context, final List<JsonFields> items) {
       super(context, items);
       this.json_field_list = items;
       this.context = context;
    }

    @Override
    public View getTitleView(int position, View convertView, ViewGroup parent) {

         if(convertView == null) {
            LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout_inflater.inflate(R.layout.expandable_group_news, null);
        }
        TextView exp_title = (TextView) convertView.findViewById(R.id.exp_header_title);
        exp_title.setText(json_field_list.get(position).title);

        return convertView;
    }

    @Override
    public View getContentView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout_inflater.inflate(R.layout.expandable_child_news, null);
        }
        TextView exp_text = (TextView) convertView.findViewById(R.id.exp_child_text);
        exp_text.setText(json_field_list.get(position).text);

        if(json_field_list.get(position).appendix != null) {

            TextView exp_links = (TextView) convertView.findViewById(R.id.exp_child_links);
            exp_links.setMovementMethod(LinkMovementMethod.getInstance());

            String text = "";

            for(JsonFields.Appendix app_link_href : json_field_list.get(position).appendix){
                text = text + "<a href=" + app_link_href.href + ">" + app_link_href.link + "</a><br>" ;
            }
            exp_links.setText(Html.fromHtml(text));
        }

        if(json_field_list.get(position).images_url != null){
            getImageFromUrl(convertView, position);
        }

        return convertView;
    }

    public void getImageFromUrl(View convertView, int position){

        ImageView images_from_url = new ImageView(context);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.exp_child_layout_id);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        images_from_url.setLayoutParams(params);
        //ImageView images_from_url = (ImageView) convertView.findViewById(R.id.images_from_url);
        //Picasso.with(context).load(json_field_list.get(position).images_url).into(images_from_url);
        new DownloadImageAsyncTask(images_from_url, context).execute(json_field_list.get(position).images_url);
        params.addRule(RelativeLayout.BELOW, R.id.exp_child_links);
        relativeLayout.addView(images_from_url);
    }

}


