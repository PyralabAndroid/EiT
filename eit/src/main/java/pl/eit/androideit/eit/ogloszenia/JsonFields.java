package pl.eit.androideit.eit.ogloszenia;

import java.util.ArrayList;

public class JsonFields {

    public String text;
    public String title;
    public String images_url;
    public ArrayList<Appendix> appendix;

    public static class Appendix {
        public String link;
        public String href;

    }
}


