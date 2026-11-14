package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.world.World;

public class MapGenStronghold$Start extends StructureStart {

    public MapGenStronghold$Start() {}

    public MapGenStronghold$Start(World world, Random random, int i, int j) {
        super(i, j);
        StructureStrongholdPieces.func_75198_a();
        StructureStrongholdPieces$Stairs2 structurestrongholdpieces$stairs2 = new StructureStrongholdPieces$Stairs2(0, random, (i << 4) + 2, (j << 4) + 2);

        this.field_75075_a.add(structurestrongholdpieces$stairs2);
        structurestrongholdpieces$stairs2.func_74861_a(structurestrongholdpieces$stairs2, this.field_75075_a, random);
        List list = structurestrongholdpieces$stairs2.field_75026_c;

        while (!list.isEmpty()) {
            int k = random.nextInt(list.size());
            StructureComponent structurecomponent = (StructureComponent) list.remove(k);

            structurecomponent.func_74861_a(structurestrongholdpieces$stairs2, this.field_75075_a, random);
        }

        this.func_75072_c();
        this.func_75067_a(world, random, 10);
    }
}
