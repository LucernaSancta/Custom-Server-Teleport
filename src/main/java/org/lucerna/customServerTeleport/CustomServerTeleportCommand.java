package org.lucerna.customServerTeleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;


public class CustomServerTeleportCommand {
    private static final String COMMAND_NAME = "customserverteleport";
    private final Logger logger;
    private final CustomServerTeleport customServerTeleport;

    public CustomServerTeleportCommand(Logger logger, CustomServerTeleport customServerTeleport) {
        this.logger = logger;
        this.customServerTeleport = customServerTeleport;
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> rootNode = BrigadierCommand.literalArgumentBuilder(COMMAND_NAME)
                .requires(source -> source.hasPermission(COMMAND_NAME + ".use"))
                .executes(this::executeHelp)
                .then(BrigadierCommand.literalArgumentBuilder("help").executes(this::executeHelp))
                .then(BrigadierCommand.literalArgumentBuilder("reload").executes(this::executeReload))
                .build();

        return new BrigadierCommand(rootNode);
    }

    public CommandMeta getCommandMeta(CommandManager commandManager, CustomServerTeleport pluginContainer) {
        return commandManager.metaBuilder(COMMAND_NAME)
                .aliases("cst")
                .plugin(pluginContainer)
                .build();
    }

    private int executeHelp(CommandContext<CommandSource> context) {
        Component message = Component.text("/" + COMMAND_NAME + " <help|reload>");
        context.getSource().sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

    private int executeReload(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        customServerTeleport.reloadCustomCommands();

        // Only if the source is a player (a.k. not the console) send the log to the console
        if (source instanceof Player) {
            Component message = Component.text("Configuration reloaded correctly", NamedTextColor.GREEN);
            source.sendMessage(message);
        }

        logger.info("Configuration reloaded correctly");

    return Command.SINGLE_SUCCESS;
    }
}
