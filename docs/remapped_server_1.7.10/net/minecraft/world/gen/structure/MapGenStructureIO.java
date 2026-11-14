package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {

    private static final Logger field_151687_a = LogManager.getLogger();
    private static Map field_143040_a = new HashMap();
    private static Map field_143038_b = new HashMap();
    private static Map field_143039_c = new HashMap();
    private static Map field_143037_d = new HashMap();

    private static void func_143034_b(Class oclass, String s) {
        MapGenStructureIO.field_143040_a.put(s, oclass);
        MapGenStructureIO.field_143038_b.put(oclass, s);
    }

    static void func_143031_a(Class oclass, String s) {
        MapGenStructureIO.field_143039_c.put(s, oclass);
        MapGenStructureIO.field_143037_d.put(oclass, s);
    }

    public static String func_143033_a(StructureStart structurestart) {
        return (String) MapGenStructureIO.field_143038_b.get(structurestart.getClass());
    }

    public static String func_143036_a(StructureComponent structurecomponent) {
        return (String) MapGenStructureIO.field_143037_d.get(structurecomponent.getClass());
    }

    public static StructureStart func_143035_a(NBTTagCompound nbttagcompound, World world) {
        StructureStart structurestart = null;

        try {
            Class oclass = (Class) MapGenStructureIO.field_143040_a.get(nbttagcompound.func_74779_i("id"));

            if (oclass != null) {
                structurestart = (StructureStart) oclass.newInstance();
            }
        } catch (Exception exception) {
            MapGenStructureIO.field_151687_a.warn("Failed Start with id " + nbttagcompound.func_74779_i("id"));
            exception.printStackTrace();
        }

        if (structurestart != null) {
            structurestart.func_143020_a(world, nbttagcompound);
        } else {
            MapGenStructureIO.field_151687_a.warn("Skipping Structure with id " + nbttagcompound.func_74779_i("id"));
        }

        return structurestart;
    }

    public static StructureComponent func_143032_b(NBTTagCompound nbttagcompound, World world) {
        StructureComponent structurecomponent = null;

        try {
            Class oclass = (Class) MapGenStructureIO.field_143039_c.get(nbttagcompound.func_74779_i("id"));

            if (oclass != null) {
                structurecomponent = (StructureComponent) oclass.newInstance();
            }
        } catch (Exception exception) {
            MapGenStructureIO.field_151687_a.warn("Failed Piece with id " + nbttagcompound.func_74779_i("id"));
            exception.printStackTrace();
        }

        if (structurecomponent != null) {
            structurecomponent.func_143009_a(world, nbttagcompound);
        } else {
            MapGenStructureIO.field_151687_a.warn("Skipping Piece with id " + nbttagcompound.func_74779_i("id"));
        }

        return structurecomponent;
    }

    static {
        func_143034_b(StructureMineshaftStart.class, "Mineshaft");
        func_143034_b(MapGenVillage$Start.class, "Village");
        func_143034_b(MapGenNetherBridge$Start.class, "Fortress");
        func_143034_b(MapGenStronghold$Start.class, "Stronghold");
        func_143034_b(MapGenScatteredFeature$Start.class, "Temple");
        StructureMineshaftPieces.func_143048_a();
        StructureVillagePieces.func_143016_a();
        StructureNetherBridgePieces.func_143049_a();
        StructureStrongholdPieces.func_143046_a();
        ComponentScatteredFeaturePieces.func_143045_a();
    }
}
