package dev.amble.brightestday.client.compat;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.client.render.BrightestDayRenderTypes;
import dev.amble.brightestday.client.render.FrostedGlass;
import dev.amble.brightestday.platform.Services;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.irisshaders.iris.vertices.ImmediateState;

import java.util.List;

public final class IrisCompat {

    private static final String IRIS = "iris";
    private static boolean loaded;

    public static void init() {
        if (!Services.PLATFORM.isModLoaded(IRIS)) return;

        try {
            IrisApi api = IrisApi.getInstance();
            for (List<RenderPipeline> pipelines : List.of(BrightestDayRenderTypes.pipelines(), FrostedGlass.pipelines())) {
                for (RenderPipeline pipeline : pipelines) api.assignPipeline(pipeline, IrisProgram.BASIC);
            }
            loaded = true;
        } catch (Throwable exception) {
            BrightestDay.LOGGER.warn("Pipelines didn't register; constructs won't render.", exception);
        }
    }

    public static boolean shadersActive() {
        return loaded && IrisApi.getInstance().isShaderPackInUse();
    }

    public static boolean bypass(boolean value) {
        if (!loaded) return false;
        boolean previous = ImmediateState.bypass;
        ImmediateState.bypass = value;
        return previous;
    }

    private IrisCompat() {}
}
