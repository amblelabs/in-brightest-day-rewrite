package dev.amble.brightestday.vosk;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

import java.util.Map;

public interface VoskLibrary extends Library {
    int LOG_WARNINGS = -1;

    void voskSetLogLevel(int level);

    Pointer voskModelNew(String path);

    Pointer voskRecognizerNewGrm(Pointer model, float sampleRate, String grammar);

    boolean voskAcceptWaveform(Pointer recognizer, short[] data, int length);

    String voskResult(Pointer recognizer);

    String voskPartialResult(Pointer recognizer);

    void voskFree(Pointer recognizer);

    // TODO if this is dumb just nuke it i just dont like the c binding camelcase stuff in java
    static VoskLibrary load() throws ClassNotFoundException {
        Map<String, String> symbols = Map.of(
                "voskSetLogLevel", "vosk_set_log_level",
                "voskModelNew", "vosk_model_new",
                "voskRecognizerNewGrm", "vosk_recognizer_new_grm",
                "voskAcceptWaveform", "vosk_recognizer_accept_waveform_s",
                "voskResult", "vosk_recognizer_result",
                "voskPartialResult", "vosk_recognizer_partial_result",
                "voskFree", "vosk_recognizer_free");
        FunctionMapper mapper = (library, method) -> symbols.getOrDefault(method.getName(), method.getName());
        Map<String, Object> options = Map.of(Library.OPTION_FUNCTION_MAPPER, mapper);

        if (Platform.isWindows()) {
            Class.forName("org.vosk.LibVosk", true, VoskLibrary.class.getClassLoader());
            return Native.load("libvosk", VoskLibrary.class, options);
        }
        return Native.load("vosk", VoskLibrary.class, options);
    }
}
