package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemShears extends Item {

    public ItemShears() {
        this.func_77625_d(1);
        this.func_77656_e(238);
        this.func_77637_a(CreativeTabs.field_78040_i);
    }

    @Override
    public boolean func_150894_a(ItemStack itemstack, World world, Block block, int i, int j, int k, EntityLivingBase entitylivingbase) {
        if (block.func_149688_o() != Material.field_151584_j && block != Blocks.field_150321_G && block != Blocks.field_150329_H && block != Blocks.field_150395_bd && block != Blocks.field_150473_bD) {
            return super.func_150894_a(itemstack, world, block, i, j, k, entitylivingbase);
        } else {
            itemstack.func_77972_a(1, entitylivingbase);
            return true;
        }
    }

    @Override
    public boolean func_150897_b(Block block) {
        return block == Blocks.field_150321_G || block == Blocks.field_150488_af || block == Blocks.field_150473_bD;
    }

    @Override
    public float func_150893_a(ItemStack itemstack, Block block) {
        return block != Blocks.field_150321_G && block.func_149688_o() != Material.field_151584_j ? (block == Blocks.field_150325_L ? 5.0F : super.func_150893_a(itemstack, block)) : 15.0F;
    }
}
