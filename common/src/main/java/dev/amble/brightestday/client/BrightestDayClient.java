package dev.amble.brightestday.client;

import dev.amble.brightestday.client.compat.IrisCompat;
import dev.amble.brightestday.client.effects.LanternChargeAnimations;
import dev.amble.brightestday.client.effects.VoiceMuteReporter;
import dev.amble.brightestday.client.flight.FlightAnimations;
import dev.amble.brightestday.client.flight.FlightAnimator;
import dev.amble.brightestday.client.flight.LaunchAnimations;
import dev.amble.brightestday.client.hud.OathHud;
import dev.amble.brightestday.client.render.EyeFlareRenderTypes;
import dev.amble.brightestday.client.render.BrightestDayRenderTypes;
import dev.amble.brightestday.platform.ClientServices;
import net.minecraft.client.Minecraft;

public class BrightestDayClient {

    public static void init() {
        BrightestDayRenderTypes.init();
        EyeFlareRenderTypes.init();
        IrisCompat.init();
        FlightAnimations.init();
        LanternChargeAnimations.init();
        OathHud.init();
        VoiceMuteReporter.init();
        ClientServices.EVENTS.onClientTick(BrightestDayClient::tick);
    }

    public static void tick(Minecraft client) {
        FlightAnimator.tick(client);
        LaunchAnimations.tick(client);
    }
}
