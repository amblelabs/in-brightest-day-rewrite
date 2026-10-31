package dev.amble.brightestday.core.ring;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.lanterns.LanternCorps;
import dev.amble.brightestday.core.util.Identifiable;
import dev.amble.brightestday.core.util.Translatable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.Set;

public abstract class RingPower<D> implements Identifiable, Translatable {

    public static final Codec<RingPower<?>> CODEC = Identifier.CODEC.comapFlatMap(
            id -> RingPowerRegistry.get(id)
                    .<DataResult<RingPower<?>>>map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown ring power: " + id)),
            RingPower::id
    );

    private final Identifier id;
    private final Set<LanternCorps> corps;
    private final MapCodec<RingPowerInstance<D>> instanceCodec;

    protected RingPower(Identifier id, Set<LanternCorps> corps, Codec<D> dataCodec) {
        this.id = id;
        this.corps = Set.copyOf(corps);
        this.instanceCodec = dataCodec.lenientOptionalFieldOf("data").xmap(
                data -> new RingPowerInstance<>(this, data.orElseGet(this::createData)),
                instance -> Optional.of(instance.data())
        );
    }

    public Set<LanternCorps> corps() {
        return this.corps;
    }

    public RingPowerCategory category() {
        return RingPowerCategory.UTILITY;
    }

    public boolean worksWithoutCharge() {
        return false;
    }

    public boolean isAvailableTo(LanternCorps corps) {
        return this.corps.contains(corps) && corps.canUse(this.category());
    }

    MapCodec<RingPowerInstance<D>> instanceCodec() {
        return this.instanceCodec;
    }

    public abstract D createData();

    public RingPowerInstance<D> createInstance() {
        return new RingPowerInstance<>(this, this.createData());
    }

    public boolean run(ServerPlayer player, D data) {
        return false;
    }

    public void tick(ServerPlayer player, D data) {}

    @Environment(EnvType.CLIENT)
    public void tick(AbstractClientPlayer player, D data) {}

    public int cost() {
        return 0;
    }

    public int drainPerSecond(ServerPlayer player, D data) {
        return 0;
    }

    public void onDepleted(ServerPlayer player, D data) {}

    public void onGranted(ServerPlayer player, D data) {}

    public void onRevoked(ServerPlayer player, D data) {}

    protected void setData(Player player, D data) {
        BrightestDayAttachments.setData(player, this, data);
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.id().getNamespace() + ".ring_power." + this.id().getPath();
    }
}
