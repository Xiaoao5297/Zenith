package net.minecraft.item;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemRecord extends Item {

    private static final Map field_150928_b = new HashMap();
    public final String field_150929_a;

    protected ItemRecord(String s) {
        this.field_150929_a = s;
        this.field_77777_bU = 1;
        this.func_77637_a(CreativeTabs.field_78026_f);
        ItemRecord.field_150928_b.put(s, this);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (world.func_147439_a(i, j, k) == Blocks.field_150421_aI && world.func_72805_g(i, j, k) == 0) {
            if (world.field_72995_K) {
                return true;
            } else {
                ((BlockJukebox) Blocks.field_150421_aI).func_149926_b(world, i, j, k, itemstack);
                world.func_72889_a((EntityPlayer) null, 1005, i, j, k, Item.func_150891_b(this));
                --itemstack.field_77994_a;
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public EnumRarity func_77613_e(ItemStack itemstack) {
        return EnumRarity.rare;
    }
}
