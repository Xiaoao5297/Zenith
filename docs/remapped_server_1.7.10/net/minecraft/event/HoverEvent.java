package net.minecraft.event;

import net.minecraft.util.IChatComponent;

public class HoverEvent {

    private final HoverEvent$Action field_150704_a;
    private final IChatComponent field_150703_b;

    public HoverEvent(HoverEvent$Action hoverevent$action, IChatComponent ichatcomponent) {
        this.field_150704_a = hoverevent$action;
        this.field_150703_b = ichatcomponent;
    }

    public HoverEvent$Action func_150701_a() {
        return this.field_150704_a;
    }

    public IChatComponent func_150702_b() {
        return this.field_150703_b;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            HoverEvent hoverevent = (HoverEvent) object;

            if (this.field_150704_a != hoverevent.field_150704_a) {
                return false;
            } else {
                if (this.field_150703_b != null) {
                    if (this.field_150703_b.equals(hoverevent.field_150703_b)) {
                        return true;
                    }
                } else if (hoverevent.field_150703_b == null) {
                    return true;
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.field_150704_a + ", value='" + this.field_150703_b + '\'' + '}';
    }

    public int hashCode() {
        int i = this.field_150704_a.hashCode();

        i = 31 * i + (this.field_150703_b != null ? this.field_150703_b.hashCode() : 0);
        return i;
    }
}
