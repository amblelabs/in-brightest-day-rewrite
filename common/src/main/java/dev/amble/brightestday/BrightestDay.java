package dev.amble.brightestday;

import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.BrightestDayBlockEntities;
import dev.amble.brightestday.core.BrightestDayBlocks;
import dev.amble.brightestday.core.BrightestDayCreativeTabs;
import dev.amble.brightestday.core.BrightestDayItems;
import dev.amble.brightestday.core.blocks.LanternRitual;
import dev.amble.brightestday.core.comms.Comms;
import dev.amble.brightestday.core.oath.OathCharge;
import dev.amble.brightestday.core.oath.VocalAnswer;
import dev.amble.brightestday.core.ring.RingPowerRegistry;
import dev.amble.brightestday.core.ring.RingPowerTicker;
import dev.amble.brightestday.core.ring.impl.FlightRingPower;
import dev.amble.brightestday.platform.Services;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class BrightestDay {

    public static final String MOD_ID = "brightestday";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String s) {
        return Identifier.fromNamespaceAndPath(MOD_ID, s);
    }

    public static void init() {
        BrightestDayAttachments.init();
        BrightestDayBlocks.register();
        BrightestDayBlockEntities.register();
        BrightestDayItems.register();
        BrightestDayCreativeTabs.register();
        RingPowerRegistry.init();
        Comms.init();
        OathCharge.init();
        VocalAnswer.init();
        LanternRitual.init();
        Services.EVENTS.onServerTick(RingPowerTicker::tick);
    }
}
