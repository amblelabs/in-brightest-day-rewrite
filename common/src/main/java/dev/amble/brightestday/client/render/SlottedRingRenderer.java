package dev.amble.brightestday.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.amble.brightestday.client.render.renderstates.RingRenderState;
import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.items.PowerRingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class SlottedRingRenderer {

    public static void extract(Avatar entity, AvatarRenderState state) {
        RingRenderState data = (RingRenderState) state;
        ItemStack ring = entity instanceof Player player ? BrightestDayAttachments.getRing(player) : ItemStack.EMPTY;
        boolean holdingRing = entity.getMainHandItem().getItem() instanceof PowerRingItem
                || entity.getOffhandItem().getItem() instanceof PowerRingItem;

        if (ring.isEmpty() || holdingRing || state.isInvisible) {
            data.brightestday$setRing(null);
            return;
        }

        ItemStackRenderState ringState = data.brightestday$getRing();
        if (ringState == null) ringState = new ItemStackRenderState();

        ItemDisplayContext context = state.mainArm == HumanoidArm.RIGHT
                ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        Minecraft.getInstance().getItemModelResolver().updateForLiving(ringState, ring, context, entity);
        data.brightestday$setRing(ringState);
    }

    public static void submit(PlayerModel model, AvatarRenderState state, HumanoidArm arm, PoseStack poseStack,
                              SubmitNodeCollector submitNodeCollector, int lightCoords, int outlineColor) {
        ItemStackRenderState ring = ((RingRenderState) state).brightestday$getRing();
        if (ring == null || ring.isEmpty() || arm != state.mainArm) return;

        poseStack.pushPose();
        model.translateToHand(state, arm, poseStack);
        poseStack.rotateDegrees(Axis.XP, -90.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F);
        poseStack.translate((arm == HumanoidArm.LEFT ? -1.0F : 1.0F) / 16.0F, 2.0F / 16.0F, -10.0F / 16.0F);
        ring.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, outlineColor);
        poseStack.popPose();
    }

    private SlottedRingRenderer() {}
}
