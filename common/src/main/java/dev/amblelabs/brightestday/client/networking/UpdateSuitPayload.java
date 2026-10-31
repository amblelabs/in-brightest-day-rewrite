package dev.amblelabs.brightestday.client.networking;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.BrightestDayAttachments;
import dev.amblelabs.brightestday.core.suits.Suit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

public record UpdateSuitPayload(Suit suit) implements CustomPacketPayload {

    public static final Type<UpdateSuitPayload> TYPE = new Type<>(BrightestDay.id("update_suit_payload"));

    public static final StreamCodec<FriendlyByteBuf, UpdateSuitPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Suit.CODEC), UpdateSuitPayload::suit,
            UpdateSuitPayload::new
    );

    public static void handle(UpdateSuitPayload payload, ServerPlayer player) {
        if (player == null) return;
        Suit suit = payload.suit();
        BrightestDayAttachments.SUIT.set(player, suit.id());
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
