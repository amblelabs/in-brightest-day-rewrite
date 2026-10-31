package dev.amblelabs.brightestday.core.flight;

import dev.amblelabs.brightestday.client.networking.LandingS2CPayload;
import dev.amblelabs.brightestday.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.WeakHashMap;

public final class Landings {
    private static final long MIN_GAP_TICKS = 10L;
    private static final int MAX_POSE = 8;

    private static final Map<ServerPlayer, Long> LAST = new WeakHashMap<>();

    public static void relay(ServerPlayer player, int pose, float strength) {
        long now = player.level().getGameTime();
        Long last = LAST.get(player);
        if (last != null && now - last < MIN_GAP_TICKS) return;
        LAST.put(player, now);
        Services.PLATFORM.sendToTracking(player, new LandingS2CPayload(player.getId(), Mth.clamp(pose, -1, MAX_POSE), Mth.clamp(strength, 0.0F, 1.0F)));
    }

    private Landings() {}
}
