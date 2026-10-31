package dev.amblelabs.brightestday.client.effects;

import dev.amblelabs.brightestday.client.networking.VoiceMutedC2SPayload;
import dev.amblelabs.brightestday.core.comms.Comms;
import dev.amblelabs.brightestday.core.comms.CommsVoicePlugin;
import dev.amblelabs.brightestday.platform.ClientServices;
import dev.amblelabs.brightestday.platform.Services;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public final class VoiceMuteReporter {
    private static @Nullable Boolean reportedMuted;

    public static void init() {
        if (!Comms.available()) return;
        ClientServices.EVENTS.onClientTick(VoiceMuteReporter::tick);
        ClientServices.EVENTS.onClientDisconnect(() -> {
            reportedMuted = null;
            Comms.localMuted = false;
        });
    }

    private static void tick(Minecraft client) {
        if (client.player == null || !Services.PLATFORM.canSendToServer(VoiceMutedC2SPayload.TYPE)) return;
        boolean muted = CommsVoicePlugin.clientMuted();
        Comms.localMuted = muted;
        if (reportedMuted != null && reportedMuted == muted) return;
        reportedMuted = muted;
        Services.PLATFORM.sendToServer(new VoiceMutedC2SPayload(muted));
    }

    private VoiceMuteReporter() {}
}
