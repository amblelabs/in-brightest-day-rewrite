package dev.amblelabs.brightestday.mixins.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.amblelabs.brightestday.client.effects.LanternChargeAnimations;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.item.ItemStackRenderState$LayerRenderState")
public abstract class ItemStackLayerRenderStateMixin {

    @WrapWithCondition(method = "applyTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/cuboid/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack$Pose;)V"))
    private boolean brightestday$bareChargeLantern(ItemTransform transform, boolean leftHand, PoseStack.Pose pose) {
        return !LanternChargeAnimations.bareTransform();
    }
}
