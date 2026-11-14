package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {

    protected final Map field_111154_a = new HashMap();
    protected final Map field_111153_b = new LowerStringMap();

    public BaseAttributeMap() {}

    public IAttributeInstance func_111151_a(IAttribute iattribute) {
        return (IAttributeInstance) this.field_111154_a.get(iattribute);
    }

    public IAttributeInstance func_111152_a(String s) {
        return (IAttributeInstance) this.field_111153_b.get(s);
    }

    public abstract IAttributeInstance func_111150_b(IAttribute iattribute);

    public Collection func_111146_a() {
        return this.field_111153_b.values();
    }

    public void func_111149_a(ModifiableAttributeInstance modifiableattributeinstance) {}

    public void func_111148_a(Multimap multimap) {
        Iterator iterator = multimap.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            IAttributeInstance iattributeinstance = this.func_111152_a((String) entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.func_111124_b((AttributeModifier) entry.getValue());
            }
        }

    }

    public void func_111147_b(Multimap multimap) {
        Iterator iterator = multimap.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            IAttributeInstance iattributeinstance = this.func_111152_a((String) entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.func_111124_b((AttributeModifier) entry.getValue());
                iattributeinstance.func_111121_a((AttributeModifier) entry.getValue());
            }
        }

    }
}
