package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityCow extends EntityAnimal {

    public EntityCow(World world) {
        super(world);
        this.func_70105_a(0.9F, 1.3F);
        this.func_70661_as().func_75491_a(true);
        this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
        this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 2.0D));
        this.field_70714_bg.func_75776_a(2, new EntityAIMate(this, 1.0D));
        this.field_70714_bg.func_75776_a(3, new EntityAITempt(this, 1.25D, Items.field_151015_O, false));
        this.field_70714_bg.func_75776_a(4, new EntityAIFollowParent(this, 1.25D));
        this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 1.0D));
        this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.field_70714_bg.func_75776_a(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean func_70650_aV() {
        return true;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
    }

    @Override
    protected String func_70639_aQ() {
        return "mob.cow.say";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.cow.hurt";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.cow.hurt";
    }

    @Override
    protected void func_145780_a(int i, int j, int k, Block block) {
        this.func_85030_a("mob.cow.step", 0.15F, 1.0F);
    }

    @Override
    protected float func_70599_aP() {
        return 0.4F;
    }

    @Override
    protected Item func_146068_u() {
        return Items.field_151116_aA;
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        int j = this.field_70146_Z.nextInt(3) + this.field_70146_Z.nextInt(1 + i);

        int k;

        for (k = 0; k < j; ++k) {
            this.func_145779_a(Items.field_151116_aA, 1);
        }

        j = this.field_70146_Z.nextInt(3) + 1 + this.field_70146_Z.nextInt(1 + i);

        for (k = 0; k < j; ++k) {
            if (this.func_70027_ad()) {
                this.func_145779_a(Items.field_151083_be, 1);
            } else {
                this.func_145779_a(Items.field_151082_bd, 1);
            }
        }

    }

    @Override
    public boolean func_70085_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (itemstack != null && itemstack.func_77973_b() == Items.field_151133_ar && !entityplayer.field_71075_bZ.field_75098_d) {
            if (itemstack.field_77994_a-- == 1) {
                entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, new ItemStack(Items.field_151117_aB));
            } else if (!entityplayer.field_71071_by.func_70441_a(new ItemStack(Items.field_151117_aB))) {
                entityplayer.func_71019_a(new ItemStack(Items.field_151117_aB, 1, 0), false);
            }

            return true;
        } else {
            return super.func_70085_c(entityplayer);
        }
    }

    @Override
    public EntityCow func_90011_a(EntityAgeable entityageable) {
        return new EntityCow(this.field_70170_p);
    }
}
