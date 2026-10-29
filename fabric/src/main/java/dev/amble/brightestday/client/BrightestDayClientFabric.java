package dev.amble.brightestday.client;

import dev.amble.brightestday.client.render.FrostedGlass;
import dev.amble.brightestday.client.render.ShaderOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class BrightestDayClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BrightestDayClient.init();

        LevelRenderEvents.START_MAIN.register(context -> ShaderOverlay.startMain());
        LevelRenderEvents.END_MAIN.register(context -> FrostedGlass.endMain());
    }
}
