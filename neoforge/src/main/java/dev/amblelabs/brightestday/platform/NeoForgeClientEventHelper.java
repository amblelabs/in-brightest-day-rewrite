package dev.amblelabs.brightestday.platform;

import dev.amblelabs.brightestday.platform.services.ClientEventHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NeoForgeClientEventHelper implements ClientEventHelper {
    private static final Map<Identifier, HudLayer> HUD_LAYERS = new LinkedHashMap<>();

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterGuiLayersEvent.class, event -> HUD_LAYERS.forEach((id, layer) ->
                event.registerAbove(VanillaGuiLayers.CONTEXTUAL_INFO_BAR, id, layer::extract)));
    }

    @Override
    public void onClientTick(Consumer<Minecraft> listener) {
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, _ -> listener.accept(Minecraft.getInstance()));
    }

    @Override
    public void onClientDisconnect(Runnable listener) {
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, _ -> listener.run());
    }

    @Override
    public void registerHudAfterInfoBar(Identifier id, HudLayer layer) {
        HUD_LAYERS.put(id, layer);
    }
}
