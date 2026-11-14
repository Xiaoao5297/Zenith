package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {

    public BlockTNT() {
        super(Material.field_151590_u);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        if (world.func_72864_z(i, j, k)) {
            this.func_149664_b(world, i, j, k, 1);
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (world.func_72864_z(i, j, k)) {
            this.func_149664_b(world, i, j, k, 1);
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public int func_149745_a(Random random) {
        return 1;
    }

    @Override
    public void func_149723_a(World world, int i, int j, int k, Explosion explosion) {
        if (!world.field_72995_K) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), explosion.func_94613_c());

            entitytntprimed.field_70516_a = world.field_73012_v.nextInt(entitytntprimed.field_70516_a / 4) + entitytntprimed.field_70516_a / 8;
            world.func_72838_d(entitytntprimed);
        }
    }

    @Override
    public void func_149664_b(World world, int i, int j, int k, int l) {
        this.func_150114_a(world, i, j, k, l, (EntityLivingBase) null);
    }

    public void func_150114_a(World world, int i, int j, int k, int l, EntityLivingBase entitylivingbase) {
        if (!world.field_72995_K) {
            if ((l & 1) == 1) {
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), entitylivingbase);

                world.func_72838_d(entitytntprimed);
                world.func_72956_a(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
            }

        }
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151033_d) {
            this.func_150114_a(world, i, j, k, 1, entityplayer);
            world.func_147468_f(i, j, k);
            entityplayer.func_71045_bC().func_77972_a(1, entityplayer);
            return true;
        } else {
            return super.func_149727_a(world, i, j, k, entityplayer, l, f, f1, f2);
        }
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (entity instanceof EntityArrow && !world.field_72995_K) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.func_70027_ad()) {
                this.func_150114_a(world, i, j, k, 1, entityarrow.field_70250_c instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.field_70250_c : null);
                world.func_147468_f(i, j, k);
            }
        }

    }

    @Override
    public boolean func_149659_a(Explosion explosion) {
        return false;
    }
}
