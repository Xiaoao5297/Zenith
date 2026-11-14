package net.minecraft.util;

public class RegistryDefaulted extends RegistrySimple {

    private final Object field_82597_b;

    public RegistryDefaulted(Object object) {
        this.field_82597_b = object;
    }

    @Override
    public Object func_82594_a(Object object) {
        Object object1 = super.func_82594_a(object);

        return object1 == null ? this.field_82597_b : object1;
    }
}
