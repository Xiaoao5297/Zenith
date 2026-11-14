package net.minecraft.world.chunk.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile {

    private static final byte[] field_76720_a = new byte[4096];
    private final File field_76718_b;
    private RandomAccessFile field_76719_c;
    private final int[] field_76716_d = new int[1024];
    private final int[] field_76717_e = new int[1024];
    private ArrayList field_76714_f;
    private int field_76715_g;
    private long field_76721_h;

    public RegionFile(File file) {
        this.field_76718_b = file;
        this.field_76715_g = 0;

        try {
            if (file.exists()) {
                this.field_76721_h = file.lastModified();
            }

            this.field_76719_c = new RandomAccessFile(file, "rw");
            int i;

            if (this.field_76719_c.length() < 4096L) {
                for (i = 0; i < 1024; ++i) {
                    this.field_76719_c.writeInt(0);
                }

                for (i = 0; i < 1024; ++i) {
                    this.field_76719_c.writeInt(0);
                }

                this.field_76715_g += 8192;
            }

            if ((this.field_76719_c.length() & 4095L) != 0L) {
                for (i = 0; (long) i < (this.field_76719_c.length() & 4095L); ++i) {
                    this.field_76719_c.write(0);
                }
            }

            i = (int) this.field_76719_c.length() / 4096;
            this.field_76714_f = new ArrayList(i);

            int j;

            for (j = 0; j < i; ++j) {
                this.field_76714_f.add(true);
            }

            this.field_76714_f.set(0, false);
            this.field_76714_f.set(1, false);
            this.field_76719_c.seek(0L);

            int k;

            for (j = 0; j < 1024; ++j) {
                k = this.field_76719_c.readInt();
                this.field_76716_d[j] = k;
                if (k != 0 && (k >> 8) + (k & 255) <= this.field_76714_f.size()) {
                    for (int l = 0; l < (k & 255); ++l) {
                        this.field_76714_f.set((k >> 8) + l, false);
                    }
                }
            }

            for (j = 0; j < 1024; ++j) {
                k = this.field_76719_c.readInt();
                this.field_76717_e[j] = k;
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    public synchronized DataInputStream func_76704_a(int i, int j) {
        if (this.func_76705_d(i, j)) {
            return null;
        } else {
            try {
                int k = this.func_76707_e(i, j);

                if (k == 0) {
                    return null;
                } else {
                    int l = k >> 8;
                    int i1 = k & 255;

                    if (l + i1 > this.field_76714_f.size()) {
                        return null;
                    } else {
                        this.field_76719_c.seek((long) (l * 4096));
                        int j1 = this.field_76719_c.readInt();

                        if (j1 > 4096 * i1) {
                            return null;
                        } else if (j1 <= 0) {
                            return null;
                        } else {
                            byte b0 = this.field_76719_c.readByte();
                            byte[] abyte;

                            if (b0 == 1) {
                                abyte = new byte[j1 - 1];
                                this.field_76719_c.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
                            } else if (b0 == 2) {
                                abyte = new byte[j1 - 1];
                                this.field_76719_c.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                            } else {
                                return null;
                            }
                        }
                    }
                }
            } catch (IOException ioexception) {
                return null;
            }
        }
    }

    public DataOutputStream func_76710_b(int i, int j) {
        return this.func_76705_d(i, j) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile$ChunkBuffer(this, i, j)));
    }

    protected synchronized void func_76706_a(int i, int j, byte[] abyte, int k) {
        try {
            int l = this.func_76707_e(i, j);
            int i1 = l >> 8;
            int j1 = l & 255;
            int k1 = (k + 5) / 4096 + 1;

            if (k1 >= 256) {
                return;
            }

            if (i1 != 0 && j1 == k1) {
                this.func_76712_a(i1, abyte, k);
            } else {
                int l1;

                for (l1 = 0; l1 < j1; ++l1) {
                    this.field_76714_f.set(i1 + l1, true);
                }

                l1 = this.field_76714_f.indexOf(true);
                int i2 = 0;
                int j2;

                if (l1 != -1) {
                    for (j2 = l1; j2 < this.field_76714_f.size(); ++j2) {
                        if (i2 != 0) {
                            if ((Boolean) this.field_76714_f.get(j2)) {
                                ++i2;
                            } else {
                                i2 = 0;
                            }
                        } else if ((Boolean) this.field_76714_f.get(j2)) {
                            l1 = j2;
                            i2 = 1;
                        }

                        if (i2 >= k1) {
                            break;
                        }
                    }
                }

                if (i2 >= k1) {
                    i1 = l1;
                    this.func_76711_a(i, j, l1 << 8 | k1);

                    for (j2 = 0; j2 < k1; ++j2) {
                        this.field_76714_f.set(i1 + j2, false);
                    }

                    this.func_76712_a(i1, abyte, k);
                } else {
                    this.field_76719_c.seek(this.field_76719_c.length());
                    i1 = this.field_76714_f.size();

                    for (j2 = 0; j2 < k1; ++j2) {
                        this.field_76719_c.write(RegionFile.field_76720_a);
                        this.field_76714_f.add(false);
                    }

                    this.field_76715_g += 4096 * k1;
                    this.func_76712_a(i1, abyte, k);
                    this.func_76711_a(i, j, i1 << 8 | k1);
                }
            }

            this.func_76713_b(i, j, (int) (MinecraftServer.func_130071_aq() / 1000L));
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    private void func_76712_a(int i, byte[] abyte, int j) {
        this.field_76719_c.seek((long) (i * 4096));
        this.field_76719_c.writeInt(j + 1);
        this.field_76719_c.writeByte(2);
        this.field_76719_c.write(abyte, 0, j);
    }

    private boolean func_76705_d(int i, int j) {
        return i < 0 || i >= 32 || j < 0 || j >= 32;
    }

    private int func_76707_e(int i, int j) {
        return this.field_76716_d[i + j * 32];
    }

    public boolean func_76709_c(int i, int j) {
        return this.func_76707_e(i, j) != 0;
    }

    private void func_76711_a(int i, int j, int k) {
        this.field_76716_d[i + j * 32] = k;
        this.field_76719_c.seek((long) ((i + j * 32) * 4));
        this.field_76719_c.writeInt(k);
    }

    private void func_76713_b(int i, int j, int k) {
        this.field_76717_e[i + j * 32] = k;
        this.field_76719_c.seek((long) (4096 + (i + j * 32) * 4));
        this.field_76719_c.writeInt(k);
    }

    public void func_76708_c() {
        if (this.field_76719_c != null) {
            this.field_76719_c.close();
        }

    }
}
