package dev.amble.brightestday.client;

import dev.amble.brightestday.client.compat.IrisCompat;
import dev.amble.brightestday.client.render.EyeFlareRenderTypes;
import dev.amble.brightestday.client.render.BrightestDayRenderTypes;

public class BrightestDayClient {

    public static void init() {
        BrightestDayRenderTypes.init();
        EyeFlareRenderTypes.init();
        IrisCompat.init();
    }
}
