package pl.eit.androideit.eit.helpers;

import android.content.Context;

import java.util.List;

import pl.eit.androideit.eit.service.DB;
import pl.eit.androideit.eit.service.model.Channel;

public class ChannelsHelper {
    public static List<Channel> getChannels(Context context) {
        DB db = new DB(context);
        return db.getChannels();
    }
}
