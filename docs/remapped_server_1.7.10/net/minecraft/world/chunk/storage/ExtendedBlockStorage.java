package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {

    private int field_76684_a;
    private int field_76682_b;
    private int field_76683_c;
    private byte[] field_76680_d;
    private NibbleArray field_76681_e;
    private NibbleArray field_76678_f;
    private NibbleArray field_76679_g;
    private NibbleArray field_76685_h;

    public ExtendedBlockStorage(int i, boolean flag) {
        this.field_76684_a = i;
        this.field_76680_d = new byte[4096];
        this.field_76678_f = new NibbleArray(this.field_76680_d.length, 4);
        this.field_76679_g = new NibbleArray(this.field_76680_d.length, 4);
        if (flag) {
            this.field_76685_h = new NibbleArray(this.field_76680_d.length, 4);
        }

    }

    public Block func_150819_a(int i, int j, int k) {
        int l = this.field_76680_d[j << 8 | k << 4 | i] & 255;

        if (this.field_76681_e != null) {
            l |= this.field_76681_e.func_76582_a(i, j, k) << 8;
        }

        return Block.func_149729_e(l);
    }

    public void func_150818_a(int i, int j, int k, Block block) {
        int l = this.field_76680_d[j << 8 | k << 4 | i] & 255;

        if (this.field_76681_e != null) {
            l |= this.field_76681_e.func_76582_a(i, j, k) << 8;
        }

        Block block1 = Block.func_149729_e(l);

        if (block1 != Blocks.field_150350_a) {
            --this.field_76682_b;
            if (block1.func_149653_t()) {
                --this.field_76683_c;
            }
        }

        if (block != Blocks.field_150350_a) {
            ++this.field_76682_b;
            if (block.func_149653_t()) {
                ++this.field_76683_c;
            }
        }

        int i1 = Block.func_149682_b(block);

        this.field_76680_d[j << 8 | k << 4 | i] = (byte) (i1 & 255);
        if (i1 > 255) {
            if (this.field_76681_e == null) {
                this.field_76681_e = new NibbleArray(this.field_76680_d.length, 4);
            }

            this.field_76681_e.func_76581_a(i, j, k, (i1 & 3840) >> 8);
        } else if (this.field_76681_e != null) {
            this.field_76681_e.func_76581_a(i, j, k, 0);
        }

    }

    public int func_76665_b(int i, int j, int k) {
        return this.field_76678_f.func_76582_a(i, j, k);
    }

    public void func_76654_b(int i, int j, int k, int l) {
        this.field_76678_f.func_76581_a(i, j, k, l);
    }

    public boolean func_76663_a() {
        return this.field_76682_b == 0;
    }

    public boolean func_76675_b() {
        return this.field_76683_c > 0;
    }

    public int func_76662_d() {
        return this.field_76684_a;
    }

    public void func_76657_c(int i, int j, int k, int l) {
        this.field_76685_h.func_76581_a(i, j, k, l);
    }

    public int func_76670_c(int i, int j, int k) {
        return this.field_76685_h.func_76582_a(i, j, k);
    }

    public void func_76677_d(int i, int j, int k, int l) {
        this.field_76679_g.func_76581_a(i, j, k, l);
    }

    public int func_76674_d(int i, int j, int k) {
        return this.field_76679_g.func_76582_a(i, j, k);
    }

    public void func_76672_e() {
        this.field_76682_b = 0;
        this.field_76683_c = 0;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    Block block = this.func_150819_a(i, j, k);

                    if (block != Blocks.field_150350_a) {
                        ++this.field_76682_b;
                        if (block.func_149653_t()) {
                            ++this.field_76683_c;
                        }
                    }
                }
            }
        }

    }

    public byte[] func_76658_g() {
        return this.field_76680_d;
    }

    public NibbleArray func_76660_i() {
        return this.field_76681_e;
    }

    public NibbleArray func_76669_j() {
        return this.field_76678_f;
    }

    public NibbleArray func_76661_k() {
        return this.field_76679_g;
    }

    public NibbleArray func_76671_l() {
        return this.field_76685_h;
    }

    public void func_76664_a(byte[] abyte) {
        this.field_76680_d = abyte;
    }

    public void func_76673_a(NibbleArray nibblearray) {
        this.field_76681_e = nibblearray;
    }

    public void func_76668_b(NibbleArray nibblearray) {
        this.field_76678_f = nibblearray;
    }

    public void func_76659_c(NibbleArray nibblearray) {
        this.field_76679_g = nibblearray;
    }

    public void func_76666_d(NibbleArray nibblearray) {
        this.field_76685_h = nibblearray;
    }
}
