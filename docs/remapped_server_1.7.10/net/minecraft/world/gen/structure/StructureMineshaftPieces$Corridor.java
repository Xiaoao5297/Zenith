package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Corridor extends StructureComponent {

    private boolean field_74958_a;
    private boolean field_74956_b;
    private boolean field_74957_c;
    private int field_74955_d;

    public StructureMineshaftPieces$Corridor() {}

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74757_a("hr", this.field_74958_a);
        nbttagcompound.func_74757_a("sc", this.field_74956_b);
        nbttagcompound.func_74757_a("hps", this.field_74957_c);
        nbttagcompound.func_74768_a("Num", this.field_74955_d);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        this.field_74958_a = nbttagcompound.func_74767_n("hr");
        this.field_74956_b = nbttagcompound.func_74767_n("sc");
        this.field_74957_c = nbttagcompound.func_74767_n("hps");
        this.field_74955_d = nbttagcompound.func_74762_e("Num");
    }

    public StructureMineshaftPieces$Corridor(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_74958_a = random.nextInt(3) == 0;
        this.field_74956_b = !this.field_74958_a && random.nextInt(23) == 0;
        if (this.field_74885_f != 2 && this.field_74885_f != 0) {
            this.field_74955_d = structureboundingbox.func_78883_b() / 5;
        } else {
            this.field_74955_d = structureboundingbox.func_78880_d() / 5;
        }

    }

    public static StructureBoundingBox func_74954_a(List list, Random random, int i, int j, int k, int l) {
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(i, j, k, i, j + 2, k);

        int i1;

        for (i1 = random.nextInt(3) + 2; i1 > 0; --i1) {
            int j1 = i1 * 5;

            switch (l) {
                case 0:
                    structureboundingbox.field_78893_d = i + 2;
                    structureboundingbox.field_78892_f = k + (j1 - 1);
                    break;
                case 1:
                    structureboundingbox.field_78897_a = i - (j1 - 1);
                    structureboundingbox.field_78892_f = k + 2;
                    break;
                case 2:
                    structureboundingbox.field_78893_d = i + 2;
                    structureboundingbox.field_78896_c = k - (j1 - 1);
                    break;
                case 3:
                    structureboundingbox.field_78893_d = i + (j1 - 1);
                    structureboundingbox.field_78892_f = k + 2;
            }

            if (StructureComponent.func_74883_a(list, structureboundingbox) == null) {
                break;
            }
        }

        return i1 > 0 ? structureboundingbox : null;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        int i = this.func_74877_c();
        int j = random.nextInt(4);

        switch (this.field_74885_f) {
            case 0:
                if (j <= 1) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78892_f + 1, this.field_74885_f, i);
                } else if (j == 2) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78892_f - 3, 1, i);
                } else {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78892_f - 3, 3, i);
                }
                break;
            case 1:
                if (j <= 1) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c, this.field_74885_f, i);
                } else if (j == 2) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c - 1, 2, i);
                } else {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78892_f + 1, 0, i);
                }
                break;
            case 2:
                if (j <= 1) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c - 1, this.field_74885_f, i);
                } else if (j == 2) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c, 1, i);
                } else {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c, 3, i);
                }
                break;
            case 3:
                if (j <= 1) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c, this.field_74885_f, i);
                } else if (j == 2) {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d - 3, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78896_c - 1, 2, i);
                } else {
                    StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d - 3, this.field_74887_e.field_78895_b - 1 + random.nextInt(3), this.field_74887_e.field_78892_f + 1, 0, i);
                }
        }

        if (i < 8) {
            int k;
            int l;

            if (this.field_74885_f != 2 && this.field_74885_f != 0) {
                for (l = this.field_74887_e.field_78897_a + 3; l + 3 <= this.field_74887_e.field_78893_d; l += 5) {
                    k = random.nextInt(5);
                    if (k == 0) {
                        StructureMineshaftPieces.access$000(structurecomponent, list, random, l, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, i + 1);
                    } else if (k == 1) {
                        StructureMineshaftPieces.access$000(structurecomponent, list, random, l, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, i + 1);
                    }
                }
            } else {
                for (l = this.field_74887_e.field_78896_c + 3; l + 3 <= this.field_74887_e.field_78892_f; l += 5) {
                    k = random.nextInt(5);
                    if (k == 0) {
                        StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, l, 1, i + 1);
                    } else if (k == 1) {
                        StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, l, 3, i + 1);
                    }
                }
            }
        }

    }

    @Override
    protected boolean func_74879_a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, WeightedRandomChestContent[] aweightedrandomchestcontent, int l) {
        int i1 = this.func_74865_a(i, k);
        int j1 = this.func_74862_a(j);
        int k1 = this.func_74873_b(i, k);

        if (structureboundingbox.func_78890_b(i1, j1, k1) && world.func_147439_a(i1, j1, k1).func_149688_o() == Material.field_151579_a) {
            int l1 = random.nextBoolean() ? 1 : 0;

            world.func_147465_d(i1, j1, k1, Blocks.field_150448_aq, this.func_151555_a(Blocks.field_150448_aq, l1), 2);
            EntityMinecartChest entityminecartchest = new EntityMinecartChest(world, (double) ((float) i1 + 0.5F), (double) ((float) j1 + 0.5F), (double) ((float) k1 + 0.5F));

            WeightedRandomChestContent.func_76293_a(random, aweightedrandomchestcontent, entityminecartchest, l);
            world.func_72838_d(entityminecartchest);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            boolean flag = false;
            boolean flag1 = true;
            boolean flag2 = false;
            boolean flag3 = true;
            int i = this.field_74955_d * 5 - 1;

            this.func_151549_a(world, structureboundingbox, 0, 0, 0, 2, 1, i, Blocks.field_150350_a, Blocks.field_150350_a, false);
            this.func_151551_a(world, structureboundingbox, random, 0.8F, 0, 2, 0, 2, 2, i, Blocks.field_150350_a, Blocks.field_150350_a, false);
            if (this.field_74956_b) {
                this.func_151551_a(world, structureboundingbox, random, 0.6F, 0, 0, 0, 2, 1, i, Blocks.field_150321_G, Blocks.field_150350_a, false);
            }

            int j;
            int k;

            for (k = 0; k < this.field_74955_d; ++k) {
                j = 2 + k * 5;
                this.func_151549_a(world, structureboundingbox, 0, 0, j, 0, 1, j, Blocks.field_150422_aJ, Blocks.field_150350_a, false);
                this.func_151549_a(world, structureboundingbox, 2, 0, j, 2, 1, j, Blocks.field_150422_aJ, Blocks.field_150350_a, false);
                if (random.nextInt(4) == 0) {
                    this.func_151549_a(world, structureboundingbox, 0, 2, j, 0, 2, j, Blocks.field_150344_f, Blocks.field_150350_a, false);
                    this.func_151549_a(world, structureboundingbox, 2, 2, j, 2, 2, j, Blocks.field_150344_f, Blocks.field_150350_a, false);
                } else {
                    this.func_151549_a(world, structureboundingbox, 0, 2, j, 2, 2, j, Blocks.field_150344_f, Blocks.field_150350_a, false);
                }

                this.func_151552_a(world, structureboundingbox, random, 0.1F, 0, 2, j - 1, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.1F, 2, 2, j - 1, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.1F, 0, 2, j + 1, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.1F, 2, 2, j + 1, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.05F, 0, 2, j - 2, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.05F, 2, 2, j - 2, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.05F, 0, 2, j + 2, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.05F, 2, 2, j + 2, Blocks.field_150321_G, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.05F, 1, 2, j - 1, Blocks.field_150478_aa, 0);
                this.func_151552_a(world, structureboundingbox, random, 0.05F, 1, 2, j + 1, Blocks.field_150478_aa, 0);
                if (random.nextInt(100) == 0) {
                    this.func_74879_a(world, structureboundingbox, random, 2, 0, j - 1, WeightedRandomChestContent.func_92080_a(StructureMineshaftPieces.access$100(), Items.field_151134_bR.func_92114_b(random)), 3 + random.nextInt(4));
                }

                if (random.nextInt(100) == 0) {
                    this.func_74879_a(world, structureboundingbox, random, 0, 0, j + 1, WeightedRandomChestContent.func_92080_a(StructureMineshaftPieces.access$100(), Items.field_151134_bR.func_92114_b(random)), 3 + random.nextInt(4));
                }

                if (this.field_74956_b && !this.field_74957_c) {
                    int l = this.func_74862_a(0);
                    int i1 = j - 1 + random.nextInt(3);
                    int j1 = this.func_74865_a(1, i1);

                    i1 = this.func_74873_b(1, i1);
                    if (structureboundingbox.func_78890_b(j1, l, i1)) {
                        this.field_74957_c = true;
                        world.func_147465_d(j1, l, i1, Blocks.field_150474_ac, 0, 2);
                        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) world.func_147438_o(j1, l, i1);

                        if (tileentitymobspawner != null) {
                            tileentitymobspawner.func_145881_a().func_98272_a("CaveSpider");
                        }
                    }
                }
            }

            for (k = 0; k <= 2; ++k) {
                for (j = 0; j <= i; ++j) {
                    byte b0 = -1;
                    Block block = this.func_151548_a(world, k, b0, j, structureboundingbox);

                    if (block.func_149688_o() == Material.field_151579_a) {
                        byte b1 = -1;

                        this.func_151550_a(world, Blocks.field_150344_f, 0, k, b1, j, structureboundingbox);
                    }
                }
            }

            if (this.field_74958_a) {
                for (k = 0; k <= i; ++k) {
                    Block block1 = this.func_151548_a(world, 1, -1, k, structureboundingbox);

                    if (block1.func_149688_o() != Material.field_151579_a && block1.func_149730_j()) {
                        this.func_151552_a(world, structureboundingbox, random, 0.7F, 1, 0, k, Blocks.field_150448_aq, this.func_151555_a(Blocks.field_150448_aq, 0));
                    }
                }
            }

            return true;
        }
    }
}
