package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {

    private int field_72818_a;
    private int field_72816_b;
    private Chunk[][] field_72817_c;
    private boolean field_72814_d;
    private World field_72815_e;

    public ChunkCache(World world, int i, int j, int k, int l, int i1, int j1, int k1) {
        this.field_72815_e = world;
        this.field_72818_a = i - k1 >> 4;
        this.field_72816_b = k - k1 >> 4;
        int l1 = l + k1 >> 4;
        int i2 = j1 + k1 >> 4;

        this.field_72817_c = new Chunk[l1 - this.field_72818_a + 1][i2 - this.field_72816_b + 1];
        this.field_72814_d = true;

        Chunk chunk;
        int j2;
        int k2;

        for (j2 = this.field_72818_a; j2 <= l1; ++j2) {
            for (k2 = this.field_72816_b; k2 <= i2; ++k2) {
                chunk = world.func_72964_e(j2, k2);
                if (chunk != null) {
                    this.field_72817_c[j2 - this.field_72818_a][k2 - this.field_72816_b] = chunk;
                }
            }
        }

        for (j2 = i >> 4; j2 <= l >> 4; ++j2) {
            for (k2 = k >> 4; k2 <= j1 >> 4; ++k2) {
                chunk = this.field_72817_c[j2 - this.field_72818_a][k2 - this.field_72816_b];
                if (chunk != null && !chunk.func_76606_c(j, i1)) {
                    this.field_72814_d = false;
                }
            }
        }

    }

    @Override
    public Block func_147439_a(int i, int j, int k) {
        Block block = Blocks.field_150350_a;

        if (j >= 0 && j < 256) {
            int l = (i >> 4) - this.field_72818_a;
            int i1 = (k >> 4) - this.field_72816_b;

            if (l >= 0 && l < this.field_72817_c.length && i1 >= 0 && i1 < this.field_72817_c[l].length) {
                Chunk chunk = this.field_72817_c[l][i1];

                if (chunk != null) {
                    block = chunk.func_150810_a(i & 15, j, k & 15);
                }
            }
        }

        return block;
    }

    @Override
    public TileEntity func_147438_o(int i, int j, int k) {
        int l = (i >> 4) - this.field_72818_a;
        int i1 = (k >> 4) - this.field_72816_b;

        return this.field_72817_c[l][i1].func_150806_e(i & 15, j, k & 15);
    }

    @Override
    public int func_72805_g(int i, int j, int k) {
        if (j < 0) {
            return 0;
        } else if (j >= 256) {
            return 0;
        } else {
            int l = (i >> 4) - this.field_72818_a;
            int i1 = (k >> 4) - this.field_72816_b;

            return this.field_72817_c[l][i1].func_76628_c(i & 15, j, k & 15);
        }
    }

    @Override
    public int func_72879_k(int i, int j, int k, int l) {
        return this.func_147439_a(i, j, k).func_149748_c(this, i, j, k, l);
    }
}
