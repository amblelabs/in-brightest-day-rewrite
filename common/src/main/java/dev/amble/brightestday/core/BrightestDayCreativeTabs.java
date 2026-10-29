package dev.amble.brightestday.core;

import dev.amble.brightestday.platform.Services;
import dev.amble.brightestday.platform.services.PlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

import static dev.amble.brightestday.core.BrightestDayItems.TAB_ITEMS;

public class BrightestDayCreativeTabs {
    private static final PlatformHelper.Registrar<CreativeModeTab> REGISTRAR = Services.PLATFORM.createRegistrar(Registries.CREATIVE_MODE_TAB);

    public static final Holder<CreativeModeTab> MAIN = REGISTRAR.registerHolder("main", identifier -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + identifier.getNamespace() + "." + identifier.getPath()))
            .icon(() -> new ItemStack(BrightestDayItems.GREEN_POWER_RING))
            .build());

    public static void fillTab(ResourceKey<CreativeModeTab> tab, Consumer<ItemLike> output) {
        if (MAIN.is(tab)) TAB_ITEMS.forEach(item -> output.accept(item.value()));
    }

    public static void register() {
        REGISTRAR.registerAll();
    }
}
