package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemAxe extends ItemTool {

    private static final Set field_150917_c = Sets.newHashSet((Object[])(Blocks.field_150344_f, Blocks.field_150342_X, Blocks.field_150364_r, Blocks.field_150363_s, Blocks.field_150486_ae, Blocks.field_150423_aK, Blocks.field_150428_aP));

    protected ItemAxe(Item$ToolMaterial item$toolmaterial) {
        super(3.0F, item$toolmaterial, ItemAxe.field_150917_c);
    }

    @Override
    public float func_150893_a(ItemStack itemstack, Block block) {
        return block.func_149688_o() != Material.field_151575_d && block.func_149688_o() != Material.field_151585_k && block.func_149688_o() != Material.field_151582_l ? super.func_150893_a(itemstack, block) : this.field_77864_a;
    }
}
