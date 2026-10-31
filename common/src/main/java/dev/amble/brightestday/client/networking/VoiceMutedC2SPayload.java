package dev.amble.brightestday.client.networking;

import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.core.comms.Comms;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record VoiceMutedC2SPayload(boolean muted) implements CustomPacketPayload {

    public static final Type<VoiceMutedC2SPayload> TYPE =
            new Type<>(BrightestDay.id("voice_muted"));

    public static final StreamCodec<ByteBuf, VoiceMutedC2SPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, VoiceMutedC2SPayload::muted,
                    VoiceMutedC2SPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(VoiceMutedC2SPayload payload, Player player) {
        Comms.setMuted(player.getUUID(), payload.muted);
    }
}
