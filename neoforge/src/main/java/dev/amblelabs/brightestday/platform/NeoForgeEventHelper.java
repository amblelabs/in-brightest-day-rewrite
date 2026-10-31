package dev.amblelabs.brightestday.platform;

import dev.amblelabs.brightestday.platform.services.EventHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.function.Consumer;

public class NeoForgeEventHelper implements EventHelper {

    @Override
    public void onServerStarted(Consumer<MinecraftServer> listener) {
        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> listener.accept(event.getServer()));
    }

    @Override
    public void onServerTick(Consumer<MinecraftServer> listener) {
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> listener.accept(event.getServer()));
    }

    @Override
    public void onPlayerDisconnect(Consumer<ServerPlayer> listener) {
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) listener.accept(player);
        });
    }
}
