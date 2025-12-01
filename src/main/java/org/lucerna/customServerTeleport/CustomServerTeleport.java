package org.lucerna.customServerTeleport;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "custom_server_teleport",
        name = "Custom Server Teleport",
        version = BuildConstants.VERSION,
        description = "A simple Velocity plugin to set custom commands and permission to switch server",
        url = "github.com/LucernaSancta/Custom-Server-Teleport",
        authors = {"LucernaSancta"}
)
public class CustomServerTeleport {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("FUCK PLEASE WORK FUCK PLEASE");
    }
}