package dev.amblelabs.brightestday.core.lanterns;

import com.mojang.serialization.Codec;
import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.core.ring.RingPowerCategory;
import dev.amblelabs.brightestday.core.util.Translatable;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.function.IntFunction;

public enum LanternCorps implements StringRepresentable, Translatable {
    GREEN("green", "will", 0x00E03C, true, true),
    BLUE("blue", "hope", 0x2D8CFF, true, true),
    YELLOW("yellow", "fear", 0xFFE01A, true, true),
    ORANGE("orange", "avarice", 0xFF8A00, true, true),
    RED("red", "rage", 0xE0141E, true, true),
    INDIGO("indigo", "compassion", 0x5B2DB3, true, true),
    STAR_SAPPHIRE("star_sapphire", "love", 0xE040C8, true, true),
    WHITE("white", "life", 0xF4F4F4, true, false),
    BLACK("black", "death", 0x2A2A2A, false, false);

    public static final Codec<LanternCorps> CODEC = StringRepresentable.fromEnum(LanternCorps::values);

    private static final IntFunction<LanternCorps> BY_ID =
            ByIdMap.continuous(LanternCorps::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<ByteBuf, LanternCorps> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, LanternCorps::ordinal);

    private final String name;
    private final String emotion;
    private final int color;
    private final boolean constructs;
    private final boolean ringPower;

    LanternCorps(String name, String emotion, int color, boolean constructs, boolean ringPower) {
        this.name = name;
        this.emotion = emotion;
        this.color = color;
        this.constructs = constructs;
        this.ringPower = ringPower;
    }

    public boolean hasRingPower() {
        return this.ringPower;
    }

    public boolean hasLantern() {
        return this.ringPower;
    }

    public boolean canUse(RingPowerCategory category) {
        return category != RingPowerCategory.CONSTRUCT || this.constructs;
    }

    public Component displayName() {
        return Component.translatable(this.getTranslationKey());
    }

    public String oathKey() {
        return BrightestDay.MOD_ID + ".oath." + this.name;
    }

    public String emotion() {
        return this.emotion;
    }

    public int color() {
        return this.color;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public String getTranslationKey() {
        return BrightestDay.MOD_ID + ".lantern_corps." + this.name;
    }
}
