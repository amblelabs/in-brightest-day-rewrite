package dev.amble.brightestday.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.amble.brightestday.client.flight.FlightAnimator;
import dev.amble.brightestday.client.render.SlottedRingRenderer;
import dev.amble.brightestday.client.render.SuitRender;
import dev.amble.brightestday.client.render.renderstates.FlightRenderState;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void brightestday$extractRenderStates(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        SlottedRingRenderer.extract(entity, state);
        SuitRender.extract(entity, state, partialTicks);
        FlightAnimator.extractDive(entity, state, partialTicks);
        FlightAnimator.extractLanding(entity, state, partialTicks);
    }

    @Inject(method = "extractCapeState", at = @At("TAIL"))
    private void brightestday$tameFlightCape(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        FlightAnimator.adjustCape(entity, state, partialTicks);
    }

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("TAIL"))
    private void brightestday$centerDivingModel(AvatarRenderState state, PoseStack poseStack, float bodyRot, float entityScale, CallbackInfo ci) {
        Vec3 offset = ((FlightRenderState) state).brightestday$getDiveOffset();
        if (offset.lengthSqr() > 1.0E-8) poseStack.translate(offset.x, offset.y, offset.z);
    }
}
