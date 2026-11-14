package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.World;

public class ItemTool extends Item {

    private Set field_150914_c;
    protected float field_77864_a = 4.0F;
    private float field_77865_bY;
    protected Item$ToolMaterial field_77862_b;

    protected ItemTool(float f, Item$ToolMaterial item$toolmaterial, Set set) {
        this.field_77862_b = item$toolmaterial;
        this.field_150914_c = set;
        this.field_77777_bU = 1;
        this.func_77656_e(item$toolmaterial.func_77997_a());
        this.field_77864_a = item$toolmaterial.func_77998_b();
        this.field_77865_bY = f + item$toolmaterial.func_78000_c();
        this.func_77637_a(CreativeTabs.field_78040_i);
    }

    @Override
    public float func_150893_a(ItemStack itemstack, Block block) {
        return this.field_150914_c.contains(block) ? this.field_77864_a : 1.0F;
    }

    @Override
    public boolean func_77644_a(ItemStack itemstack, EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        itemstack.func_77972_a(2, entitylivingbase1);
        return true;
    }

    @Override
    public boolean func_150894_a(ItemStack itemstack, World world, Block block, int i, int j, int k, EntityLivingBase entitylivingbase) {
        if ((double) block.func_149712_f(world, i, j, k) != 0.0D) {
            itemstack.func_77972_a(1, entitylivingbase);
        }

        return true;
    }

    public Item$ToolMaterial func_150913_i() {
        return this.field_77862_b;
    }

    @Override
    public int func_77619_b() {
        return this.field_77862_b.func_77995_e();
    }

    public String func_77861_e() {
        return this.field_77862_b.toString();
    }

    @Override
    public boolean func_82789_a(ItemStack itemstack, ItemStack itemstack1) {
        return this.field_77862_b.func_150995_f() == itemstack1.func_77973_b() ? true : super.func_82789_a(itemstack, itemstack1);
    }

    @Override
    public Multimap func_111205_h() {
        Multimap multimap = super.func_111205_h();

        multimap.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(ItemTool.field_111210_e, "Tool modifier", (double) this.field_77865_bY, 0));
        return multimap;
    }
}
