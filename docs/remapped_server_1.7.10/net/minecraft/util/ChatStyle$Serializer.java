package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent$Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent$Action;

public class ChatStyle$Serializer implements JsonDeserializer, JsonSerializer {

    public ChatStyle$Serializer() {}

    @Override
    public ChatStyle deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        if (jsonelement.isJsonObject()) {
            ChatStyle chatstyle = new ChatStyle();
            JsonObject jsonobject = jsonelement.getAsJsonObject();

            if (jsonobject == null) {
                return null;
            } else {
                if (jsonobject.has("bold")) {
                    ChatStyle.access$002(chatstyle, jsonobject.get("bold").getAsBoolean());
                }

                if (jsonobject.has("italic")) {
                    ChatStyle.access$102(chatstyle, jsonobject.get("italic").getAsBoolean());
                }

                if (jsonobject.has("underlined")) {
                    ChatStyle.access$202(chatstyle, jsonobject.get("underlined").getAsBoolean());
                }

                if (jsonobject.has("strikethrough")) {
                    ChatStyle.access$302(chatstyle, jsonobject.get("strikethrough").getAsBoolean());
                }

                if (jsonobject.has("obfuscated")) {
                    ChatStyle.access$402(chatstyle, jsonobject.get("obfuscated").getAsBoolean());
                }

                if (jsonobject.has("color")) {
                    ChatStyle.access$502(chatstyle, (EnumChatFormatting) jsondeserializationcontext.deserialize(jsonobject.get("color"), EnumChatFormatting.class));
                }

                JsonObject jsonobject1;
                JsonPrimitive jsonprimitive;

                if (jsonobject.has("clickEvent")) {
                    jsonobject1 = jsonobject.getAsJsonObject("clickEvent");
                    if (jsonobject1 != null) {
                        jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                        ClickEvent$Action clickevent$action = jsonprimitive == null ? null : ClickEvent$Action.func_150672_a(jsonprimitive.getAsString());
                        JsonPrimitive jsonprimitive1 = jsonobject1.getAsJsonPrimitive("value");
                        String s = jsonprimitive1 == null ? null : jsonprimitive1.getAsString();

                        if (clickevent$action != null && s != null && clickevent$action.func_150674_a()) {
                            ChatStyle.access$602(chatstyle, new ClickEvent(clickevent$action, s));
                        }
                    }
                }

                if (jsonobject.has("hoverEvent")) {
                    jsonobject1 = jsonobject.getAsJsonObject("hoverEvent");
                    if (jsonobject1 != null) {
                        jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                        HoverEvent$Action hoverevent$action = jsonprimitive == null ? null : HoverEvent$Action.func_150684_a(jsonprimitive.getAsString());
                        IChatComponent ichatcomponent = (IChatComponent) jsondeserializationcontext.deserialize(jsonobject1.get("value"), IChatComponent.class);

                        if (hoverevent$action != null && ichatcomponent != null && hoverevent$action.func_150686_a()) {
                            ChatStyle.access$702(chatstyle, new HoverEvent(hoverevent$action, ichatcomponent));
                        }
                    }
                }

                return chatstyle;
            }
        } else {
            return null;
        }
    }

    public JsonElement serialize(ChatStyle chatstyle, Type type, JsonSerializationContext jsonserializationcontext) {
        if (chatstyle.func_150229_g()) {
            return null;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (ChatStyle.access$000(chatstyle) != null) {
                jsonobject.addProperty("bold", ChatStyle.access$000(chatstyle));
            }

            if (ChatStyle.access$100(chatstyle) != null) {
                jsonobject.addProperty("italic", ChatStyle.access$100(chatstyle));
            }

            if (ChatStyle.access$200(chatstyle) != null) {
                jsonobject.addProperty("underlined", ChatStyle.access$200(chatstyle));
            }

            if (ChatStyle.access$300(chatstyle) != null) {
                jsonobject.addProperty("strikethrough", ChatStyle.access$300(chatstyle));
            }

            if (ChatStyle.access$400(chatstyle) != null) {
                jsonobject.addProperty("obfuscated", ChatStyle.access$400(chatstyle));
            }

            if (ChatStyle.access$500(chatstyle) != null) {
                jsonobject.add("color", jsonserializationcontext.serialize(ChatStyle.access$500(chatstyle)));
            }

            JsonObject jsonobject1;

            if (ChatStyle.access$600(chatstyle) != null) {
                jsonobject1 = new JsonObject();
                jsonobject1.addProperty("action", ChatStyle.access$600(chatstyle).func_150669_a().func_150673_b());
                jsonobject1.addProperty("value", ChatStyle.access$600(chatstyle).func_150668_b());
                jsonobject.add("clickEvent", jsonobject1);
            }

            if (ChatStyle.access$700(chatstyle) != null) {
                jsonobject1 = new JsonObject();
                jsonobject1.addProperty("action", ChatStyle.access$700(chatstyle).func_150701_a().func_150685_b());
                jsonobject1.add("value", jsonserializationcontext.serialize(ChatStyle.access$700(chatstyle).func_150702_b()));
                jsonobject.add("hoverEvent", jsonobject1);
            }

            return jsonobject;
        }
    }
}
