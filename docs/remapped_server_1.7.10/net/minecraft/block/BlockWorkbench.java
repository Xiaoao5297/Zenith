package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {

    protected BlockWorkbench() {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            entityplayer.func_71058_b(i, j, k);
            return true;
        }
    }
}
