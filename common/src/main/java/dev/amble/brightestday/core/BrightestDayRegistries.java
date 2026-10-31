package dev.amble.brightestday.core;

import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.core.ring.RingPower;
import dev.amble.brightestday.core.suits.Suit;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class BrightestDayRegistries {
    public static final ResourceKey<Registry<Suit>> SUIT = ResourceKey.createRegistryKey(BrightestDay.id("suit"));
    public static final ResourceKey<Registry<RingPower<?>>> RING_POWER = ResourceKey.createRegistryKey(BrightestDay.id("ring_power"));
}
