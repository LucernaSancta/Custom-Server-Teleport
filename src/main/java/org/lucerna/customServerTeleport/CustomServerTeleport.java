package org.lucerna.customServerTeleport;

import com.google.inject.Inject;

import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.BrigadierCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.nio.file.Path;
import java.util.List;
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
    private final ComponentLogger logger;
    private final ProxyServer proxy;
    private final ConfigurationLoader configurationloader;

    private Map<String, Object> config;

    @Inject
    public CustomServerTeleport(ProxyServer server, ComponentLogger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.configurationloader = new ConfigurationLoader(logger, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        logger.info("Initializing plugin");

        CommandManager commandManager = proxy.getCommandManager();

        // Initialize the config file and get server list
        config = configurationloader.getConfiguration();
        List<Map<String, Map<String, Object>>> serverList = (List<Map<String, Map<String, Object>>>) config.get("servers");

        logger.info(serverList.toString());
        // For every server in the server list
        for (Map<String, Map<String, Object>> server : serverList) {

            // Get first (end only) entry of the set
            Map.Entry<String, Map<String, Object>> entry = server.entrySet().iterator().next();
            String serverName = entry.getKey();
            Map<String, Object> details = entry.getValue();

            // Extract commands and permission
            List<String> commands = (List<String>) details.get("commands");
            String permission = (String) details.get("permission");

            // Create command meta with aliases (if any)
            CommandMeta commandMeta = commandManager.metaBuilder(commands.get(0))
                    .aliases(commands.subList(1, commands.size()).toArray(new String[0]))
                    .plugin(this)
                    .build();

            // Create the Brigadier command
            BrigadierCommand commandToRegister = CommandsEgg.createBrigadierCommand(proxy, commands.get(0), permission, serverName);

            // Finally, register the command
            commandManager.register(commandMeta, commandToRegister);
        }

        this.sendInfoMessage();
    }

    public void sendInfoMessage() {
        // Init MiniMessage
        MiniMessage mm = MiniMessage.miniMessage();
        // Plugin name and version
        logger.info(mm.deserialize(
                "<gray><gradient:#3400e0:#cf28de>Custom server Teleport</gradient> <version>",
                Placeholder.component("version", Component.text(BuildConstants.VERSION))
        ));
    }
}
