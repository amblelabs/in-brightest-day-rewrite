package dev.amblelabs.brightestday.core.suits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record Suit(Identifier id) {
    public static final Codec<Suit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(Suit::id)
    ).apply(instance, Suit::new));

    public static Identifier getCorrectedPath(Identifier id) {
        return id.withPrefix("textures/suits/");
    }
}
