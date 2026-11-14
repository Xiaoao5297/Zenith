package net.minecraft.util;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle {

    private ChatStyle field_150249_a;
    private EnumChatFormatting field_150247_b;
    private Boolean field_150248_c;
    private Boolean field_150245_d;
    private Boolean field_150246_e;
    private Boolean field_150243_f;
    private Boolean field_150244_g;
    private ClickEvent field_150251_h;
    private HoverEvent field_150252_i;
    private static final ChatStyle field_150250_j = new ChatStyle$1();

    public ChatStyle() {}

    public EnumChatFormatting func_150215_a() {
        return this.field_150247_b == null ? this.func_150224_n().func_150215_a() : this.field_150247_b;
    }

    public boolean func_150223_b() {
        return this.field_150248_c == null ? this.func_150224_n().func_150223_b() : this.field_150248_c;
    }

    public boolean func_150242_c() {
        return this.field_150245_d == null ? this.func_150224_n().func_150242_c() : this.field_150245_d;
    }

    public boolean func_150236_d() {
        return this.field_150243_f == null ? this.func_150224_n().func_150236_d() : this.field_150243_f;
    }

    public boolean func_150234_e() {
        return this.field_150246_e == null ? this.func_150224_n().func_150234_e() : this.field_150246_e;
    }

    public boolean func_150233_f() {
        return this.field_150244_g == null ? this.func_150224_n().func_150233_f() : this.field_150244_g;
    }

    public boolean func_150229_g() {
        return this.field_150248_c == null && this.field_150245_d == null && this.field_150243_f == null && this.field_150246_e == null && this.field_150244_g == null && this.field_150247_b == null && this.field_150251_h == null && this.field_150252_i == null;
    }

    public ClickEvent func_150235_h() {
        return this.field_150251_h == null ? this.func_150224_n().func_150235_h() : this.field_150251_h;
    }

    public HoverEvent func_150210_i() {
        return this.field_150252_i == null ? this.func_150224_n().func_150210_i() : this.field_150252_i;
    }

    public ChatStyle func_150238_a(EnumChatFormatting enumchatformatting) {
        this.field_150247_b = enumchatformatting;
        return this;
    }

    public ChatStyle func_150227_a(Boolean obool) {
        this.field_150248_c = obool;
        return this;
    }

    public ChatStyle func_150217_b(Boolean obool) {
        this.field_150245_d = obool;
        return this;
    }

    public ChatStyle func_150225_c(Boolean obool) {
        this.field_150243_f = obool;
        return this;
    }

    public ChatStyle func_150228_d(Boolean obool) {
        this.field_150246_e = obool;
        return this;
    }

    public ChatStyle func_150237_e(Boolean obool) {
        this.field_150244_g = obool;
        return this;
    }

    public ChatStyle func_150241_a(ClickEvent clickevent) {
        this.field_150251_h = clickevent;
        return this;
    }

    public ChatStyle func_150209_a(HoverEvent hoverevent) {
        this.field_150252_i = hoverevent;
        return this;
    }

    public ChatStyle func_150221_a(ChatStyle chatstyle) {
        this.field_150249_a = chatstyle;
        return this;
    }

    private ChatStyle func_150224_n() {
        return this.field_150249_a == null ? ChatStyle.field_150250_j : this.field_150249_a;
    }

    public String toString() {
        return "Style{hasParent=" + (this.field_150249_a != null) + ", color=" + this.field_150247_b + ", bold=" + this.field_150248_c + ", italic=" + this.field_150245_d + ", underlined=" + this.field_150246_e + ", obfuscated=" + this.field_150244_g + ", clickEvent=" + this.func_150235_h() + ", hoverEvent=" + this.func_150210_i() + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatStyle)) {
            return false;
        } else {
            ChatStyle chatstyle = (ChatStyle) object;
            boolean flag;

            if (this.func_150223_b() == chatstyle.func_150223_b() && this.func_150215_a() == chatstyle.func_150215_a() && this.func_150242_c() == chatstyle.func_150242_c() && this.func_150233_f() == chatstyle.func_150233_f() && this.func_150236_d() == chatstyle.func_150236_d() && this.func_150234_e() == chatstyle.func_150234_e()) {
                label56:
                {
                    if (this.func_150235_h() != null) {
                        if (!this.func_150235_h().equals(chatstyle.func_150235_h())) {
                            break label56;
                        }
                    } else if (chatstyle.func_150235_h() != null) {
                        break label56;
                    }

                    if (this.func_150210_i() != null) {
                        if (!this.func_150210_i().equals(chatstyle.func_150210_i())) {
                            break label56;
                        }
                    } else if (chatstyle.func_150210_i() != null) {
                        break label56;
                    }

                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode() {
        int i = this.field_150247_b.hashCode();

        i = 31 * i + this.field_150248_c.hashCode();
        i = 31 * i + this.field_150245_d.hashCode();
        i = 31 * i + this.field_150246_e.hashCode();
        i = 31 * i + this.field_150243_f.hashCode();
        i = 31 * i + this.field_150244_g.hashCode();
        i = 31 * i + this.field_150251_h.hashCode();
        i = 31 * i + this.field_150252_i.hashCode();
        return i;
    }

    public ChatStyle func_150232_l() {
        ChatStyle chatstyle = new ChatStyle();

        chatstyle.field_150248_c = this.field_150248_c;
        chatstyle.field_150245_d = this.field_150245_d;
        chatstyle.field_150243_f = this.field_150243_f;
        chatstyle.field_150246_e = this.field_150246_e;
        chatstyle.field_150244_g = this.field_150244_g;
        chatstyle.field_150247_b = this.field_150247_b;
        chatstyle.field_150251_h = this.field_150251_h;
        chatstyle.field_150252_i = this.field_150252_i;
        chatstyle.field_150249_a = this.field_150249_a;
        return chatstyle;
    }

    public ChatStyle func_150206_m() {
        ChatStyle chatstyle = new ChatStyle();

        chatstyle.func_150227_a(this.func_150223_b());
        chatstyle.func_150217_b(this.func_150242_c());
        chatstyle.func_150225_c(this.func_150236_d());
        chatstyle.func_150228_d(this.func_150234_e());
        chatstyle.func_150237_e(this.func_150233_f());
        chatstyle.func_150238_a(this.func_150215_a());
        chatstyle.func_150241_a(this.func_150235_h());
        chatstyle.func_150209_a(this.func_150210_i());
        return chatstyle;
    }
}
