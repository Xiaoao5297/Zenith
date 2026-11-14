package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {

    protected List field_150264_a = Lists.newArrayList();
    private ChatStyle field_150263_b;

    public ChatComponentStyle() {}

    @Override
    public IChatComponent func_150257_a(IChatComponent ichatcomponent) {
        ichatcomponent.func_150256_b().func_150221_a(this.func_150256_b());
        this.field_150264_a.add(ichatcomponent);
        return this;
    }

    @Override
    public List func_150253_a() {
        return this.field_150264_a;
    }

    @Override
    public IChatComponent func_150258_a(String s) {
        return this.func_150257_a(new ChatComponentText(s));
    }

    @Override
    public IChatComponent func_150255_a(ChatStyle chatstyle) {
        this.field_150263_b = chatstyle;
        Iterator iterator = this.field_150264_a.iterator();

        while (iterator.hasNext()) {
            IChatComponent ichatcomponent = (IChatComponent) iterator.next();

            ichatcomponent.func_150256_b().func_150221_a(this.func_150256_b());
        }

        return this;
    }

    @Override
    public ChatStyle func_150256_b() {
        if (this.field_150263_b == null) {
            this.field_150263_b = new ChatStyle();
            Iterator iterator = this.field_150264_a.iterator();

            while (iterator.hasNext()) {
                IChatComponent ichatcomponent = (IChatComponent) iterator.next();

                ichatcomponent.func_150256_b().func_150221_a(this.field_150263_b);
            }
        }

        return this.field_150263_b;
    }

    public Iterator iterator() {
        return Iterators.concat(Iterators.forArray(this), func_150262_a(this.field_150264_a));
    }

    @Override
    public final String func_150260_c() {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.iterator();

        while (iterator.hasNext()) {
            IChatComponent ichatcomponent = (IChatComponent) iterator.next();

            stringbuilder.append(ichatcomponent.func_150261_e());
        }

        return stringbuilder.toString();
    }

    public static Iterator func_150262_a(Iterable iterable) {
        Iterator iterator = Iterators.concat(Iterators.transform(iterable.iterator(), new ChatComponentStyle$1()));

        iterator = Iterators.transform(iterator, new ChatComponentStyle$2());
        return iterator;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentStyle)) {
            return false;
        } else {
            ChatComponentStyle chatcomponentstyle = (ChatComponentStyle) object;

            return this.field_150264_a.equals(chatcomponentstyle.field_150264_a) && this.func_150256_b().equals(chatcomponentstyle.func_150256_b());
        }
    }

    public int hashCode() {
        return 31 * this.field_150263_b.hashCode() + this.field_150264_a.hashCode();
    }

    public String toString() {
        return "BaseComponent{style=" + this.field_150263_b + ", siblings=" + this.field_150264_a + '}';
    }
}
