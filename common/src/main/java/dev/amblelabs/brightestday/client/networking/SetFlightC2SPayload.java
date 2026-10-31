package dev.amblelabs.brightestday.client.networking;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.ring.impl.FlightRingPower;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public record SetFlightC2SPayload(boolean enabled) implements CustomPacketPayload {

    public static final Type<SetFlightC2SPayload> TYPE =
            new Type<>(BrightestDay.id("set_flight"));

    public static final StreamCodec<ByteBuf, SetFlightC2SPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, SetFlightC2SPayload::enabled,
                    SetFlightC2SPayload::new
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetFlightC2SPayload payload, Player player) {
        FlightRingPower.setEnabled(player, payload.enabled);
    }
}