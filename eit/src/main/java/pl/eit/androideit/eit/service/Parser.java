package pl.eit.androideit.eit.service;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;

import pl.eit.androideit.eit.service.model.BaseSchedule;

public class Parser {

    private final Gson mGson;

    public Parser(Context context){
        mGson = createGson();
    }

    private Gson createGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public BaseSchedule parseSchedule(Reader reader) {
        return mGson.fromJson(reader, BaseSchedule.class);
    }
}

