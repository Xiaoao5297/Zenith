package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {

    public int field_98286_b = 20;
    private String field_98288_a = "Pig";
    private List field_98285_e;
    private MobSpawnerBaseLogic$WeightedRandomMinecart field_98282_f;
    public double field_98287_c;
    public double field_98284_d;
    private int field_98283_g = 200;
    private int field_98293_h = 800;
    private int field_98294_i = 4;
    private Entity field_98291_j;
    private int field_98292_k = 6;
    private int field_98289_l = 16;
    private int field_98290_m = 4;

    public MobSpawnerBaseLogic() {}

    public String func_98276_e() {
        if (this.func_98269_i() == null) {
            if (this.field_98288_a.equals("Minecart")) {
                this.field_98288_a = "MinecartRideable";
            }

            return this.field_98288_a;
        } else {
            return this.func_98269_i().field_98223_c;
        }
    }

    public void func_98272_a(String s) {
        this.field_98288_a = s;
    }

    public boolean func_98279_f() {
        return this.func_98271_a().func_72977_a((double) this.func_98275_b() + 0.5D, (double) this.func_98274_c() + 0.5D, (double) this.func_98266_d() + 0.5D, (double) this.field_98289_l) != null;
    }

    public void func_98278_g() {
        if (this.func_98279_f()) {
            double d0;

            if (this.func_98271_a().field_72995_K) {
                double d1 = (double) ((float) this.func_98275_b() + this.func_98271_a().field_73012_v.nextFloat());
                double d2 = (double) ((float) this.func_98274_c() + this.func_98271_a().field_73012_v.nextFloat());

                d0 = (double) ((float) this.func_98266_d() + this.func_98271_a().field_73012_v.nextFloat());
                this.func_98271_a().func_72869_a("smoke", d1, d2, d0, 0.0D, 0.0D, 0.0D);
                this.func_98271_a().func_72869_a("flame", d1, d2, d0, 0.0D, 0.0D, 0.0D);
                if (this.field_98286_b > 0) {
                    --this.field_98286_b;
                }

                this.field_98284_d = this.field_98287_c;
                this.field_98287_c = (this.field_98287_c + (double) (1000.0F / ((float) this.field_98286_b + 200.0F))) % 360.0D;
            } else {
                if (this.field_98286_b == -1) {
                    this.func_98273_j();
                }

                if (this.field_98286_b > 0) {
                    --this.field_98286_b;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.field_98294_i; ++i) {
                    Entity entity = EntityList.func_75620_a(this.func_98276_e(), this.func_98271_a());

                    if (entity == null) {
                        return;
                    }

                    int j = this.func_98271_a().func_72872_a(entity.getClass(), AxisAlignedBB.func_72330_a((double) this.func_98275_b(), (double) this.func_98274_c(), (double) this.func_98266_d(), (double) (this.func_98275_b() + 1), (double) (this.func_98274_c() + 1), (double) (this.func_98266_d() + 1)).func_72314_b((double) (this.field_98290_m * 2), 4.0D, (double) (this.field_98290_m * 2))).size();

                    if (j >= this.field_98292_k) {
                        this.func_98273_j();
                        return;
                    }

                    d0 = (double) this.func_98275_b() + (this.func_98271_a().field_73012_v.nextDouble() - this.func_98271_a().field_73012_v.nextDouble()) * (double) this.field_98290_m;
                    double d3 = (double) (this.func_98274_c() + this.func_98271_a().field_73012_v.nextInt(3) - 1);
                    double d4 = (double) this.func_98266_d() + (this.func_98271_a().field_73012_v.nextDouble() - this.func_98271_a().field_73012_v.nextDouble()) * (double) this.field_98290_m;
                    EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;

                    entity.func_70012_b(d0, d3, d4, this.func_98271_a().field_73012_v.nextFloat() * 360.0F, 0.0F);
                    if (entityliving == null || entityliving.func_70601_bi()) {
                        this.func_98265_a(entity);
                        this.func_98271_a().func_72926_e(2004, this.func_98275_b(), this.func_98274_c(), this.func_98266_d(), 0);
                        if (entityliving != null) {
                            entityliving.func_70656_aK();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.func_98273_j();
                }
            }

        }
    }

    public Entity func_98265_a(Entity entity) {
        if (this.func_98269_i() != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.func_70039_c(nbttagcompound);
            Iterator iterator = this.func_98269_i().field_98222_b.func_150296_c().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTBase nbtbase = this.func_98269_i().field_98222_b.func_74781_a(s);

                nbttagcompound.func_74782_a(s, nbtbase.func_74737_b());
            }

            entity.func_70020_e(nbttagcompound);
            if (entity.field_70170_p != null) {
                entity.field_70170_p.func_72838_d(entity);
            }

            NBTTagCompound nbttagcompound1;

            for (Entity entity1 = entity; nbttagcompound.func_150297_b("Riding", 10); nbttagcompound = nbttagcompound1) {
                nbttagcompound1 = nbttagcompound.func_74775_l("Riding");
                Entity entity2 = EntityList.func_75620_a(nbttagcompound1.func_74779_i("id"), entity.field_70170_p);

                if (entity2 != null) {
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                    entity2.func_70039_c(nbttagcompound2);
                    Iterator iterator1 = nbttagcompound1.func_150296_c().iterator();

                    while (iterator1.hasNext()) {
                        String s1 = (String) iterator1.next();
                        NBTBase nbtbase1 = nbttagcompound1.func_74781_a(s1);

                        nbttagcompound2.func_74782_a(s1, nbtbase1.func_74737_b());
                    }

                    entity2.func_70020_e(nbttagcompound2);
                    entity2.func_70012_b(entity1.field_70165_t, entity1.field_70163_u, entity1.field_70161_v, entity1.field_70177_z, entity1.field_70125_A);
                    if (entity.field_70170_p != null) {
                        entity.field_70170_p.func_72838_d(entity2);
                    }

                    entity1.func_70078_a(entity2);
                }

                entity1 = entity2;
            }
        } else if (entity instanceof EntityLivingBase && entity.field_70170_p != null) {
            ((EntityLiving) entity).func_110161_a((IEntityLivingData) null);
            this.func_98271_a().func_72838_d(entity);
        }

        return entity;
    }

    private void func_98273_j() {
        if (this.field_98293_h <= this.field_98283_g) {
            this.field_98286_b = this.field_98283_g;
        } else {
            int i = this.field_98293_h - this.field_98283_g;

            this.field_98286_b = this.field_98283_g + this.func_98271_a().field_73012_v.nextInt(i);
        }

        if (this.field_98285_e != null && this.field_98285_e.size() > 0) {
            this.func_98277_a((MobSpawnerBaseLogic$WeightedRandomMinecart) WeightedRandom.func_76271_a(this.func_98271_a().field_73012_v, this.field_98285_e));
        }

        this.func_98267_a(1);
    }

    public void func_98270_a(NBTTagCompound nbttagcompound) {
        this.field_98288_a = nbttagcompound.func_74779_i("EntityId");
        this.field_98286_b = nbttagcompound.func_74765_d("Delay");
        if (nbttagcompound.func_150297_b("SpawnPotentials", 9)) {
            this.field_98285_e = new ArrayList();
            NBTTagList nbttaglist = nbttagcompound.func_150295_c("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                this.field_98285_e.add(new MobSpawnerBaseLogic$WeightedRandomMinecart(this, nbttaglist.func_150305_b(i)));
            }
        } else {
            this.field_98285_e = null;
        }

        if (nbttagcompound.func_150297_b("SpawnData", 10)) {
            this.func_98277_a(new MobSpawnerBaseLogic$WeightedRandomMinecart(this, nbttagcompound.func_74775_l("SpawnData"), this.field_98288_a));
        } else {
            this.func_98277_a((MobSpawnerBaseLogic$WeightedRandomMinecart) null);
        }

        if (nbttagcompound.func_150297_b("MinSpawnDelay", 99)) {
            this.field_98283_g = nbttagcompound.func_74765_d("MinSpawnDelay");
            this.field_98293_h = nbttagcompound.func_74765_d("MaxSpawnDelay");
            this.field_98294_i = nbttagcompound.func_74765_d("SpawnCount");
        }

        if (nbttagcompound.func_150297_b("MaxNearbyEntities", 99)) {
            this.field_98292_k = nbttagcompound.func_74765_d("MaxNearbyEntities");
            this.field_98289_l = nbttagcompound.func_74765_d("RequiredPlayerRange");
        }

        if (nbttagcompound.func_150297_b("SpawnRange", 99)) {
            this.field_98290_m = nbttagcompound.func_74765_d("SpawnRange");
        }

        if (this.func_98271_a() != null && this.func_98271_a().field_72995_K) {
            this.field_98291_j = null;
        }

    }

    public void func_98280_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74778_a("EntityId", this.func_98276_e());
        nbttagcompound.func_74777_a("Delay", (short) this.field_98286_b);
        nbttagcompound.func_74777_a("MinSpawnDelay", (short) this.field_98283_g);
        nbttagcompound.func_74777_a("MaxSpawnDelay", (short) this.field_98293_h);
        nbttagcompound.func_74777_a("SpawnCount", (short) this.field_98294_i);
        nbttagcompound.func_74777_a("MaxNearbyEntities", (short) this.field_98292_k);
        nbttagcompound.func_74777_a("RequiredPlayerRange", (short) this.field_98289_l);
        nbttagcompound.func_74777_a("SpawnRange", (short) this.field_98290_m);
        if (this.func_98269_i() != null) {
            nbttagcompound.func_74782_a("SpawnData", this.func_98269_i().field_98222_b.func_74737_b());
        }

        if (this.func_98269_i() != null || this.field_98285_e != null && this.field_98285_e.size() > 0) {
            NBTTagList nbttaglist = new NBTTagList();

            if (this.field_98285_e != null && this.field_98285_e.size() > 0) {
                Iterator iterator = this.field_98285_e.iterator();

                while (iterator.hasNext()) {
                    MobSpawnerBaseLogic$WeightedRandomMinecart mobspawnerbaselogic$weightedrandomminecart = (MobSpawnerBaseLogic$WeightedRandomMinecart) iterator.next();

                    nbttaglist.func_74742_a(mobspawnerbaselogic$weightedrandomminecart.func_98220_a());
                }
            } else {
                nbttaglist.func_74742_a(this.func_98269_i().func_98220_a());
            }

            nbttagcompound.func_74782_a("SpawnPotentials", nbttaglist);
        }

    }

    public boolean func_98268_b(int i) {
        if (i == 1 && this.func_98271_a().field_72995_K) {
            this.field_98286_b = this.field_98283_g;
            return true;
        } else {
            return false;
        }
    }

    public MobSpawnerBaseLogic$WeightedRandomMinecart func_98269_i() {
        return this.field_98282_f;
    }

    public void func_98277_a(MobSpawnerBaseLogic$WeightedRandomMinecart mobspawnerbaselogic$weightedrandomminecart) {
        this.field_98282_f = mobspawnerbaselogic$weightedrandomminecart;
    }

    public abstract void func_98267_a(int i);

    public abstract World func_98271_a();

    public abstract int func_98275_b();

    public abstract int func_98274_c();

    public abstract int func_98266_d();
}
