package com.google.gson;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {

    public JsonParser() {}

    public JsonElement parse(String s) throws JsonSyntaxException {
        return this.parse((Reader) (new StringReader(s)));
    }

    public JsonElement parse(Reader reader) throws JsonIOException, JsonSyntaxException {
        try {
            JsonReader jsonreader = new JsonReader(reader);
            JsonElement jsonelement = this.parse(jsonreader);

            if (!jsonelement.isJsonNull() && jsonreader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            } else {
                return jsonelement;
            }
        } catch (MalformedJsonException malformedjsonexception) {
            throw new JsonSyntaxException(malformedjsonexception);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        } catch (NumberFormatException numberformatexception) {
            throw new JsonSyntaxException(numberformatexception);
        }
    }

    public JsonElement parse(JsonReader jsonreader) throws JsonIOException, JsonSyntaxException {
        boolean flag = jsonreader.isLenient();

        jsonreader.setLenient(true);

        JsonElement jsonelement;

        try {
            jsonelement = Streams.parse(jsonreader);
        } catch (StackOverflowError stackoverflowerror) {
            throw new JsonParseException("Failed parsing JSON source: " + jsonreader + " to Json", stackoverflowerror);
        } catch (OutOfMemoryError outofmemoryerror) {
            throw new JsonParseException("Failed parsing JSON source: " + jsonreader + " to Json", outofmemoryerror);
        } finally {
            jsonreader.setLenient(flag);
        }

        return jsonelement;
    }
}
