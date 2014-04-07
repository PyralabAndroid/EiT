package pl.eit.androideit.eit.ogloszenia;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Robert on 2014-04-01.
 */
public class JsonFields
{

        public String text;
        public String title;
        public String images_url;
        public ArrayList<Appendix> appendix;

    public static class Appendix
    {
       public String link;
       public String href;

    }




}


