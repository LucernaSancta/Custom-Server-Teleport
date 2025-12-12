package org.lucerna.customServerTeleport;

import com.google.inject.Inject;

import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.BrigadierCommand;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

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

        // Initialize the config file when the server starts
        logger.info("Initializing plugin");

        CommandManager commandManager = proxy.getCommandManager();
        config = configurationloader.getConfiguration();
        List<Map<String, Map<String, Object>>> serverList = (List<Map<String, Map<String, Object>>>) config.get("servers");

        for (Map<String, Map<String, Object>> server : serverList) {
            for (Map.Entry<String, Map<String, Object>> entry : server.entrySet()) {
                String serverName = entry.getKey();
                Map<String, Object> details = entry.getValue();

                // Extract commands and permission
                List<String> commands = (List<String>) details.get("commands");
                String permission = (String) details.get("permission");

                // Here you can add meta for the command, as aliases and the plugin to which it belongs
                CommandMeta commandMeta = commandManager.metaBuilder(commands.get(0))

                        .aliases(commands.subList(1, commands.size()).toArray(new String[0]))
                        .plugin(this)
                        .build();

                BrigadierCommand commandToRegister = CommandsEgg.createBrigadierCommand(proxy, commands.get(0), permission, serverName);

                // Finally, you can register the command
                commandManager.register(commandMeta, commandToRegister);
            }
        }

        this.sendInfoMessage();
    }

    public void sendInfoMessage() {
        MiniMessage mm = MiniMessage.miniMessage();
        logger.info(mm.deserialize("<gray><gradient:#3400e0:#cf28de>Custom server Teleport</gradient>"));
        logger.info(mm.deserialize("<gray>by</gray> Lucerna Sancta"));
    }
}
