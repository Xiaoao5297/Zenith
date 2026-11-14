package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {

    private Entity field_76387_p;

    public EntityDamageSourceIndirect(String s, Entity entity, Entity entity1) {
        super(s, entity);
        this.field_76387_p = entity1;
    }

    @Override
    public Entity func_76364_f() {
        return this.field_76386_o;
    }

    @Override
    public Entity func_76346_g() {
        return this.field_76387_p;
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase entitylivingbase) {
        IChatComponent ichatcomponent = this.field_76387_p == null ? this.field_76386_o.func_145748_c_() : this.field_76387_p.func_145748_c_();
        ItemStack itemstack = this.field_76387_p instanceof EntityLivingBase ? ((EntityLivingBase) this.field_76387_p).func_70694_bm() : null;
        String s = "death.attack." + this.field_76373_n;
        String s1 = s + ".item";

        return itemstack != null && itemstack.func_82837_s() && StatCollector.func_94522_b(s1) ? new ChatComponentTranslation(s1, new Object[]{entitylivingbase.func_145748_c_(), ichatcomponent, itemstack.func_151000_E()}) : new ChatComponentTranslation(s, new Object[]{entitylivingbase.func_145748_c_(), ichatcomponent});
    }
}
