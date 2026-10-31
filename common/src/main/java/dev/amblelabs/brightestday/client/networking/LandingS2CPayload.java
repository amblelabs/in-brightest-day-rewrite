package dev.amblelabs.brightestday.client.networking;

import dev.amblelabs.brightestday.BrightestDay;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record LandingS2CPayload(int playerId, int pose, float strength) implements CustomPacketPayload {
    public static final Type<LandingS2CPayload> TYPE = new Type<>(BrightestDay.id("landing_relay"));

    public static final StreamCodec<ByteBuf, LandingS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LandingS2CPayload::playerId,
            ByteBufCodecs.VAR_INT, LandingS2CPayload::pose,
            ByteBufCodecs.FLOAT, LandingS2CPayload::strength,
            LandingS2CPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
