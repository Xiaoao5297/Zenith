package net.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class BlockRailBase$Rail {

    private World field_150660_b;
    private int field_150661_c;
    private int field_150658_d;
    private int field_150659_e;
    private final boolean field_150656_f;
    private List field_150657_g;

    public BlockRailBase$Rail(BlockRailBase blockrailbase, World world, int i, int j, int k) {
        this.field_150662_a = blockrailbase;
        this.field_150657_g = new ArrayList();
        this.field_150660_b = world;
        this.field_150661_c = i;
        this.field_150658_d = j;
        this.field_150659_e = k;
        Block block = world.func_147439_a(i, j, k);
        int l = world.func_72805_g(i, j, k);

        if (((BlockRailBase) block).field_150053_a) {
            this.field_150656_f = true;
            l &= -9;
        } else {
            this.field_150656_f = false;
        }

        this.func_150648_a(l);
    }

    private void func_150648_a(int i) {
        this.field_150657_g.clear();
        if (i == 0) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
        } else if (i == 1) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
        } else if (i == 2) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d + 1, this.field_150659_e));
        } else if (i == 3) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d + 1, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
        } else if (i == 4) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d + 1, this.field_150659_e - 1));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
        } else if (i == 5) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d + 1, this.field_150659_e + 1));
        } else if (i == 6) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
        } else if (i == 7) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
        } else if (i == 8) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
        } else if (i == 9) {
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
            this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
        }

    }

    private void func_150651_b() {
        for (int i = 0; i < this.field_150657_g.size(); ++i) {
            BlockRailBase$Rail blockrailbase$rail = this.func_150654_a((ChunkPosition) this.field_150657_g.get(i));

            if (blockrailbase$rail != null && blockrailbase$rail.func_150653_a(this)) {
                this.field_150657_g.set(i, new ChunkPosition(blockrailbase$rail.field_150661_c, blockrailbase$rail.field_150658_d, blockrailbase$rail.field_150659_e));
            } else {
                this.field_150657_g.remove(i--);
            }
        }

    }

    private boolean func_150646_a(int i, int j, int k) {
        return BlockRailBase.func_150049_b_(this.field_150660_b, i, j, k) ? true : (BlockRailBase.func_150049_b_(this.field_150660_b, i, j + 1, k) ? true : BlockRailBase.func_150049_b_(this.field_150660_b, i, j - 1, k));
    }

    private BlockRailBase$Rail func_150654_a(ChunkPosition chunkposition) {
        return BlockRailBase.func_150049_b_(this.field_150660_b, chunkposition.field_151329_a, chunkposition.field_151327_b, chunkposition.field_151328_c) ? new BlockRailBase$Rail(this.field_150662_a, this.field_150660_b, chunkposition.field_151329_a, chunkposition.field_151327_b, chunkposition.field_151328_c) : (BlockRailBase.func_150049_b_(this.field_150660_b, chunkposition.field_151329_a, chunkposition.field_151327_b + 1, chunkposition.field_151328_c) ? new BlockRailBase$Rail(this.field_150662_a, this.field_150660_b, chunkposition.field_151329_a, chunkposition.field_151327_b + 1, chunkposition.field_151328_c) : (BlockRailBase.func_150049_b_(this.field_150660_b, chunkposition.field_151329_a, chunkposition.field_151327_b - 1, chunkposition.field_151328_c) ? new BlockRailBase$Rail(this.field_150662_a, this.field_150660_b, chunkposition.field_151329_a, chunkposition.field_151327_b - 1, chunkposition.field_151328_c) : null));
    }

    private boolean func_150653_a(BlockRailBase$Rail blockrailbase$rail) {
        for (int i = 0; i < this.field_150657_g.size(); ++i) {
            ChunkPosition chunkposition = (ChunkPosition) this.field_150657_g.get(i);

            if (chunkposition.field_151329_a == blockrailbase$rail.field_150661_c && chunkposition.field_151328_c == blockrailbase$rail.field_150659_e) {
                return true;
            }
        }

        return false;
    }

    private boolean func_150652_b(int i, int j, int k) {
        for (int l = 0; l < this.field_150657_g.size(); ++l) {
            ChunkPosition chunkposition = (ChunkPosition) this.field_150657_g.get(l);

            if (chunkposition.field_151329_a == i && chunkposition.field_151328_c == k) {
                return true;
            }
        }

        return false;
    }

    protected int func_150650_a() {
        int i = 0;

        if (this.func_150646_a(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1)) {
            ++i;
        }

        if (this.func_150646_a(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1)) {
            ++i;
        }

        if (this.func_150646_a(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e)) {
            ++i;
        }

        if (this.func_150646_a(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e)) {
            ++i;
        }

        return i;
    }

    private boolean func_150649_b(BlockRailBase$Rail blockrailbase$rail) {
        return this.func_150653_a(blockrailbase$rail) ? true : (this.field_150657_g.size() == 2 ? false : (this.field_150657_g.isEmpty() ? true : true));
    }

    private void func_150645_c(BlockRailBase$Rail blockrailbase$rail) {
        this.field_150657_g.add(new ChunkPosition(blockrailbase$rail.field_150661_c, blockrailbase$rail.field_150658_d, blockrailbase$rail.field_150659_e));
        boolean flag = this.func_150652_b(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1);
        boolean flag1 = this.func_150652_b(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1);
        boolean flag2 = this.func_150652_b(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e);
        boolean flag3 = this.func_150652_b(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e);
        byte b0 = -1;

        if (flag || flag1) {
            b0 = 0;
        }

        if (flag2 || flag3) {
            b0 = 1;
        }

        if (!this.field_150656_f) {
            if (flag1 && flag3 && !flag && !flag2) {
                b0 = 6;
            }

            if (flag1 && flag2 && !flag && !flag3) {
                b0 = 7;
            }

            if (flag && flag2 && !flag1 && !flag3) {
                b0 = 8;
            }

            if (flag && flag3 && !flag1 && !flag2) {
                b0 = 9;
            }
        }

        if (b0 == 0) {
            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e - 1)) {
                b0 = 4;
            }

            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e + 1)) {
                b0 = 5;
            }
        }

        if (b0 == 1) {
            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c + 1, this.field_150658_d + 1, this.field_150659_e)) {
                b0 = 2;
            }

            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c - 1, this.field_150658_d + 1, this.field_150659_e)) {
                b0 = 3;
            }
        }

        if (b0 < 0) {
            b0 = 0;
        }

        int i = b0;

        if (this.field_150656_f) {
            i = this.field_150660_b.func_72805_g(this.field_150661_c, this.field_150658_d, this.field_150659_e) & 8 | b0;
        }

        this.field_150660_b.func_72921_c(this.field_150661_c, this.field_150658_d, this.field_150659_e, i, 3);
    }

    private boolean func_150647_c(int i, int j, int k) {
        BlockRailBase$Rail blockrailbase$rail = this.func_150654_a(new ChunkPosition(i, j, k));

        if (blockrailbase$rail == null) {
            return false;
        } else {
            blockrailbase$rail.func_150651_b();
            return blockrailbase$rail.func_150649_b(this);
        }
    }

    public void func_150655_a(boolean flag, boolean flag1) {
        boolean flag2 = this.func_150647_c(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1);
        boolean flag3 = this.func_150647_c(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1);
        boolean flag4 = this.func_150647_c(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e);
        boolean flag5 = this.func_150647_c(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e);
        byte b0 = -1;

        if ((flag2 || flag3) && !flag4 && !flag5) {
            b0 = 0;
        }

        if ((flag4 || flag5) && !flag2 && !flag3) {
            b0 = 1;
        }

        if (!this.field_150656_f) {
            if (flag3 && flag5 && !flag2 && !flag4) {
                b0 = 6;
            }

            if (flag3 && flag4 && !flag2 && !flag5) {
                b0 = 7;
            }

            if (flag2 && flag4 && !flag3 && !flag5) {
                b0 = 8;
            }

            if (flag2 && flag5 && !flag3 && !flag4) {
                b0 = 9;
            }
        }

        if (b0 == -1) {
            if (flag2 || flag3) {
                b0 = 0;
            }

            if (flag4 || flag5) {
                b0 = 1;
            }

            if (!this.field_150656_f) {
                if (flag) {
                    if (flag3 && flag5) {
                        b0 = 6;
                    }

                    if (flag4 && flag3) {
                        b0 = 7;
                    }

                    if (flag5 && flag2) {
                        b0 = 9;
                    }

                    if (flag2 && flag4) {
                        b0 = 8;
                    }
                } else {
                    if (flag2 && flag4) {
                        b0 = 8;
                    }

                    if (flag5 && flag2) {
                        b0 = 9;
                    }

                    if (flag4 && flag3) {
                        b0 = 7;
                    }

                    if (flag3 && flag5) {
                        b0 = 6;
                    }
                }
            }
        }

        if (b0 == 0) {
            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e - 1)) {
                b0 = 4;
            }

            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e + 1)) {
                b0 = 5;
            }
        }

        if (b0 == 1) {
            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c + 1, this.field_150658_d + 1, this.field_150659_e)) {
                b0 = 2;
            }

            if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c - 1, this.field_150658_d + 1, this.field_150659_e)) {
                b0 = 3;
            }
        }

        if (b0 < 0) {
            b0 = 0;
        }

        this.func_150648_a(b0);
        int i = b0;

        if (this.field_150656_f) {
            i = this.field_150660_b.func_72805_g(this.field_150661_c, this.field_150658_d, this.field_150659_e) & 8 | b0;
        }

        if (flag1 || this.field_150660_b.func_72805_g(this.field_150661_c, this.field_150658_d, this.field_150659_e) != i) {
            this.field_150660_b.func_72921_c(this.field_150661_c, this.field_150658_d, this.field_150659_e, i, 3);

            for (int j = 0; j < this.field_150657_g.size(); ++j) {
                BlockRailBase$Rail blockrailbase$rail = this.func_150654_a((ChunkPosition) this.field_150657_g.get(j));

                if (blockrailbase$rail != null) {
                    blockrailbase$rail.func_150651_b();
                    if (blockrailbase$rail.func_150649_b(this)) {
                        blockrailbase$rail.func_150645_c(this);
                    }
                }
            }
        }

    }
}
