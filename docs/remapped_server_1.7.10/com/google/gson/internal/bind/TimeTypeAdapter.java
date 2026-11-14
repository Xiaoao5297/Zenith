package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TimeTypeAdapter extends TypeAdapter<Time> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            return typetoken.getRawType() == Time.class ? new TimeTypeAdapter() : null;
        }
    };
    private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

    public TimeTypeAdapter() {}

    @Override
    public synchronized Time read(JsonReader jsonreader) throws IOException {
        if (jsonreader.peek() == JsonToken.NULL) {
            jsonreader.nextNull();
            return null;
        } else {
            try {
                Date date = this.format.parse(jsonreader.nextString());

                return new Time(date.getTime());
            } catch (ParseException parseexception) {
                throw new JsonSyntaxException(parseexception);
            }
        }
    }

    public synchronized void write(JsonWriter jsonwriter, Time time) throws IOException {
        jsonwriter.value(time == null ? null : this.format.format(time));
    }
}
