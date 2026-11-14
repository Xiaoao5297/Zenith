package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public enum HoverEvent$Action {

    SHOW_TEXT("show_text", true), SHOW_ACHIEVEMENT("show_achievement", true), SHOW_ITEM("show_item", true);

    private static final Map field_150690_d = Maps.newHashMap();
    private final boolean field_150691_e;
    private final String field_150688_f;

    private HoverEvent$Action(String s, boolean flag) {
        this.field_150688_f = s;
        this.field_150691_e = flag;
    }

    public boolean func_150686_a() {
        return this.field_150691_e;
    }

    public String func_150685_b() {
        return this.field_150688_f;
    }

    public static HoverEvent$Action func_150684_a(String s) {
        return (HoverEvent$Action) HoverEvent$Action.field_150690_d.get(s);
    }

    static {
        HoverEvent$Action[] ahoverevent$action = values();
        int i = ahoverevent$action.length;

        for (int j = 0; j < i; ++j) {
            HoverEvent$Action hoverevent$action = ahoverevent$action[j];

            HoverEvent$Action.field_150690_d.put(hoverevent$action.func_150685_b(), hoverevent$action);
        }

    }
}
