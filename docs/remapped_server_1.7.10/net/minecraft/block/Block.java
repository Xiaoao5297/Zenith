package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Block {

    public static final RegistryNamespaced field_149771_c = new RegistryNamespacedDefaultedByKey("air");
    private CreativeTabs field_149772_a;
    protected String field_149768_d;
    public static final Block$SoundType field_149769_e = new Block$SoundType("stone", 1.0F, 1.0F);
    public static final Block$SoundType field_149766_f = new Block$SoundType("wood", 1.0F, 1.0F);
    public static final Block$SoundType field_149767_g = new Block$SoundType("gravel", 1.0F, 1.0F);
    public static final Block$SoundType field_149779_h = new Block$SoundType("grass", 1.0F, 1.0F);
    public static final Block$SoundType field_149780_i = new Block$SoundType("stone", 1.0F, 1.0F);
    public static final Block$SoundType field_149777_j = new Block$SoundType("stone", 1.0F, 1.5F);
    public static final Block$SoundType field_149778_k = new Block$1("stone", 1.0F, 1.0F);
    public static final Block$SoundType field_149775_l = new Block$SoundType("cloth", 1.0F, 1.0F);
    public static final Block$SoundType field_149776_m = new Block$SoundType("sand", 1.0F, 1.0F);
    public static final Block$SoundType field_149773_n = new Block$SoundType("snow", 1.0F, 1.0F);
    public static final Block$SoundType field_149774_o = new Block$2("ladder", 1.0F, 1.0F);
    public static final Block$SoundType field_149788_p = new Block$3("anvil", 0.3F, 1.0F);
    protected boolean field_149787_q;
    protected int field_149786_r;
    protected boolean field_149785_s;
    protected int field_149784_t;
    protected boolean field_149783_u;
    protected float field_149782_v;
    protected float field_149781_w;
    protected boolean field_149791_x = true;
    protected boolean field_149790_y = true;
    protected boolean field_149789_z;
    protected boolean field_149758_A;
    protected double field_149759_B;
    protected double field_149760_C;
    protected double field_149754_D;
    protected double field_149755_E;
    protected double field_149756_F;
    protected double field_149757_G;
    public Block$SoundType field_149762_H;
    public float field_149763_I;
    protected final Material field_149764_J;
    public float field_149765_K;
    private String field_149770_b;

    public static int func_149682_b(Block block) {
        return Block.field_149771_c.func_148757_b(block);
    }

    public static Block func_149729_e(int i) {
        return (Block) Block.field_149771_c.func_148754_a(i);
    }

    public static Block func_149634_a(Item item) {
        return func_149729_e(Item.func_150891_b(item));
    }

    public static Block func_149684_b(String s) {
        if (Block.field_149771_c.func_148741_d(s)) {
            return (Block) Block.field_149771_c.func_82594_a(s);
        } else {
            try {
                return (Block) Block.field_149771_c.func_148754_a(Integer.parseInt(s));
            } catch (NumberFormatException numberformatexception) {
                return null;
            }
        }
    }

    public boolean func_149730_j() {
        return this.field_149787_q;
    }

    public int func_149717_k() {
        return this.field_149786_r;
    }

    public int func_149750_m() {
        return this.field_149784_t;
    }

    public boolean func_149710_n() {
        return this.field_149783_u;
    }

    public Material func_149688_o() {
        return this.field_149764_J;
    }

    public MapColor func_149728_f(int i) {
        return this.func_149688_o().func_151565_r();
    }

    public static void func_149671_p() {
        Block.field_149771_c.func_148756_a(0, "air", (new BlockAir()).func_149663_c("air"));
        Block.field_149771_c.func_148756_a(1, "stone", (new BlockStone()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("stone").func_149658_d("stone"));
        Block.field_149771_c.func_148756_a(2, "grass", (new BlockGrass()).func_149711_c(0.6F).func_149672_a(Block.field_149779_h).func_149663_c("grass").func_149658_d("grass"));
        Block.field_149771_c.func_148756_a(3, "dirt", (new BlockDirt()).func_149711_c(0.5F).func_149672_a(Block.field_149767_g).func_149663_c("dirt").func_149658_d("dirt"));
        Block block = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("stonebrick").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("cobblestone");

        Block.field_149771_c.func_148756_a(4, "cobblestone", block);
        Block block1 = (new BlockWood()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(Block.field_149766_f).func_149663_c("wood").func_149658_d("planks");

        Block.field_149771_c.func_148756_a(5, "planks", block1);
        Block.field_149771_c.func_148756_a(6, "sapling", (new BlockSapling()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("sapling").func_149658_d("sapling"));
        Block.field_149771_c.func_148756_a(7, "bedrock", (new Block(Material.field_151576_e)).func_149722_s().func_149752_b(6000000.0F).func_149672_a(Block.field_149780_i).func_149663_c("bedrock").func_149649_H().func_149647_a(CreativeTabs.field_78030_b).func_149658_d("bedrock"));
        Block.field_149771_c.func_148756_a(8, "flowing_water", (new BlockDynamicLiquid(Material.field_151586_h)).func_149711_c(100.0F).func_149713_g(3).func_149663_c("water").func_149649_H().func_149658_d("water_flow"));
        Block.field_149771_c.func_148756_a(9, "water", (new BlockStaticLiquid(Material.field_151586_h)).func_149711_c(100.0F).func_149713_g(3).func_149663_c("water").func_149649_H().func_149658_d("water_still"));
        Block.field_149771_c.func_148756_a(10, "flowing_lava", (new BlockDynamicLiquid(Material.field_151587_i)).func_149711_c(100.0F).func_149715_a(1.0F).func_149663_c("lava").func_149649_H().func_149658_d("lava_flow"));
        Block.field_149771_c.func_148756_a(11, "lava", (new BlockStaticLiquid(Material.field_151587_i)).func_149711_c(100.0F).func_149715_a(1.0F).func_149663_c("lava").func_149649_H().func_149658_d("lava_still"));
        Block.field_149771_c.func_148756_a(12, "sand", (new BlockSand()).func_149711_c(0.5F).func_149672_a(Block.field_149776_m).func_149663_c("sand").func_149658_d("sand"));
        Block.field_149771_c.func_148756_a(13, "gravel", (new BlockGravel()).func_149711_c(0.6F).func_149672_a(Block.field_149767_g).func_149663_c("gravel").func_149658_d("gravel"));
        Block.field_149771_c.func_148756_a(14, "gold_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreGold").func_149658_d("gold_ore"));
        Block.field_149771_c.func_148756_a(15, "iron_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreIron").func_149658_d("iron_ore"));
        Block.field_149771_c.func_148756_a(16, "coal_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreCoal").func_149658_d("coal_ore"));
        Block.field_149771_c.func_148756_a(17, "log", (new BlockOldLog()).func_149663_c("log").func_149658_d("log"));
        Block.field_149771_c.func_148756_a(18, "leaves", (new BlockOldLeaf()).func_149663_c("leaves").func_149658_d("leaves"));
        Block.field_149771_c.func_148756_a(19, "sponge", (new BlockSponge()).func_149711_c(0.6F).func_149672_a(Block.field_149779_h).func_149663_c("sponge").func_149658_d("sponge"));
        Block.field_149771_c.func_148756_a(20, "glass", (new BlockGlass(Material.field_151592_s, false)).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149663_c("glass").func_149658_d("glass"));
        Block.field_149771_c.func_148756_a(21, "lapis_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreLapis").func_149658_d("lapis_ore"));
        Block.field_149771_c.func_148756_a(22, "lapis_block", (new BlockCompressed(MapColor.field_151652_H)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("blockLapis").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("lapis_block"));
        Block.field_149771_c.func_148756_a(23, "dispenser", (new BlockDispenser()).func_149711_c(3.5F).func_149672_a(Block.field_149780_i).func_149663_c("dispenser").func_149658_d("dispenser"));
        Block block2 = (new BlockSandStone()).func_149672_a(Block.field_149780_i).func_149711_c(0.8F).func_149663_c("sandStone").func_149658_d("sandstone");

        Block.field_149771_c.func_148756_a(24, "sandstone", block2);
        Block.field_149771_c.func_148756_a(25, "noteblock", (new BlockNote()).func_149711_c(0.8F).func_149663_c("musicBlock").func_149658_d("noteblock"));
        Block.field_149771_c.func_148756_a(26, "bed", (new BlockBed()).func_149711_c(0.2F).func_149663_c("bed").func_149649_H().func_149658_d("bed"));
        Block.field_149771_c.func_148756_a(27, "golden_rail", (new BlockRailPowered()).func_149711_c(0.7F).func_149672_a(Block.field_149777_j).func_149663_c("goldenRail").func_149658_d("rail_golden"));
        Block.field_149771_c.func_148756_a(28, "detector_rail", (new BlockRailDetector()).func_149711_c(0.7F).func_149672_a(Block.field_149777_j).func_149663_c("detectorRail").func_149658_d("rail_detector"));
        Block.field_149771_c.func_148756_a(29, "sticky_piston", (new BlockPistonBase(true)).func_149663_c("pistonStickyBase"));
        Block.field_149771_c.func_148756_a(30, "web", (new BlockWeb()).func_149713_g(1).func_149711_c(4.0F).func_149663_c("web").func_149658_d("web"));
        Block.field_149771_c.func_148756_a(31, "tallgrass", (new BlockTallGrass()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("tallgrass"));
        Block.field_149771_c.func_148756_a(32, "deadbush", (new BlockDeadBush()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("deadbush").func_149658_d("deadbush"));
        Block.field_149771_c.func_148756_a(33, "piston", (new BlockPistonBase(false)).func_149663_c("pistonBase"));
        Block.field_149771_c.func_148756_a(34, "piston_head", new BlockPistonExtension());
        Block.field_149771_c.func_148756_a(35, "wool", (new BlockColored(Material.field_151580_n)).func_149711_c(0.8F).func_149672_a(Block.field_149775_l).func_149663_c("cloth").func_149658_d("wool_colored"));
        Block.field_149771_c.func_148756_a(36, "piston_extension", new BlockPistonMoving());
        Block.field_149771_c.func_148756_a(37, "yellow_flower", (new BlockFlower(0)).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("flower1").func_149658_d("flower_dandelion"));
        Block.field_149771_c.func_148756_a(38, "red_flower", (new BlockFlower(1)).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("flower2").func_149658_d("flower_rose"));
        Block.field_149771_c.func_148756_a(39, "brown_mushroom", (new BlockMushroom()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149715_a(0.125F).func_149663_c("mushroom").func_149658_d("mushroom_brown"));
        Block.field_149771_c.func_148756_a(40, "red_mushroom", (new BlockMushroom()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("mushroom").func_149658_d("mushroom_red"));
        Block.field_149771_c.func_148756_a(41, "gold_block", (new BlockCompressed(MapColor.field_151647_F)).func_149711_c(3.0F).func_149752_b(10.0F).func_149672_a(Block.field_149777_j).func_149663_c("blockGold").func_149658_d("gold_block"));
        Block.field_149771_c.func_148756_a(42, "iron_block", (new BlockCompressed(MapColor.field_151668_h)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(Block.field_149777_j).func_149663_c("blockIron").func_149658_d("iron_block"));
        Block.field_149771_c.func_148756_a(43, "double_stone_slab", (new BlockStoneSlab(true)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("stoneSlab"));
        Block.field_149771_c.func_148756_a(44, "stone_slab", (new BlockStoneSlab(false)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("stoneSlab"));
        Block block3 = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("brick").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("brick");

        Block.field_149771_c.func_148756_a(45, "brick_block", block3);
        Block.field_149771_c.func_148756_a(46, "tnt", (new BlockTNT()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("tnt").func_149658_d("tnt"));
        Block.field_149771_c.func_148756_a(47, "bookshelf", (new BlockBookshelf()).func_149711_c(1.5F).func_149672_a(Block.field_149766_f).func_149663_c("bookshelf").func_149658_d("bookshelf"));
        Block.field_149771_c.func_148756_a(48, "mossy_cobblestone", (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("stoneMoss").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("cobblestone_mossy"));
        Block.field_149771_c.func_148756_a(49, "obsidian", (new BlockObsidian()).func_149711_c(50.0F).func_149752_b(2000.0F).func_149672_a(Block.field_149780_i).func_149663_c("obsidian").func_149658_d("obsidian"));
        Block.field_149771_c.func_148756_a(50, "torch", (new BlockTorch()).func_149711_c(0.0F).func_149715_a(0.9375F).func_149672_a(Block.field_149766_f).func_149663_c("torch").func_149658_d("torch_on"));
        Block.field_149771_c.func_148756_a(51, "fire", (new BlockFire()).func_149711_c(0.0F).func_149715_a(1.0F).func_149672_a(Block.field_149766_f).func_149663_c("fire").func_149649_H().func_149658_d("fire"));
        Block.field_149771_c.func_148756_a(52, "mob_spawner", (new BlockMobSpawner()).func_149711_c(5.0F).func_149672_a(Block.field_149777_j).func_149663_c("mobSpawner").func_149649_H().func_149658_d("mob_spawner"));
        Block.field_149771_c.func_148756_a(53, "oak_stairs", (new BlockStairs(block1, 0)).func_149663_c("stairsWood"));
        Block.field_149771_c.func_148756_a(54, "chest", (new BlockChest(0)).func_149711_c(2.5F).func_149672_a(Block.field_149766_f).func_149663_c("chest"));
        Block.field_149771_c.func_148756_a(55, "redstone_wire", (new BlockRedstoneWire()).func_149711_c(0.0F).func_149672_a(Block.field_149769_e).func_149663_c("redstoneDust").func_149649_H().func_149658_d("redstone_dust"));
        Block.field_149771_c.func_148756_a(56, "diamond_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreDiamond").func_149658_d("diamond_ore"));
        Block.field_149771_c.func_148756_a(57, "diamond_block", (new BlockCompressed(MapColor.field_151648_G)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(Block.field_149777_j).func_149663_c("blockDiamond").func_149658_d("diamond_block"));
        Block.field_149771_c.func_148756_a(58, "crafting_table", (new BlockWorkbench()).func_149711_c(2.5F).func_149672_a(Block.field_149766_f).func_149663_c("workbench").func_149658_d("crafting_table"));
        Block.field_149771_c.func_148756_a(59, "wheat", (new BlockCrops()).func_149663_c("crops").func_149658_d("wheat"));
        Block block4 = (new BlockFarmland()).func_149711_c(0.6F).func_149672_a(Block.field_149767_g).func_149663_c("farmland").func_149658_d("farmland");

        Block.field_149771_c.func_148756_a(60, "farmland", block4);
        Block.field_149771_c.func_148756_a(61, "furnace", (new BlockFurnace(false)).func_149711_c(3.5F).func_149672_a(Block.field_149780_i).func_149663_c("furnace").func_149647_a(CreativeTabs.field_78031_c));
        Block.field_149771_c.func_148756_a(62, "lit_furnace", (new BlockFurnace(true)).func_149711_c(3.5F).func_149672_a(Block.field_149780_i).func_149715_a(0.875F).func_149663_c("furnace"));
        Block.field_149771_c.func_148756_a(63, "standing_sign", (new BlockSign(TileEntitySign.class, true)).func_149711_c(1.0F).func_149672_a(Block.field_149766_f).func_149663_c("sign").func_149649_H());
        Block.field_149771_c.func_148756_a(64, "wooden_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(Block.field_149766_f).func_149663_c("doorWood").func_149649_H().func_149658_d("door_wood"));
        Block.field_149771_c.func_148756_a(65, "ladder", (new BlockLadder()).func_149711_c(0.4F).func_149672_a(Block.field_149774_o).func_149663_c("ladder").func_149658_d("ladder"));
        Block.field_149771_c.func_148756_a(66, "rail", (new BlockRail()).func_149711_c(0.7F).func_149672_a(Block.field_149777_j).func_149663_c("rail").func_149658_d("rail_normal"));
        Block.field_149771_c.func_148756_a(67, "stone_stairs", (new BlockStairs(block, 0)).func_149663_c("stairsStone"));
        Block.field_149771_c.func_148756_a(68, "wall_sign", (new BlockSign(TileEntitySign.class, false)).func_149711_c(1.0F).func_149672_a(Block.field_149766_f).func_149663_c("sign").func_149649_H());
        Block.field_149771_c.func_148756_a(69, "lever", (new BlockLever()).func_149711_c(0.5F).func_149672_a(Block.field_149766_f).func_149663_c("lever").func_149658_d("lever"));
        Block.field_149771_c.func_148756_a(70, "stone_pressure_plate", (new BlockPressurePlate("stone", Material.field_151576_e, BlockPressurePlate$Sensitivity.mobs)).func_149711_c(0.5F).func_149672_a(Block.field_149780_i).func_149663_c("pressurePlate"));
        Block.field_149771_c.func_148756_a(71, "iron_door", (new BlockDoor(Material.field_151573_f)).func_149711_c(5.0F).func_149672_a(Block.field_149777_j).func_149663_c("doorIron").func_149649_H().func_149658_d("door_iron"));
        Block.field_149771_c.func_148756_a(72, "wooden_pressure_plate", (new BlockPressurePlate("planks_oak", Material.field_151575_d, BlockPressurePlate$Sensitivity.everything)).func_149711_c(0.5F).func_149672_a(Block.field_149766_f).func_149663_c("pressurePlate"));
        Block.field_149771_c.func_148756_a(73, "redstone_ore", (new BlockRedstoneOre(false)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreRedstone").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("redstone_ore"));
        Block.field_149771_c.func_148756_a(74, "lit_redstone_ore", (new BlockRedstoneOre(true)).func_149715_a(0.625F).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreRedstone").func_149658_d("redstone_ore"));
        Block.field_149771_c.func_148756_a(75, "unlit_redstone_torch", (new BlockRedstoneTorch(false)).func_149711_c(0.0F).func_149672_a(Block.field_149766_f).func_149663_c("notGate").func_149658_d("redstone_torch_off"));
        Block.field_149771_c.func_148756_a(76, "redstone_torch", (new BlockRedstoneTorch(true)).func_149711_c(0.0F).func_149715_a(0.5F).func_149672_a(Block.field_149766_f).func_149663_c("notGate").func_149647_a(CreativeTabs.field_78028_d).func_149658_d("redstone_torch_on"));
        Block.field_149771_c.func_148756_a(77, "stone_button", (new BlockButtonStone()).func_149711_c(0.5F).func_149672_a(Block.field_149780_i).func_149663_c("button"));
        Block.field_149771_c.func_148756_a(78, "snow_layer", (new BlockSnow()).func_149711_c(0.1F).func_149672_a(Block.field_149773_n).func_149663_c("snow").func_149713_g(0).func_149658_d("snow"));
        Block.field_149771_c.func_148756_a(79, "ice", (new BlockIce()).func_149711_c(0.5F).func_149713_g(3).func_149672_a(Block.field_149778_k).func_149663_c("ice").func_149658_d("ice"));
        Block.field_149771_c.func_148756_a(80, "snow", (new BlockSnowBlock()).func_149711_c(0.2F).func_149672_a(Block.field_149773_n).func_149663_c("snow").func_149658_d("snow"));
        Block.field_149771_c.func_148756_a(81, "cactus", (new BlockCactus()).func_149711_c(0.4F).func_149672_a(Block.field_149775_l).func_149663_c("cactus").func_149658_d("cactus"));
        Block.field_149771_c.func_148756_a(82, "clay", (new BlockClay()).func_149711_c(0.6F).func_149672_a(Block.field_149767_g).func_149663_c("clay").func_149658_d("clay"));
        Block.field_149771_c.func_148756_a(83, "reeds", (new BlockReed()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("reeds").func_149649_H().func_149658_d("reeds"));
        Block.field_149771_c.func_148756_a(84, "jukebox", (new BlockJukebox()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("jukebox").func_149658_d("jukebox"));
        Block.field_149771_c.func_148756_a(85, "fence", (new BlockFence("planks_oak", Material.field_151575_d)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(Block.field_149766_f).func_149663_c("fence"));
        Block block5 = (new BlockPumpkin(false)).func_149711_c(1.0F).func_149672_a(Block.field_149766_f).func_149663_c("pumpkin").func_149658_d("pumpkin");

        Block.field_149771_c.func_148756_a(86, "pumpkin", block5);
        Block.field_149771_c.func_148756_a(87, "netherrack", (new BlockNetherrack()).func_149711_c(0.4F).func_149672_a(Block.field_149780_i).func_149663_c("hellrock").func_149658_d("netherrack"));
        Block.field_149771_c.func_148756_a(88, "soul_sand", (new BlockSoulSand()).func_149711_c(0.5F).func_149672_a(Block.field_149776_m).func_149663_c("hellsand").func_149658_d("soul_sand"));
        Block.field_149771_c.func_148756_a(89, "glowstone", (new BlockGlowstone(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149715_a(1.0F).func_149663_c("lightgem").func_149658_d("glowstone"));
        Block.field_149771_c.func_148756_a(90, "portal", (new BlockPortal()).func_149711_c(-1.0F).func_149672_a(Block.field_149778_k).func_149715_a(0.75F).func_149663_c("portal").func_149658_d("portal"));
        Block.field_149771_c.func_148756_a(91, "lit_pumpkin", (new BlockPumpkin(true)).func_149711_c(1.0F).func_149672_a(Block.field_149766_f).func_149715_a(1.0F).func_149663_c("litpumpkin").func_149658_d("pumpkin"));
        Block.field_149771_c.func_148756_a(92, "cake", (new BlockCake()).func_149711_c(0.5F).func_149672_a(Block.field_149775_l).func_149663_c("cake").func_149649_H().func_149658_d("cake"));
        Block.field_149771_c.func_148756_a(93, "unpowered_repeater", (new BlockRedstoneRepeater(false)).func_149711_c(0.0F).func_149672_a(Block.field_149766_f).func_149663_c("diode").func_149649_H().func_149658_d("repeater_off"));
        Block.field_149771_c.func_148756_a(94, "powered_repeater", (new BlockRedstoneRepeater(true)).func_149711_c(0.0F).func_149715_a(0.625F).func_149672_a(Block.field_149766_f).func_149663_c("diode").func_149649_H().func_149658_d("repeater_on"));
        Block.field_149771_c.func_148756_a(95, "stained_glass", (new BlockStainedGlass(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149663_c("stainedGlass").func_149658_d("glass"));
        Block.field_149771_c.func_148756_a(96, "trapdoor", (new BlockTrapDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(Block.field_149766_f).func_149663_c("trapdoor").func_149649_H().func_149658_d("trapdoor"));
        Block.field_149771_c.func_148756_a(97, "monster_egg", (new BlockSilverfish()).func_149711_c(0.75F).func_149663_c("monsterStoneEgg"));
        Block block6 = (new BlockStoneBrick()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("stonebricksmooth").func_149658_d("stonebrick");

        Block.field_149771_c.func_148756_a(98, "stonebrick", block6);
        Block.field_149771_c.func_148756_a(99, "brown_mushroom_block", (new BlockHugeMushroom(Material.field_151575_d, 0)).func_149711_c(0.2F).func_149672_a(Block.field_149766_f).func_149663_c("mushroom").func_149658_d("mushroom_block"));
        Block.field_149771_c.func_148756_a(100, "red_mushroom_block", (new BlockHugeMushroom(Material.field_151575_d, 1)).func_149711_c(0.2F).func_149672_a(Block.field_149766_f).func_149663_c("mushroom").func_149658_d("mushroom_block"));
        Block.field_149771_c.func_148756_a(101, "iron_bars", (new BlockPane("iron_bars", "iron_bars", Material.field_151573_f, true)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(Block.field_149777_j).func_149663_c("fenceIron"));
        Block.field_149771_c.func_148756_a(102, "glass_pane", (new BlockPane("glass", "glass_pane_top", Material.field_151592_s, false)).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149663_c("thinGlass"));
        Block block7 = (new BlockMelon()).func_149711_c(1.0F).func_149672_a(Block.field_149766_f).func_149663_c("melon").func_149658_d("melon");

        Block.field_149771_c.func_148756_a(103, "melon_block", block7);
        Block.field_149771_c.func_148756_a(104, "pumpkin_stem", (new BlockStem(block5)).func_149711_c(0.0F).func_149672_a(Block.field_149766_f).func_149663_c("pumpkinStem").func_149658_d("pumpkin_stem"));
        Block.field_149771_c.func_148756_a(105, "melon_stem", (new BlockStem(block7)).func_149711_c(0.0F).func_149672_a(Block.field_149766_f).func_149663_c("pumpkinStem").func_149658_d("melon_stem"));
        Block.field_149771_c.func_148756_a(106, "vine", (new BlockVine()).func_149711_c(0.2F).func_149672_a(Block.field_149779_h).func_149663_c("vine").func_149658_d("vine"));
        Block.field_149771_c.func_148756_a(107, "fence_gate", (new BlockFenceGate()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(Block.field_149766_f).func_149663_c("fenceGate"));
        Block.field_149771_c.func_148756_a(108, "brick_stairs", (new BlockStairs(block3, 0)).func_149663_c("stairsBrick"));
        Block.field_149771_c.func_148756_a(109, "stone_brick_stairs", (new BlockStairs(block6, 0)).func_149663_c("stairsStoneBrickSmooth"));
        Block.field_149771_c.func_148756_a(110, "mycelium", (new BlockMycelium()).func_149711_c(0.6F).func_149672_a(Block.field_149779_h).func_149663_c("mycel").func_149658_d("mycelium"));
        Block.field_149771_c.func_148756_a(111, "waterlily", (new BlockLilyPad()).func_149711_c(0.0F).func_149672_a(Block.field_149779_h).func_149663_c("waterlily").func_149658_d("waterlily"));
        Block block8 = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("netherBrick").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("nether_brick");

        Block.field_149771_c.func_148756_a(112, "nether_brick", block8);
        Block.field_149771_c.func_148756_a(113, "nether_brick_fence", (new BlockFence("nether_brick", Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("netherFence"));
        Block.field_149771_c.func_148756_a(114, "nether_brick_stairs", (new BlockStairs(block8, 0)).func_149663_c("stairsNetherBrick"));
        Block.field_149771_c.func_148756_a(115, "nether_wart", (new BlockNetherWart()).func_149663_c("netherStalk").func_149658_d("nether_wart"));
        Block.field_149771_c.func_148756_a(116, "enchanting_table", (new BlockEnchantmentTable()).func_149711_c(5.0F).func_149752_b(2000.0F).func_149663_c("enchantmentTable").func_149658_d("enchanting_table"));
        Block.field_149771_c.func_148756_a(117, "brewing_stand", (new BlockBrewingStand()).func_149711_c(0.5F).func_149715_a(0.125F).func_149663_c("brewingStand").func_149658_d("brewing_stand"));
        Block.field_149771_c.func_148756_a(118, "cauldron", (new BlockCauldron()).func_149711_c(2.0F).func_149663_c("cauldron").func_149658_d("cauldron"));
        Block.field_149771_c.func_148756_a(119, "end_portal", (new BlockEndPortal(Material.field_151567_E)).func_149711_c(-1.0F).func_149752_b(6000000.0F));
        Block.field_149771_c.func_148756_a(120, "end_portal_frame", (new BlockEndPortalFrame()).func_149672_a(Block.field_149778_k).func_149715_a(0.125F).func_149711_c(-1.0F).func_149663_c("endPortalFrame").func_149752_b(6000000.0F).func_149647_a(CreativeTabs.field_78031_c).func_149658_d("endframe"));
        Block.field_149771_c.func_148756_a(121, "end_stone", (new Block(Material.field_151576_e)).func_149711_c(3.0F).func_149752_b(15.0F).func_149672_a(Block.field_149780_i).func_149663_c("whiteStone").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("end_stone"));
        Block.field_149771_c.func_148756_a(122, "dragon_egg", (new BlockDragonEgg()).func_149711_c(3.0F).func_149752_b(15.0F).func_149672_a(Block.field_149780_i).func_149715_a(0.125F).func_149663_c("dragonEgg").func_149658_d("dragon_egg"));
        Block.field_149771_c.func_148756_a(123, "redstone_lamp", (new BlockRedstoneLight(false)).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149663_c("redstoneLight").func_149647_a(CreativeTabs.field_78028_d).func_149658_d("redstone_lamp_off"));
        Block.field_149771_c.func_148756_a(124, "lit_redstone_lamp", (new BlockRedstoneLight(true)).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149663_c("redstoneLight").func_149658_d("redstone_lamp_on"));
        Block.field_149771_c.func_148756_a(125, "double_wooden_slab", (new BlockWoodSlab(true)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(Block.field_149766_f).func_149663_c("woodSlab"));
        Block.field_149771_c.func_148756_a(126, "wooden_slab", (new BlockWoodSlab(false)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(Block.field_149766_f).func_149663_c("woodSlab"));
        Block.field_149771_c.func_148756_a(127, "cocoa", (new BlockCocoa()).func_149711_c(0.2F).func_149752_b(5.0F).func_149672_a(Block.field_149766_f).func_149663_c("cocoa").func_149658_d("cocoa"));
        Block.field_149771_c.func_148756_a(128, "sandstone_stairs", (new BlockStairs(block2, 0)).func_149663_c("stairsSandStone"));
        Block.field_149771_c.func_148756_a(129, "emerald_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("oreEmerald").func_149658_d("emerald_ore"));
        Block.field_149771_c.func_148756_a(130, "ender_chest", (new BlockEnderChest()).func_149711_c(22.5F).func_149752_b(1000.0F).func_149672_a(Block.field_149780_i).func_149663_c("enderChest").func_149715_a(0.5F));
        Block.field_149771_c.func_148756_a(131, "tripwire_hook", (new BlockTripWireHook()).func_149663_c("tripWireSource").func_149658_d("trip_wire_source"));
        Block.field_149771_c.func_148756_a(132, "tripwire", (new BlockTripWire()).func_149663_c("tripWire").func_149658_d("trip_wire"));
        Block.field_149771_c.func_148756_a(133, "emerald_block", (new BlockCompressed(MapColor.field_151653_I)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(Block.field_149777_j).func_149663_c("blockEmerald").func_149658_d("emerald_block"));
        Block.field_149771_c.func_148756_a(134, "spruce_stairs", (new BlockStairs(block1, 1)).func_149663_c("stairsWoodSpruce"));
        Block.field_149771_c.func_148756_a(135, "birch_stairs", (new BlockStairs(block1, 2)).func_149663_c("stairsWoodBirch"));
        Block.field_149771_c.func_148756_a(136, "jungle_stairs", (new BlockStairs(block1, 3)).func_149663_c("stairsWoodJungle"));
        Block.field_149771_c.func_148756_a(137, "command_block", (new BlockCommandBlock()).func_149722_s().func_149752_b(6000000.0F).func_149663_c("commandBlock").func_149658_d("command_block"));
        Block.field_149771_c.func_148756_a(138, "beacon", (new BlockBeacon()).func_149663_c("beacon").func_149715_a(1.0F).func_149658_d("beacon"));
        Block.field_149771_c.func_148756_a(139, "cobblestone_wall", (new BlockWall(block)).func_149663_c("cobbleWall"));
        Block.field_149771_c.func_148756_a(140, "flower_pot", (new BlockFlowerPot()).func_149711_c(0.0F).func_149672_a(Block.field_149769_e).func_149663_c("flowerPot").func_149658_d("flower_pot"));
        Block.field_149771_c.func_148756_a(141, "carrots", (new BlockCarrot()).func_149663_c("carrots").func_149658_d("carrots"));
        Block.field_149771_c.func_148756_a(142, "potatoes", (new BlockPotato()).func_149663_c("potatoes").func_149658_d("potatoes"));
        Block.field_149771_c.func_148756_a(143, "wooden_button", (new BlockButtonWood()).func_149711_c(0.5F).func_149672_a(Block.field_149766_f).func_149663_c("button"));
        Block.field_149771_c.func_148756_a(144, "skull", (new BlockSkull()).func_149711_c(1.0F).func_149672_a(Block.field_149780_i).func_149663_c("skull").func_149658_d("skull"));
        Block.field_149771_c.func_148756_a(145, "anvil", (new BlockAnvil()).func_149711_c(5.0F).func_149672_a(Block.field_149788_p).func_149752_b(2000.0F).func_149663_c("anvil"));
        Block.field_149771_c.func_148756_a(146, "trapped_chest", (new BlockChest(1)).func_149711_c(2.5F).func_149672_a(Block.field_149766_f).func_149663_c("chestTrap"));
        Block.field_149771_c.func_148756_a(147, "light_weighted_pressure_plate", (new BlockPressurePlateWeighted("gold_block", Material.field_151573_f, 15)).func_149711_c(0.5F).func_149672_a(Block.field_149766_f).func_149663_c("weightedPlate_light"));
        Block.field_149771_c.func_148756_a(148, "heavy_weighted_pressure_plate", (new BlockPressurePlateWeighted("iron_block", Material.field_151573_f, 150)).func_149711_c(0.5F).func_149672_a(Block.field_149766_f).func_149663_c("weightedPlate_heavy"));
        Block.field_149771_c.func_148756_a(149, "unpowered_comparator", (new BlockRedstoneComparator(false)).func_149711_c(0.0F).func_149672_a(Block.field_149766_f).func_149663_c("comparator").func_149649_H().func_149658_d("comparator_off"));
        Block.field_149771_c.func_148756_a(150, "powered_comparator", (new BlockRedstoneComparator(true)).func_149711_c(0.0F).func_149715_a(0.625F).func_149672_a(Block.field_149766_f).func_149663_c("comparator").func_149649_H().func_149658_d("comparator_on"));
        Block.field_149771_c.func_148756_a(151, "daylight_detector", (new BlockDaylightDetector()).func_149711_c(0.2F).func_149672_a(Block.field_149766_f).func_149663_c("daylightDetector").func_149658_d("daylight_detector"));
        Block.field_149771_c.func_148756_a(152, "redstone_block", (new BlockCompressedPowered(MapColor.field_151656_f)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(Block.field_149777_j).func_149663_c("blockRedstone").func_149658_d("redstone_block"));
        Block.field_149771_c.func_148756_a(153, "quartz_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(Block.field_149780_i).func_149663_c("netherquartz").func_149658_d("quartz_ore"));
        Block.field_149771_c.func_148756_a(154, "hopper", (new BlockHopper()).func_149711_c(3.0F).func_149752_b(8.0F).func_149672_a(Block.field_149766_f).func_149663_c("hopper").func_149658_d("hopper"));
        Block block9 = (new BlockQuartz()).func_149672_a(Block.field_149780_i).func_149711_c(0.8F).func_149663_c("quartzBlock").func_149658_d("quartz_block");

        Block.field_149771_c.func_148756_a(155, "quartz_block", block9);
        Block.field_149771_c.func_148756_a(156, "quartz_stairs", (new BlockStairs(block9, 0)).func_149663_c("stairsQuartz"));
        Block.field_149771_c.func_148756_a(157, "activator_rail", (new BlockRailPowered()).func_149711_c(0.7F).func_149672_a(Block.field_149777_j).func_149663_c("activatorRail").func_149658_d("rail_activator"));
        Block.field_149771_c.func_148756_a(158, "dropper", (new BlockDropper()).func_149711_c(3.5F).func_149672_a(Block.field_149780_i).func_149663_c("dropper").func_149658_d("dropper"));
        Block.field_149771_c.func_148756_a(159, "stained_hardened_clay", (new BlockColored(Material.field_151576_e)).func_149711_c(1.25F).func_149752_b(7.0F).func_149672_a(Block.field_149780_i).func_149663_c("clayHardenedStained").func_149658_d("hardened_clay_stained"));
        Block.field_149771_c.func_148756_a(160, "stained_glass_pane", (new BlockStainedGlassPane()).func_149711_c(0.3F).func_149672_a(Block.field_149778_k).func_149663_c("thinStainedGlass").func_149658_d("glass"));
        Block.field_149771_c.func_148756_a(161, "leaves2", (new BlockNewLeaf()).func_149663_c("leaves").func_149658_d("leaves"));
        Block.field_149771_c.func_148756_a(162, "log2", (new BlockNewLog()).func_149663_c("log").func_149658_d("log"));
        Block.field_149771_c.func_148756_a(163, "acacia_stairs", (new BlockStairs(block1, 4)).func_149663_c("stairsWoodAcacia"));
        Block.field_149771_c.func_148756_a(164, "dark_oak_stairs", (new BlockStairs(block1, 5)).func_149663_c("stairsWoodDarkOak"));
        Block.field_149771_c.func_148756_a(170, "hay_block", (new BlockHay()).func_149711_c(0.5F).func_149672_a(Block.field_149779_h).func_149663_c("hayBlock").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("hay_block"));
        Block.field_149771_c.func_148756_a(171, "carpet", (new BlockCarpet()).func_149711_c(0.1F).func_149672_a(Block.field_149775_l).func_149663_c("woolCarpet").func_149713_g(0));
        Block.field_149771_c.func_148756_a(172, "hardened_clay", (new BlockHardenedClay()).func_149711_c(1.25F).func_149752_b(7.0F).func_149672_a(Block.field_149780_i).func_149663_c("clayHardened").func_149658_d("hardened_clay"));
        Block.field_149771_c.func_148756_a(173, "coal_block", (new Block(Material.field_151576_e)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(Block.field_149780_i).func_149663_c("blockCoal").func_149647_a(CreativeTabs.field_78030_b).func_149658_d("coal_block"));
        Block.field_149771_c.func_148756_a(174, "packed_ice", (new BlockPackedIce()).func_149711_c(0.5F).func_149672_a(Block.field_149778_k).func_149663_c("icePacked").func_149658_d("ice_packed"));
        Block.field_149771_c.func_148756_a(175, "double_plant", new BlockDoublePlant());
        Iterator iterator = Block.field_149771_c.iterator();

        while (iterator.hasNext()) {
            Block block10 = (Block) iterator.next();

            if (block10.field_149764_J == Material.field_151579_a) {
                block10.field_149783_u = false;
            } else {
                boolean flag = false;
                boolean flag1 = block10.func_149645_b() == 10;
                boolean flag2 = block10 instanceof BlockSlab;
                boolean flag3 = block10 == block4;
                boolean flag4 = block10.field_149785_s;
                boolean flag5 = block10.field_149786_r == 0;

                if (flag1 || flag2 || flag3 || flag4 || flag5) {
                    flag = true;
                }

                block10.field_149783_u = flag;
            }
        }

    }

    protected Block(Material material) {
        this.field_149762_H = Block.field_149769_e;
        this.field_149763_I = 1.0F;
        this.field_149765_K = 0.6F;
        this.field_149764_J = material;
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.field_149787_q = this.func_149662_c();
        this.field_149786_r = this.func_149662_c() ? 255 : 0;
        this.field_149785_s = !material.func_76228_b();
    }

    protected Block func_149672_a(Block$SoundType block$soundtype) {
        this.field_149762_H = block$soundtype;
        return this;
    }

    protected Block func_149713_g(int i) {
        this.field_149786_r = i;
        return this;
    }

    protected Block func_149715_a(float f) {
        this.field_149784_t = (int) (15.0F * f);
        return this;
    }

    protected Block func_149752_b(float f) {
        this.field_149781_w = f * 3.0F;
        return this;
    }

    public boolean func_149721_r() {
        return this.field_149764_J.func_76218_k() && this.func_149686_d() && !this.func_149744_f();
    }

    public boolean func_149686_d() {
        return true;
    }

    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return !this.field_149764_J.func_76230_c();
    }

    public int func_149645_b() {
        return 0;
    }

    protected Block func_149711_c(float f) {
        this.field_149782_v = f;
        if (this.field_149781_w < f * 5.0F) {
            this.field_149781_w = f * 5.0F;
        }

        return this;
    }

    protected Block func_149722_s() {
        this.func_149711_c(-1.0F);
        return this;
    }

    public float func_149712_f(World world, int i, int j, int k) {
        return this.field_149782_v;
    }

    protected Block func_149675_a(boolean flag) {
        this.field_149789_z = flag;
        return this;
    }

    public boolean func_149653_t() {
        return this.field_149789_z;
    }

    public boolean func_149716_u() {
        return this.field_149758_A;
    }

    protected final void func_149676_a(float f, float f1, float f2, float f3, float f4, float f5) {
        this.field_149759_B = (double) f;
        this.field_149760_C = (double) f1;
        this.field_149754_D = (double) f2;
        this.field_149755_E = (double) f3;
        this.field_149756_F = (double) f4;
        this.field_149757_G = (double) f5;
    }

    public boolean func_149747_d(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return iblockaccess.func_147439_a(i, j, k).func_149688_o().func_76220_a();
    }

    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        AxisAlignedBB axisalignedbb1 = this.func_149668_a(world, i, j, k);

        if (axisalignedbb1 != null && axisalignedbb.func_72326_a(axisalignedbb1)) {
            list.add(axisalignedbb1);
        }

    }

    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return AxisAlignedBB.func_72330_a((double) i + this.field_149759_B, (double) j + this.field_149760_C, (double) k + this.field_149754_D, (double) i + this.field_149755_E, (double) j + this.field_149756_F, (double) k + this.field_149757_G);
    }

    public boolean func_149662_c() {
        return true;
    }

    public boolean func_149678_a(int i, boolean flag) {
        return this.func_149703_v();
    }

    public boolean func_149703_v() {
        return true;
    }

    public void func_149674_a(World world, int i, int j, int k, Random random) {}

    public void func_149664_b(World world, int i, int j, int k, int l) {}

    public void func_149695_a(World world, int i, int j, int k, Block block) {}

    public int func_149738_a(World world) {
        return 10;
    }

    public void func_149726_b(World world, int i, int j, int k) {}

    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {}

    public int func_149745_a(Random random) {
        return 1;
    }

    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(this);
    }

    public float func_149737_a(EntityPlayer entityplayer, World world, int i, int j, int k) {
        float f = this.func_149712_f(world, i, j, k);

        return f < 0.0F ? 0.0F : (!entityplayer.func_146099_a(this) ? entityplayer.func_146096_a(this, false) / f / 100.0F : entityplayer.func_146096_a(this, true) / f / 30.0F);
    }

    public final void func_149697_b(World world, int i, int j, int k, int l, int i1) {
        this.func_149690_a(world, i, j, k, l, 1.0F, i1);
    }

    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!world.field_72995_K) {
            int j1 = this.func_149679_a(i1, world.field_73012_v);

            for (int k1 = 0; k1 < j1; ++k1) {
                if (world.field_73012_v.nextFloat() <= f) {
                    Item item = this.func_149650_a(l, world.field_73012_v, i1);

                    if (item != null) {
                        this.func_149642_a(world, i, j, k, new ItemStack(item, 1, this.func_149692_a(l)));
                    }
                }
            }

        }
    }

    protected void func_149642_a(World world, int i, int j, int k, ItemStack itemstack) {
        if (!world.field_72995_K && world.func_82736_K().func_82766_b("doTileDrops")) {
            float f = 0.7F;
            double d0 = (double) (world.field_73012_v.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.field_73012_v.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.field_73012_v.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) i + d0, (double) j + d1, (double) k + d2, itemstack);

            entityitem.field_145804_b = 10;
            world.func_72838_d(entityitem);
        }
    }

    protected void func_149657_c(World world, int i, int j, int k, int l) {
        if (!world.field_72995_K) {
            while (l > 0) {
                int i1 = EntityXPOrb.func_70527_a(l);

                l -= i1;
                world.func_72838_d(new EntityXPOrb(world, (double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, i1));
            }
        }

    }

    public int func_149692_a(int i) {
        return 0;
    }

    public float func_149638_a(Entity entity) {
        return this.field_149781_w / 5.0F;
    }

    public MovingObjectPosition func_149731_a(World world, int i, int j, int k, Vec3 vec3, Vec3 vec31) {
        this.func_149719_a(world, i, j, k);
        vec3 = vec3.func_72441_c((double) (-i), (double) (-j), (double) (-k));
        vec31 = vec31.func_72441_c((double) (-i), (double) (-j), (double) (-k));
        Vec3 vec32 = vec3.func_72429_b(vec31, this.field_149759_B);
        Vec3 vec33 = vec3.func_72429_b(vec31, this.field_149755_E);
        Vec3 vec34 = vec3.func_72435_c(vec31, this.field_149760_C);
        Vec3 vec35 = vec3.func_72435_c(vec31, this.field_149756_F);
        Vec3 vec36 = vec3.func_72434_d(vec31, this.field_149754_D);
        Vec3 vec37 = vec3.func_72434_d(vec31, this.field_149757_G);

        if (!this.func_149654_a(vec32)) {
            vec32 = null;
        }

        if (!this.func_149654_a(vec33)) {
            vec33 = null;
        }

        if (!this.func_149687_b(vec34)) {
            vec34 = null;
        }

        if (!this.func_149687_b(vec35)) {
            vec35 = null;
        }

        if (!this.func_149661_c(vec36)) {
            vec36 = null;
        }

        if (!this.func_149661_c(vec37)) {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || vec3.func_72436_e(vec32) < vec3.func_72436_e(vec38))) {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || vec3.func_72436_e(vec33) < vec3.func_72436_e(vec38))) {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || vec3.func_72436_e(vec34) < vec3.func_72436_e(vec38))) {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || vec3.func_72436_e(vec35) < vec3.func_72436_e(vec38))) {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || vec3.func_72436_e(vec36) < vec3.func_72436_e(vec38))) {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || vec3.func_72436_e(vec37) < vec3.func_72436_e(vec38))) {
            vec38 = vec37;
        }

        if (vec38 == null) {
            return null;
        } else {
            byte b0 = -1;

            if (vec38 == vec32) {
                b0 = 4;
            }

            if (vec38 == vec33) {
                b0 = 5;
            }

            if (vec38 == vec34) {
                b0 = 0;
            }

            if (vec38 == vec35) {
                b0 = 1;
            }

            if (vec38 == vec36) {
                b0 = 2;
            }

            if (vec38 == vec37) {
                b0 = 3;
            }

            return new MovingObjectPosition(i, j, k, b0, vec38.func_72441_c((double) i, (double) j, (double) k));
        }
    }

    private boolean func_149654_a(Vec3 vec3) {
        return vec3 == null ? false : vec3.field_72448_b >= this.field_149760_C && vec3.field_72448_b <= this.field_149756_F && vec3.field_72449_c >= this.field_149754_D && vec3.field_72449_c <= this.field_149757_G;
    }

    private boolean func_149687_b(Vec3 vec3) {
        return vec3 == null ? false : vec3.field_72450_a >= this.field_149759_B && vec3.field_72450_a <= this.field_149755_E && vec3.field_72449_c >= this.field_149754_D && vec3.field_72449_c <= this.field_149757_G;
    }

    private boolean func_149661_c(Vec3 vec3) {
        return vec3 == null ? false : vec3.field_72450_a >= this.field_149759_B && vec3.field_72450_a <= this.field_149755_E && vec3.field_72448_b >= this.field_149760_C && vec3.field_72448_b <= this.field_149756_F;
    }

    public void func_149723_a(World world, int i, int j, int k, Explosion explosion) {}

    public boolean func_149705_a(World world, int i, int j, int k, int l, ItemStack itemstack) {
        return this.func_149707_d(world, i, j, k, l);
    }

    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        return this.func_149742_c(world, i, j, k);
    }

    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147439_a(i, j, k).field_149764_J.func_76222_j();
    }

    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        return false;
    }

    public void func_149724_b(World world, int i, int j, int k, Entity entity) {}

    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        return i1;
    }

    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {}

    public void func_149640_a(World world, int i, int j, int k, Entity entity, Vec3 vec3) {}

    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {}

    public final double func_149704_x() {
        return this.field_149759_B;
    }

    public final double func_149753_y() {
        return this.field_149755_E;
    }

    public final double func_149665_z() {
        return this.field_149760_C;
    }

    public final double func_149669_A() {
        return this.field_149756_F;
    }

    public final double func_149706_B() {
        return this.field_149754_D;
    }

    public final double func_149693_C() {
        return this.field_149757_G;
    }

    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return 0;
    }

    public boolean func_149744_f() {
        return false;
    }

    public void func_149670_a(World world, int i, int j, int k, Entity entity) {}

    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return 0;
    }

    public void func_149683_g() {}

    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        entityplayer.func_71064_a(StatList.field_75934_C[func_149682_b(this)], 1);
        entityplayer.func_71020_j(0.025F);
        if (this.func_149700_E() && EnchantmentHelper.func_77502_d(entityplayer)) {
            ItemStack itemstack = this.func_149644_j(l);

            if (itemstack != null) {
                this.func_149642_a(world, i, j, k, itemstack);
            }
        } else {
            int i1 = EnchantmentHelper.func_77517_e(entityplayer);

            this.func_149697_b(world, i, j, k, l, i1);
        }

    }

    protected boolean func_149700_E() {
        return this.func_149686_d() && !this.field_149758_A;
    }

    protected ItemStack func_149644_j(int i) {
        int j = 0;
        Item item = Item.func_150898_a(this);

        if (item != null && item.func_77614_k()) {
            j = i;
        }

        return new ItemStack(item, 1, j);
    }

    public int func_149679_a(int i, Random random) {
        return this.func_149745_a(random);
    }

    public boolean func_149718_j(World world, int i, int j, int k) {
        return true;
    }

    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {}

    public void func_149714_e(World world, int i, int j, int k, int l) {}

    public Block func_149663_c(String s) {
        this.field_149770_b = s;
        return this;
    }

    public String func_149732_F() {
        return StatCollector.func_74838_a(this.func_149739_a() + ".name");
    }

    public String func_149739_a() {
        return "tile." + this.field_149770_b;
    }

    public boolean func_149696_a(World world, int i, int j, int k, int l, int i1) {
        return false;
    }

    public boolean func_149652_G() {
        return this.field_149790_y;
    }

    protected Block func_149649_H() {
        this.field_149790_y = false;
        return this;
    }

    public int func_149656_h() {
        return this.field_149764_J.func_76227_m();
    }

    public void func_149746_a(World world, int i, int j, int k, Entity entity, float f) {}

    public int func_149643_k(World world, int i, int j, int k) {
        return this.func_149692_a(world.func_72805_g(i, j, k));
    }

    public Block func_149647_a(CreativeTabs creativetabs) {
        this.field_149772_a = creativetabs;
        return this;
    }

    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {}

    public void func_149725_f(World world, int i, int j, int k, int l) {}

    public void func_149639_l(World world, int i, int j, int k) {}

    public boolean func_149698_L() {
        return true;
    }

    public boolean func_149659_a(Explosion explosion) {
        return true;
    }

    public boolean func_149667_c(Block block) {
        return this == block;
    }

    public static boolean func_149680_a(Block block, Block block1) {
        return block != null && block1 != null ? (block == block1 ? true : block.func_149667_c(block1)) : false;
    }

    public boolean func_149740_M() {
        return false;
    }

    public int func_149736_g(World world, int i, int j, int k, int l) {
        return 0;
    }

    protected Block func_149658_d(String s) {
        this.field_149768_d = s;
        return this;
    }
}
