package dev.amblelabs.brightestday;

import dev.amblelabs.brightestday.client.networking.LandingC2SPayload;
import dev.amblelabs.brightestday.client.networking.LandingS2CPayload;
import dev.amblelabs.brightestday.client.networking.LanternRitualS2CPayload;
import dev.amblelabs.brightestday.client.networking.OathS2CPayload;
import dev.amblelabs.brightestday.client.networking.SetFlightC2SPayload;
import dev.amblelabs.brightestday.client.networking.UpdateSuitPayload;
import dev.amblelabs.brightestday.client.networking.VoiceMutedC2SPayload;
import dev.amblelabs.brightestday.core.BrightestDayCreativeTabs;
import dev.amblelabs.brightestday.core.BrightestDayRegistries;
import dev.amblelabs.brightestday.core.items.PowerRingItem;
import dev.amblelabs.brightestday.core.suits.Suit;
import dev.amblelabs.brightestday.platform.Services;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class BrightestDayFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BrightestDay.init();
        registerDynamicRegistries();
        registerNetworking();
        CreativeModeTabEvents.modifyOutputEvent(BrightestDayCreativeTabs.MAIN.unwrapKey().orElseThrow())
                .register(entries -> BrightestDayCreativeTabs.fillTab(BrightestDayCreativeTabs.MAIN.unwrapKey().orElseThrow(), entries::accept));

        Services.DISCOVERY.addDiscoverer(player -> TrinketsApi.getAttachment(player)
                .equipped(stack -> stack.getItem() instanceof PowerRingItem, false)
                .stream().map(TrinketSlotAccess::get).toList());
    }

    private void registerDynamicRegistries() {
        DynamicRegistries.registerSynced(BrightestDayRegistries.SUIT, Suit.CODEC);
    }

    private void registerNetworking() {
        PayloadTypeRegistry.serverboundPlay().register(UpdateSuitPayload.TYPE, UpdateSuitPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetFlightC2SPayload.TYPE, SetFlightC2SPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(LandingC2SPayload.TYPE, LandingC2SPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VoiceMutedC2SPayload.TYPE, VoiceMutedC2SPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(LandingS2CPayload.TYPE, LandingS2CPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OathS2CPayload.TYPE, OathS2CPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(LanternRitualS2CPayload.TYPE, LanternRitualS2CPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(UpdateSuitPayload.TYPE,
                (payload, ctx) -> UpdateSuitPayload.handle(payload, ctx.player()));

        ServerPlayNetworking.registerGlobalReceiver(SetFlightC2SPayload.TYPE,
                (payload, ctx) -> SetFlightC2SPayload.handle(payload, ctx.player()));

        ServerPlayNetworking.registerGlobalReceiver(LandingC2SPayload.TYPE,
                (payload, ctx) -> LandingC2SPayload.handle(ctx.player(), payload));

        ServerPlayNetworking.registerGlobalReceiver(VoiceMutedC2SPayload.TYPE,
                (payload, ctx) -> VoiceMutedC2SPayload.handle(payload, ctx.player()));
    }
}
