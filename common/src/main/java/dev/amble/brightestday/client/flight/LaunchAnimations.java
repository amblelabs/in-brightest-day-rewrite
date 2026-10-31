package dev.amble.brightestday.client.flight;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

// TODO swap these for GroundLaunch's once launching is ported
public final class LaunchAnimations {
    private static final int CHARGING_STATE = 1;
    private static final int LAUNCHED_STATE = 2;
    private static final int FULL_CHARGE = 40;
    private static final int OFF_TICKS = 5;

    private static final Map<Player, Integer> STATES = new WeakHashMap<>();
    private static final Map<Player, Integer> RELEASING = new WeakHashMap<>();
    private static final Map<Player, Integer> CHARGING = new WeakHashMap<>();

    public static boolean active(Player player) {
        return STATES.containsKey(player);
    }

    public static void apply(Player player, int state) {
        PlayerAnimationController controller = FlightAnimations.controller(player);
        if (controller == null) return;
        Integer previous = STATES.put(player, state);
        switch (state) {
            case CHARGING_STATE -> {
                RELEASING.remove(player);
                CHARGING.put(player, 0);
                if (previous == null || previous != CHARGING_STATE) FlightAnimations.launchCharge(controller);
            }
            case LAUNCHED_STATE -> {
                CHARGING.remove(player);
                FlightAnimations.launchOff(controller);
                RELEASING.put(player, OFF_TICKS);
            }
            default -> {
                RELEASING.remove(player);
                CHARGING.remove(player);
                STATES.remove(player);
                if (previous != null && !FlightAnimator.phaseActive(player)) FlightAnimations.rise(controller);
            }
        }
    }

    public static void tick(Minecraft client) {
        if (client.isPaused()) return;
        CHARGING.replaceAll((player, ticks) -> {
            if (player == client.player || player.isRemoved()) return ticks;
            int next = Math.min(ticks + 1, FULL_CHARGE);
            // GroundLaunchClient.chargeEffects(client, player, next);
            return next;
        });
        CHARGING.keySet().removeIf(Player::isRemoved);
        Iterator<Map.Entry<Player, Integer>> iterator = RELEASING.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining > 0) {
                entry.setValue(remaining);
                continue;
            }
            iterator.remove();
            Player player = entry.getKey();
            STATES.remove(player);
            PlayerAnimationController controller = FlightAnimations.controller(player);
            if (controller != null && !FlightAnimator.phaseActive(player)) FlightAnimations.rise(controller);
        }
    }

    private LaunchAnimations() {}
}
