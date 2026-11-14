package net.minecraft.util;

import java.util.Iterator;

public class ChatComponentText extends ChatComponentStyle {

    private final String field_150267_b;

    public ChatComponentText(String s) {
        this.field_150267_b = s;
    }

    public String func_150265_g() {
        return this.field_150267_b;
    }

    @Override
    public String func_150261_e() {
        return this.field_150267_b;
    }

    @Override
    public ChatComponentText func_150259_f() {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.field_150267_b);

        chatcomponenttext.func_150255_a(this.func_150256_b().func_150232_l());
        Iterator iterator = this.func_150253_a().iterator();

        while (iterator.hasNext()) {
            IChatComponent ichatcomponent = (IChatComponent) iterator.next();

            chatcomponenttext.func_150257_a(ichatcomponent.func_150259_f());
        }

        return chatcomponenttext;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentText)) {
            return false;
        } else {
            ChatComponentText chatcomponenttext = (ChatComponentText) object;

            return this.field_150267_b.equals(chatcomponenttext.func_150265_g()) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.field_150267_b + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
    }
}
