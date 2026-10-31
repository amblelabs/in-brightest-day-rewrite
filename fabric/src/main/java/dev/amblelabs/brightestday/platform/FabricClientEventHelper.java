package dev.amblelabs.brightestday.platform;

import dev.amblelabs.brightestday.platform.services.ClientEventHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class FabricClientEventHelper implements ClientEventHelper {

    @Override
    public void onClientTick(Consumer<Minecraft> listener) {
        ClientTickEvents.END_CLIENT_TICK.register(listener::accept);
    }

    @Override
    public void onClientDisconnect(Runnable listener) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> listener.run());
    }

    @Override
    public void registerHudAfterInfoBar(Identifier id, HudLayer layer) {
        HudElementRegistry.attachElementAfter(VanillaHudElements.INFO_BAR, id, layer::extract);
    }
}
