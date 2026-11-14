package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings$GameType;

public class ItemInWorldManager {

    public World field_73092_a;
    public EntityPlayerMP field_73090_b;
    private WorldSettings$GameType field_73091_c;
    private boolean field_73088_d;
    private int field_73089_e;
    private int field_73086_f;
    private int field_73087_g;
    private int field_73099_h;
    private int field_73100_i;
    private boolean field_73097_j;
    private int field_73098_k;
    private int field_73095_l;
    private int field_73096_m;
    private int field_73093_n;
    private int field_73094_o;

    public ItemInWorldManager(World world) {
        this.field_73091_c = WorldSettings$GameType.NOT_SET;
        this.field_73094_o = -1;
        this.field_73092_a = world;
    }

    public void func_73076_a(WorldSettings$GameType worldsettings$gametype) {
        this.field_73091_c = worldsettings$gametype;
        worldsettings$gametype.func_77147_a(this.field_73090_b.field_71075_bZ);
        this.field_73090_b.func_71016_p();
    }

    public WorldSettings$GameType func_73081_b() {
        return this.field_73091_c;
    }

    public boolean func_73083_d() {
        return this.field_73091_c.func_77145_d();
    }

    public void func_73077_b(WorldSettings$GameType worldsettings$gametype) {
        if (this.field_73091_c == WorldSettings$GameType.NOT_SET) {
            this.field_73091_c = worldsettings$gametype;
        }

        this.func_73076_a(this.field_73091_c);
    }

    public void func_73075_a() {
        ++this.field_73100_i;
        float f;
        int i;

        if (this.field_73097_j) {
            int j = this.field_73100_i - this.field_73093_n;
            Block block = this.field_73092_a.func_147439_a(this.field_73098_k, this.field_73095_l, this.field_73096_m);

            if (block.func_149688_o() == Material.field_151579_a) {
                this.field_73097_j = false;
            } else {
                f = block.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_73098_k, this.field_73095_l, this.field_73096_m) * (float) (j + 1);
                i = (int) (f * 10.0F);
                if (i != this.field_73094_o) {
                    this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73098_k, this.field_73095_l, this.field_73096_m, i);
                    this.field_73094_o = i;
                }

                if (f >= 1.0F) {
                    this.field_73097_j = false;
                    this.func_73084_b(this.field_73098_k, this.field_73095_l, this.field_73096_m);
                }
            }
        } else if (this.field_73088_d) {
            Block block1 = this.field_73092_a.func_147439_a(this.field_73086_f, this.field_73087_g, this.field_73099_h);

            if (block1.func_149688_o() == Material.field_151579_a) {
                this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73086_f, this.field_73087_g, this.field_73099_h, -1);
                this.field_73094_o = -1;
                this.field_73088_d = false;
            } else {
                int k = this.field_73100_i - this.field_73089_e;

                f = block1.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_73086_f, this.field_73087_g, this.field_73099_h) * (float) (k + 1);
                i = (int) (f * 10.0F);
                if (i != this.field_73094_o) {
                    this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73086_f, this.field_73087_g, this.field_73099_h, i);
                    this.field_73094_o = i;
                }
            }
        }

    }

    public void func_73074_a(int i, int j, int k, int l) {
        if (!this.field_73091_c.func_82752_c() || this.field_73090_b.func_82246_f(i, j, k)) {
            if (this.func_73083_d()) {
                if (!this.field_73092_a.func_72886_a((EntityPlayer) null, i, j, k, l)) {
                    this.func_73084_b(i, j, k);
                }

            } else {
                this.field_73092_a.func_72886_a((EntityPlayer) null, i, j, k, l);
                this.field_73089_e = this.field_73100_i;
                float f = 1.0F;
                Block block = this.field_73092_a.func_147439_a(i, j, k);

                if (block.func_149688_o() != Material.field_151579_a) {
                    block.func_149699_a(this.field_73092_a, i, j, k, this.field_73090_b);
                    f = block.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, i, j, k);
                }

                if (block.func_149688_o() != Material.field_151579_a && f >= 1.0F) {
                    this.func_73084_b(i, j, k);
                } else {
                    this.field_73088_d = true;
                    this.field_73086_f = i;
                    this.field_73087_g = j;
                    this.field_73099_h = k;
                    int i1 = (int) (f * 10.0F);

                    this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), i, j, k, i1);
                    this.field_73094_o = i1;
                }

            }
        }
    }

    public void func_73082_a(int i, int j, int k) {
        if (i == this.field_73086_f && j == this.field_73087_g && k == this.field_73099_h) {
            int l = this.field_73100_i - this.field_73089_e;
            Block block = this.field_73092_a.func_147439_a(i, j, k);

            if (block.func_149688_o() != Material.field_151579_a) {
                float f = block.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, i, j, k) * (float) (l + 1);

                if (f >= 0.7F) {
                    this.field_73088_d = false;
                    this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), i, j, k, -1);
                    this.func_73084_b(i, j, k);
                } else if (!this.field_73097_j) {
                    this.field_73088_d = false;
                    this.field_73097_j = true;
                    this.field_73098_k = i;
                    this.field_73095_l = j;
                    this.field_73096_m = k;
                    this.field_73093_n = this.field_73089_e;
                }
            }
        }

    }

    public void func_73073_c(int i, int j, int k) {
        this.field_73088_d = false;
        this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73086_f, this.field_73087_g, this.field_73099_h, -1);
    }

    private boolean func_73079_d(int i, int j, int k) {
        Block block = this.field_73092_a.func_147439_a(i, j, k);
        int l = this.field_73092_a.func_72805_g(i, j, k);

        block.func_149681_a(this.field_73092_a, i, j, k, l, this.field_73090_b);
        boolean flag = this.field_73092_a.func_147468_f(i, j, k);

        if (flag) {
            block.func_149664_b(this.field_73092_a, i, j, k, l);
        }

        return flag;
    }

    public boolean func_73084_b(int i, int j, int k) {
        if (this.field_73091_c.func_82752_c() && !this.field_73090_b.func_82246_f(i, j, k)) {
            return false;
        } else if (this.field_73091_c.func_77145_d() && this.field_73090_b.func_70694_bm() != null && this.field_73090_b.func_70694_bm().func_77973_b() instanceof ItemSword) {
            return false;
        } else {
            Block block = this.field_73092_a.func_147439_a(i, j, k);
            int l = this.field_73092_a.func_72805_g(i, j, k);

            this.field_73092_a.func_72889_a(this.field_73090_b, 2001, i, j, k, Block.func_149682_b(block) + (this.field_73092_a.func_72805_g(i, j, k) << 12));
            boolean flag = this.func_73079_d(i, j, k);

            if (this.func_73083_d()) {
                this.field_73090_b.field_71135_a.func_147359_a(new S23PacketBlockChange(i, j, k, this.field_73092_a));
            } else {
                ItemStack itemstack = this.field_73090_b.func_71045_bC();
                boolean flag1 = this.field_73090_b.func_146099_a(block);

                if (itemstack != null) {
                    itemstack.func_150999_a(this.field_73092_a, block, i, j, k, this.field_73090_b);
                    if (itemstack.field_77994_a == 0) {
                        this.field_73090_b.func_71028_bD();
                    }
                }

                if (flag && flag1) {
                    block.func_149636_a(this.field_73092_a, this.field_73090_b, i, j, k, l);
                }
            }

            return flag;
        }
    }

    public boolean func_73085_a(EntityPlayer entityplayer, World world, ItemStack itemstack) {
        int i = itemstack.field_77994_a;
        int j = itemstack.func_77960_j();
        ItemStack itemstack1 = itemstack.func_77957_a(world, entityplayer);

        if (itemstack1 == itemstack && (itemstack1 == null || itemstack1.field_77994_a == i && itemstack1.func_77988_m() <= 0 && itemstack1.func_77960_j() == j)) {
            return false;
        } else {
            entityplayer.field_71071_by.field_70462_a[entityplayer.field_71071_by.field_70461_c] = itemstack1;
            if (this.func_73083_d()) {
                itemstack1.field_77994_a = i;
                if (itemstack1.func_77984_f()) {
                    itemstack1.func_77964_b(j);
                }
            }

            if (itemstack1.field_77994_a == 0) {
                entityplayer.field_71071_by.field_70462_a[entityplayer.field_71071_by.field_70461_c] = null;
            }

            if (!entityplayer.func_71039_bw()) {
                ((EntityPlayerMP) entityplayer).func_71120_a(entityplayer.field_71069_bz);
            }

            return true;
        }
    }

    public boolean func_73078_a(EntityPlayer entityplayer, World world, ItemStack itemstack, int i, int j, int k, int l, float f, float f1, float f2) {
        if ((!entityplayer.func_70093_af() || entityplayer.func_70694_bm() == null) && world.func_147439_a(i, j, k).func_149727_a(world, i, j, k, entityplayer, l, f, f1, f2)) {
            return true;
        } else if (itemstack == null) {
            return false;
        } else if (this.func_73083_d()) {
            int i1 = itemstack.func_77960_j();
            int j1 = itemstack.field_77994_a;
            boolean flag = itemstack.func_77943_a(entityplayer, world, i, j, k, l, f, f1, f2);

            itemstack.func_77964_b(i1);
            itemstack.field_77994_a = j1;
            return flag;
        } else {
            return itemstack.func_77943_a(entityplayer, world, i, j, k, l, f, f1, f2);
        }
    }

    public void func_73080_a(WorldServer worldserver) {
        this.field_73092_a = worldserver;
    }
}
