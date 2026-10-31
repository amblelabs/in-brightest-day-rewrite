package dev.amblelabs.brightestday.client.render;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import dev.amblelabs.brightestday.BrightestDay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.List;

public final class BrightestDayRenderTypes {

    private static final RenderPipeline GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LIGHTNING_SNIPPET)
                    .withLocation(BrightestDay.id("pipeline/flight_glow"))
                    .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
                    .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                    .withCull(false)
                    .build()
    );

    private static final OitPipelineSet OIT_GLOW = RenderPipelines.register(
            OitPipelineSet.builder("brightestday_flight_glow", RenderPipeline.builder(RenderPipelines.LIGHTNING_SNIPPET).withShaderDefine("OIT_ADDITIVE").withCull(false))
                    .withDepthBoundsModifier(builder -> builder.withCull(false))
                    .withTransmittanceModifier(builder -> builder.withCull(false))
                    .withAccumulateModifier(builder -> builder.withCull(false))
                    .build()
    );

    public static final RenderType GLOW = RenderType.create(
            "brightestday_flight_glow",
            RenderSetup.builder(GLOW_PIPELINE).setOitPipelines(OIT_GLOW).sortOnUpload().createRenderSetup()
    );

    private static final RenderPipeline GLASS_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(BrightestDay.id("pipeline/construct_glass"))
                    .withCull(false)
                    .build()
    );

    private static final RenderType GLASS = RenderType.create(
            "brightestday_construct_glass",
            RenderSetup.builder(GLASS_PIPELINE).setOitPipelines(RenderPipelines.OIT_DEBUG_QUADS).sortOnUpload().createRenderSetup()
    );

    public static final RenderType GLOW_UNSORTED = RenderType.create(
            "brightestday_flight_glow_unsorted",
            RenderSetup.builder(GLOW_PIPELINE).setOitPipelines(OIT_GLOW).createRenderSetup()
    );

    private static final RenderType GLASS_UNSORTED = RenderType.create(
            "brightestday_construct_glass_unsorted",
            RenderSetup.builder(GLASS_PIPELINE).setOitPipelines(RenderPipelines.OIT_DEBUG_QUADS).createRenderSetup()
    );

    private static final RenderPipeline XRAY_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(BrightestDay.id("pipeline/xray"))
                    .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                    .withCull(false)
                    .build()
    );

    private static final OitPipelineSet OIT_XRAY = RenderPipelines.register(
            OitPipelineSet.builder("brightestday_xray", RenderPipeline.builder(RenderPipelines.OIT_DEBUG_FILLED_SNIPPET))
                    .withoutDepthTest()
                    .build()
    );

    public static final RenderType XRAY = RenderType.create(
            "brightestday_xray",
            RenderSetup.builder(XRAY_PIPELINE).setOitPipelines(OIT_XRAY).sortOnUpload().createRenderSetup()
    );

    public static RenderType glassUnsorted() {
        return GLASS_UNSORTED;
    }

    public static RenderType glass() {
        return GLASS;
    }

    public static List<RenderPipeline> pipelines() {
        return List.of(GLOW_PIPELINE, GLASS_PIPELINE, XRAY_PIPELINE);
    }

    public static void init() {}

    private BrightestDayRenderTypes() {}
}
