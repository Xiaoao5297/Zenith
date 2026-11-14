package net.minecraft.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {

    private static final Logger field_151476_f = LogManager.getLogger();
    public static final IAttribute field_111267_a = (new RangedAttribute("generic.maxHealth", 20.0D, 0.0D, Double.MAX_VALUE)).func_111117_a("Max Health").func_111112_a(true);
    public static final IAttribute field_111265_b = (new RangedAttribute("generic.followRange", 32.0D, 0.0D, 2048.0D)).func_111117_a("Follow Range");
    public static final IAttribute field_111266_c = (new RangedAttribute("generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).func_111117_a("Knockback Resistance");
    public static final IAttribute field_111263_d = (new RangedAttribute("generic.movementSpeed", 0.699999988079071D, 0.0D, Double.MAX_VALUE)).func_111117_a("Movement Speed").func_111112_a(true);
    public static final IAttribute field_111264_e = new RangedAttribute("generic.attackDamage", 2.0D, 0.0D, Double.MAX_VALUE);

    public static NBTTagList func_111257_a(BaseAttributeMap baseattributemap) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = baseattributemap.func_111146_a().iterator();

        while (iterator.hasNext()) {
            IAttributeInstance iattributeinstance = (IAttributeInstance) iterator.next();

            nbttaglist.func_74742_a(func_111261_a(iattributeinstance));
        }

        return nbttaglist;
    }

    private static NBTTagCompound func_111261_a(IAttributeInstance iattributeinstance) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        IAttribute iattribute = iattributeinstance.func_111123_a();

        nbttagcompound.func_74778_a("Name", iattribute.func_111108_a());
        nbttagcompound.func_74780_a("Base", iattributeinstance.func_111125_b());
        Collection collection = iattributeinstance.func_111122_c();

        if (collection != null && !collection.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

                if (attributemodifier.func_111165_e()) {
                    nbttaglist.func_74742_a(func_111262_a(attributemodifier));
                }
            }

            nbttagcompound.func_74782_a("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    private static NBTTagCompound func_111262_a(AttributeModifier attributemodifier) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.func_74778_a("Name", attributemodifier.func_111166_b());
        nbttagcompound.func_74780_a("Amount", attributemodifier.func_111164_d());
        nbttagcompound.func_74768_a("Operation", attributemodifier.func_111169_c());
        nbttagcompound.func_74772_a("UUIDMost", attributemodifier.func_111167_a().getMostSignificantBits());
        nbttagcompound.func_74772_a("UUIDLeast", attributemodifier.func_111167_a().getLeastSignificantBits());
        return nbttagcompound;
    }

    public static void func_151475_a(BaseAttributeMap baseattributemap, NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
            IAttributeInstance iattributeinstance = baseattributemap.func_111152_a(nbttagcompound.func_74779_i("Name"));

            if (iattributeinstance != null) {
                func_111258_a(iattributeinstance, nbttagcompound);
            } else {
                SharedMonsterAttributes.field_151476_f.warn("Ignoring unknown attribute '" + nbttagcompound.func_74779_i("Name") + "'");
            }
        }

    }

    private static void func_111258_a(IAttributeInstance iattributeinstance, NBTTagCompound nbttagcompound) {
        iattributeinstance.func_111128_a(nbttagcompound.func_74769_h("Base"));
        if (nbttagcompound.func_150297_b("Modifiers", 9)) {
            NBTTagList nbttaglist = nbttagcompound.func_150295_c("Modifiers", 10);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                AttributeModifier attributemodifier = func_111259_a(nbttaglist.func_150305_b(i));
                AttributeModifier attributemodifier1 = iattributeinstance.func_111127_a(attributemodifier.func_111167_a());

                if (attributemodifier1 != null) {
                    iattributeinstance.func_111124_b(attributemodifier1);
                }

                iattributeinstance.func_111121_a(attributemodifier);
            }
        }

    }

    public static AttributeModifier func_111259_a(NBTTagCompound nbttagcompound) {
        UUID uuid = new UUID(nbttagcompound.func_74763_f("UUIDMost"), nbttagcompound.func_74763_f("UUIDLeast"));

        return new AttributeModifier(uuid, nbttagcompound.func_74779_i("Name"), nbttagcompound.func_74769_h("Amount"), nbttagcompound.func_74762_e("Operation"));
    }
}
