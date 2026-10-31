package dev.amble.brightestday.platform;

import dev.amble.brightestday.platform.services.EventHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class FabricEventHelper implements EventHelper {

    @Override
    public void onServerStarted(Consumer<MinecraftServer> listener) {
        ServerLifecycleEvents.SERVER_STARTED.register(listener::accept);
    }

    @Override
    public void onServerTick(Consumer<MinecraftServer> listener) {
        ServerTickEvents.END_SERVER_TICK.register(listener::accept);
    }

    @Override
    public void onPlayerDisconnect(Consumer<ServerPlayer> listener) {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> listener.accept(handler.player));
    }
}
