package dev.amble.brightestday;

import dev.amble.brightestday.core.ring.RingPowerTicker;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(BrightestDay.MOD_ID)
public class BrightestDayNeoForge {

    public static IEventBus EVENT_BUS;

    public BrightestDayNeoForge(IEventBus eventBus) {
        EVENT_BUS = eventBus;

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        BrightestDay.init();
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> RingPowerTicker.tick(event.getServer()));
    }
}
