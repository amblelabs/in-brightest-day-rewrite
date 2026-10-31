package dev.amblelabs.brightestday.platform;

import dev.amblelabs.brightestday.platform.services.ClientEventHelper;

public class ClientServices {

    public static final ClientEventHelper EVENTS = Services.load(ClientEventHelper.class);
}
