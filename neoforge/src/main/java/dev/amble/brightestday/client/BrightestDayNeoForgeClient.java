package dev.amble.brightestday.client;

import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.client.flight.FlightAnimator;
import dev.amble.brightestday.client.networking.LandingS2CPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = BrightestDay.MOD_ID, dist = Dist.CLIENT)
public class BrightestDayNeoForgeClient {

    public BrightestDayNeoForgeClient(IEventBus eventBus) {
        BrightestDayClient.init();

        eventBus.addListener(RegisterClientPayloadHandlersEvent.class, event ->
                event.register(LandingS2CPayload.TYPE, (payload, context) -> FlightAnimator.handleLandingRelay(payload)));
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> BrightestDayClient.tick(Minecraft.getInstance()));
    }
}
