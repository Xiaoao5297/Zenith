package net.minecraft.util;

public class ChatComponentTranslationFormatException extends IllegalArgumentException {

    public ChatComponentTranslationFormatException(ChatComponentTranslation chatcomponenttranslation, String s) {
        super(String.format("Error parsing: %s: %s", chatcomponenttranslation, s));
    }

    public ChatComponentTranslationFormatException(ChatComponentTranslation chatcomponenttranslation, int i) {
        super(String.format("Invalid index %d requested for %s", i, chatcomponenttranslation));
    }

    public ChatComponentTranslationFormatException(ChatComponentTranslation chatcomponenttranslation, Throwable throwable) {
        super(String.format("Error while parsing: %s", chatcomponenttranslation), throwable);
    }
}
