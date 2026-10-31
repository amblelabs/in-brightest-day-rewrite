package dev.amblelabs.brightestday.core.ring;

import dev.amblelabs.brightestday.core.BrightestDayRegistries;
import dev.amblelabs.brightestday.core.lanterns.LanternCorps;
import dev.amblelabs.brightestday.core.ring.impl.FlightRingPower;
import dev.amblelabs.brightestday.platform.Services;
import dev.amblelabs.brightestday.platform.services.PlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RingPowerRegistry {

    public static final Registry<RingPower<?>> REGISTRY = Services.PLATFORM.createRegistry(BrightestDayRegistries.RING_POWER);
    private static final PlatformHelper.Registrar<RingPower<?>> REGISTRAR = Services.PLATFORM.createRegistrar(BrightestDayRegistries.RING_POWER);
    private static final Map<LanternCorps, List<RingPower<?>>> BY_CORPS = new ConcurrentHashMap<>();

    public static final FlightRingPower FLIGHT_RING_POWER = register(new FlightRingPower());

    public static Registry<RingPower<?>> ringPowerRegistry() {
        return REGISTRY;
    }

    public static <T extends RingPower<?>> T register(T power) {
        REGISTRAR.register(power.id(), () -> power);
        return power;
    }

    public static Optional<RingPower<?>> get(Identifier id) {
        return REGISTRY.get(id).map(Holder::value);
    }

    public static List<RingPower<?>> forCorps(LanternCorps corps, @Nullable LanternCorps borrowed, Set<LanternCorps> mimicked) {
        if (borrowed == null && mimicked.isEmpty()) return forCorps(corps);
        return REGISTRY.stream()
                .filter(power -> power.isAvailableTo(corps)
                        || (borrowed != null && power.isAvailableTo(borrowed))
                        || (/*power instanceof ConstructRingPower && */mimicked.stream().anyMatch(power::isAvailableTo)))
                .toList();
    }

    public static List<RingPower<?>> forCorps(LanternCorps corps) {
        return BY_CORPS.computeIfAbsent(corps, key -> REGISTRY.stream().filter(power -> power.isAvailableTo(key)).toList());
    }

    public static void init() {
        REGISTRAR.registerAll();
    }
}
