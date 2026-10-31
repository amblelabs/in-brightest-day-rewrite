package dev.amblelabs.brightestday.client;

import dev.amblelabs.brightestday.client.effects.LanternChargeAnimations;
import dev.amblelabs.brightestday.client.flight.FlightAnimator;
import dev.amblelabs.brightestday.client.hud.OathHud;
import dev.amblelabs.brightestday.client.networking.LandingS2CPayload;
import dev.amblelabs.brightestday.client.networking.LanternRitualS2CPayload;
import dev.amblelabs.brightestday.client.networking.OathS2CPayload;
import dev.amblelabs.brightestday.client.render.FrostedGlass;
import dev.amblelabs.brightestday.client.render.LanternBlockEntityRenderer;
import dev.amblelabs.brightestday.client.render.ShaderOverlay;
import dev.amblelabs.brightestday.client.render.SlottedRingLayer;
import dev.amblelabs.brightestday.core.BrightestDayBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

public class BrightestDayClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BrightestDayClient.init();

        ClientPlayNetworking.registerGlobalReceiver(LandingS2CPayload.TYPE, (payload, _) -> FlightAnimator.handleLandingRelay(payload));
        ClientPlayNetworking.registerGlobalReceiver(OathS2CPayload.TYPE, (payload, _) -> OathHud.handle(payload));
        ClientPlayNetworking.registerGlobalReceiver(LanternRitualS2CPayload.TYPE, (payload, _) -> LanternChargeAnimations.handleRitual(payload));

        LevelRenderEvents.START_MAIN.register(_ -> ShaderOverlay.startMain());
        LevelRenderEvents.END_MAIN.register(_ -> FrostedGlass.endMain());

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((_, entityRenderer, helper, _) -> {
            if (entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
                helper.register(new SlottedRingLayer(avatarRenderer));
            }
        });

        BlockEntityRenderers.register(BrightestDayBlockEntities.LANTERN.value(), LanternBlockEntityRenderer::new);
    }
}
