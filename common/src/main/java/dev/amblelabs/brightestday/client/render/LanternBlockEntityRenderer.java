package dev.amblelabs.brightestday.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.amblelabs.brightestday.client.render.renderstates.LanternBlockEntityRenderState;
import dev.amblelabs.brightestday.core.blockentities.LanternBlockEntity;
import dev.amblelabs.brightestday.core.blocks.BrightestDayLanternBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class LanternBlockEntityRenderer
        implements BlockEntityRenderer<LanternBlockEntity, LanternBlockEntityRenderState> {

    private final BlockModelResolver blockModelResolver;

    public LanternBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public @NonNull LanternBlockEntityRenderState createRenderState() {
        return new LanternBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull LanternBlockEntity blockEntity, @NonNull LanternBlockEntityRenderState state,
                                   float partialTick, @NonNull Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, breakProgress);

        state.yRot = RotationSegment.convertToDegrees(blockEntity.getBlockState().getValue(BrightestDayLanternBlock.ROTATION));

        this.blockModelResolver.update(state.model, blockEntity.getBlockState(), BlockDisplayContext.create());
    }

    @Override
    public void submit(LanternBlockEntityRenderState state, PoseStack poseStack,
                       @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.rotateDegrees(Axis.YP, -state.yRot);
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        state.model.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}