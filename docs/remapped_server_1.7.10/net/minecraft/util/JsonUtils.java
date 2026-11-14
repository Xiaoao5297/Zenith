package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {

    public static boolean func_151202_d(JsonObject jsonobject, String s) {
        return !func_151204_g(jsonobject, s) ? false : jsonobject.get(s).isJsonArray();
    }

    public static boolean func_151204_g(JsonObject jsonobject, String s) {
        return jsonobject == null ? false : jsonobject.get(s) != null;
    }

    public static String func_151206_a(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive()) {
            return jsonelement.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a string, was " + func_151222_d(jsonelement));
        }
    }

    public static String func_151200_h(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return func_151206_a(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a string");
        }
    }

    public static int func_151215_f(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Int, was " + func_151222_d(jsonelement));
        }
    }

    public static int func_151203_m(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return func_151215_f(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Int");
        }
    }

    public static JsonObject func_151210_l(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonObject()) {
            return jsonelement.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a JsonObject, was " + func_151222_d(jsonelement));
        }
    }

    public static JsonArray func_151207_m(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonArray()) {
            return jsonelement.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a JsonArray, was " + func_151222_d(jsonelement));
        }
    }

    public static JsonArray func_151214_t(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return func_151207_m(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a JsonArray");
        }
    }

    public static String func_151222_d(JsonElement jsonelement) {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(jsonelement), "...", 10);

        if (jsonelement == null) {
            return "null (missing)";
        } else if (jsonelement.isJsonNull()) {
            return "null (json)";
        } else if (jsonelement.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (jsonelement.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (jsonelement.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();

                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }
}
