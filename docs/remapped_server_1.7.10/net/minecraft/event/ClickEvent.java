package net.minecraft.event;

public class ClickEvent {

    private final ClickEvent$Action field_150671_a;
    private final String field_150670_b;

    public ClickEvent(ClickEvent$Action clickevent$action, String s) {
        this.field_150671_a = clickevent$action;
        this.field_150670_b = s;
    }

    public ClickEvent$Action func_150669_a() {
        return this.field_150671_a;
    }

    public String func_150668_b() {
        return this.field_150670_b;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            ClickEvent clickevent = (ClickEvent) object;

            if (this.field_150671_a != clickevent.field_150671_a) {
                return false;
            } else {
                if (this.field_150670_b != null) {
                    if (this.field_150670_b.equals(clickevent.field_150670_b)) {
                        return true;
                    }
                } else if (clickevent.field_150670_b == null) {
                    return true;
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "ClickEvent{action=" + this.field_150671_a + ", value='" + this.field_150670_b + '\'' + '}';
    }

    public int hashCode() {
        int i = this.field_150671_a.hashCode();

        i = 31 * i + (this.field_150670_b != null ? this.field_150670_b.hashCode() : 0);
        return i;
    }
}
