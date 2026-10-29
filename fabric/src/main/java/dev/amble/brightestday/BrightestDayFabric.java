package dev.amble.brightestday;

import dev.amble.brightestday.core.BrightestDayRegistries;
import dev.amble.brightestday.core.suits.Suit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class BrightestDayFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BrightestDay.init();
        registerDynamicRegistries();
    }

    private void registerDynamicRegistries() {
        DynamicRegistries.registerSynced(BrightestDayRegistries.SUIT, Suit.CODEC);
    }
}
