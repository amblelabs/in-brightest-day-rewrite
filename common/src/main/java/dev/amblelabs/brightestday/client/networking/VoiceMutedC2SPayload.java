package dev.amblelabs.brightestday.client.networking;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.comms.Comms;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public record VoiceMutedC2SPayload(boolean muted) implements CustomPacketPayload {

    public static final Type<VoiceMutedC2SPayload> TYPE =
            new Type<>(BrightestDay.id("voice_muted"));

    public static final StreamCodec<ByteBuf, VoiceMutedC2SPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, VoiceMutedC2SPayload::muted,
                    VoiceMutedC2SPayload::new
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(VoiceMutedC2SPayload payload, Player player) {
        Comms.setMuted(player.getUUID(), payload.muted);
    }
}
