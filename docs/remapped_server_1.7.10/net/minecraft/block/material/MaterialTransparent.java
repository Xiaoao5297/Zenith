package net.minecraft.block.material;

public class MaterialTransparent extends Material {

    public MaterialTransparent(MapColor mapcolor) {
        super(mapcolor);
        this.func_76231_i();
    }

    @Override
    public boolean func_76220_a() {
        return false;
    }

    @Override
    public boolean func_76228_b() {
        return false;
    }

    @Override
    public boolean func_76230_c() {
        return false;
    }
}
