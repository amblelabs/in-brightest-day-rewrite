package dev.amble.brightestday.core;

import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.core.suits.Suit;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class BrightestDayRegistries {
    public static final ResourceKey<Registry<Suit>> SUIT = ResourceKey.createRegistryKey(BrightestDay.id("suit"));
}
