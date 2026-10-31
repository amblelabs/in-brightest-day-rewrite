package dev.amble.brightestday.client;

import dev.amble.brightestday.client.flight.FlightAnimator;
import dev.amble.brightestday.client.networking.LandingS2CPayload;
import dev.amble.brightestday.client.render.FrostedGlass;
import dev.amble.brightestday.client.render.LanternBlockEntityRenderer;
import dev.amble.brightestday.client.render.ShaderOverlay;
import dev.amble.brightestday.client.render.SlottedRingLayer;
import dev.amble.brightestday.client.util.FabricSuitRenderCallback;
import dev.amble.brightestday.core.BrightestDayBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

public class BrightestDayClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BrightestDayClient.init();

        ClientPlayNetworking.registerGlobalReceiver(LandingS2CPayload.TYPE, (payload, context) -> FlightAnimator.handleLandingRelay(payload));
        ClientTickEvents.END_CLIENT_TICK.register(BrightestDayClient::tick);

        LevelRenderEvents.START_MAIN.register(context -> ShaderOverlay.startMain());
        LevelRenderEvents.END_MAIN.register(context -> FrostedGlass.endMain());
        FabricSuitRenderCallback.init();

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, entityRenderer, helper, context) -> {
            if (entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
                helper.register(new SlottedRingLayer(avatarRenderer));
            }
        });

        BlockEntityRendererRegistry.register(BrightestDayBlockEntities.LANTERN.value(), LanternBlockEntityRenderer::new);
    }
}
