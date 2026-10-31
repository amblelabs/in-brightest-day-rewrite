package dev.amblelabs.brightestday.core.comms;

import de.maxhenkel.voicechat.api.VoicechatClientApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.oath.OathCharge;
import dev.amblelabs.brightestday.core.oath.VocalAnswer;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommsVoicePlugin implements VoicechatPlugin {
    private static volatile @Nullable VoicechatClientApi clientVoice;
    private static final Map<UUID, OpusDecoder> DECODERS = new ConcurrentHashMap<>();

    @Override
    public String getPluginId() {
        return BrightestDay.MOD_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(ClientVoicechatConnectionEvent.class, event -> clientVoice = event.isConnected() ? event.getVoicechat() : null);
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
        registration.registerEvent(PlayerDisconnectedEvent.class, event -> forget(event.getPlayerUuid()));
        registration.registerEvent(VoicechatServerStoppedEvent.class, _ -> List.copyOf(DECODERS.keySet()).forEach(CommsVoicePlugin::forget));
    }

    public static boolean clientMuted() {
        VoicechatClientApi api = clientVoice;
        return api != null && (api.isMuted() || api.isDisabled());
    }

    private static void forget(UUID id) {
        OpusDecoder decoder = DECODERS.remove(id);
        if (decoder != null) decoder.close();
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        VoicechatServerApi api = event.getVoicechat();
        Comms.setVoiceReady(id -> {
            VoicechatConnection connection = api.getConnectionOf(id);
            return connection != null && connection.isConnected() && !connection.isDisabled();
        });
        BrightestDay.LOGGER.info("Oath listening registered with Simple Voice Chat");
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        VoicechatConnection sender = event.getSenderConnection();
        if (sender == null) return;
        UUID id = sender.getPlayer().getUuid();

        boolean oath = OathCharge.listening(id);
        boolean answering = VocalAnswer.listening(id);
        if (!oath && !answering) {
            forget(id);
            return;
        }

        OpusDecoder decoder = DECODERS.computeIfAbsent(id, _ -> event.getVoicechat().createDecoder());
        byte[] opus = event.getPacket().getOpusEncodedData();
        if (opus.length == 0) {
            decoder.resetState();
            return;
        }
        short[] samples = decoder.decode(opus);
        if (oath) OathCharge.hear(id, samples);
        if (answering) VocalAnswer.hear(id, samples);
    }
}
