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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTypeAdapter extends TypeAdapter<Date> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            return typetoken.getRawType() == Date.class ? new DateTypeAdapter() : null;
        }
    };
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    private final DateFormat iso8601Format;

    public DateTypeAdapter() {
        this.enUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
        this.localFormat = DateFormat.getDateTimeInstance(2, 2);
        this.iso8601Format = buildIso8601Format();
    }

    private static DateFormat buildIso8601Format() {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

        simpledateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpledateformat;
    }

    @Override
    public Date read(JsonReader jsonreader) throws IOException {
        if (jsonreader.peek() == JsonToken.NULL) {
            jsonreader.nextNull();
            return null;
        } else {
            return this.deserializeToDate(jsonreader.nextString());
        }
    }

    private synchronized Date deserializeToDate(String s) {
        try {
            return this.localFormat.parse(s);
        } catch (ParseException parseexception) {
            try {
                return this.enUsFormat.parse(s);
            } catch (ParseException parseexception1) {
                try {
                    return this.iso8601Format.parse(s);
                } catch (ParseException parseexception2) {
                    throw new JsonSyntaxException(s, parseexception2);
                }
            }
        }
    }

    public synchronized void write(JsonWriter jsonwriter, Date date) throws IOException {
        if (date == null) {
            jsonwriter.nullValue();
        } else {
            String s = this.enUsFormat.format(date);

            jsonwriter.value(s);
        }
    }
}
