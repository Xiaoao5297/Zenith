package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {

    public ItemEnderEye() {
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        Block block = world.func_147439_a(i, j, k);
        int i1 = world.func_72805_g(i, j, k);

        if (entityplayer.func_82247_a(i, j, k, l, itemstack) && block == Blocks.field_150378_br && !BlockEndPortalFrame.func_150020_b(i1)) {
            if (world.field_72995_K) {
                return true;
            } else {
                world.func_72921_c(i, j, k, i1 + 4, 2);
                world.func_147453_f(i, j, k, Blocks.field_150378_br);
                --itemstack.field_77994_a;

                int j1;

                for (j1 = 0; j1 < 16; ++j1) {
                    double d0 = (double) ((float) i + (5.0F + ItemEnderEye.field_77697_d.nextFloat() * 6.0F) / 16.0F);
                    double d1 = (double) ((float) j + 0.8125F);
                    double d2 = (double) ((float) k + (5.0F + ItemEnderEye.field_77697_d.nextFloat() * 6.0F) / 16.0F);
                    double d3 = 0.0D;
                    double d4 = 0.0D;
                    double d5 = 0.0D;

                    world.func_72869_a("smoke", d0, d1, d2, d3, d4, d5);
                }

                j1 = i1 & 3;
                int k1 = 0;
                int l1 = 0;
                boolean flag = false;
                boolean flag1 = true;
                int i2 = Direction.field_71577_f[j1];

                int j2;
                int k2;
                int l2;

                for (k2 = -2; k2 <= 2; ++k2) {
                    j2 = i + Direction.field_71583_a[i2] * k2;
                    l2 = k + Direction.field_71581_b[i2] * k2;
                    if (world.func_147439_a(j2, j, l2) == Blocks.field_150378_br) {
                        if (!BlockEndPortalFrame.func_150020_b(world.func_72805_g(j2, j, l2))) {
                            flag1 = false;
                            break;
                        }

                        l1 = k2;
                        if (!flag) {
                            k1 = k2;
                            flag = true;
                        }
                    }
                }

                if (flag1 && l1 == k1 + 2) {
                    for (k2 = k1; k2 <= l1; ++k2) {
                        j2 = i + Direction.field_71583_a[i2] * k2;
                        l2 = k + Direction.field_71581_b[i2] * k2;
                        j2 += Direction.field_71583_a[j1] * 4;
                        l2 += Direction.field_71581_b[j1] * 4;
                        if (world.func_147439_a(j2, j, l2) != Blocks.field_150378_br || !BlockEndPortalFrame.func_150020_b(world.func_72805_g(j2, j, l2))) {
                            flag1 = false;
                            break;
                        }
                    }

                    int i3;

                    for (k2 = k1 - 1; k2 <= l1 + 1; k2 += 4) {
                        for (j2 = 1; j2 <= 3; ++j2) {
                            l2 = i + Direction.field_71583_a[i2] * k2;
                            i3 = k + Direction.field_71581_b[i2] * k2;
                            l2 += Direction.field_71583_a[j1] * j2;
                            i3 += Direction.field_71581_b[j1] * j2;
                            if (world.func_147439_a(l2, j, i3) != Blocks.field_150378_br || !BlockEndPortalFrame.func_150020_b(world.func_72805_g(l2, j, i3))) {
                                flag1 = false;
                                break;
                            }
                        }
                    }

                    if (flag1) {
                        for (k2 = k1; k2 <= l1; ++k2) {
                            for (j2 = 1; j2 <= 3; ++j2) {
                                l2 = i + Direction.field_71583_a[i2] * k2;
                                i3 = k + Direction.field_71581_b[i2] * k2;
                                l2 += Direction.field_71583_a[j1] * j2;
                                i3 += Direction.field_71581_b[j1] * j2;
                                world.func_147465_d(l2, j, i3, Blocks.field_150384_bq, 0, 2);
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        MovingObjectPosition movingobjectposition = this.func_77621_a(world, entityplayer, false);

        if (movingobjectposition != null && movingobjectposition.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK && world.func_147439_a(movingobjectposition.field_72311_b, movingobjectposition.field_72312_c, movingobjectposition.field_72309_d) == Blocks.field_150378_br) {
            return itemstack;
        } else {
            if (!world.field_72995_K) {
                ChunkPosition chunkposition = world.func_147440_b("Stronghold", (int) entityplayer.field_70165_t, (int) entityplayer.field_70163_u, (int) entityplayer.field_70161_v);

                if (chunkposition != null) {
                    EntityEnderEye entityendereye = new EntityEnderEye(world, entityplayer.field_70165_t, entityplayer.field_70163_u + 1.62D - (double) entityplayer.field_70129_M, entityplayer.field_70161_v);

                    entityendereye.func_70220_a((double) chunkposition.field_151329_a, chunkposition.field_151327_b, (double) chunkposition.field_151328_c);
                    world.func_72838_d(entityendereye);
                    world.func_72956_a(entityplayer, "random.bow", 0.5F, 0.4F / (ItemEnderEye.field_77697_d.nextFloat() * 0.4F + 0.8F));
                    world.func_72889_a((EntityPlayer) null, 1002, (int) entityplayer.field_70165_t, (int) entityplayer.field_70163_u, (int) entityplayer.field_70161_v, 0);
                    if (!entityplayer.field_71075_bZ.field_75098_d) {
                        --itemstack.field_77994_a;
                    }
                }
            }

            return itemstack;
        }
    }
}
