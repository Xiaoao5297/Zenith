package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class BlockPortal$Size {

    private final World field_150867_a;
    private final int field_150865_b;
    private final int field_150866_c;
    private final int field_150863_d;
    private int field_150864_e = 0;
    private ChunkCoordinates field_150861_f;
    private int field_150862_g;
    private int field_150868_h;

    public BlockPortal$Size(World world, int i, int j, int k, int l) {
        this.field_150867_a = world;
        this.field_150865_b = l;
        this.field_150863_d = BlockPortal.field_150001_a[l][0];
        this.field_150866_c = BlockPortal.field_150001_a[l][1];

        for (int i1 = j; j > i1 - 21 && j > 0 && this.func_150857_a(world.func_147439_a(i, j - 1, k)); --j) {
            ;
        }

        int j1 = this.func_150853_a(i, j, k, this.field_150863_d) - 1;

        if (j1 >= 0) {
            this.field_150861_f = new ChunkCoordinates(i + j1 * Direction.field_71583_a[this.field_150863_d], j, k + j1 * Direction.field_71581_b[this.field_150863_d]);
            this.field_150868_h = this.func_150853_a(this.field_150861_f.field_71574_a, this.field_150861_f.field_71572_b, this.field_150861_f.field_71573_c, this.field_150866_c);
            if (this.field_150868_h < 2 || this.field_150868_h > 21) {
                this.field_150861_f = null;
                this.field_150868_h = 0;
            }
        }

        if (this.field_150861_f != null) {
            this.field_150862_g = this.func_150858_a();
        }

    }

    protected int func_150853_a(int i, int j, int k, int l) {
        int i1 = Direction.field_71583_a[l];
        int j1 = Direction.field_71581_b[l];

        Block block;
        int k1;

        for (k1 = 0; k1 < 22; ++k1) {
            block = this.field_150867_a.func_147439_a(i + i1 * k1, j, k + j1 * k1);
            if (!this.func_150857_a(block)) {
                break;
            }

            Block block1 = this.field_150867_a.func_147439_a(i + i1 * k1, j - 1, k + j1 * k1);

            if (block1 != Blocks.field_150343_Z) {
                break;
            }
        }

        block = this.field_150867_a.func_147439_a(i + i1 * k1, j, k + j1 * k1);
        return block == Blocks.field_150343_Z ? k1 : 0;
    }

    protected int func_150858_a() {
        int i;
        int j;
        int k;
        int l;

        label56:
        for (this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g) {
            i = this.field_150861_f.field_71572_b + this.field_150862_g;

            for (k = 0; k < this.field_150868_h; ++k) {
                j = this.field_150861_f.field_71574_a + k * Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][1]];
                l = this.field_150861_f.field_71573_c + k * Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][1]];
                Block block = this.field_150867_a.func_147439_a(j, i, l);

                if (!this.func_150857_a(block)) {
                    break label56;
                }

                if (block == Blocks.field_150427_aO) {
                    ++this.field_150864_e;
                }

                if (k == 0) {
                    block = this.field_150867_a.func_147439_a(j + Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][0]], i, l + Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][0]]);
                    if (block != Blocks.field_150343_Z) {
                        break label56;
                    }
                } else if (k == this.field_150868_h - 1) {
                    block = this.field_150867_a.func_147439_a(j + Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][1]], i, l + Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][1]]);
                    if (block != Blocks.field_150343_Z) {
                        break label56;
                    }
                }
            }
        }

        for (i = 0; i < this.field_150868_h; ++i) {
            k = this.field_150861_f.field_71574_a + i * Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][1]];
            j = this.field_150861_f.field_71572_b + this.field_150862_g;
            l = this.field_150861_f.field_71573_c + i * Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][1]];
            if (this.field_150867_a.func_147439_a(k, j, l) != Blocks.field_150343_Z) {
                this.field_150862_g = 0;
                break;
            }
        }

        if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
            return this.field_150862_g;
        } else {
            this.field_150861_f = null;
            this.field_150868_h = 0;
            this.field_150862_g = 0;
            return 0;
        }
    }

    protected boolean func_150857_a(Block block) {
        return block.field_149764_J == Material.field_151579_a || block == Blocks.field_150480_ab || block == Blocks.field_150427_aO;
    }

    public boolean func_150860_b() {
        return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
    }

    public void func_150859_c() {
        for (int i = 0; i < this.field_150868_h; ++i) {
            int j = this.field_150861_f.field_71574_a + Direction.field_71583_a[this.field_150866_c] * i;
            int k = this.field_150861_f.field_71573_c + Direction.field_71581_b[this.field_150866_c] * i;

            for (int l = 0; l < this.field_150862_g; ++l) {
                int i1 = this.field_150861_f.field_71572_b + l;

                this.field_150867_a.func_147465_d(j, i1, k, Blocks.field_150427_aO, this.field_150865_b, 2);
            }
        }

    }
}
