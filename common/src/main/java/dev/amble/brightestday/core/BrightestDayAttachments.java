package dev.amble.brightestday.core;

import com.mojang.serialization.Codec;
import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.platform.Services;
import dev.amble.brightestday.platform.attachment.Attachment;
import dev.amble.brightestday.platform.attachment.AttachmentBuilder;
import dev.amble.brightestday.platform.attachment.AttachmentRegistrar;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

// TODO implement all this nonsense
public class BrightestDayAttachments {
    private static final AttachmentRegistrar REGISTRAR = Services.PLATFORM.createAttachmentRegistrar();

    /*public static final Attachment<List<RingPowerInstance<?>>> POWERS = REGISTRAR.register("powers",
            AttachmentBuilder.<List<RingPowerInstance<?>>>builder(List::of)
                    .persistent(RingPowerInstance.CODEC.listOf())
                    .copyOnDeath()
                    .sync(RingPowerInstance.LIST_STREAM_CODEC));*/

    public static final Attachment<ItemStack> RING = REGISTRAR.register("ring",
            AttachmentBuilder.builder(() -> ItemStack.EMPTY)
                    .persistent(ItemStack.OPTIONAL_CODEC)
                    .copyOnDeath()
                    .sync(ItemStack.OPTIONAL_STREAM_CODEC));

    public static final Attachment<Identifier> SUIT = REGISTRAR.register("suit",
            AttachmentBuilder.builder(() -> BrightestDay.id("lanterns_suit"))
                    .persistent(Identifier.CODEC)
                    .copyOnDeath()
                    .sync(Identifier.STREAM_CODEC));

    /*public static final Attachment<ColorTweak> COLOR_TWEAK = REGISTRAR.register("color_tweak",
            AttachmentBuilder.builder(() -> ColorTweak.NONE)
                    .persistent(ColorTweak.CODEC)
                    .copyOnDeath()
                    .sync(ColorTweak.STREAM_CODEC));

    public static final Attachment<EyePaint> EYES = REGISTRAR.register("eyes",
            AttachmentBuilder.builder(() -> EyePaint.EMPTY)
                    .persistent(EyePaint.CODEC)
                    .copyOnDeath()
                    .sync(EyePaint.STREAM_CODEC));

    public static final Attachment<InsigniaAnchor> INSIGNIA_ANCHOR = REGISTRAR.register("insignia_anchor",
            AttachmentBuilder.builder(() -> InsigniaAnchor.DEFAULT)
                    .persistent(InsigniaAnchor.CODEC)
                    .copyOnDeath()
                    .sync(InsigniaAnchor.STREAM_CODEC));

    public static InsigniaAnchor getInsigniaAnchor(Player player) {
        return INSIGNIA_ANCHOR.get(player);
    }

    public static void setInsigniaAnchor(Player player, InsigniaAnchor anchor) {
        INSIGNIA_ANCHOR.set(player, anchor.sanitized());
    }

    public static EyePaint getEyes(Player player) {
        return EYES.get(player);
    }

    public static void setEyes(Player player, EyePaint eyes) {
        EYES.set(player, eyes.sanitized());
    }

    public static ColorTweak getColorTweak(Player player) {
        return COLOR_TWEAK.get(player);
    }

    public static void setColorTweak(Player player, ColorTweak tweak) {
        COLOR_TWEAK.set(player, tweak.clamped());
    }*/

    public static ItemStack getRing(Player player) {
        return RING.get(player);
    }

    public static void setRing(Player player, ItemStack ring) {
        RING.set(player, ring.isEmpty() ? ItemStack.EMPTY : ring.copy());
    }

    /*public static List<RingPowerInstance<?>> get(Player player) {
        return POWERS.get(player);
    }

    @SuppressWarnings("unchecked")
    public static <D> Optional<RingPowerInstance<D>> get(Player player, RingPower<D> power) {
        for (RingPowerInstance<?> instance : get(player)) {
            if (instance.is(power)) return Optional.of((RingPowerInstance<D>) instance);
        }
        return Optional.empty();
    }

    public static List<RingPowerInstance<?>> constructs(Player player) {
        return get(player).stream()
                .filter(instance -> instance.power().category() == RingPowerCategory.CONSTRUCT)
                .toList();
    }

    public static boolean has(Player player, RingPower<?> power) {
        return get(player, power).isPresent();
    }

    public static <D> void setData(Player player, RingPower<D> power, D data) {
        List<RingPowerInstance<?>> current = get(player);
        List<RingPowerInstance<?>> updated = new ArrayList<>(current.size());
        boolean found = false;
        for (RingPowerInstance<?> instance : current) {
            if (instance.is(power)) {
                updated.add(new RingPowerInstance<>(power, data));
                found = true;
            } else {
                updated.add(instance);
            }
        }
        if (found) POWERS.set(player, List.copyOf(updated));
    }

    public static void sync(ServerPlayer player, @Nullable LanternCorps corps) {
        List<RingPowerInstance<?>> current = get(player);
        List<RingPower<?>> available = corps == null
                ? List.of()
                : RingPowerRegistry.forCorps(corps, CorpsSynergy.borrowsWill(player) ? LanternCorps.GREEN : null, CorpsMimicry.mimicked(player, corps));
        ItemStack wornRing = PowerRingItem.getWornRing(player);
        boolean constructRing = ConstructRings.isConstruct(wornRing);
        if (corps == LanternCorps.WHITE && wornRing.has(BrightestDayComponents.FUSED_WHITE)) available = WhiteFusion.arsenal();
        if (corps == LanternCorps.RED && RedBlood.flowing(player)) {
            List<RingPower<?>> lent = new ArrayList<>(available);
            for (RingPower<?> power : RedBlood.borrowed()) if (!lent.contains(power)) lent.add(power);
            available = List.copyOf(lent);
        }
        if (corps != null && CentralPowerBattery.required(corps) && !CentralPowerBattery.active(player.level().getServer(), corps) && !CentralPowerBattery.severed(player.level().getServer(), corps)) {
            available = List.of();
            if (player.tickCount % 200 == 0) player.sendOverlayMessage(Component.translatable("message.brightestday.battery.dormant").withColor(corps.color()));
        }
        if (corps != null) {
            Set<RingPower<?>> locked = constructRing ? RingRanks.locked(player, corps, 1) : RingRanks.locked(player, corps);
            if (constructRing) available = available.stream().filter(power -> power != RingPowerRegistry.CONSTRUCT_RING && power != RingPowerRegistry.RING_COMPASS).toList();
            if (!locked.isEmpty()) available = available.stream().filter(power -> !locked.contains(power)).toList();
            if (!IndigoOne.isIndigoOne(player)) available = available.stream().filter(power -> power != RingPowerRegistry.GATHER).toList();
            if (!Comms.available()) available = available.stream().filter(power -> power != RingPowerRegistry.COMMS).toList();
            if (PowerRingItem.isDormant(PowerRingItem.getWornRing(player))) available = available.stream().filter(power -> power == RingPowerRegistry.ARMED).toList();
            if (RingSync.locked(player)) available = available.stream().filter(power -> power == RingPowerRegistry.ARMED || power == RingPowerRegistry.COMMS || power == RingPowerRegistry.RING_COMPASS).toList();
        }

        if (current.size() == available.size()) {
            boolean unchanged = true;
            for (int i = 0; i < current.size() && unchanged; i++) {
                unchanged = current.get(i).is(available.get(i));
            }
            if (unchanged) return;
        }

        List<RingPowerInstance<?>> updated = new ArrayList<>(available.size());
        List<RingPowerInstance<?>> granted = new ArrayList<>();
        for (RingPower<?> power : available) {
            RingPowerInstance<?> instance = get(player, power).orElse(null);
            if (instance == null) {
                instance = power.createInstance();
                granted.add(instance);
            }
            updated.add(instance);
        }

        POWERS.set(player, List.copyOf(updated));

        for (RingPowerInstance<?> instance : current) {
            if (!updated.contains(instance)) revoked(player, instance);
        }
        for (RingPowerInstance<?> instance : granted) {
            granted(player, instance);
        }
    }

    private static <D> void granted(ServerPlayer player, RingPowerInstance<D> instance) {
        instance.power().onGranted(player, instance.data());
    }

    private static <D> void revoked(ServerPlayer player, RingPowerInstance<D> instance) {
        instance.power().onRevoked(player, instance.data());
    }*/

    public static void init() {
        REGISTRAR.registerAll();
    }
}
