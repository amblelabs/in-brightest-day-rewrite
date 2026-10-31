package dev.amble.brightestday;

import dev.amble.brightestday.client.networking.SetFlightC2SPayload;
import dev.amble.brightestday.client.networking.UpdateSuitPayload;
import dev.amble.brightestday.core.BrightestDayCreativeTabs;
import dev.amble.brightestday.core.BrightestDayRegistries;
import dev.amble.brightestday.core.ring.RingPowerTicker;
import dev.amble.brightestday.core.suits.Suit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class BrightestDayFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BrightestDay.init();
        registerDynamicRegistries();
        registerNetworking();
        ServerTickEvents.END_SERVER_TICK.register(RingPowerTicker::tick);
        CreativeModeTabEvents.modifyOutputEvent(BrightestDayCreativeTabs.MAIN.unwrapKey().orElseThrow())
                .register(entries -> BrightestDayCreativeTabs.fillTab(BrightestDayCreativeTabs.MAIN.unwrapKey().orElseThrow(), entries::accept));
    }

    private void registerDynamicRegistries() {
        DynamicRegistries.registerSynced(BrightestDayRegistries.SUIT, Suit.CODEC);
    }

    private void registerNetworking() {
        PayloadTypeRegistry.serverboundPlay().register(UpdateSuitPayload.TYPE, UpdateSuitPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetFlightC2SPayload.TYPE, SetFlightC2SPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UpdateSuitPayload.TYPE,
                (payload, ctx) -> UpdateSuitPayload.handle(payload, ctx.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetFlightC2SPayload.TYPE,
                (payload, ctx) -> SetFlightC2SPayload.handle(payload, ctx.player()));
    }
}
