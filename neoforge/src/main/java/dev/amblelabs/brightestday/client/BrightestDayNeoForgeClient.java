package dev.amblelabs.brightestday.client;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.client.effects.LanternChargeAnimations;
import dev.amblelabs.brightestday.client.flight.FlightAnimator;
import dev.amblelabs.brightestday.client.hud.OathHud;
import dev.amblelabs.brightestday.client.networking.LandingS2CPayload;
import dev.amblelabs.brightestday.client.networking.LanternRitualS2CPayload;
import dev.amblelabs.brightestday.client.networking.OathS2CPayload;
import dev.amblelabs.brightestday.platform.NeoForgeClientEventHelper;
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
            event.register(LandingS2CPayload.TYPE, (payload, _) -> FlightAnimator.handleLandingRelay(payload));
            event.register(OathS2CPayload.TYPE, (payload, _) -> OathHud.handle(payload));
            event.register(LanternRitualS2CPayload.TYPE, (payload, _) -> LanternChargeAnimations.handleRitual(payload));
        });
    }
}
