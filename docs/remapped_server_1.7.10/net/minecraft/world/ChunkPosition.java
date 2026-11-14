package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition {

    public final int field_151329_a;
    public final int field_151327_b;
    public final int field_151328_c;

    public ChunkPosition(int i, int j, int k) {
        this.field_151329_a = i;
        this.field_151327_b = j;
        this.field_151328_c = k;
    }

    public ChunkPosition(Vec3 vec3) {
        this(MathHelper.func_76128_c(vec3.field_72450_a), MathHelper.func_76128_c(vec3.field_72448_b), MathHelper.func_76128_c(vec3.field_72449_c));
    }

    public boolean equals(Object object) {
        if (!(object instanceof ChunkPosition)) {
            return false;
        } else {
            ChunkPosition chunkposition = (ChunkPosition) object;

            return chunkposition.field_151329_a == this.field_151329_a && chunkposition.field_151327_b == this.field_151327_b && chunkposition.field_151328_c == this.field_151328_c;
        }
    }

    public int hashCode() {
        return this.field_151329_a * 8976890 + this.field_151327_b * 981131 + this.field_151328_c;
    }
}
