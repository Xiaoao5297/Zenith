package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {

    protected Entity field_76386_o;

    public EntityDamageSource(String s, Entity entity) {
        super(s);
        this.field_76386_o = entity;
    }

    @Override
    public Entity func_76346_g() {
        return this.field_76386_o;
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase entitylivingbase) {
        ItemStack itemstack = this.field_76386_o instanceof EntityLivingBase ? ((EntityLivingBase) this.field_76386_o).func_70694_bm() : null;
        String s = "death.attack." + this.field_76373_n;
        String s1 = s + ".item";

        return itemstack != null && itemstack.func_82837_s() && StatCollector.func_94522_b(s1) ? new ChatComponentTranslation(s1, new Object[]{entitylivingbase.func_145748_c_(), this.field_76386_o.func_145748_c_(), itemstack.func_151000_E()}) : new ChatComponentTranslation(s, new Object[]{entitylivingbase.func_145748_c_(), this.field_76386_o.func_145748_c_()});
    }

    @Override
    public boolean func_76350_n() {
        return this.field_76386_o != null && this.field_76386_o instanceof EntityLivingBase && !(this.field_76386_o instanceof EntityPlayer);
    }
}
