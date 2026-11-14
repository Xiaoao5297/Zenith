package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.world.World;

public class MapGenNetherBridge$Start extends StructureStart {

    public MapGenNetherBridge$Start() {}

    public MapGenNetherBridge$Start(World world, Random random, int i, int j) {
        super(i, j);
        StructureNetherBridgePieces$Start structurenetherbridgepieces$start = new StructureNetherBridgePieces$Start(random, (i << 4) + 2, (j << 4) + 2);

        this.field_75075_a.add(structurenetherbridgepieces$start);
        structurenetherbridgepieces$start.func_74861_a(structurenetherbridgepieces$start, this.field_75075_a, random);
        ArrayList arraylist = structurenetherbridgepieces$start.field_74967_d;

        while (!arraylist.isEmpty()) {
            int k = random.nextInt(arraylist.size());
            StructureComponent structurecomponent = (StructureComponent) arraylist.remove(k);

            structurecomponent.func_74861_a(structurenetherbridgepieces$start, this.field_75075_a, random);
        }

        this.func_75072_c();
        this.func_75070_a(world, random, 48, 70);
    }
}
