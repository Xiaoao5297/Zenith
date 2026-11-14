package net.minecraft.util;

public class RegistryNamespacedDefaultedByKey extends RegistryNamespaced {

    private final String field_148760_d;
    private Object field_148761_e;

    public RegistryNamespacedDefaultedByKey(String s) {
        this.field_148760_d = s;
    }

    @Override
    public void func_148756_a(int i, String s, Object object) {
        if (this.field_148760_d.equals(s)) {
            this.field_148761_e = object;
        }

        super.func_148756_a(i, s, object);
    }

    @Override
    public Object func_82594_a(String s) {
        Object object = super.func_82594_a(s);

        return object == null ? this.field_148761_e : object;
    }

    @Override
    public Object func_148754_a(int i) {
        Object object = super.func_148754_a(i);

        return object == null ? this.field_148761_e : object;
    }
}
