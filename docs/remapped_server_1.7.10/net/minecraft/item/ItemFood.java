package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFood extends Item {

    public final int field_77855_a;
    private final int field_77853_b;
    private final float field_77854_c;
    private final boolean field_77856_bY;
    private boolean field_77852_bZ;
    private int field_77851_ca;
    private int field_77850_cb;
    private int field_77857_cc;
    private float field_77858_cd;

    public ItemFood(int i, float f, boolean flag) {
        this.field_77855_a = 32;
        this.field_77853_b = i;
        this.field_77856_bY = flag;
        this.field_77854_c = f;
        this.func_77637_a(CreativeTabs.field_78039_h);
    }

    public ItemFood(int i, boolean flag) {
        this(i, 0.6F, flag);
    }

    @Override
    public ItemStack func_77654_b(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        --itemstack.field_77994_a;
        entityplayer.func_71024_bL().func_151686_a(this, itemstack);
        world.func_72956_a(entityplayer, "random.burp", 0.5F, world.field_73012_v.nextFloat() * 0.1F + 0.9F);
        this.func_77849_c(itemstack, world, entityplayer);
        return itemstack;
    }

    protected void func_77849_c(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!world.field_72995_K && this.field_77851_ca > 0 && world.field_73012_v.nextFloat() < this.field_77858_cd) {
            entityplayer.func_70690_d(new PotionEffect(this.field_77851_ca, this.field_77850_cb * 20, this.field_77857_cc));
        }

    }

    @Override
    public int func_77626_a(ItemStack itemstack) {
        return 32;
    }

    @Override
    public EnumAction func_77661_b(ItemStack itemstack) {
        return EnumAction.eat;
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (entityplayer.func_71043_e(this.field_77852_bZ)) {
            entityplayer.func_71008_a(itemstack, this.func_77626_a(itemstack));
        }

        return itemstack;
    }

    public int func_150905_g(ItemStack itemstack) {
        return this.field_77853_b;
    }

    public float func_150906_h(ItemStack itemstack) {
        return this.field_77854_c;
    }

    public boolean func_77845_h() {
        return this.field_77856_bY;
    }

    public ItemFood func_77844_a(int i, int j, int k, float f) {
        this.field_77851_ca = i;
        this.field_77850_cb = j;
        this.field_77857_cc = k;
        this.field_77858_cd = f;
        return this;
    }

    public ItemFood func_77848_i() {
        this.field_77852_bZ = true;
        return this;
    }
}
