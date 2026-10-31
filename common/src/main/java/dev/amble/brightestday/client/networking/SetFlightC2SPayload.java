package dev.amble.brightestday.client.networking;

import de.maxhenkel.voicechat.api.ServerPlayer;
import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.core.ring.impl.FlightRingPower;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record SetFlightC2SPayload(boolean enabled) implements CustomPacketPayload {

    public static final Type<SetFlightC2SPayload> TYPE =
            new Type<>(BrightestDay.id("set_flight"));

    public static final StreamCodec<ByteBuf, SetFlightC2SPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, SetFlightC2SPayload::enabled,
                    SetFlightC2SPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetFlightC2SPayload payload, Player player) {
        FlightRingPower.setEnabled(player, payload.enabled);
    }
}