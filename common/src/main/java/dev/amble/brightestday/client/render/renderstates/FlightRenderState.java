package dev.amble.brightestday.client.render.renderstates;

import net.minecraft.world.phys.Vec3;

public interface FlightRenderState {
    Vec3 brightestday$getDiveOffset();

    void brightestday$setDiveOffset(Vec3 offset);
}
