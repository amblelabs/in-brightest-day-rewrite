package dev.amblelabs.brightestday.client;

import dev.amblelabs.brightestday.client.compat.IrisCompat;
import dev.amblelabs.brightestday.client.effects.LanternChargeAnimations;
import dev.amblelabs.brightestday.client.effects.VoiceMuteReporter;
import dev.amblelabs.brightestday.client.flight.FlightAnimations;
import dev.amblelabs.brightestday.client.flight.FlightAnimator;
import dev.amblelabs.brightestday.client.flight.LaunchAnimations;
import dev.amblelabs.brightestday.client.hud.OathHud;
import dev.amblelabs.brightestday.client.render.EyeFlareRenderTypes;
import dev.amblelabs.brightestday.client.render.BrightestDayRenderTypes;
import dev.amblelabs.brightestday.platform.ClientServices;
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
