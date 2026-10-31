package dev.amblelabs.brightestday.core;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.blocks.BrightestDayLanternBlock;
import dev.amblelabs.brightestday.core.lanterns.LanternCorps;
import dev.amblelabs.brightestday.platform.Services;
import dev.amblelabs.brightestday.platform.services.PlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class BrightestDayBlocks {

    private static final PlatformHelper.Registrar<Block> REGISTRAR = Services.PLATFORM.createRegistrar(Registries.BLOCK);

    public static final Holder<Block> GREEN_LANTERN_BLOCK = block("green_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.GREEN, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    public static final Holder<Block> YELLOW_LANTERN_BLOCK = block("yellow_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.YELLOW, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    public static final Holder<Block> ORANGE_LANTERN_BLOCK = block("orange_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.ORANGE, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    public static final Holder<Block> RED_LANTERN_BLOCK = block("red_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.RED, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    public static final Holder<Block> STAR_SAPPHIRE_LANTERN_BLOCK = block("star_sapphire_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.STAR_SAPPHIRE, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    public static final Holder<Block> INDIGO_LANTERN_BLOCK = block("indigo_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.INDIGO, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    public static final Holder<Block> BLUE_LANTERN_BLOCK = block("blue_lantern",
            properties -> new BrightestDayLanternBlock(LanternCorps.BLUE, properties
                    .strength(0.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
            ));

    private static <T extends Block> Holder<T> block(String name, Function<BlockBehaviour.Properties, T> function) {
        return REGISTRAR.registerHolder(name, id -> {
            ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().setId(blockKey);
            return function.apply(properties);
        });
    }

    private static <T extends Block> void registerBlockItem(String name, T block) {
        Identifier id = Identifier.fromNamespaceAndPath(BrightestDay.MOD_ID, name);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

        Item.Properties properties = new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(itemKey);

        Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, properties));
    }

    public static void register() {
        REGISTRAR.registerAll();
    }
}
