package dev.amble.brightestday.core.ring.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.items.PowerRingItem;
import dev.amble.brightestday.core.lanterns.LanternCorps;
import dev.amble.brightestday.core.ring.RingPower;
import dev.amble.brightestday.core.ring.RingPowerCategory;
import dev.amble.brightestday.core.ring.RingPowerRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

// TODO implement launching, boosting and dodging in future
public class FlightRingPower extends RingPower<FlightRingPower.Data> {

    // public static BooleanSupplier launching = () -> false;

    public static final int DEFAULT_SPEED_LEVEL = 5;
    private static final double[] SPEED_STEPS = {0.2, 0.35, 0.5, 0.65, 0.9, 1.2, 1.6, 2.2, 3.0, 4.0};

    public FlightRingPower() {
        super(BrightestDay.id("flight"), EnumSet.allOf(LanternCorps.class), Data.CODEC);
    }

    public record Data(boolean enabled, int speedLevel) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("enabled", false).forGetter(Data::enabled),
                Codec.INT.optionalFieldOf("speed_level", DEFAULT_SPEED_LEVEL).forGetter(Data::speedLevel)
        ).apply(instance, Data::new));
    }

    @Override
    public Data createData() {
        return new Data(false, DEFAULT_SPEED_LEVEL);
    }

    @Override
    public RingPowerCategory category() {
        return RingPowerCategory.MOVEMENT;
    }

    @Override
    public boolean run(ServerPlayer player, Data data) {
        setEnabled(player, !data.enabled());
        return true;
    }

    @Override
    public void tick(ServerPlayer player, Data data) {
        if (isFlying(player)) player.resetFallDistance();
    }

    @Override
    public int drainPerSecond(ServerPlayer player, Data data) {
        if (!isFlying(player)) return 0;
        double scale = 1.0 - 0.25 + 0.25 * speedForLevel(data.speedLevel()) / 1.2;
        double drain = 0.5 * scale;
        int whole = Mth.floor(drain);
        return whole + (player.getRandom().nextDouble() < drain - whole ? 1 : 0);
    }

    @Override
    public void onDepleted(ServerPlayer player, Data data) {
        setEnabled(player, false);
    }

    public static boolean hasFlight(Player player) {
        return BrightestDayAttachments.has(player, RingPowerRegistry.FLIGHT_RING_POWER);
    }

    public static boolean canFly(Player player) {
        return BrightestDayAttachments.get(player, RingPowerRegistry.FLIGHT_RING_POWER)
                .map(instance -> instance.data().enabled())
                .orElse(false);
    }

    public static void setEnabled(Player player, boolean enabled) {
        if (!hasFlight(player)) return;
        if (enabled && !PowerRingItem.hasCharge(player)) return;

        BrightestDayAttachments.setData(player, RingPowerRegistry.FLIGHT_RING_POWER, new Data(enabled, speedLevel(player)));
        if (enabled && player.getAbilities().flying) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    public static int speedLevel(Player player) {
        return BrightestDayAttachments.get(player, RingPowerRegistry.FLIGHT_RING_POWER)
                .map(instance -> Mth.clamp(instance.data().speedLevel(), 0, SPEED_STEPS.length - 1))
                .orElse(DEFAULT_SPEED_LEVEL);
    }

    public static void setSpeedLevel(Player player, int level) {
        if (!hasFlight(player)) return;
        BrightestDayAttachments.setData(player, RingPowerRegistry.FLIGHT_RING_POWER, new Data(canFly(player), Mth.clamp(level, 0, SPEED_STEPS.length - 1)));
    }

    public static double speedForLevel(int level) {
        return SPEED_STEPS[Mth.clamp(level, 0, SPEED_STEPS.length - 1)];
    }

    public static double cruiseSpeed(Player player) {
        return speedForLevel(speedLevel(player));
    }

    public static boolean isFlying(Player player) {
        return canFly(player)
                && !player.onGround()
                && !player.isPassenger()
                && !player.isSpectator()
                && !player.isSleeping()
                && !player.isFallFlying()
                && !player.getAbilities().flying;
    }

    public static boolean isDiving(Player player, boolean wasDiving) {
        if (!isFlying(player)) return false;
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        Vec3 facing = new Vec3(-Mth.sin(yaw), 0.0, Mth.cos(yaw));
        Vec3 step = player.position().subtract(player.xo, player.yo, player.zo);
        double travel = Math.max(step.dot(facing), -step.y);
        return travel > (wasDiving ? 0.7 : 0.8);
    }

    public static boolean fitsStanding(Player player) {
        AABB box = EntityDimensions.scalable(0.6F, 1.8F).scale(player.getScale()).makeBoundingBox(player.position());
        return player.level().noCollision(player, box.deflate(1.0E-7));
    }

    public static void travel(Player player, Vec3 input, boolean ascending) {
        // boolean boosting = isBoosting(player);
        double cruise = cruiseSpeed(player);
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        Vec3 left = new Vec3(Mth.cos(yaw), 0.0, Mth.sin(yaw));
        // boolean launched = launching.getAsBoolean();
        double vertical = (ascending ? 1.0 : 0.0) - (player.isShiftKeyDown() /*&& !launched*/ ? 1.0 : 0.0);

        Vec3 wish = player.getLookAngle().scale(input.z)
                .add(left.scale(input.x))
                .add(0.0, vertical, 0.0);
        if (/*(boosting || launched) && */wish.lengthSqr() < 1.0E-4) wish = player.getLookAngle();

        Vec3 target;
        double response;
        if (wish.lengthSqr() < 1.0E-4) {
            target = Vec3.ZERO;
            response = 0.12;
        } else {
            target = wish.normalize().scale(/*(boosting ? boostSpeed(cruise) : */cruise)/* * (1.0F - OrangeHoard.slowdown(player)))*/;
            response = /*boosting ? BOOST_RESPONSE : */0.15;
        }

        Vec3 velocity = player.getDeltaMovement();
        velocity = velocity.add(target.subtract(velocity).scale(response));

        player.setDeltaMovement(velocity);
        player.move(MoverType.SELF, velocity);
        // dodge(player, left);
    }

    /*public static void startDodge(Player player, boolean right) {
        DODGES.put(player, new Dodge(right ? -1 : 1, player.tickCount, 0.0));
    }*/

    /*private static void dodge(Player player, Vec3 left) {
        Dodge dodge = DODGES.get(player);
        if (dodge == null) return;
        float progress = Math.min(1.0F, (player.tickCount - dodge.start()) / (float) ROLL_TICKS);
        double done = rollCurve(progress);
        double step = (done - dodge.done()) * BrightestDayConfig.get().aileronRollDistance;
        if (progress >= 1.0F) DODGES.remove(player);
        else DODGES.put(player, new Dodge(dodge.direction(), dodge.start(), done));
        if (step > 0.0) player.move(MoverType.SELF, left.scale(step * dodge.direction()));
    }*/

    /*public static float rollCurve(float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }*/
}
