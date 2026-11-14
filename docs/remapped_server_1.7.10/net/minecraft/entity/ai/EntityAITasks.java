package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {

    private static final Logger field_151506_a = LogManager.getLogger();
    private List field_75782_a = new ArrayList();
    private List field_75780_b = new ArrayList();
    private final Profiler field_75781_c;
    private int field_75778_d;
    private int field_75779_e = 3;

    public EntityAITasks(Profiler profiler) {
        this.field_75781_c = profiler;
    }

    public void func_75776_a(int i, EntityAIBase entityaibase) {
        this.field_75782_a.add(new EntityAITasks$EntityAITaskEntry(this, i, entityaibase));
    }

    public void func_85156_a(EntityAIBase entityaibase) {
        Iterator iterator = this.field_75782_a.iterator();

        while (iterator.hasNext()) {
            EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry = (EntityAITasks$EntityAITaskEntry) iterator.next();
            EntityAIBase entityaibase1 = entityaitasks$entityaitaskentry.field_75733_a;

            if (entityaibase1 == entityaibase) {
                if (this.field_75780_b.contains(entityaitasks$entityaitaskentry)) {
                    entityaibase1.func_75251_c();
                    this.field_75780_b.remove(entityaitasks$entityaitaskentry);
                }

                iterator.remove();
            }
        }

    }

    public void func_75774_a() {
        ArrayList arraylist = new ArrayList();
        Iterator iterator;
        EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry;

        if (this.field_75778_d++ % this.field_75779_e == 0) {
            iterator = this.field_75782_a.iterator();

            while (iterator.hasNext()) {
                entityaitasks$entityaitaskentry = (EntityAITasks$EntityAITaskEntry) iterator.next();
                boolean flag = this.field_75780_b.contains(entityaitasks$entityaitaskentry);

                if (flag) {
                    if (this.func_75775_b(entityaitasks$entityaitaskentry) && this.func_75773_a(entityaitasks$entityaitaskentry)) {
                        continue;
                    }

                    entityaitasks$entityaitaskentry.field_75733_a.func_75251_c();
                    this.field_75780_b.remove(entityaitasks$entityaitaskentry);
                }

                if (this.func_75775_b(entityaitasks$entityaitaskentry) && entityaitasks$entityaitaskentry.field_75733_a.func_75250_a()) {
                    arraylist.add(entityaitasks$entityaitaskentry);
                    this.field_75780_b.add(entityaitasks$entityaitaskentry);
                }
            }
        } else {
            iterator = this.field_75780_b.iterator();

            while (iterator.hasNext()) {
                entityaitasks$entityaitaskentry = (EntityAITasks$EntityAITaskEntry) iterator.next();
                if (!entityaitasks$entityaitaskentry.field_75733_a.func_75253_b()) {
                    entityaitasks$entityaitaskentry.field_75733_a.func_75251_c();
                    iterator.remove();
                }
            }
        }

        this.field_75781_c.func_76320_a("goalStart");
        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            entityaitasks$entityaitaskentry = (EntityAITasks$EntityAITaskEntry) iterator.next();
            this.field_75781_c.func_76320_a(entityaitasks$entityaitaskentry.field_75733_a.getClass().getSimpleName());
            entityaitasks$entityaitaskentry.field_75733_a.func_75249_e();
            this.field_75781_c.func_76319_b();
        }

        this.field_75781_c.func_76319_b();
        this.field_75781_c.func_76320_a("goalTick");
        iterator = this.field_75780_b.iterator();

        while (iterator.hasNext()) {
            entityaitasks$entityaitaskentry = (EntityAITasks$EntityAITaskEntry) iterator.next();
            entityaitasks$entityaitaskentry.field_75733_a.func_75246_d();
        }

        this.field_75781_c.func_76319_b();
    }

    private boolean func_75773_a(EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry) {
        this.field_75781_c.func_76320_a("canContinue");
        boolean flag = entityaitasks$entityaitaskentry.field_75733_a.func_75253_b();

        this.field_75781_c.func_76319_b();
        return flag;
    }

    private boolean func_75775_b(EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry) {
        this.field_75781_c.func_76320_a("canUse");
        Iterator iterator = this.field_75782_a.iterator();

        while (iterator.hasNext()) {
            EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry1 = (EntityAITasks$EntityAITaskEntry) iterator.next();

            if (entityaitasks$entityaitaskentry1 != entityaitasks$entityaitaskentry) {
                if (entityaitasks$entityaitaskentry.field_75731_b >= entityaitasks$entityaitaskentry1.field_75731_b) {
                    if (this.field_75780_b.contains(entityaitasks$entityaitaskentry1) && !this.func_75777_a(entityaitasks$entityaitaskentry, entityaitasks$entityaitaskentry1)) {
                        this.field_75781_c.func_76319_b();
                        return false;
                    }
                } else if (this.field_75780_b.contains(entityaitasks$entityaitaskentry1) && !entityaitasks$entityaitaskentry1.field_75733_a.func_75252_g()) {
                    this.field_75781_c.func_76319_b();
                    return false;
                }
            }
        }

        this.field_75781_c.func_76319_b();
        return true;
    }

    private boolean func_75777_a(EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry, EntityAITasks$EntityAITaskEntry entityaitasks$entityaitaskentry1) {
        return (entityaitasks$entityaitaskentry.field_75733_a.func_75247_h() & entityaitasks$entityaitaskentry1.field_75733_a.func_75247_h()) == 0;
    }
}
