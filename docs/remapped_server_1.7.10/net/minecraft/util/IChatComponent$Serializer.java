package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

public class IChatComponent$Serializer implements JsonDeserializer, JsonSerializer {

    private static final Gson field_150700_a;

    public IChatComponent$Serializer() {}

    @Override
    public IChatComponent deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        if (jsonelement.isJsonPrimitive()) {
            return new ChatComponentText(jsonelement.getAsString());
        } else if (!jsonelement.isJsonObject()) {
            if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();
                IChatComponent ichatcomponent = null;
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    IChatComponent ichatcomponent1 = this.deserialize(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);

                    if (ichatcomponent == null) {
                        ichatcomponent = ichatcomponent1;
                    } else {
                        ichatcomponent.func_150257_a(ichatcomponent1);
                    }
                }

                return ichatcomponent;
            } else {
                throw new JsonParseException("Don't know how to turn " + jsonelement.toString() + " into a Component");
            }
        } else {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            Object object;

            if (jsonobject.has("text")) {
                object = new ChatComponentText(jsonobject.get("text").getAsString());
            } else {
                if (!jsonobject.has("translate")) {
                    throw new JsonParseException("Don't know how to turn " + jsonelement.toString() + " into a Component");
                }

                String s = jsonobject.get("translate").getAsString();

                if (jsonobject.has("with")) {
                    JsonArray jsonarray1 = jsonobject.getAsJsonArray("with");
                    Object[] aobject = new Object[jsonarray1.size()];

                    for (int i = 0; i < aobject.length; ++i) {
                        aobject[i] = this.deserialize(jsonarray1.get(i), type, jsondeserializationcontext);
                        if (aobject[i] instanceof ChatComponentText) {
                            ChatComponentText chatcomponenttext = (ChatComponentText) aobject[i];

                            if (chatcomponenttext.func_150256_b().func_150229_g() && chatcomponenttext.func_150253_a().isEmpty()) {
                                aobject[i] = chatcomponenttext.func_150265_g();
                            }
                        }
                    }

                    object = new ChatComponentTranslation(s, aobject);
                } else {
                    object = new ChatComponentTranslation(s, new Object[0]);
                }
            }

            if (jsonobject.has("extra")) {
                JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");

                if (jsonarray2.size() <= 0) {
                    throw new JsonParseException("Unexpected empty array of components");
                }

                for (int j = 0; j < jsonarray2.size(); ++j) {
                    ((IChatComponent) object).func_150257_a(this.deserialize(jsonarray2.get(j), type, jsondeserializationcontext));
                }
            }

            ((IChatComponent) object).func_150255_a((ChatStyle) jsondeserializationcontext.deserialize(jsonelement, ChatStyle.class));
            return (IChatComponent) object;
        }
    }

    private void func_150695_a(ChatStyle chatstyle, JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
        JsonElement jsonelement = jsonserializationcontext.serialize(chatstyle);

        if (jsonelement.isJsonObject()) {
            JsonObject jsonobject1 = (JsonObject) jsonelement;
            Iterator iterator = jsonobject1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                jsonobject.add((String) entry.getKey(), (JsonElement) entry.getValue());
            }
        }

    }

    public JsonElement serialize(IChatComponent ichatcomponent, Type type, JsonSerializationContext jsonserializationcontext) {
        if (ichatcomponent instanceof ChatComponentText && ichatcomponent.func_150256_b().func_150229_g() && ichatcomponent.func_150253_a().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText) ichatcomponent).func_150265_g());
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!ichatcomponent.func_150256_b().func_150229_g()) {
                this.func_150695_a(ichatcomponent.func_150256_b(), jsonobject, jsonserializationcontext);
            }

            if (!ichatcomponent.func_150253_a().isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = ichatcomponent.func_150253_a().iterator();

                while (iterator.hasNext()) {
                    IChatComponent ichatcomponent1 = (IChatComponent) iterator.next();

                    jsonarray.add(this.serialize(ichatcomponent1, ichatcomponent1.getClass(), jsonserializationcontext));
                }

                jsonobject.add("extra", jsonarray);
            }

            if (ichatcomponent instanceof ChatComponentText) {
                jsonobject.addProperty("text", ((ChatComponentText) ichatcomponent).func_150265_g());
            } else {
                if (!(ichatcomponent instanceof ChatComponentTranslation)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + ichatcomponent + " as a Component");
                }

                ChatComponentTranslation chatcomponenttranslation = (ChatComponentTranslation) ichatcomponent;

                jsonobject.addProperty("translate", chatcomponenttranslation.func_150268_i());
                if (chatcomponenttranslation.func_150271_j() != null && chatcomponenttranslation.func_150271_j().length > 0) {
                    JsonArray jsonarray1 = new JsonArray();
                    Object[] aobject = chatcomponenttranslation.func_150271_j();
                    int i = aobject.length;

                    for (int j = 0; j < i; ++j) {
                        Object object = aobject[j];

                        if (object instanceof IChatComponent) {
                            jsonarray1.add(this.serialize((IChatComponent) object, object.getClass(), jsonserializationcontext));
                        } else {
                            jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                        }
                    }

                    jsonobject.add("with", jsonarray1);
                }
            }

            return jsonobject;
        }
    }

    public static String func_150696_a(IChatComponent ichatcomponent) {
        return IChatComponent$Serializer.field_150700_a.toJson((Object) ichatcomponent);
    }

    public static IChatComponent func_150699_a(String s) {
        return (IChatComponent) IChatComponent$Serializer.field_150700_a.fromJson(s, IChatComponent.class);
    }

    static {
        GsonBuilder gsonbuilder = new GsonBuilder();

        gsonbuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent$Serializer());
        gsonbuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle$Serializer());
        gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
        field_150700_a = gsonbuilder.create();
    }
}
