package net.minecraft.util;

import com.google.common.base.Function;
import java.util.Iterator;

final class ChatComponentStyle$1 implements Function {

    ChatComponentStyle$1() {}

    public Iterator apply(IChatComponent ichatcomponent) {
        return ichatcomponent.iterator();
    }
}
