package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISit extends EntityAIBase {

    private EntityTameable field_75272_a;
    private boolean field_75271_b;

    public EntityAISit(EntityTameable entitytameable) {
        this.field_75272_a = entitytameable;
        this.func_75248_a(5);
    }

    @Override
    public boolean func_75250_a() {
        if (!this.field_75272_a.func_70909_n()) {
            return false;
        } else if (this.field_75272_a.func_70090_H()) {
            return false;
        } else if (!this.field_75272_a.field_70122_E) {
            return false;
        } else {
            EntityLivingBase entitylivingbase = this.field_75272_a.func_70902_q();

            return entitylivingbase == null ? true : (this.field_75272_a.func_70068_e(entitylivingbase) < 144.0D && entitylivingbase.func_70643_av() != null ? false : this.field_75271_b);
        }
    }

    @Override
    public void func_75249_e() {
        this.field_75272_a.func_70661_as().func_75499_g();
        this.field_75272_a.func_70904_g(true);
    }

    @Override
    public void func_75251_c() {
        this.field_75272_a.func_70904_g(false);
    }

    public void func_75270_a(boolean flag) {
        this.field_75271_b = flag;
    }
}
