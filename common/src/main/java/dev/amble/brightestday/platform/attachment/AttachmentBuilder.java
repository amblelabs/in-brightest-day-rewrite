package dev.amble.brightestday.platform.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public final class AttachmentBuilder<T> {

    private final Supplier<T> initializer;
    private @Nullable Codec<T> codec;
    private boolean copyOnDeath;
    private @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> syncCodec;

    private AttachmentBuilder(Supplier<T> initializer) {
        this.initializer = Objects.requireNonNull(initializer);
    }

    public static <T> AttachmentBuilder<T> builder(Supplier<T> initializer) {
        return new AttachmentBuilder<>(initializer);
    }

    public AttachmentBuilder<T> persistent(Codec<T> codec) {
        this.codec = Objects.requireNonNull(codec);
        return this;
    }

    public AttachmentBuilder<T> copyOnDeath() {
        this.copyOnDeath = true;
        return this;
    }

    public AttachmentBuilder<T> sync(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        this.syncCodec = Objects.requireNonNull(streamCodec);
        return this;
    }

    public Supplier<T> initializer() {
        return initializer;
    }

    public @Nullable Codec<T> codec() {
        return codec;
    }

    public boolean shouldCopyOnDeath() {
        return copyOnDeath;
    }

    public @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> syncCodec() {
        return syncCodec;
    }

    void validate() {
        if (copyOnDeath && codec == null)
            throw new IllegalStateException("copyOnDeath needs a persistent codec");
    }
}
