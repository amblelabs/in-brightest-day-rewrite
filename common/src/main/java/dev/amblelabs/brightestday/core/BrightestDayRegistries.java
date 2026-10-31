package dev.amblelabs.brightestday.core;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.ring.RingPower;
import dev.amblelabs.brightestday.core.suits.Suit;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class BrightestDayRegistries {
    public static final ResourceKey<Registry<Suit>> SUIT = ResourceKey.createRegistryKey(BrightestDay.id("suit"));
    public static final ResourceKey<Registry<RingPower<?>>> RING_POWER = ResourceKey.createRegistryKey(BrightestDay.id("ring_power"));
}
