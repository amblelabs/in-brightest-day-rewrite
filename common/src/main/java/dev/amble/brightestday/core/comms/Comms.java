package dev.amble.brightestday.core.comms;

import dev.amble.brightestday.platform.Services;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class Comms {
    public static final String VOICE_CHAT_MOD_ID = "voicechat";

    private static volatile Predicate<UUID> voiceReady = id -> false;
    private static final Set<UUID> MUTED = ConcurrentHashMap.newKeySet();
    public static volatile boolean localMuted;

    public static void init() {
        Services.EVENTS.onPlayerDisconnect(player -> MUTED.remove(player.getUUID()));
    }

    public static boolean available() {
        return Services.PLATFORM.isModLoaded(VOICE_CHAT_MOD_ID);
    }

    public static void setVoiceReady(Predicate<UUID> check) {
        voiceReady = check;
    }

    public static boolean voiceReady(UUID player) {
        return available() && !MUTED.contains(player) && voiceReady.test(player);
    }

    public static void setMuted(UUID player, boolean muted) {
        if (muted) MUTED.add(player);
        else MUTED.remove(player);
    }

    public static boolean muted(Player player) {
        return player.level().isClientSide() ? localMuted : MUTED.contains(player.getUUID());
    }

    private Comms() {}
}
