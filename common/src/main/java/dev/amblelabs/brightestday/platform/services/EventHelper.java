package dev.amblelabs.brightestday.platform.services;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public interface EventHelper {

    void onServerStarted(Consumer<MinecraftServer> listener);

    void onServerTick(Consumer<MinecraftServer> listener);

    void onPlayerDisconnect(Consumer<ServerPlayer> listener);
}
