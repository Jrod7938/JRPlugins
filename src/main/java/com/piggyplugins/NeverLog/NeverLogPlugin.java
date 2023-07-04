package com.piggyplugins.NeverLog;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.time.Duration;

@PluginDescriptor(name = "NeverLog",
        tags = {"piggy","neverlog"}
)
public class NeverLogPlugin extends Plugin {
    @Inject
    private Client client;

    @Override
    protected void startUp() throws Exception {
        client.setIdleTimeout((int) Duration.ofMinutes(60*5).toMillis() / Constants.CLIENT_TICK_LENGTH);
    }
}
