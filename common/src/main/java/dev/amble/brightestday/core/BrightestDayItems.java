package dev.amble.brightestday.core;

import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.core.items.PowerRingItem;
import dev.amble.brightestday.core.lanterns.LanternCorps;
import dev.amble.brightestday.platform.Services;
import dev.amble.brightestday.platform.services.PlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BrightestDayItems {

    public static final List<Holder<Item>> TAB_ITEMS = new ArrayList<>();

    private static final PlatformHelper.Registrar<Item> REGISTRAR = Services.PLATFORM.createRegistrar(Registries.ITEM);

    public static final Holder<Item> GREEN_POWER_RING = item("green_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.GREEN)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));

    public static final Holder<Item> YELLOW_POWER_RING = item("yellow_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.YELLOW)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> ORANGE_POWER_RING = item("orange_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.ORANGE)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> RED_POWER_RING = item("red_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.RED)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> STAR_SAPPHIRE_POWER_RING = item("star_sapphire_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.STAR_SAPPHIRE)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> INDIGO_POWER_RING = item("indigo_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.INDIGO)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> BLUE_POWER_RING = item("blue_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.BLUE)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> WHITE_POWER_RING = item("white_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.WHITE)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));
    public static final Holder<Item> BLACK_POWER_RING = item("black_power_ring", properties ->
            new PowerRingItem(properties
                    .fireResistant()
                    .stacksTo(1)
                    .component(BrightestDayComponents.POWER_TYPE, 0)
                    .component(BrightestDayComponents.LANTERN_CORPS, LanternCorps.BLACK)
                    .component(DataComponents.TOOLTIP_STYLE, BrightestDay.id("ring"))
            ));

    public static final Holder<Item> GREEN_LANTERN = blockItem("green_lantern", BrightestDayBlocks.GREEN_LANTERN_BLOCK);
    public static final Holder<Item> YELLOW_LANTERN = blockItem("yellow_lantern", BrightestDayBlocks.YELLOW_LANTERN_BLOCK);
    public static final Holder<Item> ORANGE_LANTERN = blockItem("orange_lantern", BrightestDayBlocks.ORANGE_LANTERN_BLOCK);
    public static final Holder<Item> RED_LANTERN = blockItem("red_lantern", BrightestDayBlocks.RED_LANTERN_BLOCK);
    public static final Holder<Item> STAR_SAPPHIRE_LANTERN = blockItem("star_sapphire_lantern", BrightestDayBlocks.STAR_SAPPHIRE_LANTERN_BLOCK);
    public static final Holder<Item> INDIGO_LANTERN = blockItem("indigo_lantern", BrightestDayBlocks.INDIGO_LANTERN_BLOCK);
    public static final Holder<Item> BLUE_LANTERN = blockItem("blue_lantern", BrightestDayBlocks.BLUE_LANTERN_BLOCK);

    private static Holder<Item> item(String name, Function<Item.Properties, Item> factory) {
        Holder<Item> holder = REGISTRAR.registerHolder(name, identifier -> factory.apply(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, identifier))));
        TAB_ITEMS.add(holder);
        return holder;
    }

    private static Holder<Item> blockItem(String name, Holder<Block> block) {
        return item(name, properties -> {
            BlockItem item = new BlockItem(block.value(), properties.useBlockDescriptionPrefix());
            item.registerBlocks(Item.BY_BLOCK, item);
            return item;
        });
    }

    public static void register() {
        REGISTRAR.registerAll();
    }
}
