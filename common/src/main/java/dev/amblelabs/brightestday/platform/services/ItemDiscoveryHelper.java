package dev.amblelabs.brightestday.platform.services;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// TODO: make the stuff a bit more concrete and not just blasting a list of random item stacks maybe
public class ItemDiscoveryHelper {

    private final List<Function<Player, List<ItemStack>>> ITEM_DISCOVERY = new ArrayList<>();

    public List<ItemStack> collectItems(Player player) {
        List<ItemStack> stacks = new ArrayList<>();
        for (var discoverer : ITEM_DISCOVERY) {
            stacks.addAll(discoverer.apply(player));
        }
        return stacks;
    }

    public void addDiscoverer(Function<Player, List<ItemStack>> discoverer) {
        ITEM_DISCOVERY.add(discoverer);
    }
}
