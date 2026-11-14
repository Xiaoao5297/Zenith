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
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class SqlDateTypeAdapter extends TypeAdapter<Date> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            return typetoken.getRawType() == Date.class ? new SqlDateTypeAdapter() : null;
        }
    };
    private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

    public SqlDateTypeAdapter() {}

    @Override
    public synchronized Date read(JsonReader jsonreader) throws IOException {
        if (jsonreader.peek() == JsonToken.NULL) {
            jsonreader.nextNull();
            return null;
        } else {
            try {
                long i = this.format.parse(jsonreader.nextString()).getTime();

                return new Date(i);
            } catch (ParseException parseexception) {
                throw new JsonSyntaxException(parseexception);
            }
        }
    }

    public synchronized void write(JsonWriter jsonwriter, Date date) throws IOException {
        jsonwriter.value(date == null ? null : this.format.format(date));
    }
}
