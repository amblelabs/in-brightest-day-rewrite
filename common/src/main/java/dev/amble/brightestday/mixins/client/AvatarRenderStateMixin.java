package dev.amble.brightestday.mixins.client;

import dev.amble.brightestday.client.render.renderstates.FlightRenderState;
import dev.amble.brightestday.client.render.renderstates.RingRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements RingRenderState, FlightRenderState {
    @Unique
    private @Nullable ItemStackRenderState brightestday$ring;

    @Unique
    private Vec3 brightestday$diveOffset = Vec3.ZERO;

    @Override
    public @Nullable ItemStackRenderState brightestday$getRing() {
        return this.brightestday$ring;
    }

    @Override
    public void brightestday$setRing(@Nullable ItemStackRenderState ring) {
        this.brightestday$ring = ring;
    }

    @Override
    public Vec3 brightestday$getDiveOffset() {
        return this.brightestday$diveOffset;
    }

    @Override
    public void brightestday$setDiveOffset(Vec3 offset) {
        this.brightestday$diveOffset = offset;
    }
}
