package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;

public class StructureMineshaftStart extends StructureStart {

    public StructureMineshaftStart() {}

    public StructureMineshaftStart(World world, Random random, int i, int j) {
        super(i, j);
        StructureMineshaftPieces$Room structuremineshaftpieces$room = new StructureMineshaftPieces$Room(0, random, (i << 4) + 2, (j << 4) + 2);

        this.field_75075_a.add(structuremineshaftpieces$room);
        structuremineshaftpieces$room.func_74861_a(structuremineshaftpieces$room, this.field_75075_a, random);
        this.func_75072_c();
        this.func_75067_a(world, random, 10);
    }
}
