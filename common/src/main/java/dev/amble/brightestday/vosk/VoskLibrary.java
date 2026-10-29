package dev.amble.brightestday.vosk;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

public interface VoskLibrary extends Library {
    Pointer voskModelNew(String path);

    Pointer voskRecognizerNewGrm(Pointer model, float sampleRate, String grammar);

    boolean voskAcceptWaveform(Pointer recognizer, short[] data, int length);

    String voskResult(Pointer recognizer);

    String voskPartialResult(Pointer recognizer);

    void voskFree(Pointer recognizer);

    static VoskLibrary load() throws ClassNotFoundException {
        if (Platform.isWindows()) {
            Class.forName("org.vosk.LibVosk", true, VoskLibrary.class.getClassLoader());
            return Native.load("libvosk", VoskLibrary.class);
        }
        return Native.load("vosk", VoskLibrary.class);
    }
}
