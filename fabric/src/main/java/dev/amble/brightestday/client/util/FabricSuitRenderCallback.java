package dev.amble.brightestday.client.util;

import dev.amble.brightestday.client.render.util.SuitRenderCallback;
import dev.amble.brightestday.client.render.util.SuitRenderTickEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FabricSuitRenderCallback implements SuitRenderTickEvent {

    public static void init() {}

    @Override
    public void registerSuitRenderCallback(SuitRenderCallback cb) {
        ClientTickEvents.END_CLIENT_TICK.register(cb::tick);
    }
}
