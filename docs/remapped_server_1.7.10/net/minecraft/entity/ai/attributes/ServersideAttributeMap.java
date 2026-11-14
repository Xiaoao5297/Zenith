package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {

    private final Set field_111162_d = Sets.newHashSet();
    protected final Map field_111163_c = new LowerStringMap();

    public ServersideAttributeMap() {}

    @Override
    public ModifiableAttributeInstance func_111151_a(IAttribute iattribute) {
        return (ModifiableAttributeInstance) super.func_111151_a(iattribute);
    }

    @Override
    public ModifiableAttributeInstance func_111152_a(String s) {
        IAttributeInstance iattributeinstance = super.func_111152_a(s);

        if (iattributeinstance == null) {
            iattributeinstance = (IAttributeInstance) this.field_111163_c.get(s);
        }

        return (ModifiableAttributeInstance) iattributeinstance;
    }

    @Override
    public IAttributeInstance func_111150_b(IAttribute iattribute) {
        if (this.field_111153_b.containsKey(iattribute.func_111108_a())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        } else {
            ModifiableAttributeInstance modifiableattributeinstance = new ModifiableAttributeInstance(this, iattribute);

            this.field_111153_b.put(iattribute.func_111108_a(), modifiableattributeinstance);
            if (iattribute instanceof RangedAttribute && ((RangedAttribute) iattribute).func_111116_f() != null) {
                this.field_111163_c.put(((RangedAttribute) iattribute).func_111116_f(), modifiableattributeinstance);
            }

            this.field_111154_a.put(iattribute, modifiableattributeinstance);
            return modifiableattributeinstance;
        }
    }

    @Override
    public void func_111149_a(ModifiableAttributeInstance modifiableattributeinstance) {
        if (modifiableattributeinstance.func_111123_a().func_111111_c()) {
            this.field_111162_d.add(modifiableattributeinstance);
        }

    }

    public Set func_111161_b() {
        return this.field_111162_d;
    }

    public Collection func_111160_c() {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = this.func_111146_a().iterator();

        while (iterator.hasNext()) {
            IAttributeInstance iattributeinstance = (IAttributeInstance) iterator.next();

            if (iattributeinstance.func_111123_a().func_111111_c()) {
                hashset.add(iattributeinstance);
            }
        }

        return hashset;
    }
}
