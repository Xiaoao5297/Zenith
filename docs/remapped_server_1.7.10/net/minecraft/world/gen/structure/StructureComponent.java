package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public abstract class StructureComponent {

    protected StructureBoundingBox field_74887_e;
    protected int field_74885_f;
    protected int field_74886_g;

    public StructureComponent() {}

    protected StructureComponent(int i) {
        this.field_74886_g = i;
        this.field_74885_f = -1;
    }

    public NBTTagCompound func_143010_b() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.func_74778_a("id", MapGenStructureIO.func_143036_a(this));
        nbttagcompound.func_74782_a("BB", this.field_74887_e.func_151535_h());
        nbttagcompound.func_74768_a("O", this.field_74885_f);
        nbttagcompound.func_74768_a("GD", this.field_74886_g);
        this.func_143012_a(nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void func_143012_a(NBTTagCompound nbttagcompound);

    public void func_143009_a(World world, NBTTagCompound nbttagcompound) {
        if (nbttagcompound.func_74764_b("BB")) {
            this.field_74887_e = new StructureBoundingBox(nbttagcompound.func_74759_k("BB"));
        }

        this.field_74885_f = nbttagcompound.func_74762_e("O");
        this.field_74886_g = nbttagcompound.func_74762_e("GD");
        this.func_143011_b(nbttagcompound);
    }

    protected abstract void func_143011_b(NBTTagCompound nbttagcompound);

    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {}

    public abstract boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox);

    public StructureBoundingBox func_74874_b() {
        return this.field_74887_e;
    }

    public int func_74877_c() {
        return this.field_74886_g;
    }

    public static StructureComponent func_74883_a(List list, StructureBoundingBox structureboundingbox) {
        Iterator iterator = list.iterator();

        StructureComponent structurecomponent;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            structurecomponent = (StructureComponent) iterator.next();
        } while (structurecomponent.func_74874_b() == null || !structurecomponent.func_74874_b().func_78884_a(structureboundingbox));

        return structurecomponent;
    }

    public ChunkPosition func_151553_a() {
        return new ChunkPosition(this.field_74887_e.func_78881_e(), this.field_74887_e.func_78879_f(), this.field_74887_e.func_78891_g());
    }

    protected boolean func_74860_a(World world, StructureBoundingBox structureboundingbox) {
        int i = Math.max(this.field_74887_e.field_78897_a - 1, structureboundingbox.field_78897_a);
        int j = Math.max(this.field_74887_e.field_78895_b - 1, structureboundingbox.field_78895_b);
        int k = Math.max(this.field_74887_e.field_78896_c - 1, structureboundingbox.field_78896_c);
        int l = Math.min(this.field_74887_e.field_78893_d + 1, structureboundingbox.field_78893_d);
        int i1 = Math.min(this.field_74887_e.field_78894_e + 1, structureboundingbox.field_78894_e);
        int j1 = Math.min(this.field_74887_e.field_78892_f + 1, structureboundingbox.field_78892_f);

        int k1;
        int l1;

        for (k1 = i; k1 <= l; ++k1) {
            for (l1 = k; l1 <= j1; ++l1) {
                if (world.func_147439_a(k1, j, l1).func_149688_o().func_76224_d()) {
                    return true;
                }

                if (world.func_147439_a(k1, i1, l1).func_149688_o().func_76224_d()) {
                    return true;
                }
            }
        }

        for (k1 = i; k1 <= l; ++k1) {
            for (l1 = j; l1 <= i1; ++l1) {
                if (world.func_147439_a(k1, l1, k).func_149688_o().func_76224_d()) {
                    return true;
                }

                if (world.func_147439_a(k1, l1, j1).func_149688_o().func_76224_d()) {
                    return true;
                }
            }
        }

        for (k1 = k; k1 <= j1; ++k1) {
            for (l1 = j; l1 <= i1; ++l1) {
                if (world.func_147439_a(i, l1, k1).func_149688_o().func_76224_d()) {
                    return true;
                }

                if (world.func_147439_a(l, l1, k1).func_149688_o().func_76224_d()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected int func_74865_a(int i, int j) {
        switch (this.field_74885_f) {
            case 0:
            case 2:
                return this.field_74887_e.field_78897_a + i;
            case 1:
                return this.field_74887_e.field_78893_d - j;
            case 3:
                return this.field_74887_e.field_78897_a + j;
            default:
                return i;
        }
    }

    protected int func_74862_a(int i) {
        return this.field_74885_f == -1 ? i : i + this.field_74887_e.field_78895_b;
    }

    protected int func_74873_b(int i, int j) {
        switch (this.field_74885_f) {
            case 0:
                return this.field_74887_e.field_78896_c + j;
            case 1:
            case 3:
                return this.field_74887_e.field_78896_c + i;
            case 2:
                return this.field_74887_e.field_78892_f - j;
            default:
                return j;
        }
    }

    protected int func_151555_a(Block block, int i) {
        if (block == Blocks.field_150448_aq) {
            if (this.field_74885_f == 1 || this.field_74885_f == 3) {
                if (i == 1) {
                    return 0;
                }

                return 1;
            }
        } else if (block != Blocks.field_150466_ao && block != Blocks.field_150454_av) {
            if (block != Blocks.field_150446_ar && block != Blocks.field_150476_ad && block != Blocks.field_150387_bl && block != Blocks.field_150390_bg && block != Blocks.field_150372_bz) {
                if (block == Blocks.field_150468_ap) {
                    if (this.field_74885_f == 0) {
                        if (i == 2) {
                            return 3;
                        }

                        if (i == 3) {
                            return 2;
                        }
                    } else if (this.field_74885_f == 1) {
                        if (i == 2) {
                            return 4;
                        }

                        if (i == 3) {
                            return 5;
                        }

                        if (i == 4) {
                            return 2;
                        }

                        if (i == 5) {
                            return 3;
                        }
                    } else if (this.field_74885_f == 3) {
                        if (i == 2) {
                            return 5;
                        }

                        if (i == 3) {
                            return 4;
                        }

                        if (i == 4) {
                            return 2;
                        }

                        if (i == 5) {
                            return 3;
                        }
                    }
                } else if (block == Blocks.field_150430_aB) {
                    if (this.field_74885_f == 0) {
                        if (i == 3) {
                            return 4;
                        }

                        if (i == 4) {
                            return 3;
                        }
                    } else if (this.field_74885_f == 1) {
                        if (i == 3) {
                            return 1;
                        }

                        if (i == 4) {
                            return 2;
                        }

                        if (i == 2) {
                            return 3;
                        }

                        if (i == 1) {
                            return 4;
                        }
                    } else if (this.field_74885_f == 3) {
                        if (i == 3) {
                            return 2;
                        }

                        if (i == 4) {
                            return 1;
                        }

                        if (i == 2) {
                            return 3;
                        }

                        if (i == 1) {
                            return 4;
                        }
                    }
                } else if (block != Blocks.field_150479_bC && !(block instanceof BlockDirectional)) {
                    if (block == Blocks.field_150331_J || block == Blocks.field_150320_F || block == Blocks.field_150442_at || block == Blocks.field_150367_z) {
                        if (this.field_74885_f == 0) {
                            if (i == 2 || i == 3) {
                                return Facing.field_71588_a[i];
                            }
                        } else if (this.field_74885_f == 1) {
                            if (i == 2) {
                                return 4;
                            }

                            if (i == 3) {
                                return 5;
                            }

                            if (i == 4) {
                                return 2;
                            }

                            if (i == 5) {
                                return 3;
                            }
                        } else if (this.field_74885_f == 3) {
                            if (i == 2) {
                                return 5;
                            }

                            if (i == 3) {
                                return 4;
                            }

                            if (i == 4) {
                                return 2;
                            }

                            if (i == 5) {
                                return 3;
                            }
                        }
                    }
                } else if (this.field_74885_f == 0) {
                    if (i == 0 || i == 2) {
                        return Direction.field_71580_e[i];
                    }
                } else if (this.field_74885_f == 1) {
                    if (i == 2) {
                        return 1;
                    }

                    if (i == 0) {
                        return 3;
                    }

                    if (i == 1) {
                        return 2;
                    }

                    if (i == 3) {
                        return 0;
                    }
                } else if (this.field_74885_f == 3) {
                    if (i == 2) {
                        return 3;
                    }

                    if (i == 0) {
                        return 1;
                    }

                    if (i == 1) {
                        return 2;
                    }

                    if (i == 3) {
                        return 0;
                    }
                }
            } else if (this.field_74885_f == 0) {
                if (i == 2) {
                    return 3;
                }

                if (i == 3) {
                    return 2;
                }
            } else if (this.field_74885_f == 1) {
                if (i == 0) {
                    return 2;
                }

                if (i == 1) {
                    return 3;
                }

                if (i == 2) {
                    return 0;
                }

                if (i == 3) {
                    return 1;
                }
            } else if (this.field_74885_f == 3) {
                if (i == 0) {
                    return 2;
                }

                if (i == 1) {
                    return 3;
                }

                if (i == 2) {
                    return 1;
                }

                if (i == 3) {
                    return 0;
                }
            }
        } else if (this.field_74885_f == 0) {
            if (i == 0) {
                return 2;
            }

            if (i == 2) {
                return 0;
            }
        } else {
            if (this.field_74885_f == 1) {
                return i + 1 & 3;
            }

            if (this.field_74885_f == 3) {
                return i + 3 & 3;
            }
        }

        return i;
    }

    protected void func_151550_a(World world, Block block, int i, int j, int k, int l, StructureBoundingBox structureboundingbox) {
        int i1 = this.func_74865_a(j, l);
        int j1 = this.func_74862_a(k);
        int k1 = this.func_74873_b(j, l);

        if (structureboundingbox.func_78890_b(i1, j1, k1)) {
            world.func_147465_d(i1, j1, k1, block, i, 2);
        }
    }

    protected Block func_151548_a(World world, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        int l = this.func_74865_a(i, k);
        int i1 = this.func_74862_a(j);
        int j1 = this.func_74873_b(i, k);

        return !structureboundingbox.func_78890_b(l, i1, j1) ? Blocks.field_150350_a : world.func_147439_a(l, i1, j1);
    }

    protected void func_74878_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    this.func_151550_a(world, Blocks.field_150350_a, 0, l1, k1, i2, structureboundingbox);
                }
            }
        }

    }

    protected void func_151549_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, Block block, Block block1, boolean flag) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || this.func_151548_a(world, l1, k1, i2, structureboundingbox).func_149688_o() != Material.field_151579_a) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.func_151550_a(world, block1, 0, l1, k1, i2, structureboundingbox);
                        } else {
                            this.func_151550_a(world, block, 0, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void func_151556_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, Block block, int k1, Block block1, int l1, boolean flag) {
        for (int i2 = j; i2 <= i1; ++i2) {
            for (int j2 = i; j2 <= l; ++j2) {
                for (int k2 = k; k2 <= j1; ++k2) {
                    if (!flag || this.func_151548_a(world, j2, i2, k2, structureboundingbox).func_149688_o() != Material.field_151579_a) {
                        if (i2 != j && i2 != i1 && j2 != i && j2 != l && k2 != k && k2 != j1) {
                            this.func_151550_a(world, block1, l1, j2, i2, k2, structureboundingbox);
                        } else {
                            this.func_151550_a(world, block, k1, j2, i2, k2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void func_74882_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, boolean flag, Random random, StructureComponent$BlockSelector structurecomponent$blockselector) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || this.func_151548_a(world, l1, k1, i2, structureboundingbox).func_149688_o() != Material.field_151579_a) {
                        structurecomponent$blockselector.func_75062_a(random, l1, k1, i2, k1 == j || k1 == i1 || l1 == i || l1 == l || i2 == k || i2 == j1);
                        this.func_151550_a(world, structurecomponent$blockselector.func_151561_a(), structurecomponent$blockselector.func_75064_b(), l1, k1, i2, structureboundingbox);
                    }
                }
            }
        }

    }

    protected void func_151551_a(World world, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, int l, int i1, int j1, Block block, Block block1, boolean flag) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (random.nextFloat() <= f && (!flag || this.func_151548_a(world, l1, k1, i2, structureboundingbox).func_149688_o() != Material.field_151579_a)) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.func_151550_a(world, block1, 0, l1, k1, i2, structureboundingbox);
                        } else {
                            this.func_151550_a(world, block, 0, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void func_151552_a(World world, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, Block block, int l) {
        if (random.nextFloat() < f) {
            this.func_151550_a(world, block, l, i, j, k, structureboundingbox);
        }

    }

    protected void func_151547_a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, Block block, boolean flag) {
        float f = (float) (l - i + 1);
        float f1 = (float) (i1 - j + 1);
        float f2 = (float) (j1 - k + 1);
        float f3 = (float) i + f / 2.0F;
        float f4 = (float) k + f2 / 2.0F;

        for (int k1 = j; k1 <= i1; ++k1) {
            float f5 = (float) (k1 - j) / f1;

            for (int l1 = i; l1 <= l; ++l1) {
                float f6 = ((float) l1 - f3) / (f * 0.5F);

                for (int i2 = k; i2 <= j1; ++i2) {
                    float f7 = ((float) i2 - f4) / (f2 * 0.5F);

                    if (!flag || this.func_151548_a(world, l1, k1, i2, structureboundingbox).func_149688_o() != Material.field_151579_a) {
                        float f8 = f6 * f6 + f5 * f5 + f7 * f7;

                        if (f8 <= 1.05F) {
                            this.func_151550_a(world, block, 0, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void func_74871_b(World world, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        int l = this.func_74865_a(i, k);
        int i1 = this.func_74862_a(j);
        int j1 = this.func_74873_b(i, k);

        if (structureboundingbox.func_78890_b(l, i1, j1)) {
            while (!world.func_147437_c(l, i1, j1) && i1 < 255) {
                world.func_147465_d(l, i1, j1, Blocks.field_150350_a, 0, 2);
                ++i1;
            }

        }
    }

    protected void func_151554_b(World world, Block block, int i, int j, int k, int l, StructureBoundingBox structureboundingbox) {
        int i1 = this.func_74865_a(j, l);
        int j1 = this.func_74862_a(k);
        int k1 = this.func_74873_b(j, l);

        if (structureboundingbox.func_78890_b(i1, j1, k1)) {
            while ((world.func_147437_c(i1, j1, k1) || world.func_147439_a(i1, j1, k1).func_149688_o().func_76224_d()) && j1 > 1) {
                world.func_147465_d(i1, j1, k1, block, i, 2);
                --j1;
            }

        }
    }

    protected boolean func_74879_a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, WeightedRandomChestContent[] aweightedrandomchestcontent, int l) {
        int i1 = this.func_74865_a(i, k);
        int j1 = this.func_74862_a(j);
        int k1 = this.func_74873_b(i, k);

        if (structureboundingbox.func_78890_b(i1, j1, k1) && world.func_147439_a(i1, j1, k1) != Blocks.field_150486_ae) {
            world.func_147465_d(i1, j1, k1, Blocks.field_150486_ae, 0, 2);
            TileEntityChest tileentitychest = (TileEntityChest) world.func_147438_o(i1, j1, k1);

            if (tileentitychest != null) {
                WeightedRandomChestContent.func_76293_a(random, aweightedrandomchestcontent, tileentitychest, l);
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean func_74869_a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, int l, WeightedRandomChestContent[] aweightedrandomchestcontent, int i1) {
        int j1 = this.func_74865_a(i, k);
        int k1 = this.func_74862_a(j);
        int l1 = this.func_74873_b(i, k);

        if (structureboundingbox.func_78890_b(j1, k1, l1) && world.func_147439_a(j1, k1, l1) != Blocks.field_150367_z) {
            world.func_147465_d(j1, k1, l1, Blocks.field_150367_z, this.func_151555_a(Blocks.field_150367_z, l), 2);
            TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.func_147438_o(j1, k1, l1);

            if (tileentitydispenser != null) {
                WeightedRandomChestContent.func_150706_a(random, aweightedrandomchestcontent, tileentitydispenser, i1);
            }

            return true;
        } else {
            return false;
        }
    }

    protected void func_74881_a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, int l) {
        int i1 = this.func_74865_a(i, k);
        int j1 = this.func_74862_a(j);
        int k1 = this.func_74873_b(i, k);

        if (structureboundingbox.func_78890_b(i1, j1, k1)) {
            ItemDoor.func_150924_a(world, i1, j1, k1, l, Blocks.field_150466_ao);
        }

    }
}
