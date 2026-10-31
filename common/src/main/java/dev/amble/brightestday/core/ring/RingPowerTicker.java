package dev.amble.brightestday.core.ring;

import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.items.PowerRingItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

public final class RingPowerTicker {
    private static final Map<ServerPlayer, Boolean> CHARGED = new WeakHashMap<>();
    private static final int DEPLETION_RECHECK_TICKS = 10;

    public static void tick(MinecraftServer server) {
        boolean drainTick = server.getTickCount() % 20 == 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ItemStack ring = BrightestDayAttachments.getRing(player);
            if (!ring.isEmpty() /*&& !CorpsSynergy.empoweredByHope(player)*/ && PowerRingItem.tickCharge(ring, player.level())) {
                BrightestDayAttachments.setRing(player, ring);
            }

            BrightestDayAttachments.sync(player, PowerRingItem.getWornCorps(player).orElse(null));
            // RingBenefits.tick(player, server.getTickCount());
            // ConstructTools.tick(player, server.getTickCount());
            depleteOnEmpty(player, server.getTickCount());
            // warnOnLowCharge(player);
            // if (drainTick) RingRanks.clampCharge(player);

            for (RingPowerInstance<?> instance : BrightestDayAttachments.get(player)) {
                instance.tick(player);
                if (drainTick) drain(player, instance);
            }
        }
    }

    private static void depleteOnEmpty(ServerPlayer player, int serverTick) {
        boolean charged = PowerRingItem.hasCharge(player);
        Boolean wasCharged = CHARGED.put(player, charged);
        if (charged) return;

        boolean justDied = !Boolean.FALSE.equals(wasCharged);
        /*if (justDied) {
            ArmedRingPower.lower(player);
            ConstructDismissal.dismissAll(player);
        }*/
        if (!justDied && serverTick % DEPLETION_RECHECK_TICKS != 0) return;

        for (RingPowerInstance<?> instance : BrightestDayAttachments.get(player)) {
            if (!instance.power().worksWithoutCharge()) instance.onDepleted(player);
        }
    }

    private static void drain(ServerPlayer player, RingPowerInstance<?> instance) {
        if (player.hasInfiniteMaterials()) return;

        int drain = /*CorpsCombat.utilityCost(player, */instance.drainPerSecond(player);
        if (drain > 0 && !PowerRingItem.drainWorn(player, drain)) {
            instance.onDepleted(player);
        }
    }

    private RingPowerTicker() {}
}
