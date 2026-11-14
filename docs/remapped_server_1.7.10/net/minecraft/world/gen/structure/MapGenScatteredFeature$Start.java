package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenScatteredFeature$Start extends StructureStart {

    public MapGenScatteredFeature$Start() {}

    public MapGenScatteredFeature$Start(World world, Random random, int i, int j) {
        super(i, j);
        BiomeGenBase biomegenbase = world.func_72807_a(i * 16 + 8, j * 16 + 8);

        if (biomegenbase != BiomeGenBase.field_76782_w && biomegenbase != BiomeGenBase.field_76792_x) {
            if (biomegenbase == BiomeGenBase.field_76780_h) {
                ComponentScatteredFeaturePieces$SwampHut componentscatteredfeaturepieces$swamphut = new ComponentScatteredFeaturePieces$SwampHut(random, i * 16, j * 16);

                this.field_75075_a.add(componentscatteredfeaturepieces$swamphut);
            } else {
                ComponentScatteredFeaturePieces$DesertPyramid componentscatteredfeaturepieces$desertpyramid = new ComponentScatteredFeaturePieces$DesertPyramid(random, i * 16, j * 16);

                this.field_75075_a.add(componentscatteredfeaturepieces$desertpyramid);
            }
        } else {
            ComponentScatteredFeaturePieces$JunglePyramid componentscatteredfeaturepieces$junglepyramid = new ComponentScatteredFeaturePieces$JunglePyramid(random, i * 16, j * 16);

            this.field_75075_a.add(componentscatteredfeaturepieces$junglepyramid);
        }

        this.func_75072_c();
    }
}
