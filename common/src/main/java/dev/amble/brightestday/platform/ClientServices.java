package dev.amble.brightestday.platform;

import dev.amble.brightestday.platform.services.ClientEventHelper;

public class ClientServices {

    public static final ClientEventHelper EVENTS = Services.load(ClientEventHelper.class);
}
