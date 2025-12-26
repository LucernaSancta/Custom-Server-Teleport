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

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.nio.file.Path;
import java.util.*;


// TODO: Refactor ALL messages; make them custom and set proper logic for sending only to source (when source is console) or also to the console


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
    private final MessageManager msg;
    public final ConfigurationLoader configurationloader;

    private Map<String, Object> config;
    private List<String> command_list;

    @Inject
    public CustomServerTeleport(ProxyServer server, ComponentLogger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.msg = new MessageManager(this.logger);
        this.configurationloader = new ConfigurationLoader(logger, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        logger.info("Initializing plugin");


        // Initialize the config file and get server list
        config = configurationloader.getConfiguration();

        this.command_list = new ArrayList<String>();

        this.initializeCommand();
        this.registerCustomCommands();
        this.sendInfoMessage();
    }

    private void registerCustomCommands() {
        CommandManager commandManager = proxy.getCommandManager();

        List<Map<String, Object>> serverList = (List<Map<String, Object>>) config.get("servers");

        // For every server in the server list
        command_registration_loop:
        for (Map<String, Object> server : serverList) {

            // Get first (end only) entry of the set
            Map.Entry<String, Object> entry = server.entrySet().iterator().next();
            String serverName = entry.getKey();
            Map<String, Object> details = (Map<String, Object>) entry.getValue();

            // Extract commands and permission
            List<String> commands = (List<String>) details.get("commands");

            String main_command = commands.get(0);
            String[] aliases = commands.subList(1, commands.size()).toArray(new String[0]);
            String permission = (String) details.get("permission");

            // Check if commands are already registered
            for (String command : commands) {
                if (commandManager.hasCommand(command)) {
                    logger.error("Command '{}' is already used somewhere else", command);
                    continue command_registration_loop;
                }
            }

            // Add command to the command list so that it can be unregistered on reload
            command_list.addAll(commands);

            // Create command meta with aliases (if any)
            CommandMeta commandMeta = commandManager.metaBuilder(main_command)
                    .aliases(aliases)
                    .plugin(this)
                    .build();

            // Create the Brigadier command
            BrigadierCommand commandToRegister = CommandsEgg.createBrigadierCommand(proxy, commands.get(0), permission, serverName);

            // Finally, register the command
            commandManager.register(commandMeta, commandToRegister);
        }
    }

    private void unRegisterCustomCommands() {
        logger.warn("Unregistering commands...");

        CommandManager commandManager = proxy.getCommandManager();

        // Unregister all commands
        for (String command : command_list) {
            commandManager.unregister(command);
        }

        // Remove all item from the command list (since no command is registered)
        command_list.clear();
    }

    public void reloadCustomCommands() {

        this.unRegisterCustomCommands();

        // Reload the config and get the new one
        configurationloader.reload();
        config = configurationloader.getConfiguration();

        this.registerCustomCommands();
    }

    private void sendInfoMessage() {
        // Plugin name and version
        logger.info(
                msg.deserialize(msg.format(
                "<gray><gradient:#3400e0:#cf28de>Custom server Teleport</gradient> {}", BuildConstants.VERSION)));
    }

    private void initializeCommand() {
        CommandManager commandManager = proxy.getCommandManager();
        CustomServerTeleportCommand customserverteleportcommand = new CustomServerTeleportCommand(logger, this);
        BrigadierCommand commandToRegister = customserverteleportcommand.createBrigadierCommand();
        commandManager.register(customserverteleportcommand.getCommandMeta(commandManager, this), commandToRegister);
    }
}
