package net.minecraft.entity.player;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList$EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent$Action;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender {

    public InventoryPlayer field_71071_by = new InventoryPlayer(this);
    private InventoryEnderChest field_71078_a = new InventoryEnderChest();
    public Container field_71069_bz;
    public Container field_71070_bA;
    protected FoodStats field_71100_bB = new FoodStats();
    protected int field_71101_bC;
    public float field_71107_bF;
    public float field_71109_bG;
    public int field_71090_bL;
    public double field_71091_bM;
    public double field_71096_bN;
    public double field_71097_bO;
    public double field_71094_bP;
    public double field_71095_bQ;
    public double field_71085_bR;
    protected boolean field_71083_bS;
    public ChunkCoordinates field_71081_bT;
    private int field_71076_b;
    public float field_71079_bU;
    public float field_71089_bV;
    private ChunkCoordinates field_71077_c;
    private boolean field_82248_d;
    private ChunkCoordinates field_71073_d;
    public PlayerCapabilities field_71075_bZ = new PlayerCapabilities();
    public int field_71068_ca;
    public int field_71067_cb;
    public float field_71106_cc;
    private ItemStack field_71074_e;
    private int field_71072_f;
    protected float field_71108_cd = 0.1F;
    protected float field_71102_ce = 0.02F;
    private int field_82249_h;
    private final GameProfile field_146106_i;
    public EntityFishHook field_71104_cf;

    public EntityPlayer(World world, GameProfile gameprofile) {
        super(world);
        this.field_96093_i = func_146094_a(gameprofile);
        this.field_146106_i = gameprofile;
        this.field_71069_bz = new ContainerPlayer(this.field_71071_by, !world.field_72995_K, this);
        this.field_71070_bA = this.field_71069_bz;
        this.field_70129_M = 1.62F;
        ChunkCoordinates chunkcoordinates = world.func_72861_E();

        this.func_70012_b((double) chunkcoordinates.field_71574_a + 0.5D, (double) (chunkcoordinates.field_71572_b + 1), (double) chunkcoordinates.field_71573_c + 0.5D, 0.0F, 0.0F);
        this.field_70741_aB = 180.0F;
        this.field_70174_ab = 20;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0D);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, (byte) 0);
        this.field_70180_af.func_75682_a(17, 0.0F);
        this.field_70180_af.func_75682_a(18, 0);
    }

    public boolean func_71039_bw() {
        return this.field_71074_e != null;
    }

    public void func_71034_by() {
        if (this.field_71074_e != null) {
            this.field_71074_e.func_77974_b(this.field_70170_p, this, this.field_71072_f);
        }

        this.func_71041_bz();
    }

    public void func_71041_bz() {
        this.field_71074_e = null;
        this.field_71072_f = 0;
        if (!this.field_70170_p.field_72995_K) {
            this.func_70019_c(false);
        }

    }

    public boolean func_70632_aY() {
        return this.func_71039_bw() && this.field_71074_e.func_77973_b().func_77661_b(this.field_71074_e) == EnumAction.block;
    }

    @Override
    public void func_70071_h_() {
        if (this.field_71074_e != null) {
            ItemStack itemstack = this.field_71071_by.func_70448_g();

            if (itemstack == this.field_71074_e) {
                if (this.field_71072_f <= 25 && this.field_71072_f % 4 == 0) {
                    this.func_71010_c(itemstack, 5);
                }

                if (--this.field_71072_f == 0 && !this.field_70170_p.field_72995_K) {
                    this.func_71036_o();
                }
            } else {
                this.func_71041_bz();
            }
        }

        if (this.field_71090_bL > 0) {
            --this.field_71090_bL;
        }

        if (this.func_70608_bn()) {
            ++this.field_71076_b;
            if (this.field_71076_b > 100) {
                this.field_71076_b = 100;
            }

            if (!this.field_70170_p.field_72995_K) {
                if (!this.func_71065_l()) {
                    this.func_70999_a(true, true, false);
                } else if (this.field_70170_p.func_72935_r()) {
                    this.func_70999_a(false, true, true);
                }
            }
        } else if (this.field_71076_b > 0) {
            ++this.field_71076_b;
            if (this.field_71076_b >= 110) {
                this.field_71076_b = 0;
            }
        }

        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.field_71070_bA != null && !this.field_71070_bA.func_75145_c(this)) {
            this.func_71053_j();
            this.field_71070_bA = this.field_71069_bz;
        }

        if (this.func_70027_ad() && this.field_71075_bZ.field_75102_a) {
            this.func_70066_B();
        }

        this.field_71091_bM = this.field_71094_bP;
        this.field_71096_bN = this.field_71095_bQ;
        this.field_71097_bO = this.field_71085_bR;
        double d0 = this.field_70165_t - this.field_71094_bP;
        double d1 = this.field_70163_u - this.field_71095_bQ;
        double d2 = this.field_70161_v - this.field_71085_bR;
        double d3 = 10.0D;

        if (d0 > d3) {
            this.field_71091_bM = this.field_71094_bP = this.field_70165_t;
        }

        if (d2 > d3) {
            this.field_71097_bO = this.field_71085_bR = this.field_70161_v;
        }

        if (d1 > d3) {
            this.field_71096_bN = this.field_71095_bQ = this.field_70163_u;
        }

        if (d0 < -d3) {
            this.field_71091_bM = this.field_71094_bP = this.field_70165_t;
        }

        if (d2 < -d3) {
            this.field_71097_bO = this.field_71085_bR = this.field_70161_v;
        }

        if (d1 < -d3) {
            this.field_71096_bN = this.field_71095_bQ = this.field_70163_u;
        }

        this.field_71094_bP += d0 * 0.25D;
        this.field_71085_bR += d2 * 0.25D;
        this.field_71095_bQ += d1 * 0.25D;
        if (this.field_70154_o == null) {
            this.field_71073_d = null;
        }

        if (!this.field_70170_p.field_72995_K) {
            this.field_71100_bB.func_75118_a(this);
            this.func_71064_a(StatList.field_75948_k, 1);
        }

    }

    @Override
    public int func_82145_z() {
        return this.field_71075_bZ.field_75102_a ? 0 : 80;
    }

    @Override
    protected String func_145776_H() {
        return "game.player.swim";
    }

    @Override
    protected String func_145777_O() {
        return "game.player.swim.splash";
    }

    @Override
    public int func_82147_ab() {
        return 10;
    }

    @Override
    public void func_85030_a(String s, float f, float f1) {
        this.field_70170_p.func_85173_a(this, s, f, f1);
    }

    protected void func_71010_c(ItemStack itemstack, int i) {
        if (itemstack.func_77975_n() == EnumAction.drink) {
            this.func_85030_a("random.drink", 0.5F, this.field_70170_p.field_73012_v.nextFloat() * 0.1F + 0.9F);
        }

        if (itemstack.func_77975_n() == EnumAction.eat) {
            for (int j = 0; j < i; ++j) {
                Vec3 vec3 = Vec3.func_72443_a(((double) this.field_70146_Z.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

                vec3.func_72440_a(-this.field_70125_A * 3.1415927F / 180.0F);
                vec3.func_72442_b(-this.field_70177_z * 3.1415927F / 180.0F);
                Vec3 vec31 = Vec3.func_72443_a(((double) this.field_70146_Z.nextFloat() - 0.5D) * 0.3D, (double) (-this.field_70146_Z.nextFloat()) * 0.6D - 0.3D, 0.6D);

                vec31.func_72440_a(-this.field_70125_A * 3.1415927F / 180.0F);
                vec31.func_72442_b(-this.field_70177_z * 3.1415927F / 180.0F);
                vec31 = vec31.func_72441_c(this.field_70165_t, this.field_70163_u + (double) this.func_70047_e(), this.field_70161_v);
                String s = "iconcrack_" + Item.func_150891_b(itemstack.func_77973_b());

                if (itemstack.func_77981_g()) {
                    s = s + "_" + itemstack.func_77960_j();
                }

                this.field_70170_p.func_72869_a(s, vec31.field_72450_a, vec31.field_72448_b, vec31.field_72449_c, vec3.field_72450_a, vec3.field_72448_b + 0.05D, vec3.field_72449_c);
            }

            this.func_85030_a("random.eat", 0.5F + 0.5F * (float) this.field_70146_Z.nextInt(2), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
        }

    }

    protected void func_71036_o() {
        if (this.field_71074_e != null) {
            this.func_71010_c(this.field_71074_e, 16);
            int i = this.field_71074_e.field_77994_a;
            ItemStack itemstack = this.field_71074_e.func_77950_b(this.field_70170_p, this);

            if (itemstack != this.field_71074_e || itemstack != null && itemstack.field_77994_a != i) {
                this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c] = itemstack;
                if (itemstack.field_77994_a == 0) {
                    this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c] = null;
                }
            }

            this.func_71041_bz();
        }

    }

    @Override
    protected boolean func_70610_aX() {
        return this.func_110143_aJ() <= 0.0F || this.func_70608_bn();
    }

    protected void func_71053_j() {
        this.field_71070_bA = this.field_71069_bz;
    }

    @Override
    public void func_70078_a(Entity entity) {
        if (this.field_70154_o != null && entity == null) {
            if (!this.field_70170_p.field_72995_K) {
                this.func_110145_l(this.field_70154_o);
            }

            if (this.field_70154_o != null) {
                this.field_70154_o.field_70153_n = null;
            }

            this.field_70154_o = null;
        } else {
            super.func_70078_a(entity);
        }
    }

    @Override
    public void func_70098_U() {
        if (!this.field_70170_p.field_72995_K && this.func_70093_af()) {
            this.func_70078_a((Entity) null);
            this.func_70095_a(false);
        } else {
            double d0 = this.field_70165_t;
            double d1 = this.field_70163_u;
            double d2 = this.field_70161_v;
            float f = this.field_70177_z;
            float f1 = this.field_70125_A;

            super.func_70098_U();
            this.field_71107_bF = this.field_71109_bG;
            this.field_71109_bG = 0.0F;
            this.func_71015_k(this.field_70165_t - d0, this.field_70163_u - d1, this.field_70161_v - d2);
            if (this.field_70154_o instanceof EntityPig) {
                this.field_70125_A = f1;
                this.field_70177_z = f;
                this.field_70761_aq = ((EntityPig) this.field_70154_o).field_70761_aq;
            }

        }
    }

    @Override
    protected void func_70626_be() {
        super.func_70626_be();
        this.func_82168_bl();
    }

    @Override
    public void func_70636_d() {
        if (this.field_71101_bC > 0) {
            --this.field_71101_bC;
        }

        if (this.field_70170_p.field_73013_u == EnumDifficulty.PEACEFUL && this.func_110143_aJ() < this.func_110138_aP() && this.field_70170_p.func_82736_K().func_82766_b("naturalRegeneration") && this.field_70173_aa % 20 * 12 == 0) {
            this.func_70691_i(1.0F);
        }

        this.field_71071_by.func_70429_k();
        this.field_71107_bF = this.field_71109_bG;
        super.func_70636_d();
        IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

        if (!this.field_70170_p.field_72995_K) {
            iattributeinstance.func_111128_a((double) this.field_71075_bZ.func_75094_b());
        }

        this.field_70747_aH = this.field_71102_ce;
        if (this.func_70051_ag()) {
            this.field_70747_aH = (float) ((double) this.field_70747_aH + (double) this.field_71102_ce * 0.3D);
        }

        this.func_70659_e((float) iattributeinstance.func_111126_e());
        float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
        float f1 = (float) Math.atan(-this.field_70181_x * 0.20000000298023224D) * 15.0F;

        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
            f = 0.0F;
        }

        if (this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
            f1 = 0.0F;
        }

        this.field_71109_bG += (f - this.field_71109_bG) * 0.4F;
        this.field_70726_aT += (f1 - this.field_70726_aT) * 0.8F;
        if (this.func_110143_aJ() > 0.0F) {
            AxisAlignedBB axisalignedbb = null;

            if (this.field_70154_o != null && !this.field_70154_o.field_70128_L) {
                axisalignedbb = this.field_70121_D.func_111270_a(this.field_70154_o.field_70121_D).func_72314_b(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.field_70121_D.func_72314_b(1.0D, 0.5D, 1.0D);
            }

            List list = this.field_70170_p.func_72839_b(this, axisalignedbb);

            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (!entity.field_70128_L) {
                        this.func_71044_o(entity);
                    }
                }
            }
        }

    }

    private void func_71044_o(Entity entity) {
        entity.func_70100_b_(this);
    }

    public int func_71037_bA() {
        return this.field_70180_af.func_75679_c(18);
    }

    public void func_85040_s(int i) {
        this.field_70180_af.func_75692_b(18, i);
    }

    public void func_85039_t(int i) {
        int j = this.func_71037_bA();

        this.field_70180_af.func_75692_b(18, j + i);
    }

    @Override
    public void func_70645_a(DamageSource damagesource) {
        super.func_70645_a(damagesource);
        this.func_70105_a(0.2F, 0.2F);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70181_x = 0.10000000149011612D;
        if (this.func_70005_c_().equals("Notch")) {
            this.func_146097_a(new ItemStack(Items.field_151034_e, 1), true, false);
        }

        if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
            this.field_71071_by.func_70436_m();
        }

        if (damagesource != null) {
            this.field_70159_w = (double) (-MathHelper.func_76134_b((this.field_70739_aP + this.field_70177_z) * 3.1415927F / 180.0F) * 0.1F);
            this.field_70179_y = (double) (-MathHelper.func_76126_a((this.field_70739_aP + this.field_70177_z) * 3.1415927F / 180.0F) * 0.1F);
        } else {
            this.field_70159_w = this.field_70179_y = 0.0D;
        }

        this.field_70129_M = 0.1F;
        this.func_71064_a(StatList.field_75960_y, 1);
    }

    @Override
    protected String func_70621_aR() {
        return "game.player.hurt";
    }

    @Override
    protected String func_70673_aS() {
        return "game.player.die";
    }

    @Override
    public void func_70084_c(Entity entity, int i) {
        this.func_85039_t(i);
        Collection collection = this.func_96123_co().func_96520_a(IScoreObjectiveCriteria.field_96640_e);

        if (entity instanceof EntityPlayer) {
            this.func_71064_a(StatList.field_75932_A, 1);
            collection.addAll(this.func_96123_co().func_96520_a(IScoreObjectiveCriteria.field_96639_d));
        } else {
            this.func_71064_a(StatList.field_75959_z, 1);
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
            Score score = this.func_96123_co().func_96529_a(this.func_70005_c_(), scoreobjective);

            score.func_96648_a();
        }

    }

    public EntityItem func_71040_bB(boolean flag) {
        return this.func_146097_a(this.field_71071_by.func_70298_a(this.field_71071_by.field_70461_c, flag && this.field_71071_by.func_70448_g() != null ? this.field_71071_by.func_70448_g().field_77994_a : 1), false, true);
    }

    public EntityItem func_71019_a(ItemStack itemstack, boolean flag) {
        return this.func_146097_a(itemstack, false, false);
    }

    public EntityItem func_146097_a(ItemStack itemstack, boolean flag, boolean flag1) {
        if (itemstack == null) {
            return null;
        } else if (itemstack.field_77994_a == 0) {
            return null;
        } else {
            EntityItem entityitem = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u - 0.30000001192092896D + (double) this.func_70047_e(), this.field_70161_v, itemstack);

            entityitem.field_145804_b = 40;
            if (flag1) {
                entityitem.func_145799_b(this.func_70005_c_());
            }

            float f = 0.1F;
            float f1;

            if (flag) {
                f1 = this.field_70146_Z.nextFloat() * 0.5F;
                float f2 = this.field_70146_Z.nextFloat() * 3.1415927F * 2.0F;

                entityitem.field_70159_w = (double) (-MathHelper.func_76126_a(f2) * f1);
                entityitem.field_70179_y = (double) (MathHelper.func_76134_b(f2) * f1);
                entityitem.field_70181_x = 0.20000000298023224D;
            } else {
                f = 0.3F;
                entityitem.field_70159_w = (double) (-MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F) * f);
                entityitem.field_70179_y = (double) (MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F) * f);
                entityitem.field_70181_x = (double) (-MathHelper.func_76126_a(this.field_70125_A / 180.0F * 3.1415927F) * f + 0.1F);
                f = 0.02F;
                f1 = this.field_70146_Z.nextFloat() * 3.1415927F * 2.0F;
                f *= this.field_70146_Z.nextFloat();
                entityitem.field_70159_w += Math.cos((double) f1) * (double) f;
                entityitem.field_70181_x += (double) ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.1F);
                entityitem.field_70179_y += Math.sin((double) f1) * (double) f;
            }

            this.func_71012_a(entityitem);
            this.func_71064_a(StatList.field_75952_v, 1);
            return entityitem;
        }
    }

    protected void func_71012_a(EntityItem entityitem) {
        this.field_70170_p.func_72838_d(entityitem);
    }

    public float func_146096_a(Block block, boolean flag) {
        float f = this.field_71071_by.func_146023_a(block);

        if (f > 1.0F) {
            int i = EnchantmentHelper.func_77509_b(this);
            ItemStack itemstack = this.field_71071_by.func_70448_g();

            if (i > 0 && itemstack != null) {
                float f1 = (float) (i * i + 1);

                if (!itemstack.func_150998_b(block) && f <= 1.0F) {
                    f += f1 * 0.08F;
                } else {
                    f += f1;
                }
            }
        }

        if (this.func_70644_a(Potion.field_76422_e)) {
            f *= 1.0F + (float) (this.func_70660_b(Potion.field_76422_e).func_76458_c() + 1) * 0.2F;
        }

        if (this.func_70644_a(Potion.field_76419_f)) {
            f *= 1.0F - (float) (this.func_70660_b(Potion.field_76419_f).func_76458_c() + 1) * 0.2F;
        }

        if (this.func_70055_a(Material.field_151586_h) && !EnchantmentHelper.func_77510_g(this)) {
            f /= 5.0F;
        }

        if (!this.field_70122_E) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean func_146099_a(Block block) {
        return this.field_71071_by.func_146025_b(block);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_96093_i = func_146094_a(this.field_146106_i);
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Inventory", 10);

        this.field_71071_by.func_70443_b(nbttaglist);
        this.field_71071_by.field_70461_c = nbttagcompound.func_74762_e("SelectedItemSlot");
        this.field_71083_bS = nbttagcompound.func_74767_n("Sleeping");
        this.field_71076_b = nbttagcompound.func_74765_d("SleepTimer");
        this.field_71106_cc = nbttagcompound.func_74760_g("XpP");
        this.field_71068_ca = nbttagcompound.func_74762_e("XpLevel");
        this.field_71067_cb = nbttagcompound.func_74762_e("XpTotal");
        this.func_85040_s(nbttagcompound.func_74762_e("Score"));
        if (this.field_71083_bS) {
            this.field_71081_bT = new ChunkCoordinates(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v));
            this.func_70999_a(true, true, false);
        }

        if (nbttagcompound.func_150297_b("SpawnX", 99) && nbttagcompound.func_150297_b("SpawnY", 99) && nbttagcompound.func_150297_b("SpawnZ", 99)) {
            this.field_71077_c = new ChunkCoordinates(nbttagcompound.func_74762_e("SpawnX"), nbttagcompound.func_74762_e("SpawnY"), nbttagcompound.func_74762_e("SpawnZ"));
            this.field_82248_d = nbttagcompound.func_74767_n("SpawnForced");
        }

        this.field_71100_bB.func_75112_a(nbttagcompound);
        this.field_71075_bZ.func_75095_b(nbttagcompound);
        if (nbttagcompound.func_150297_b("EnderItems", 9)) {
            NBTTagList nbttaglist1 = nbttagcompound.func_150295_c("EnderItems", 10);

            this.field_71078_a.func_70486_a(nbttaglist1);
        }

    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74782_a("Inventory", this.field_71071_by.func_70442_a(new NBTTagList()));
        nbttagcompound.func_74768_a("SelectedItemSlot", this.field_71071_by.field_70461_c);
        nbttagcompound.func_74757_a("Sleeping", this.field_71083_bS);
        nbttagcompound.func_74777_a("SleepTimer", (short) this.field_71076_b);
        nbttagcompound.func_74776_a("XpP", this.field_71106_cc);
        nbttagcompound.func_74768_a("XpLevel", this.field_71068_ca);
        nbttagcompound.func_74768_a("XpTotal", this.field_71067_cb);
        nbttagcompound.func_74768_a("Score", this.func_71037_bA());
        if (this.field_71077_c != null) {
            nbttagcompound.func_74768_a("SpawnX", this.field_71077_c.field_71574_a);
            nbttagcompound.func_74768_a("SpawnY", this.field_71077_c.field_71572_b);
            nbttagcompound.func_74768_a("SpawnZ", this.field_71077_c.field_71573_c);
            nbttagcompound.func_74757_a("SpawnForced", this.field_82248_d);
        }

        this.field_71100_bB.func_75117_b(nbttagcompound);
        this.field_71075_bZ.func_75091_a(nbttagcompound);
        nbttagcompound.func_74782_a("EnderItems", this.field_71078_a.func_70487_g());
    }

    public void func_71007_a(IInventory iinventory) {}

    public void func_146093_a(TileEntityHopper tileentityhopper) {}

    public void func_96125_a(EntityMinecartHopper entityminecarthopper) {}

    public void func_110298_a(EntityHorse entityhorse, IInventory iinventory) {}

    public void func_71002_c(int i, int j, int k, String s) {}

    public void func_82244_d(int i, int j, int k) {}

    public void func_71058_b(int i, int j, int k) {}

    @Override
    public float func_70047_e() {
        return 0.12F;
    }

    protected void func_71061_d_() {
        this.field_70129_M = 1.62F;
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else if (this.field_71075_bZ.field_75102_a && !damagesource.func_76357_e()) {
            return false;
        } else {
            this.field_70708_bq = 0;
            if (this.func_110143_aJ() <= 0.0F) {
                return false;
            } else {
                if (this.func_70608_bn() && !this.field_70170_p.field_72995_K) {
                    this.func_70999_a(true, true, false);
                }

                if (damagesource.func_76350_n()) {
                    if (this.field_70170_p.field_73013_u == EnumDifficulty.PEACEFUL) {
                        f = 0.0F;
                    }

                    if (this.field_70170_p.field_73013_u == EnumDifficulty.EASY) {
                        f = f / 2.0F + 1.0F;
                    }

                    if (this.field_70170_p.field_73013_u == EnumDifficulty.HARD) {
                        f = f * 3.0F / 2.0F;
                    }
                }

                if (f == 0.0F) {
                    return false;
                } else {
                    Entity entity = damagesource.func_76346_g();

                    if (entity instanceof EntityArrow && ((EntityArrow) entity).field_70250_c != null) {
                        entity = ((EntityArrow) entity).field_70250_c;
                    }

                    this.func_71064_a(StatList.field_75961_x, Math.round(f * 10.0F));
                    return super.func_70097_a(damagesource, f);
                }
            }
        }
    }

    public boolean func_96122_a(EntityPlayer entityplayer) {
        Team team = this.func_96124_cp();
        Team team1 = entityplayer.func_96124_cp();

        return team == null ? true : (!team.func_142054_a(team1) ? true : team.func_96665_g());
    }

    @Override
    protected void func_70675_k(float f) {
        this.field_71071_by.func_70449_g(f);
    }

    @Override
    public int func_70658_aO() {
        return this.field_71071_by.func_70430_l();
    }

    public float func_82243_bO() {
        int i = 0;
        ItemStack[] aitemstack = this.field_71071_by.field_70460_b;
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null) {
                ++i;
            }
        }

        return (float) i / (float) this.field_71071_by.field_70460_b.length;
    }

    @Override
    protected void func_70665_d(DamageSource damagesource, float f) {
        if (!this.func_85032_ar()) {
            if (!damagesource.func_76363_c() && this.func_70632_aY() && f > 0.0F) {
                f = (1.0F + f) * 0.5F;
            }

            f = this.func_70655_b(damagesource, f);
            f = this.func_70672_c(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.func_110139_bj(), 0.0F);
            this.func_110149_m(this.func_110139_bj() - (f1 - f));
            if (f != 0.0F) {
                this.func_71020_j(damagesource.func_76345_d());
                float f2 = this.func_110143_aJ();

                this.func_70606_j(this.func_110143_aJ() - f);
                this.func_110142_aN().func_94547_a(damagesource, f2, f);
            }
        }
    }

    public void func_146101_a(TileEntityFurnace tileentityfurnace) {}

    public void func_146102_a(TileEntityDispenser tileentitydispenser) {}

    public void func_146100_a(TileEntity tileentity) {}

    public void func_146095_a(CommandBlockLogic commandblocklogic) {}

    public void func_146098_a(TileEntityBrewingStand tileentitybrewingstand) {}

    public void func_146104_a(TileEntityBeacon tileentitybeacon) {}

    public void func_71030_a(IMerchant imerchant, String s) {}

    public void func_71048_c(ItemStack itemstack) {}

    public boolean func_70998_m(Entity entity) {
        ItemStack itemstack = this.func_71045_bC();
        ItemStack itemstack1 = itemstack != null ? itemstack.func_77946_l() : null;

        if (!entity.func_130002_c(this)) {
            if (itemstack != null && entity instanceof EntityLivingBase) {
                if (this.field_71075_bZ.field_75098_d) {
                    itemstack = itemstack1;
                }

                if (itemstack.func_111282_a(this, (EntityLivingBase) entity)) {
                    if (itemstack.field_77994_a <= 0 && !this.field_71075_bZ.field_75098_d) {
                        this.func_71028_bD();
                    }

                    return true;
                }
            }

            return false;
        } else {
            if (itemstack != null && itemstack == this.func_71045_bC()) {
                if (itemstack.field_77994_a <= 0 && !this.field_71075_bZ.field_75098_d) {
                    this.func_71028_bD();
                } else if (itemstack.field_77994_a < itemstack1.field_77994_a && this.field_71075_bZ.field_75098_d) {
                    itemstack.field_77994_a = itemstack1.field_77994_a;
                }
            }

            return true;
        }
    }

    public ItemStack func_71045_bC() {
        return this.field_71071_by.func_70448_g();
    }

    public void func_71028_bD() {
        this.field_71071_by.func_70299_a(this.field_71071_by.field_70461_c, (ItemStack) null);
    }

    @Override
    public double func_70033_W() {
        return (double) (this.field_70129_M - 0.5F);
    }

    public void func_71059_n(Entity entity) {
        if (entity.func_70075_an()) {
            if (!entity.func_85031_j(this)) {
                float f = (float) this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
                int i = 0;
                float f1 = 0.0F;

                if (entity instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.func_77512_a(this, (EntityLivingBase) entity);
                    i += EnchantmentHelper.func_77507_b(this, (EntityLivingBase) entity);
                }

                if (this.func_70051_ag()) {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = this.field_70143_R > 0.0F && !this.field_70122_E && !this.func_70617_f_() && !this.func_70090_H() && !this.func_70644_a(Potion.field_76440_q) && this.field_70154_o == null && entity instanceof EntityLivingBase;

                    if (flag && f > 0.0F) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.func_90036_a(this);

                    if (entity instanceof EntityLivingBase && j > 0 && !entity.func_70027_ad()) {
                        flag1 = true;
                        entity.func_70015_d(1);
                    }

                    boolean flag2 = entity.func_70097_a(DamageSource.func_76365_a(this), f);

                    if (flag2) {
                        if (i > 0) {
                            entity.func_70024_g((double) (-MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F) * (float) i * 0.5F));
                            this.field_70159_w *= 0.6D;
                            this.field_70179_y *= 0.6D;
                            this.func_70031_b(false);
                        }

                        if (flag) {
                            this.func_71009_b(entity);
                        }

                        if (f1 > 0.0F) {
                            this.func_71047_c(entity);
                        }

                        if (f >= 18.0F) {
                            this.func_71029_a(AchievementList.field_75999_E);
                        }

                        this.func_130011_c(entity);
                        if (entity instanceof EntityLivingBase) {
                            EnchantmentHelper.func_151384_a((EntityLivingBase) entity, this);
                        }

                        EnchantmentHelper.func_151385_b(this, entity);
                        ItemStack itemstack = this.func_71045_bC();
                        Object object = entity;

                        if (entity instanceof EntityDragonPart) {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart) entity).field_70259_a;

                            if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase) {
                                object = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if (itemstack != null && object instanceof EntityLivingBase) {
                            itemstack.func_77961_a((EntityLivingBase) object, this);
                            if (itemstack.field_77994_a <= 0) {
                                this.func_71028_bD();
                            }
                        }

                        if (entity instanceof EntityLivingBase) {
                            this.func_71064_a(StatList.field_75951_w, Math.round(f * 10.0F));
                            if (j > 0) {
                                entity.func_70015_d(j * 4);
                            }
                        }

                        this.func_71020_j(0.3F);
                    } else if (flag1) {
                        entity.func_70066_B();
                    }
                }

            }
        }
    }

    public void func_71009_b(Entity entity) {}

    public void func_71047_c(Entity entity) {}

    @Override
    public void func_70106_y() {
        super.func_70106_y();
        this.field_71069_bz.func_75134_a(this);
        if (this.field_71070_bA != null) {
            this.field_71070_bA.func_75134_a(this);
        }

    }

    @Override
    public boolean func_70094_T() {
        return !this.field_71083_bS && super.func_70094_T();
    }

    public GameProfile func_146103_bH() {
        return this.field_146106_i;
    }

    public EntityPlayer$EnumStatus func_71018_a(int i, int j, int k) {
        if (!this.field_70170_p.field_72995_K) {
            if (this.func_70608_bn() || !this.func_70089_S()) {
                return EntityPlayer$EnumStatus.OTHER_PROBLEM;
            }

            if (!this.field_70170_p.field_73011_w.func_76569_d()) {
                return EntityPlayer$EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (this.field_70170_p.func_72935_r()) {
                return EntityPlayer$EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.field_70165_t - (double) i) > 3.0D || Math.abs(this.field_70163_u - (double) j) > 2.0D || Math.abs(this.field_70161_v - (double) k) > 3.0D) {
                return EntityPlayer$EnumStatus.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = this.field_70170_p.func_72872_a(EntityMob.class, AxisAlignedBB.func_72330_a((double) i - d0, (double) j - d1, (double) k - d0, (double) i + d0, (double) j + d1, (double) k + d0));

            if (!list.isEmpty()) {
                return EntityPlayer$EnumStatus.NOT_SAFE;
            }
        }

        if (this.func_70115_ae()) {
            this.func_70078_a((Entity) null);
        }

        this.func_70105_a(0.2F, 0.2F);
        this.field_70129_M = 0.2F;
        if (this.field_70170_p.func_72899_e(i, j, k)) {
            int l = this.field_70170_p.func_72805_g(i, j, k);
            int i1 = BlockBed.func_149895_l(l);
            float f = 0.5F;
            float f1 = 0.5F;

            switch (i1) {
                case 0:
                    f1 = 0.9F;
                    break;
                case 1:
                    f = 0.1F;
                    break;
                case 2:
                    f1 = 0.1F;
                    break;
                case 3:
                    f = 0.9F;
            }

            this.func_71013_b(i1);
            this.func_70107_b((double) ((float) i + f), (double) ((float) j + 0.9375F), (double) ((float) k + f1));
        } else {
            this.func_70107_b((double) ((float) i + 0.5F), (double) ((float) j + 0.9375F), (double) ((float) k + 0.5F));
        }

        this.field_71083_bS = true;
        this.field_71076_b = 0;
        this.field_71081_bT = new ChunkCoordinates(i, j, k);
        this.field_70159_w = this.field_70179_y = this.field_70181_x = 0.0D;
        if (!this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_72854_c();
        }

        return EntityPlayer$EnumStatus.OK;
    }

    private void func_71013_b(int i) {
        this.field_71079_bU = 0.0F;
        this.field_71089_bV = 0.0F;
        switch (i) {
            case 0:
                this.field_71089_bV = -1.8F;
                break;
            case 1:
                this.field_71079_bU = 1.8F;
                break;
            case 2:
                this.field_71089_bV = 1.8F;
                break;
            case 3:
                this.field_71079_bU = -1.8F;
        }

    }

    public void func_70999_a(boolean flag, boolean flag1, boolean flag2) {
        this.func_70105_a(0.6F, 1.8F);
        this.func_71061_d_();
        ChunkCoordinates chunkcoordinates = this.field_71081_bT;
        ChunkCoordinates chunkcoordinates1 = this.field_71081_bT;

        if (chunkcoordinates != null && this.field_70170_p.func_147439_a(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c) == Blocks.field_150324_C) {
            BlockBed.func_149979_a(this.field_70170_p, chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c, false);
            chunkcoordinates1 = BlockBed.func_149977_a(this.field_70170_p, chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c, 0);
            if (chunkcoordinates1 == null) {
                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b + 1, chunkcoordinates.field_71573_c);
            }

            this.func_70107_b((double) ((float) chunkcoordinates1.field_71574_a + 0.5F), (double) ((float) chunkcoordinates1.field_71572_b + this.field_70129_M + 0.1F), (double) ((float) chunkcoordinates1.field_71573_c + 0.5F));
        }

        this.field_71083_bS = false;
        if (!this.field_70170_p.field_72995_K && flag1) {
            this.field_70170_p.func_72854_c();
        }

        if (flag) {
            this.field_71076_b = 0;
        } else {
            this.field_71076_b = 100;
        }

        if (flag2) {
            this.func_71063_a(this.field_71081_bT, false);
        }

    }

    private boolean func_71065_l() {
        return this.field_70170_p.func_147439_a(this.field_71081_bT.field_71574_a, this.field_71081_bT.field_71572_b, this.field_71081_bT.field_71573_c) == Blocks.field_150324_C;
    }

    public static ChunkCoordinates func_71056_a(World world, ChunkCoordinates chunkcoordinates, boolean flag) {
        IChunkProvider ichunkprovider = world.func_72863_F();

        ichunkprovider.func_73158_c(chunkcoordinates.field_71574_a - 3 >> 4, chunkcoordinates.field_71573_c - 3 >> 4);
        ichunkprovider.func_73158_c(chunkcoordinates.field_71574_a + 3 >> 4, chunkcoordinates.field_71573_c - 3 >> 4);
        ichunkprovider.func_73158_c(chunkcoordinates.field_71574_a - 3 >> 4, chunkcoordinates.field_71573_c + 3 >> 4);
        ichunkprovider.func_73158_c(chunkcoordinates.field_71574_a + 3 >> 4, chunkcoordinates.field_71573_c + 3 >> 4);
        if (world.func_147439_a(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c) == Blocks.field_150324_C) {
            ChunkCoordinates chunkcoordinates1 = BlockBed.func_149977_a(world, chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c, 0);

            return chunkcoordinates1;
        } else {
            Material material = world.func_147439_a(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c).func_149688_o();
            Material material1 = world.func_147439_a(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b + 1, chunkcoordinates.field_71573_c).func_149688_o();
            boolean flag1 = !material.func_76220_a() && !material.func_76224_d();
            boolean flag2 = !material1.func_76220_a() && !material1.func_76224_d();

            return flag && flag1 && flag2 ? chunkcoordinates : null;
        }
    }

    @Override
    public boolean func_70608_bn() {
        return this.field_71083_bS;
    }

    public boolean func_71026_bH() {
        return this.field_71083_bS && this.field_71076_b >= 100;
    }

    protected void func_82239_b(int i, boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (b0 | 1 << i));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (b0 & ~(1 << i)));
        }

    }

    public void func_146105_b(IChatComponent ichatcomponent) {}

    public ChunkCoordinates func_70997_bJ() {
        return this.field_71077_c;
    }

    public boolean func_82245_bX() {
        return this.field_82248_d;
    }

    public void func_71063_a(ChunkCoordinates chunkcoordinates, boolean flag) {
        if (chunkcoordinates != null) {
            this.field_71077_c = new ChunkCoordinates(chunkcoordinates);
            this.field_82248_d = flag;
        } else {
            this.field_71077_c = null;
            this.field_82248_d = false;
        }

    }

    public void func_71029_a(StatBase statbase) {
        this.func_71064_a(statbase, 1);
    }

    public void func_71064_a(StatBase statbase, int i) {}

    @Override
    public void func_70664_aZ() {
        super.func_70664_aZ();
        this.func_71064_a(StatList.field_75953_u, 1);
        if (this.func_70051_ag()) {
            this.func_71020_j(0.8F);
        } else {
            this.func_71020_j(0.2F);
        }

    }

    @Override
    public void func_70612_e(float f, float f1) {
        double d0 = this.field_70165_t;
        double d1 = this.field_70163_u;
        double d2 = this.field_70161_v;

        if (this.field_71075_bZ.field_75100_b && this.field_70154_o == null) {
            double d3 = this.field_70181_x;
            float f2 = this.field_70747_aH;

            this.field_70747_aH = this.field_71075_bZ.func_75093_a();
            super.func_70612_e(f, f1);
            this.field_70181_x = d3 * 0.6D;
            this.field_70747_aH = f2;
        } else {
            super.func_70612_e(f, f1);
        }

        this.func_71000_j(this.field_70165_t - d0, this.field_70163_u - d1, this.field_70161_v - d2);
    }

    @Override
    public float func_70689_ay() {
        return (float) this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
    }

    public void func_71000_j(double d0, double d1, double d2) {
        if (this.field_70154_o == null) {
            int i;

            if (this.func_70055_a(Material.field_151586_h)) {
                i = Math.round(MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.func_71064_a(StatList.field_75957_q, i);
                    this.func_71020_j(0.015F * (float) i * 0.01F);
                }
            } else if (this.func_70090_H()) {
                i = Math.round(MathHelper.func_76133_a(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.func_71064_a(StatList.field_75946_m, i);
                    this.func_71020_j(0.015F * (float) i * 0.01F);
                }
            } else if (this.func_70617_f_()) {
                if (d1 > 0.0D) {
                    this.func_71064_a(StatList.field_75944_o, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.field_70122_E) {
                i = Math.round(MathHelper.func_76133_a(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.func_71064_a(StatList.field_75945_l, i);
                    if (this.func_70051_ag()) {
                        this.func_71020_j(0.099999994F * (float) i * 0.01F);
                    } else {
                        this.func_71020_j(0.01F * (float) i * 0.01F);
                    }
                }
            } else {
                i = Math.round(MathHelper.func_76133_a(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.func_71064_a(StatList.field_75958_p, i);
                }
            }

        }
    }

    private void func_71015_k(double d0, double d1, double d2) {
        if (this.field_70154_o != null) {
            int i = Math.round(MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                if (this.field_70154_o instanceof EntityMinecart) {
                    this.func_71064_a(StatList.field_75956_r, i);
                    if (this.field_71073_d == null) {
                        this.field_71073_d = new ChunkCoordinates(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v));
                    } else if ((double) this.field_71073_d.func_71569_e(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)) >= 1000000.0D) {
                        this.func_71064_a(AchievementList.field_76025_q, 1);
                    }
                } else if (this.field_70154_o instanceof EntityBoat) {
                    this.func_71064_a(StatList.field_75955_s, i);
                } else if (this.field_70154_o instanceof EntityPig) {
                    this.func_71064_a(StatList.field_75954_t, i);
                } else if (this.field_70154_o instanceof EntityHorse) {
                    this.func_71064_a(StatList.field_151185_q, i);
                }
            }
        }

    }

    @Override
    protected void func_70069_a(float f) {
        if (!this.field_71075_bZ.field_75101_c) {
            if (f >= 2.0F) {
                this.func_71064_a(StatList.field_75943_n, (int) Math.round((double) f * 100.0D));
            }

            super.func_70069_a(f);
        }
    }

    @Override
    protected String func_146067_o(int i) {
        return i > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
    }

    @Override
    public void func_70074_a(EntityLivingBase entitylivingbase) {
        if (entitylivingbase instanceof IMob) {
            this.func_71029_a(AchievementList.field_76023_s);
        }

        int i = EntityList.func_75619_a(entitylivingbase);
        EntityList$EntityEggInfo entitylist$entityegginfo = (EntityList$EntityEggInfo) EntityList.field_75627_a.get(i);

        if (entitylist$entityegginfo != null) {
            this.func_71064_a(entitylist$entityegginfo.field_151512_d, 1);
        }

    }

    @Override
    public void func_70110_aj() {
        if (!this.field_71075_bZ.field_75100_b) {
            super.func_70110_aj();
        }

    }

    public ItemStack func_82169_q(int i) {
        return this.field_71071_by.func_70440_f(i);
    }

    public void func_71023_q(int i) {
        this.func_85039_t(i);
        int j = Integer.MAX_VALUE - this.field_71067_cb;

        if (i > j) {
            i = j;
        }

        this.field_71106_cc += (float) i / (float) this.func_71050_bK();

        for (this.field_71067_cb += i; this.field_71106_cc >= 1.0F; this.field_71106_cc /= (float) this.func_71050_bK()) {
            this.field_71106_cc = (this.field_71106_cc - 1.0F) * (float) this.func_71050_bK();
            this.func_82242_a(1);
        }

    }

    public void func_82242_a(int i) {
        this.field_71068_ca += i;
        if (this.field_71068_ca < 0) {
            this.field_71068_ca = 0;
            this.field_71106_cc = 0.0F;
            this.field_71067_cb = 0;
        }

        if (i > 0 && this.field_71068_ca % 5 == 0 && (float) this.field_82249_h < (float) this.field_70173_aa - 100.0F) {
            float f = this.field_71068_ca > 30 ? 1.0F : (float) this.field_71068_ca / 30.0F;

            this.field_70170_p.func_72956_a(this, "random.levelup", f * 0.75F, 1.0F);
            this.field_82249_h = this.field_70173_aa;
        }

    }

    public int func_71050_bK() {
        return this.field_71068_ca >= 30 ? 62 + (this.field_71068_ca - 30) * 7 : (this.field_71068_ca >= 15 ? 17 + (this.field_71068_ca - 15) * 3 : 17);
    }

    public void func_71020_j(float f) {
        if (!this.field_71075_bZ.field_75102_a) {
            if (!this.field_70170_p.field_72995_K) {
                this.field_71100_bB.func_75113_a(f);
            }

        }
    }

    public FoodStats func_71024_bL() {
        return this.field_71100_bB;
    }

    public boolean func_71043_e(boolean flag) {
        return (flag || this.field_71100_bB.func_75121_c()) && !this.field_71075_bZ.field_75102_a;
    }

    public boolean func_70996_bM() {
        return this.func_110143_aJ() > 0.0F && this.func_110143_aJ() < this.func_110138_aP();
    }

    public void func_71008_a(ItemStack itemstack, int i) {
        if (itemstack != this.field_71074_e) {
            this.field_71074_e = itemstack;
            this.field_71072_f = i;
            if (!this.field_70170_p.field_72995_K) {
                this.func_70019_c(true);
            }

        }
    }

    public boolean func_82246_f(int i, int j, int k) {
        if (this.field_71075_bZ.field_75099_e) {
            return true;
        } else {
            Block block = this.field_70170_p.func_147439_a(i, j, k);

            if (block.func_149688_o() != Material.field_151579_a) {
                if (block.func_149688_o().func_85157_q()) {
                    return true;
                }

                if (this.func_71045_bC() != null) {
                    ItemStack itemstack = this.func_71045_bC();

                    if (itemstack.func_150998_b(block) || itemstack.func_150997_a(block) > 1.0F) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean func_82247_a(int i, int j, int k, int l, ItemStack itemstack) {
        return this.field_71075_bZ.field_75099_e ? true : (itemstack != null ? itemstack.func_82835_x() : false);
    }

    @Override
    protected int func_70693_a(EntityPlayer entityplayer) {
        if (this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
            return 0;
        } else {
            int i = this.field_71068_ca * 7;

            return i > 100 ? 100 : i;
        }
    }

    @Override
    protected boolean func_70684_aJ() {
        return true;
    }

    public void func_71049_a(EntityPlayer entityplayer, boolean flag) {
        if (flag) {
            this.field_71071_by.func_70455_b(entityplayer.field_71071_by);
            this.func_70606_j(entityplayer.func_110143_aJ());
            this.field_71100_bB = entityplayer.field_71100_bB;
            this.field_71068_ca = entityplayer.field_71068_ca;
            this.field_71067_cb = entityplayer.field_71067_cb;
            this.field_71106_cc = entityplayer.field_71106_cc;
            this.func_85040_s(entityplayer.func_71037_bA());
            this.field_82152_aq = entityplayer.field_82152_aq;
        } else if (this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
            this.field_71071_by.func_70455_b(entityplayer.field_71071_by);
            this.field_71068_ca = entityplayer.field_71068_ca;
            this.field_71067_cb = entityplayer.field_71067_cb;
            this.field_71106_cc = entityplayer.field_71106_cc;
            this.func_85040_s(entityplayer.func_71037_bA());
        }

        this.field_71078_a = entityplayer.field_71078_a;
    }

    @Override
    protected boolean func_70041_e_() {
        return !this.field_71075_bZ.field_75100_b;
    }

    public void func_71016_p() {}

    public void func_71033_a(WorldSettings$GameType worldsettings$gametype) {}

    @Override
    public String func_70005_c_() {
        return this.field_146106_i.getName();
    }

    @Override
    public World func_130014_f_() {
        return this.field_70170_p;
    }

    public InventoryEnderChest func_71005_bN() {
        return this.field_71078_a;
    }

    @Override
    public ItemStack func_71124_b(int i) {
        return i == 0 ? this.field_71071_by.func_70448_g() : this.field_71071_by.field_70460_b[i - 1];
    }

    @Override
    public ItemStack func_70694_bm() {
        return this.field_71071_by.func_70448_g();
    }

    @Override
    public void func_70062_b(int i, ItemStack itemstack) {
        this.field_71071_by.field_70460_b[i] = itemstack;
    }

    @Override
    public ItemStack[] func_70035_c() {
        return this.field_71071_by.field_70460_b;
    }

    @Override
    public boolean func_96092_aw() {
        return !this.field_71075_bZ.field_75100_b;
    }

    public Scoreboard func_96123_co() {
        return this.field_70170_p.func_96441_U();
    }

    @Override
    public Team func_96124_cp() {
        return this.func_96123_co().func_96509_i(this.func_70005_c_());
    }

    @Override
    public IChatComponent func_145748_c_() {
        ChatComponentText chatcomponenttext = new ChatComponentText(ScorePlayerTeam.func_96667_a(this.func_96124_cp(), this.func_70005_c_()));

        chatcomponenttext.func_150256_b().func_150241_a(new ClickEvent(ClickEvent$Action.SUGGEST_COMMAND, "/msg " + this.func_70005_c_() + " "));
        return chatcomponenttext;
    }

    @Override
    public void func_110149_m(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.func_70096_w().func_75692_b(17, f);
    }

    @Override
    public float func_110139_bj() {
        return this.func_70096_w().func_111145_d(17);
    }

    public static UUID func_146094_a(GameProfile gameprofile) {
        UUID uuid = gameprofile.getId();

        if (uuid == null) {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));
        }

        return uuid;
    }
}
