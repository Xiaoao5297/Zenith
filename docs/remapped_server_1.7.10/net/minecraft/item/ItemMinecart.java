package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemMinecart extends Item {

    private static final IBehaviorDispenseItem field_96602_b = new ItemMinecart$1();
    public int field_77841_a;

    public ItemMinecart(int i) {
        this.field_77777_bU = 1;
        this.field_77841_a = i;
        this.func_77637_a(CreativeTabs.field_78029_e);
        BlockDispenser.field_149943_a.func_82595_a(this, ItemMinecart.field_96602_b);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (BlockRailBase.func_150051_a(world.func_147439_a(i, j, k))) {
            if (!world.field_72995_K) {
                EntityMinecart entityminecart = EntityMinecart.func_94090_a(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.field_77841_a);

                if (itemstack.func_82837_s()) {
                    entityminecart.func_96094_a(itemstack.func_82833_r());
                }

                world.func_72838_d(entityminecart);
            }

            --itemstack.field_77994_a;
            return true;
        } else {
            return false;
        }
    }
}
