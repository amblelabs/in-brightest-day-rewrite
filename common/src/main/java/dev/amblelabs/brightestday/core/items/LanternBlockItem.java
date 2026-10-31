package dev.amblelabs.brightestday.core.items;

import dev.amblelabs.brightestday.core.BrightestDayAttachments;
import dev.amblelabs.brightestday.core.BrightestDayComponents;
import dev.amblelabs.brightestday.core.blocks.BrightestDayLanternBlock;
import dev.amblelabs.brightestday.core.blocks.LanternCharging;
import dev.amblelabs.brightestday.core.lanterns.LanternCorps;
import dev.amblelabs.brightestday.core.oath.OathCharge;
import dev.amblelabs.brightestday.core.ring.impl.FlightRingPower;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.WeakHashMap;

public class LanternBlockItem extends BlockItem {
    public static final int CHARGE_TICKS = 90;
    public static final int INSERT_TICKS = 30;
    private static final int OATH_USE_TICKS = 20 * 60 * 5;
    private static final double MAX_DRIFT = 0.2;
    private static final int PARTICLE_INTERVAL = 2;
    private static final int SOUND_INTERVAL = 10;

    private static final Map<LivingEntity, Vec3> ANCHORS = new WeakHashMap<>();

    public LanternBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public LanternCorps corps() {
        return ((BrightestDayLanternBlock) this.getBlock()).corps();
    }

    public static boolean isChargingLantern(Player player) {
        return player.getOffhandItem().getItem() instanceof LanternBlockItem lantern
                && PowerRingItem.getWornCorps(player).orElse(null) == lantern.corps();
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && context.getHand() == InteractionHand.OFF_HAND && isChargingLantern(player)) {
            return this.startCharging(context.getLevel(), player, context.getHand());
        }
        return super.useOn(context);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND && isChargingLantern(player)) {
            return this.startCharging(level, player, hand);
        }
        return super.use(level, player, hand);
    }

    private InteractionResult startCharging(Level level, Player player, InteractionHand hand) {
        ItemStack ring = PowerRingItem.getWornRing(player);
        if (PowerRingItem.getRingPower(ring) >= BrightestDayComponents.MAX_POWER) return InteractionResult.PASS;

        if (!isSteady(player)) {
            if (!level.isClientSide()) player.sendOverlayMessage(Component.translatable("message.brightestday.stand_still_to_charge"));
            return InteractionResult.FAIL;
        }

        ANCHORS.put(player, player.position());
        if (player instanceof ServerPlayer server) OathCharge.begin(server, this.corps(), false);
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    private static boolean isSteady(Player player) {
        return player.onGround() || FlightRingPower.isFlying(player);
    }

    private static boolean isStill(Player player) {
        if (FlightRingPower.isFlying(player)) {
            ANCHORS.put(player, player.position());
            return true;
        }
        Vec3 anchor = ANCHORS.get(player);
        if (anchor == null) return false;
        Vec3 position = player.position();
        return Mth.lengthSquared(position.x - anchor.x, position.z - anchor.z) <= MAX_DRIFT * MAX_DRIFT;
    }

    private static boolean reciting(LivingEntity entity) {
        return entity instanceof ServerPlayer player && OathCharge.active(player);
    }

    private static void cancel(Player player) {
        ANCHORS.remove(player);
        if (player instanceof ServerPlayer server) OathCharge.end(server);
        player.stopUsingItem();
    }

    @Override
    public void onUseTick(@NonNull Level level, @NonNull LivingEntity entity, @NonNull ItemStack stack, int ticksRemaining) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide() && !player.isLocalPlayer()) return;

        if (!isSteady(player) || !isStill(player) || !isChargingLantern(player)) {
            if (!level.isClientSide()) player.sendOverlayMessage(Component.translatable("message.brightestday.stand_still_to_charge"));
            cancel(player);
            return;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer server)) return;

        boolean oath = reciting(server);
        if (oath && !OathCharge.tick(server, this.corps().color())) {
            server.sendOverlayMessage(Component.translatable("message.brightestday.oath.silent").withColor(this.corps().color()));
            cancel(player);
            return;
        }

        int charge = OathCharge.timed(server, CHARGE_TICKS);
        int elapsed = (oath ? OATH_USE_TICKS : charge) - ticksRemaining - INSERT_TICKS;
        if (elapsed < 0) return;
        if (elapsed == 0) LanternCharging.inserted(serverLevel, player.blockPosition());

        float progress;
        if (oath) {
            progress = OathCharge.progress(server);
        } else {
            progress = (float) elapsed / (charge - INSERT_TICKS);
            ItemStack ring = PowerRingItem.getWornRing(player);
            PowerRingItem.chargeRing(ring, Mth.ceil((float) BrightestDayComponents.MAX_POWER / (charge - INSERT_TICKS)));
            if (ring == BrightestDayAttachments.getRing(player)) BrightestDayAttachments.setRing(player, ring);
        }

        if (elapsed % PARTICLE_INTERVAL == 0) {
            float angle = elapsed * 0.6F;
            double radius = 0.9 - progress * 0.5;
            DustParticleOptions dust = new DustParticleOptions(this.corps().color(), 1.2F);
            for (int strand = 0; strand < 2; strand++) {
                float a = angle + strand * Mth.PI;
                serverLevel.sendParticles(dust,
                        player.getX() + Mth.cos(a) * radius, player.getY() + progress * 2.0, player.getZ() + Mth.sin(a) * radius,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        if (elapsed > 0 && elapsed % SOUND_INTERVAL == 0) LanternCharging.chime(serverLevel, player.blockPosition(), progress);

        if (oath && OathCharge.complete(server)) {
            this.complete(serverLevel, player, false);
            cancel(player);
        }
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, Level level, @NonNull LivingEntity entity) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            ANCHORS.remove(entity);
            return stack;
        }
        this.complete(level, player, !reciting(player));
        ANCHORS.remove(player);
        if (player instanceof ServerPlayer server) OathCharge.end(server);
        return stack;
    }

    private void complete(Level level, Player player, boolean announce) {
        ItemStack ring = PowerRingItem.getWornRing(player);
        PowerRingItem.setMaxPower(ring);
        ring.remove(BrightestDayComponents.RING_DEATHS);
        if (ring == BrightestDayAttachments.getRing(player)) BrightestDayAttachments.setRing(player, ring);

        if (announce) {
            player.sendSystemMessage(Component.translatable(this.corps().oathKey())
                    .withStyle(ChatFormatting.BOLD)
                    .withColor(this.corps().color()));
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity, int remainingTime) {
        ANCHORS.remove(entity);
        if (entity instanceof ServerPlayer server) OathCharge.end(server);
        return false;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
        return reciting(user) ? OATH_USE_TICKS : OathCharge.timed(user, CHARGE_TICKS);
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.NONE;
    }
}
