package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {

    protected BlockDoor(Material material) {
        super(material);
        float f = 0.5F;
        float f1 = 1.0F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = this.func_150012_g(iblockaccess, i, j, k);

        return (l & 4) != 0;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 7;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        return super.func_149668_a(world, i, j, k);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_150011_b(this.func_150012_g(iblockaccess, i, j, k));
    }

    public int func_150013_e(IBlockAccess iblockaccess, int i, int j, int k) {
        return this.func_150012_g(iblockaccess, i, j, k) & 3;
    }

    public boolean func_150015_f(IBlockAccess iblockaccess, int i, int j, int k) {
        return (this.func_150012_g(iblockaccess, i, j, k) & 4) != 0;
    }

    private void func_150011_b(int i) {
        float f = 0.1875F;

        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int j = i & 3;
        boolean flag = (i & 4) != 0;
        boolean flag1 = (i & 16) != 0;

        if (j == 0) {
            if (flag) {
                if (!flag1) {
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                } else {
                    this.func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            } else {
                this.func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        } else if (j == 1) {
            if (flag) {
                if (!flag1) {
                    this.func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            } else {
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
        } else if (j == 2) {
            if (flag) {
                if (!flag1) {
                    this.func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                } else {
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            } else {
                this.func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        } else if (j == 3) {
            if (flag) {
                if (!flag1) {
                    this.func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                } else {
                    this.func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            } else {
                this.func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
        }

    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {}

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (this.field_149764_J == Material.field_151573_f) {
            return true;
        } else {
            int i1 = this.func_150012_g(world, i, j, k);
            int j1 = i1 & 7;

            j1 ^= 4;
            if ((i1 & 8) == 0) {
                world.func_72921_c(i, j, k, j1, 2);
                world.func_147458_c(i, j, k, i, j, k);
            } else {
                world.func_72921_c(i, j - 1, k, j1, 2);
                world.func_147458_c(i, j - 1, k, i, j, k);
            }

            world.func_72889_a(entityplayer, 1003, i, j, k, 0);
            return true;
        }
    }

    public void func_150014_a(World world, int i, int j, int k, boolean flag) {
        int l = this.func_150012_g(world, i, j, k);
        boolean flag1 = (l & 4) != 0;

        if (flag1 != flag) {
            int i1 = l & 7;

            i1 ^= 4;
            if ((l & 8) == 0) {
                world.func_72921_c(i, j, k, i1, 2);
                world.func_147458_c(i, j, k, i, j, k);
            } else {
                world.func_72921_c(i, j - 1, k, i1, 2);
                world.func_147458_c(i, j - 1, k, i, j, k);
            }

            world.func_72889_a((EntityPlayer) null, 1003, i, j, k, 0);
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        int l = world.func_72805_g(i, j, k);

        if ((l & 8) == 0) {
            boolean flag = false;

            if (world.func_147439_a(i, j + 1, k) != this) {
                world.func_147468_f(i, j, k);
                flag = true;
            }

            if (!World.func_147466_a(world, i, j - 1, k)) {
                world.func_147468_f(i, j, k);
                flag = true;
                if (world.func_147439_a(i, j + 1, k) == this) {
                    world.func_147468_f(i, j + 1, k);
                }
            }

            if (flag) {
                if (!world.field_72995_K) {
                    this.func_149697_b(world, i, j, k, l, 0);
                }
            } else {
                boolean flag1 = world.func_72864_z(i, j, k) || world.func_72864_z(i, j + 1, k);

                if ((flag1 || block.func_149744_f()) && block != this) {
                    this.func_150014_a(world, i, j, k, flag1);
                }
            }
        } else {
            if (world.func_147439_a(i, j - 1, k) != this) {
                world.func_147468_f(i, j, k);
            }

            if (block != this) {
                this.func_149695_a(world, i, j - 1, k, block);
            }
        }

    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return (i & 8) != 0 ? null : (this.field_149764_J == Material.field_151573_f ? Items.field_151139_aw : Items.field_151135_aq);
    }

    @Override
    public MovingObjectPosition func_149731_a(World world, int i, int j, int k, Vec3 vec3, Vec3 vec31) {
        this.func_149719_a(world, i, j, k);
        return super.func_149731_a(world, i, j, k, vec3, vec31);
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return j >= 255 ? false : World.func_147466_a(world, i, j - 1, k) && super.func_149742_c(world, i, j, k) && super.func_149742_c(world, i, j + 1, k);
    }

    @Override
    public int func_149656_h() {
        return 1;
    }

    public int func_150012_g(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        boolean flag = (l & 8) != 0;
        int i1;
        int j1;

        if (flag) {
            i1 = iblockaccess.func_72805_g(i, j - 1, k);
            j1 = l;
        } else {
            i1 = l;
            j1 = iblockaccess.func_72805_g(i, j + 1, k);
        }

        boolean flag1 = (j1 & 1) != 0;

        return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
    }

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        if (entityplayer.field_71075_bZ.field_75098_d && (l & 8) != 0 && world.func_147439_a(i, j - 1, k) == this) {
            world.func_147468_f(i, j - 1, k);
        }

    }
}
