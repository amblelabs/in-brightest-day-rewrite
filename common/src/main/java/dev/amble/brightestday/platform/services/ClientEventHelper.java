package dev.amble.brightestday.platform.services;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public interface ClientEventHelper {

    void onClientTick(Consumer<Minecraft> listener);

    void onClientDisconnect(Runnable listener);

    void registerHudAfterInfoBar(Identifier id, HudLayer layer);

    @FunctionalInterface
    interface HudLayer {
        void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }
}
