package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced extends RegistrySimple implements IObjectIntIterable {

    protected final ObjectIntIdentityMap field_148759_a = new ObjectIntIdentityMap();
    protected final Map field_148758_b;

    public RegistryNamespaced() {
        this.field_148758_b = ((BiMap) this.field_82596_a).inverse();
    }

    public void func_148756_a(int i, String s, Object object) {
        this.field_148759_a.func_148746_a(object, i);
        this.func_82595_a(func_148755_c(s), object);
    }

    @Override
    protected Map func_148740_a() {
        return HashBiMap.create();
    }

    public Object func_82594_a(String s) {
        return super.func_82594_a(func_148755_c(s));
    }

    public String func_148750_c(Object object) {
        return (String) this.field_148758_b.get(object);
    }

    public boolean func_148741_d(String s) {
        return super.func_148741_d(func_148755_c(s));
    }

    public int func_148757_b(Object object) {
        return this.field_148759_a.func_148747_b(object);
    }

    public Object func_148754_a(int i) {
        return this.field_148759_a.func_148745_a(i);
    }

    public Iterator iterator() {
        return this.field_148759_a.iterator();
    }

    public boolean func_148753_b(int i) {
        return this.field_148759_a.func_148744_b(i);
    }

    private static String func_148755_c(String s) {
        return s.indexOf(58) == -1 ? "minecraft:" + s : s;
    }
}
