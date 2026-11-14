package com.google.gson;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class DefaultDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    private final DateFormat iso8601Format;

    DefaultDateTypeAdapter() {
        this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
    }

    DefaultDateTypeAdapter(String s) {
        this(new SimpleDateFormat(s, Locale.US), new SimpleDateFormat(s));
    }

    DefaultDateTypeAdapter(int i) {
        this(DateFormat.getDateInstance(i, Locale.US), DateFormat.getDateInstance(i));
    }

    public DefaultDateTypeAdapter(int i, int j) {
        this(DateFormat.getDateTimeInstance(i, j, Locale.US), DateFormat.getDateTimeInstance(i, j));
    }

    DefaultDateTypeAdapter(DateFormat dateformat, DateFormat dateformat1) {
        this.enUsFormat = dateformat;
        this.localFormat = dateformat1;
        this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonserializationcontext) {
        DateFormat dateformat = this.localFormat;

        synchronized (this.localFormat) {
            String s = this.enUsFormat.format(date);

            return new JsonPrimitive(s);
        }
    }

    @Override
    public Date deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
        if (!(jsonelement instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        } else {
            Date date = this.deserializeToDate(jsonelement);

            if (type == Date.class) {
                return date;
            } else if (type == Timestamp.class) {
                return new Timestamp(date.getTime());
            } else if (type == java.sql.Date.class) {
                return new java.sql.Date(date.getTime());
            } else {
                throw new IllegalArgumentException(this.getClass() + " cannot deserialize to " + type);
            }
        }
    }

    private Date deserializeToDate(JsonElement jsonelement) {
        DateFormat dateformat = this.localFormat;

        synchronized (this.localFormat) {
            Date date;

            try {
                date = this.localFormat.parse(jsonelement.getAsString());
            } catch (ParseException parseexception) {
                try {
                    date = this.enUsFormat.parse(jsonelement.getAsString());
                } catch (ParseException parseexception1) {
                    try {
                        date = this.iso8601Format.parse(jsonelement.getAsString());
                    } catch (ParseException parseexception2) {
                        throw new JsonSyntaxException(jsonelement.getAsString(), parseexception2);
                    }

                    return date;
                }

                return date;
            }

            return date;
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(DefaultDateTypeAdapter.class.getSimpleName());
        stringbuilder.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
        return stringbuilder.toString();
    }
}
