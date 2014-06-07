package pl.eit.androideit.eit.ogloszenia;


import android.content.Context;
import android.text.Html;

import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import pl.eit.androideit.eit.NewsActivity;

/**
 * Created by Robert on 2014-04-08.
 */
public class XMLParseRSS {
    // We don't use namespaces
    private static final String ns = null;
    private Charset mCharset;
    private ArrayList<Item> mItemArrayList;

    public XMLParseRSS() {
    }

    public static final XmlNamespaceDictionary DICTIONARY =
            new XmlNamespaceDictionary()
                    .set("", "").set("atom", "http://www.w3.org/2005/Atom");
    private XmlObjectParser mXmlObjectParser;

    public ArrayList<Item> parse(String stream) throws XmlPullParserException, IOException {
            mXmlObjectParser = new XmlObjectParser(DICTIONARY);
            mCharset = Charset.forName("UTF-8");
            InputStream is = new ByteArrayInputStream(stream.getBytes("UTF-8"));
            News temp = mXmlObjectParser.parseAndClose(is, mCharset, News.class);
            mItemArrayList = new ArrayList<Item>();

            for(Item item: temp.newsChannel.item){
                mItemArrayList.add(item);
            }
        return  mItemArrayList;
    }
}

