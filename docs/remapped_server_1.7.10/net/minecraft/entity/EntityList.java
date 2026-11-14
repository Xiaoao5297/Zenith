package net.minecraft.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
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
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityList {

    private static final Logger field_151516_b = LogManager.getLogger();
    private static Map field_75625_b = new HashMap();
    private static Map field_75626_c = new HashMap();
    private static Map field_75623_d = new HashMap();
    private static Map field_75624_e = new HashMap();
    private static Map field_75622_f = new HashMap();
    public static HashMap field_75627_a = new LinkedHashMap();

    private static void func_75618_a(Class oclass, String s, int i) {
        if (EntityList.field_75625_b.containsKey(s)) {
            throw new IllegalArgumentException("ID is already registered: " + s);
        } else if (EntityList.field_75623_d.containsKey(i)) {
            throw new IllegalArgumentException("ID is already registered: " + i);
        } else {
            EntityList.field_75625_b.put(s, oclass);
            EntityList.field_75626_c.put(oclass, s);
            EntityList.field_75623_d.put(i, oclass);
            EntityList.field_75624_e.put(oclass, i);
            EntityList.field_75622_f.put(s, i);
        }
    }

    private static void func_75614_a(Class oclass, String s, int i, int j, int k) {
        func_75618_a(oclass, s, i);
        EntityList.field_75627_a.put(i, new EntityList$EntityEggInfo(i, j, k));
    }

    public static Entity func_75620_a(String s, World world) {
        Entity entity = null;

        try {
            Class oclass = (Class) EntityList.field_75625_b.get(s);

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(World.class).newInstance(world);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return entity;
    }

    public static Entity func_75615_a(NBTTagCompound nbttagcompound, World world) {
        Entity entity = null;

        if ("Minecart".equals(nbttagcompound.func_74779_i("id"))) {
            switch (nbttagcompound.func_74762_e("Type")) {
                case 0:
                    nbttagcompound.func_74778_a("id", "MinecartRideable");
                    break;
                case 1:
                    nbttagcompound.func_74778_a("id", "MinecartChest");
                    break;
                case 2:
                    nbttagcompound.func_74778_a("id", "MinecartFurnace");
            }

            nbttagcompound.func_82580_o("Type");
        }

        try {
            Class oclass = (Class) EntityList.field_75625_b.get(nbttagcompound.func_74779_i("id"));

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(World.class).newInstance(world);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity != null) {
            entity.func_70020_e(nbttagcompound);
        } else {
            EntityList.field_151516_b.warn("Skipping Entity with id " + nbttagcompound.func_74779_i("id"));
        }

        return entity;
    }

    public static Entity func_75616_a(int i, World world) {
        Entity entity = null;

        try {
            Class oclass = func_90035_a(i);

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(World.class).newInstance(world);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity == null) {
            EntityList.field_151516_b.warn("Skipping Entity with id " + i);
        }

        return entity;
    }

    public static int func_75619_a(Entity entity) {
        Class oclass = entity.getClass();

        return EntityList.field_75624_e.containsKey(oclass) ? (Integer) EntityList.field_75624_e.get(oclass) : 0;
    }

    public static Class func_90035_a(int i) {
        return (Class) EntityList.field_75623_d.get(i);
    }

    public static String func_75621_b(Entity entity) {
        return (String) EntityList.field_75626_c.get(entity.getClass());
    }

    public static String func_75617_a(int i) {
        Class oclass = func_90035_a(i);

        return oclass != null ? (String) EntityList.field_75626_c.get(oclass) : null;
    }

    public static void func_151514_a() {}

    public static Set func_151515_b() {
        return Collections.unmodifiableSet(EntityList.field_75622_f.keySet());
    }

    static {
        func_75618_a(EntityItem.class, "Item", 1);
        func_75618_a(EntityXPOrb.class, "XPOrb", 2);
        func_75618_a(EntityLeashKnot.class, "LeashKnot", 8);
        func_75618_a(EntityPainting.class, "Painting", 9);
        func_75618_a(EntityArrow.class, "Arrow", 10);
        func_75618_a(EntitySnowball.class, "Snowball", 11);
        func_75618_a(EntityLargeFireball.class, "Fireball", 12);
        func_75618_a(EntitySmallFireball.class, "SmallFireball", 13);
        func_75618_a(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        func_75618_a(EntityEnderEye.class, "EyeOfEnderSignal", 15);
        func_75618_a(EntityPotion.class, "ThrownPotion", 16);
        func_75618_a(EntityExpBottle.class, "ThrownExpBottle", 17);
        func_75618_a(EntityItemFrame.class, "ItemFrame", 18);
        func_75618_a(EntityWitherSkull.class, "WitherSkull", 19);
        func_75618_a(EntityTNTPrimed.class, "PrimedTnt", 20);
        func_75618_a(EntityFallingBlock.class, "FallingSand", 21);
        func_75618_a(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
        func_75618_a(EntityBoat.class, "Boat", 41);
        func_75618_a(EntityMinecartEmpty.class, "MinecartRideable", 42);
        func_75618_a(EntityMinecartChest.class, "MinecartChest", 43);
        func_75618_a(EntityMinecartFurnace.class, "MinecartFurnace", 44);
        func_75618_a(EntityMinecartTNT.class, "MinecartTNT", 45);
        func_75618_a(EntityMinecartHopper.class, "MinecartHopper", 46);
        func_75618_a(EntityMinecartMobSpawner.class, "MinecartSpawner", 47);
        func_75618_a(EntityMinecartCommandBlock.class, "MinecartCommandBlock", 40);
        func_75618_a(EntityLiving.class, "Mob", 48);
        func_75618_a(EntityMob.class, "Monster", 49);
        func_75614_a(EntityCreeper.class, "Creeper", 50, 894731, 0);
        func_75614_a(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
        func_75614_a(EntitySpider.class, "Spider", 52, 3419431, 11013646);
        func_75618_a(EntityGiantZombie.class, "Giant", 53);
        func_75614_a(EntityZombie.class, "Zombie", 54, 44975, 7969893);
        func_75614_a(EntitySlime.class, "Slime", 55, 5349438, 8306542);
        func_75614_a(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
        func_75614_a(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
        func_75614_a(EntityEnderman.class, "Enderman", 58, 1447446, 0);
        func_75614_a(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
        func_75614_a(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
        func_75614_a(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
        func_75614_a(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
        func_75618_a(EntityDragon.class, "EnderDragon", 63);
        func_75618_a(EntityWither.class, "WitherBoss", 64);
        func_75614_a(EntityBat.class, "Bat", 65, 4996656, 986895);
        func_75614_a(EntityWitch.class, "Witch", 66, 3407872, 5349438);
        func_75614_a(EntityPig.class, "Pig", 90, 15771042, 14377823);
        func_75614_a(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
        func_75614_a(EntityCow.class, "Cow", 92, 4470310, 10592673);
        func_75614_a(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
        func_75614_a(EntitySquid.class, "Squid", 94, 2243405, 7375001);
        func_75614_a(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
        func_75614_a(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
        func_75618_a(EntitySnowman.class, "SnowMan", 97);
        func_75614_a(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
        func_75618_a(EntityIronGolem.class, "VillagerGolem", 99);
        func_75614_a(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
        func_75614_a(EntityVillager.class, "Villager", 120, 5651507, 12422002);
        func_75618_a(EntityEnderCrystal.class, "EnderCrystal", 200);
    }
}
