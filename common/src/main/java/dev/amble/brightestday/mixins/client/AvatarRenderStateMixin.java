package dev.amble.brightestday.mixins.client;

import dev.amble.brightestday.client.render.renderstates.RingRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements RingRenderState {
    @Unique
    private @Nullable ItemStackRenderState brightestday$ring;

    @Override
    public @Nullable ItemStackRenderState brightestday$getRing() {
        return this.brightestday$ring;
    }

    @Override
    public void brightestday$setRing(@Nullable ItemStackRenderState ring) {
        this.brightestday$ring = ring;
    }
}
