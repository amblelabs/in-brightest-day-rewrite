package dev.amblelabs.brightestday;

import dev.amblelabs.brightestday.client.networking.LandingC2SPayload;
import dev.amblelabs.brightestday.client.networking.LandingS2CPayload;
import dev.amblelabs.brightestday.client.networking.LanternRitualS2CPayload;
import dev.amblelabs.brightestday.client.networking.OathS2CPayload;
import dev.amblelabs.brightestday.client.networking.SetFlightC2SPayload;
import dev.amblelabs.brightestday.client.networking.UpdateSuitPayload;
import dev.amblelabs.brightestday.client.networking.VoiceMutedC2SPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
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
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(UpdateSuitPayload.TYPE, UpdateSuitPayload.STREAM_CODEC,
                (payload, context) -> UpdateSuitPayload.handle(payload, (ServerPlayer) context.player()));
        registrar.playToServer(SetFlightC2SPayload.TYPE, SetFlightC2SPayload.STREAM_CODEC,
                (payload, context) -> SetFlightC2SPayload.handle(payload, context.player()));
        registrar.playToServer(LandingC2SPayload.TYPE, LandingC2SPayload.STREAM_CODEC,
                (payload, context) -> LandingC2SPayload.handle((ServerPlayer) context.player(), payload));
        registrar.playToServer(VoiceMutedC2SPayload.TYPE, VoiceMutedC2SPayload.STREAM_CODEC,
                (payload, context) -> VoiceMutedC2SPayload.handle(payload, context.player()));
        registrar.playToClient(LandingS2CPayload.TYPE, LandingS2CPayload.STREAM_CODEC);
        registrar.playToClient(OathS2CPayload.TYPE, OathS2CPayload.STREAM_CODEC);
        registrar.playToClient(LanternRitualS2CPayload.TYPE, LanternRitualS2CPayload.STREAM_CODEC);
    }
}
