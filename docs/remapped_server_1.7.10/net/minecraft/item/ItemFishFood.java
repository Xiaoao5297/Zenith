package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {

    private final boolean field_150907_b;

    public ItemFishFood(boolean flag) {
        super(0, 0.0F, false);
        this.field_150907_b = flag;
    }

    @Override
    public int func_150905_g(ItemStack itemstack) {
        ItemFishFood$FishType itemfishfood$fishtype = ItemFishFood$FishType.func_150978_a(itemstack);

        return this.field_150907_b && itemfishfood$fishtype.func_150973_i() ? itemfishfood$fishtype.func_150970_e() : itemfishfood$fishtype.func_150975_c();
    }

    @Override
    public float func_150906_h(ItemStack itemstack) {
        ItemFishFood$FishType itemfishfood$fishtype = ItemFishFood$FishType.func_150978_a(itemstack);

        return this.field_150907_b && itemfishfood$fishtype.func_150973_i() ? itemfishfood$fishtype.func_150977_f() : itemfishfood$fishtype.func_150967_d();
    }

    @Override
    public String func_150896_i(ItemStack itemstack) {
        return ItemFishFood$FishType.func_150978_a(itemstack) == ItemFishFood$FishType.PUFFERFISH ? PotionHelper.field_151423_m : null;
    }

    @Override
    protected void func_77849_c(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        ItemFishFood$FishType itemfishfood$fishtype = ItemFishFood$FishType.func_150978_a(itemstack);

        if (itemfishfood$fishtype == ItemFishFood$FishType.PUFFERFISH) {
            entityplayer.func_70690_d(new PotionEffect(Potion.field_76436_u.field_76415_H, 1200, 3));
            entityplayer.func_70690_d(new PotionEffect(Potion.field_76438_s.field_76415_H, 300, 2));
            entityplayer.func_70690_d(new PotionEffect(Potion.field_76431_k.field_76415_H, 300, 1));
        }

        super.func_77849_c(itemstack, world, entityplayer);
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        ItemFishFood$FishType itemfishfood$fishtype = ItemFishFood$FishType.func_150978_a(itemstack);

        return this.func_77658_a() + "." + itemfishfood$fishtype.func_150972_b() + "." + (this.field_150907_b && itemfishfood$fishtype.func_150973_i() ? "cooked" : "raw");
    }
}
