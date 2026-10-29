package dev.amble.brightestday;

import dev.amble.brightestday.core.BrightestDayCreativeTabs;
import dev.amble.brightestday.core.BrightestDayRegistries;
import dev.amble.brightestday.core.suits.Suit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class BrightestDayFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BrightestDay.init();
        registerDynamicRegistries();
        CreativeModeTabEvents.modifyOutputEvent(BrightestDayCreativeTabs.MAIN.unwrapKey().orElseThrow())
                .register(entries -> BrightestDayCreativeTabs.fillTab(BrightestDayCreativeTabs.MAIN.unwrapKey().orElseThrow(), entries::accept));
    }

    private void registerDynamicRegistries() {
        DynamicRegistries.registerSynced(BrightestDayRegistries.SUIT, Suit.CODEC);
    }
}
