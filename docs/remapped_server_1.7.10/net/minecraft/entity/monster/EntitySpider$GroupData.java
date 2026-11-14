package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.potion.Potion;

public class EntitySpider$GroupData implements IEntityLivingData {

    public int field_111105_a;

    public EntitySpider$GroupData() {}

    public void func_111104_a(Random random) {
        int i = random.nextInt(5);

        if (i <= 1) {
            this.field_111105_a = Potion.field_76424_c.field_76415_H;
        } else if (i <= 2) {
            this.field_111105_a = Potion.field_76420_g.field_76415_H;
        } else if (i <= 3) {
            this.field_111105_a = Potion.field_76428_l.field_76415_H;
        } else if (i <= 4) {
            this.field_111105_a = Potion.field_76441_p.field_76415_H;
        }

    }
}
