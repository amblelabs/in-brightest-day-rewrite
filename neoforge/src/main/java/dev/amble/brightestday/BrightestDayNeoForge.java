package dev.amble.brightestday;

import dev.amble.brightestday.client.networking.LandingC2SPayload;
import dev.amble.brightestday.client.networking.LandingS2CPayload;
import dev.amble.brightestday.client.networking.SetFlightC2SPayload;
import dev.amble.brightestday.client.networking.UpdateSuitPayload;
import dev.amble.brightestday.core.ring.RingPowerTicker;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(BrightestDay.MOD_ID)
public class BrightestDayNeoForge {

    public static IEventBus EVENT_BUS;

    public BrightestDayNeoForge(IEventBus eventBus) {
        EVENT_BUS = eventBus;

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        BrightestDay.init();
        eventBus.addListener(RegisterPayloadHandlersEvent.class, BrightestDayNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> RingPowerTicker.tick(event.getServer()));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(UpdateSuitPayload.TYPE, UpdateSuitPayload.STREAM_CODEC,
                (payload, context) -> UpdateSuitPayload.handle(payload, (ServerPlayer) context.player()));
        registrar.playToServer(SetFlightC2SPayload.TYPE, SetFlightC2SPayload.STREAM_CODEC,
                (payload, context) -> SetFlightC2SPayload.handle(payload, context.player()));
        registrar.playToServer(LandingC2SPayload.TYPE, LandingC2SPayload.STREAM_CODEC,
                (payload, context) -> LandingC2SPayload.handle((ServerPlayer) context.player(), payload));
        registrar.playToClient(LandingS2CPayload.TYPE, LandingS2CPayload.STREAM_CODEC);
    }
}
