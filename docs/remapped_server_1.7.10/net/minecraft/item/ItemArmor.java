package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemArmor extends Item {

    private static final int[] field_77882_bY = new int[]{11, 16, 15, 13};
    private static final String[] field_94606_cu = new String[]{"leather_helmet_overlay", "leather_chestplate_overlay", "leather_leggings_overlay", "leather_boots_overlay"};
    public static final String[] field_94603_a = new String[]{"empty_armor_slot_helmet", "empty_armor_slot_chestplate", "empty_armor_slot_leggings", "empty_armor_slot_boots"};
    private static final IBehaviorDispenseItem field_96605_cw = new ItemArmor$1();
    public final int field_77881_a;
    public final int field_77879_b;
    public final int field_77880_c;
    private final ItemArmor$ArmorMaterial field_77878_bZ;

    public ItemArmor(ItemArmor$ArmorMaterial itemarmor$armormaterial, int i, int j) {
        this.field_77878_bZ = itemarmor$armormaterial;
        this.field_77881_a = j;
        this.field_77880_c = i;
        this.field_77879_b = itemarmor$armormaterial.func_78044_b(j);
        this.func_77656_e(itemarmor$armormaterial.func_78046_a(j));
        this.field_77777_bU = 1;
        this.func_77637_a(CreativeTabs.field_78037_j);
        BlockDispenser.field_149943_a.func_82595_a(this, ItemArmor.field_96605_cw);
    }

    @Override
    public int func_77619_b() {
        return this.field_77878_bZ.func_78045_a();
    }

    public ItemArmor$ArmorMaterial func_82812_d() {
        return this.field_77878_bZ;
    }

    public boolean func_82816_b_(ItemStack itemstack) {
        return this.field_77878_bZ != ItemArmor$ArmorMaterial.CLOTH ? false : (!itemstack.func_77942_o() ? false : (!itemstack.func_77978_p().func_150297_b("display", 10) ? false : itemstack.func_77978_p().func_74775_l("display").func_150297_b("color", 3)));
    }

    public int func_82814_b(ItemStack itemstack) {
        if (this.field_77878_bZ != ItemArmor$ArmorMaterial.CLOTH) {
            return -1;
        } else {
            NBTTagCompound nbttagcompound = itemstack.func_77978_p();

            if (nbttagcompound == null) {
                return 10511680;
            } else {
                NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("display");

                return nbttagcompound1 == null ? 10511680 : (nbttagcompound1.func_150297_b("color", 3) ? nbttagcompound1.func_74762_e("color") : 10511680);
            }
        }
    }

    public void func_82815_c(ItemStack itemstack) {
        if (this.field_77878_bZ == ItemArmor$ArmorMaterial.CLOTH) {
            NBTTagCompound nbttagcompound = itemstack.func_77978_p();

            if (nbttagcompound != null) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("display");

                if (nbttagcompound1.func_74764_b("color")) {
                    nbttagcompound1.func_82580_o("color");
                }

            }
        }
    }

    public void func_82813_b(ItemStack itemstack, int i) {
        if (this.field_77878_bZ != ItemArmor$ArmorMaterial.CLOTH) {
            throw new UnsupportedOperationException("Can't dye non-leather!");
        } else {
            NBTTagCompound nbttagcompound = itemstack.func_77978_p();

            if (nbttagcompound == null) {
                nbttagcompound = new NBTTagCompound();
                itemstack.func_77982_d(nbttagcompound);
            }

            NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("display");

            if (!nbttagcompound.func_150297_b("display", 10)) {
                nbttagcompound.func_74782_a("display", nbttagcompound1);
            }

            nbttagcompound1.func_74768_a("color", i);
        }
    }

    @Override
    public boolean func_82789_a(ItemStack itemstack, ItemStack itemstack1) {
        return this.field_77878_bZ.func_151685_b() == itemstack1.func_77973_b() ? true : super.func_82789_a(itemstack, itemstack1);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        int i = EntityLiving.func_82159_b(itemstack) - 1;
        ItemStack itemstack1 = entityplayer.func_82169_q(i);

        if (itemstack1 == null) {
            entityplayer.func_70062_b(i, itemstack.func_77946_l());
            itemstack.field_77994_a = 0;
        }

        return itemstack;
    }
}
