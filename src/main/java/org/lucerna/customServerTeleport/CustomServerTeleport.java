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
        config = configurationloader.getConfiguration();

        logger.info("Initializing plugin");

        CommandManager commandManager = proxy.getCommandManager();
        // Here you can add meta for the command, as aliases and the plugin to which it belongs (RECOMMENDED)
        CommandMeta commandMeta = commandManager.metaBuilder("test")
                // This will create a new alias for the command "/test"
                // with the same arguments and functionality
                .aliases("otherAlias", "anotherAlias")
                .plugin(this)
                .build();

        // You can replace this with "new EchoCommand()" or "new TestCommand()"
        // SimpleCommand simpleCommand = new TestCommand();
        // RawCommand rawCommand = new EchoCommand();
        // The registration is done in the same way, since all 3 interfaces implement "Command"
        BrigadierCommand commandToRegister = CommandsEgg.createBrigadierCommand(proxy, "test", "comm.test", "servername");

        // Finally, you can register the command
        commandManager.register(commandMeta, commandToRegister);

        this.sendInfoMessage();
    }

    public void sendInfoMessage() {
        MiniMessage mm = MiniMessage.miniMessage();
        logger.info(mm.deserialize("<gray><gradient:#3400e0:#cf28de>Custom server Teleport</gradient>"));
        logger.info(mm.deserialize("<gray>by</gray> Lucerna Sancta"));
    }
}
