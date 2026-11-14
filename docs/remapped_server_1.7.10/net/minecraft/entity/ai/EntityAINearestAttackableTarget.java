package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAINearestAttackableTarget extends EntityAITarget {

    private final Class field_75307_b;
    private final int field_75308_c;
    private final EntityAINearestAttackableTarget$Sorter field_75306_g;
    private final IEntitySelector field_82643_g;
    private EntityLivingBase field_75309_a;

    public EntityAINearestAttackableTarget(EntityCreature entitycreature, Class oclass, int i, boolean flag) {
        this(entitycreature, oclass, i, flag, false);
    }

    public EntityAINearestAttackableTarget(EntityCreature entitycreature, Class oclass, int i, boolean flag, boolean flag1) {
        this(entitycreature, oclass, i, flag, flag1, (IEntitySelector) null);
    }

    public EntityAINearestAttackableTarget(EntityCreature entitycreature, Class oclass, int i, boolean flag, boolean flag1, IEntitySelector ientityselector) {
        super(entitycreature, flag, flag1);
        this.field_75307_b = oclass;
        this.field_75308_c = i;
        this.field_75306_g = new EntityAINearestAttackableTarget$Sorter(entitycreature);
        this.func_75248_a(1);
        this.field_82643_g = new EntityAINearestAttackableTarget$1(this, ientityselector);
    }

    @Override
    public boolean func_75250_a() {
        if (this.field_75308_c > 0 && this.field_75299_d.func_70681_au().nextInt(this.field_75308_c) != 0) {
            return false;
        } else {
            double d0 = this.func_111175_f();
            List list = this.field_75299_d.field_70170_p.func_82733_a(this.field_75307_b, this.field_75299_d.field_70121_D.func_72314_b(d0, 4.0D, d0), this.field_82643_g);

            Collections.sort(list, this.field_75306_g);
            if (list.isEmpty()) {
                return false;
            } else {
                this.field_75309_a = (EntityLivingBase) list.get(0);
                return true;
            }
        }
    }

    @Override
    public void func_75249_e() {
        this.field_75299_d.func_70624_b(this.field_75309_a);
        super.func_75249_e();
    }
}
