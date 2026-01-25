package mod.pilot.horseshoe_crab_takeover.items;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import mod.pilot.horseshoe_crab_takeover.items.unique.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HorseshoeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Horseshoe_Crab_Takeover.MOD_ID);

    public static final RegistryObject<Item> HORSESHOE_CRAB_SPAWNEGG = ITEMS.register("old_horseshoe_crab_spawn",
            () -> new ForgeSpawnEggItem(HorseshoeEntities.OLD_HORSESHOE_CRAB, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> NEW_HORSESHOE_CRAB_SPAWNEGG = ITEMS.register("horseshoe_crab_spawn",
            () -> new WorldEntitySpawnEgg(HorseshoeEntities.HORSESHOE_CRAB,
                    -1, -1, new Item.Properties()));

    public static final RegistryObject<Item> A_STAR_GRID_WAND = ITEMS.register("a_star_grid_wand",
            () -> new AStarGridWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NODE_3D_WAND = ITEMS.register("3d_grid_wand",
            () -> new Node3DGridWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BIT_PACKAGE_WAND = ITEMS.register("bit_package_wand",
            () -> new BitPackageTestWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item>  FACE_DEBUG_WAND = ITEMS.register("face_debug_wand",
            () -> new FaceDebugRenderWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item>  GSTAR_NODE_WAND = ITEMS.register("gstar_node_wand",
            () -> new GStarNodeGeneratorWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item>  GREEDY_CHUNK_READER = ITEMS.register("gchunk_reader",
            () -> new GreedyChunkReaderWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item>  QUADSPACE_FILL_WAND = ITEMS.register("quadspace_wand",
            () -> new QuadSpaceFillWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CHUNK_CONTEXT_READER = ITEMS.register("chunk_context_wand",
            () -> new GreedyChunkRelativeWand(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GREEDY_CHUNK_GRABBER = ITEMS.register("chunk_grabber_wand",
            () -> new GreedyChunkGrabberWand(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
