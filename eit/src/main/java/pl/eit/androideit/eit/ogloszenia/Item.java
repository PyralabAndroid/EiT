package pl.eit.androideit.eit.ogloszenia;

import com.google.api.client.util.Key;

/**
 * Created by Robert on 2014-04-08.
 */
public class Item {
    @Key("title")
    public String title;
    @Key("description")
    public String description;
    @Key("link")
    public String link;
}
