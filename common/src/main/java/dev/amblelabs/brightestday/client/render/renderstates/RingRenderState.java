package dev.amblelabs.brightestday.client.render.renderstates;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jspecify.annotations.Nullable;

public interface RingRenderState {
    @Nullable ItemStackRenderState brightestday$getRing();

    void brightestday$setRing(@Nullable ItemStackRenderState ring);
}
