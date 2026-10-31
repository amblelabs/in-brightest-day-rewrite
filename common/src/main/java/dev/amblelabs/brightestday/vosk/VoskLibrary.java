package dev.amblelabs.brightestday.vosk;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import dev.amblelabs.lib.jna.JnaClass;
import dev.amblelabs.lib.jna.JnaHelper;

import java.util.Map;

@JnaClass(mapper = JnaClass.Mapper.SNAKE_CASE)
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
        Map<String, ?> options = JnaHelper.makeOptions(VoskLibrary.class);

        if (Platform.isWindows()) {
            Class.forName("org.vosk.LibVosk", true, VoskLibrary.class.getClassLoader());
            return Native.load("libvosk", VoskLibrary.class, options);
        }
        return Native.load("vosk", VoskLibrary.class, options);
    }
}
