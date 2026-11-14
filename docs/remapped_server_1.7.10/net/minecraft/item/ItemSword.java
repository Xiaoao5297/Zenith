package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemSword extends Item {

    private float field_150934_a;
    private final Item$ToolMaterial field_150933_b;

    public ItemSword(Item$ToolMaterial item$toolmaterial) {
        this.field_150933_b = item$toolmaterial;
        this.field_77777_bU = 1;
        this.func_77656_e(item$toolmaterial.func_77997_a());
        this.func_77637_a(CreativeTabs.field_78037_j);
        this.field_150934_a = 4.0F + item$toolmaterial.func_78000_c();
    }

    public float func_150931_i() {
        return this.field_150933_b.func_78000_c();
    }

    @Override
    public float func_150893_a(ItemStack itemstack, Block block) {
        if (block == Blocks.field_150321_G) {
            return 15.0F;
        } else {
            Material material = block.func_149688_o();

            return material != Material.field_151585_k && material != Material.field_151582_l && material != Material.field_151589_v && material != Material.field_151584_j && material != Material.field_151572_C ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean func_77644_a(ItemStack itemstack, EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        itemstack.func_77972_a(1, entitylivingbase1);
        return true;
    }

    @Override
    public boolean func_150894_a(ItemStack itemstack, World world, Block block, int i, int j, int k, EntityLivingBase entitylivingbase) {
        if ((double) block.func_149712_f(world, i, j, k) != 0.0D) {
            itemstack.func_77972_a(2, entitylivingbase);
        }

        return true;
    }

    @Override
    public EnumAction func_77661_b(ItemStack itemstack) {
        return EnumAction.block;
    }

    @Override
    public int func_77626_a(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        entityplayer.func_71008_a(itemstack, this.func_77626_a(itemstack));
        return itemstack;
    }

    @Override
    public boolean func_150897_b(Block block) {
        return block == Blocks.field_150321_G;
    }

    @Override
    public int func_77619_b() {
        return this.field_150933_b.func_77995_e();
    }

    public String func_150932_j() {
        return this.field_150933_b.toString();
    }

    @Override
    public boolean func_82789_a(ItemStack itemstack, ItemStack itemstack1) {
        return this.field_150933_b.func_150995_f() == itemstack1.func_77973_b() ? true : super.func_82789_a(itemstack, itemstack1);
    }

    @Override
    public Multimap func_111205_h() {
        Multimap multimap = super.func_111205_h();

        multimap.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(ItemSword.field_111210_e, "Weapon modifier", (double) this.field_150934_a, 0));
        return multimap;
    }
}
