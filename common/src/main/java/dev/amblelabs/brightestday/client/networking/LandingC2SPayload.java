package dev.amblelabs.brightestday.client.networking;

import dev.amblelabs.brightestday.BrightestDay;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.amblelabs.brightestday.core.flight.Landings;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

public record LandingC2SPayload(int pose, float strength) implements CustomPacketPayload {
    public static final Type<LandingC2SPayload> TYPE = new Type<>(BrightestDay.id("landing"));

    public static final StreamCodec<ByteBuf, LandingC2SPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LandingC2SPayload::pose,
            ByteBufCodecs.FLOAT, LandingC2SPayload::strength,
            LandingC2SPayload::new
    );

    public static void handle(ServerPlayer player, LandingC2SPayload payload) {
        Landings.relay(player, payload.pose, payload.strength);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
