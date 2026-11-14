package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemPickaxe extends ItemTool {

    private static final Set field_150915_c = Sets.newHashSet((Object[])(Blocks.field_150347_e, Blocks.field_150334_T, Blocks.field_150333_U, Blocks.field_150348_b, Blocks.field_150322_A, Blocks.field_150341_Y, Blocks.field_150366_p, Blocks.field_150339_S, Blocks.field_150365_q, Blocks.field_150340_R, Blocks.field_150352_o, Blocks.field_150482_ag, Blocks.field_150484_ah, Blocks.field_150432_aD, Blocks.field_150424_aL, Blocks.field_150369_x, Blocks.field_150368_y, Blocks.field_150450_ax, Blocks.field_150439_ay, Blocks.field_150448_aq, Blocks.field_150319_E, Blocks.field_150318_D, Blocks.field_150408_cc));

    protected ItemPickaxe(Item$ToolMaterial item$toolmaterial) {
        super(2.0F, item$toolmaterial, ItemPickaxe.field_150915_c);
    }

    @Override
    public boolean func_150897_b(Block block) {
        return block == Blocks.field_150343_Z ? this.field_77862_b.func_77996_d() == 3 : (block != Blocks.field_150484_ah && block != Blocks.field_150482_ag ? (block != Blocks.field_150412_bA && block != Blocks.field_150475_bE ? (block != Blocks.field_150340_R && block != Blocks.field_150352_o ? (block != Blocks.field_150339_S && block != Blocks.field_150366_p ? (block != Blocks.field_150368_y && block != Blocks.field_150369_x ? (block != Blocks.field_150450_ax && block != Blocks.field_150439_ay ? (block.func_149688_o() == Material.field_151576_e ? true : (block.func_149688_o() == Material.field_151573_f ? true : block.func_149688_o() == Material.field_151574_g)) : this.field_77862_b.func_77996_d() >= 2) : this.field_77862_b.func_77996_d() >= 1) : this.field_77862_b.func_77996_d() >= 1) : this.field_77862_b.func_77996_d() >= 2) : this.field_77862_b.func_77996_d() >= 2) : this.field_77862_b.func_77996_d() >= 2);
    }

    @Override
    public float func_150893_a(ItemStack itemstack, Block block) {
        return block.func_149688_o() != Material.field_151573_f && block.func_149688_o() != Material.field_151574_g && block.func_149688_o() != Material.field_151576_e ? super.func_150893_a(itemstack, block) : this.field_77864_a;
    }
}
