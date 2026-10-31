package dev.amble.brightestday.client;

import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.client.effects.LanternChargeAnimations;
import dev.amble.brightestday.client.flight.FlightAnimator;
import dev.amble.brightestday.client.hud.OathHud;
import dev.amble.brightestday.client.networking.LandingS2CPayload;
import dev.amble.brightestday.client.networking.LanternRitualS2CPayload;
import dev.amble.brightestday.client.networking.OathS2CPayload;
import dev.amble.brightestday.platform.NeoForgeClientEventHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@Mod(value = BrightestDay.MOD_ID, dist = Dist.CLIENT)
public class BrightestDayNeoForgeClient {

    public BrightestDayNeoForgeClient(IEventBus eventBus) {
        NeoForgeClientEventHelper.register(eventBus);
        BrightestDayClient.init();

        eventBus.addListener(RegisterClientPayloadHandlersEvent.class, event -> {
            event.register(LandingS2CPayload.TYPE, (payload, context) -> FlightAnimator.handleLandingRelay(payload));
            event.register(OathS2CPayload.TYPE, (payload, context) -> OathHud.handle(payload));
            event.register(LanternRitualS2CPayload.TYPE, (payload, context) -> LanternChargeAnimations.handleRitual(payload));
        });
    }
}
