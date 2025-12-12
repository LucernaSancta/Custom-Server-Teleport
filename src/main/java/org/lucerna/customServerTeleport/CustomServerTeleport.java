package org.lucerna.customServerTeleport;

import com.velocitypowered.api.plugin.annotation.DataDirectory;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;

@Plugin(
        id = "custom_server_teleport",
        name = "Custom Server Teleport",
        version = BuildConstants.VERSION,
        description = "A simple Velocity plugin to set custom commands and permission to switch server",
        url = "github.com/LucernaSancta/Custom-Server-Teleport",
        authors = {"LucernaSancta"}
)
public class CustomServerTeleport {
    private final Logger logger;
    private final ProxyServer proxy;
    private final ConfigurationLoader configurationloader;

    private Map<String, Object> config;

    @Inject
    public CustomServerTeleport(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.configurationloader = new ConfigurationLoader(logger, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Initialize the config file when the server starts
        config = configurationloader.getConfiguration();

        logger.info(config.toString());

        // Log loaded config values to confirm it's working
        logger.info("Loaded servers: " + config.get("servers"));

        logger.info("Plugin initialized successfully.");
    }

}
