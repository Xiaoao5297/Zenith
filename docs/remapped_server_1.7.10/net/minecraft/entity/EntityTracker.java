package net.minecraft.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {

    private static final Logger field_151249_a = LogManager.getLogger();
    private final WorldServer field_72795_a;
    private Set field_72793_b = new HashSet();
    private IntHashMap field_72794_c = new IntHashMap();
    private int field_72792_d;

    public EntityTracker(WorldServer worldserver) {
        this.field_72795_a = worldserver;
        this.field_72792_d = worldserver.func_73046_m().func_71203_ab().func_72372_a();
    }

    public void func_72786_a(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            this.func_72791_a(entity, 512, 2);
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entity;
            Iterator iterator = this.field_72793_b.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                if (entitytrackerentry.field_73132_a != entityplayermp) {
                    entitytrackerentry.func_73117_b(entityplayermp);
                }
            }
        } else if (entity instanceof EntityFishHook) {
            this.func_72785_a(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.func_72785_a(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.func_72785_a(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.func_72785_a(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.func_72785_a(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            this.func_72785_a(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderEye) {
            this.func_72785_a(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.func_72785_a(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.func_72785_a(entity, 64, 10, true);
        } else if (entity instanceof EntityExpBottle) {
            this.func_72785_a(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworkRocket) {
            this.func_72785_a(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.func_72785_a(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecart) {
            this.func_72785_a(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.func_72785_a(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            this.func_72785_a(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            this.func_72785_a(entity, 80, 3, false);
        } else if (entity instanceof EntityBat) {
            this.func_72785_a(entity, 80, 3, false);
        } else if (entity instanceof IAnimals) {
            this.func_72785_a(entity, 80, 3, true);
        } else if (entity instanceof EntityDragon) {
            this.func_72785_a(entity, 160, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.func_72785_a(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.func_72785_a(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.func_72785_a(entity, 160, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityXPOrb) {
            this.func_72785_a(entity, 160, 20, true);
        } else if (entity instanceof EntityEnderCrystal) {
            this.func_72785_a(entity, 256, Integer.MAX_VALUE, false);
        }

    }

    public void func_72791_a(Entity entity, int i, int j) {
        this.func_72785_a(entity, i, j, false);
    }

    public void func_72785_a(Entity entity, int i, int j, boolean flag) {
        if (i > this.field_72792_d) {
            i = this.field_72792_d;
        }

        try {
            if (this.field_72794_c.func_76037_b(entity.func_145782_y())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, flag);

            this.field_72793_b.add(entitytrackerentry);
            this.field_72794_c.func_76038_a(entity.func_145782_y(), entitytrackerentry);
            entitytrackerentry.func_73125_b(this.field_72795_a.field_73010_i);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Adding entity to track");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Entity To Track");

            crashreportcategory.func_71507_a("Tracking range", i + " blocks");
            crashreportcategory.func_71500_a("Update interval", new EntityTracker$1(this, j));
            entity.func_85029_a(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.func_85058_a("Entity That Is Already Tracked");

            ((EntityTrackerEntry) this.field_72794_c.func_76041_a(entity.func_145782_y())).field_73132_a.func_85029_a(crashreportcategory1);

            try {
                throw new ReportedException(crashreport);
            } catch (ReportedException reportedexception) {
                EntityTracker.field_151249_a.error("\"Silently\" catching entity tracking error.", (Throwable) reportedexception);
            }
        }

    }

    public void func_72790_b(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entity;
            Iterator iterator = this.field_72793_b.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                entitytrackerentry.func_73118_a(entityplayermp);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) this.field_72794_c.func_76049_d(entity.func_145782_y());

        if (entitytrackerentry1 != null) {
            this.field_72793_b.remove(entitytrackerentry1);
            entitytrackerentry1.func_73119_a();
        }

    }

    public void func_72788_a() {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.field_72793_b.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.func_73122_a(this.field_72795_a.field_73010_i);
            if (entitytrackerentry.field_73133_n && entitytrackerentry.field_73132_a instanceof EntityPlayerMP) {
                arraylist.add((EntityPlayerMP) entitytrackerentry.field_73132_a);
            }
        }

        for (int i = 0; i < arraylist.size(); ++i) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) arraylist.get(i);
            Iterator iterator1 = this.field_72793_b.iterator();

            while (iterator1.hasNext()) {
                EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) iterator1.next();

                if (entitytrackerentry1.field_73132_a != entityplayermp) {
                    entitytrackerentry1.func_73117_b(entityplayermp);
                }
            }
        }

    }

    public void func_151247_a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.field_72794_c.func_76041_a(entity.func_145782_y());

        if (entitytrackerentry != null) {
            entitytrackerentry.func_151259_a(packet);
        }

    }

    public void func_151248_b(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.field_72794_c.func_76041_a(entity.func_145782_y());

        if (entitytrackerentry != null) {
            entitytrackerentry.func_151261_b(packet);
        }

    }

    public void func_72787_a(EntityPlayerMP entityplayermp) {
        Iterator iterator = this.field_72793_b.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.func_73123_c(entityplayermp);
        }

    }

    public void func_85172_a(EntityPlayerMP entityplayermp, Chunk chunk) {
        Iterator iterator = this.field_72793_b.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            if (entitytrackerentry.field_73132_a != entityplayermp && entitytrackerentry.field_73132_a.field_70176_ah == chunk.field_76635_g && entitytrackerentry.field_73132_a.field_70164_aj == chunk.field_76647_h) {
                entitytrackerentry.func_73117_b(entityplayermp);
            }
        }

    }
}
