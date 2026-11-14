package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {

    private final BaseAttributeMap field_111138_a;
    private final IAttribute field_111136_b;
    private final Map field_111137_c = Maps.newHashMap();
    private final Map field_111134_d = Maps.newHashMap();
    private final Map field_111135_e = Maps.newHashMap();
    private double field_111132_f;
    private boolean field_111133_g = true;
    private double field_111139_h;

    public ModifiableAttributeInstance(BaseAttributeMap baseattributemap, IAttribute iattribute) {
        this.field_111138_a = baseattributemap;
        this.field_111136_b = iattribute;
        this.field_111132_f = iattribute.func_111110_b();

        for (int i = 0; i < 3; ++i) {
            this.field_111137_c.put(i, new HashSet());
        }

    }

    @Override
    public IAttribute func_111123_a() {
        return this.field_111136_b;
    }

    @Override
    public double func_111125_b() {
        return this.field_111132_f;
    }

    @Override
    public void func_111128_a(double d0) {
        if (d0 != this.func_111125_b()) {
            this.field_111132_f = d0;
            this.func_111131_f();
        }
    }

    public Collection func_111130_a(int i) {
        return (Collection) this.field_111137_c.get(i);
    }

    @Override
    public Collection func_111122_c() {
        HashSet hashset = new HashSet();

        for (int i = 0; i < 3; ++i) {
            hashset.addAll(this.func_111130_a(i));
        }

        return hashset;
    }

    @Override
    public AttributeModifier func_111127_a(UUID uuid) {
        return (AttributeModifier) this.field_111135_e.get(uuid);
    }

    @Override
    public void func_111121_a(AttributeModifier attributemodifier) {
        if (this.func_111127_a(attributemodifier.func_111167_a()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            Object object = (Set) this.field_111134_d.get(attributemodifier.func_111166_b());

            if (object == null) {
                object = new HashSet();
                this.field_111134_d.put(attributemodifier.func_111166_b(), object);
            }

            ((Set) this.field_111137_c.get(attributemodifier.func_111169_c())).add(attributemodifier);
            ((Set) object).add(attributemodifier);
            this.field_111135_e.put(attributemodifier.func_111167_a(), attributemodifier);
            this.func_111131_f();
        }
    }

    private void func_111131_f() {
        this.field_111133_g = true;
        this.field_111138_a.func_111149_a(this);
    }

    @Override
    public void func_111124_b(AttributeModifier attributemodifier) {
        for (int i = 0; i < 3; ++i) {
            Set set = (Set) this.field_111137_c.get(i);

            set.remove(attributemodifier);
        }

        Set set1 = (Set) this.field_111134_d.get(attributemodifier.func_111166_b());

        if (set1 != null) {
            set1.remove(attributemodifier);
            if (set1.isEmpty()) {
                this.field_111134_d.remove(attributemodifier.func_111166_b());
            }
        }

        this.field_111135_e.remove(attributemodifier.func_111167_a());
        this.func_111131_f();
    }

    @Override
    public double func_111126_e() {
        if (this.field_111133_g) {
            this.field_111139_h = this.func_111129_g();
            this.field_111133_g = false;
        }

        return this.field_111139_h;
    }

    private double func_111129_g() {
        double d0 = this.func_111125_b();

        AttributeModifier attributemodifier;

        for (Iterator iterator = this.func_111130_a(0).iterator(); iterator.hasNext(); d0 += attributemodifier.func_111164_d()) {
            attributemodifier = (AttributeModifier) iterator.next();
        }

        double d1 = d0;

        AttributeModifier attributemodifier1;
        Iterator iterator1;

        for (iterator1 = this.func_111130_a(1).iterator(); iterator1.hasNext(); d1 += d0 * attributemodifier1.func_111164_d()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        for (iterator1 = this.func_111130_a(2).iterator(); iterator1.hasNext(); d1 *= 1.0D + attributemodifier1.func_111164_d()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        return this.field_111136_b.func_111109_a(d1);
    }
}
