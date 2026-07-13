// https://docs.papermc.io/velocity/dev/command-api/

package org.lucerna.customServerTeleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;


public class CommandsEgg {

    public static BrigadierCommand createBrigadierCommand(
            final ProxyServer proxy,
            final MessageManager msg,
            final String command,
            final String permission,
            final String servername,
            final String send_message
    ) {

        LiteralCommandNode<CommandSource> rootNode = BrigadierCommand.literalArgumentBuilder(command)


            .requires(source -> source.hasPermission(permission))
            .executes(context -> {
                // Get the subject that executed the command
                CommandSource source = context.getSource();
                // If source IS NOT a player
                if (!(source instanceof Player)) {
                    msg.send(source, MessageManager.SOURCE_ONLY, "<red>This command can only be executed by a player!</red>");
                    return Command.SINGLE_SUCCESS;
                } else {
                    return checkAndSend((Player) source, (Player) source, proxy, msg, servername, send_message);
                }
            })


            // If an argument is provided
            .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                // Require the .others permission to perform this action
                .requires(source -> source.hasPermission(permission+".others"))
                // Suggest as argument the names of all connected players are provided
                .suggests((ctx, builder) -> {
                    for (Player player : proxy.getAllPlayers()) {
                        builder.suggest(player.getUsername());
                    }
                    return builder.buildFuture();
                })
                // Here the logic of the command with an argument
                .executes(context -> {
                    // Get the source
                    CommandSource source = context.getSource();
                    // Get the player in the argument
                    String argumentProvided = context.getArgument("player", String.class);
                    Optional<Player> player_sent = proxy.getPlayer(argumentProvided);
                    // Check if the player exists
                    if (!player_sent.isPresent()) {
                        msg.send(source, MessageManager.SOURCE_AND_CONSOLE_ERROR, "<red>The specified player {} does not exist.</red>", argumentProvided);
                        return Command.SINGLE_SUCCESS;
                    }

                    return checkAndSend(source, player_sent.get(), proxy, msg, servername, send_message);
                })
            )
            // Build :)
            .build();

        // Return BrigadierCommand "egg"
        return new BrigadierCommand(rootNode);
    }


    private static int checkAndSend(
            // This two sources method is fucking stupid and unnecessarily complicated
            CommandSource player_source,
            Player player_sent,
            ProxyServer proxy,
            MessageManager msg,
            String servername,
            String send_message
    ) {
        Optional<RegisteredServer> target_server = proxy.getServer(servername);


        // Check if available
        if (target_server.isEmpty()) {
            msg.send(player_source, MessageManager.SOURCE_AND_CONSOLE_ERROR, "<red>Server {} is not available!</red>", servername);
            return Command.SINGLE_SUCCESS;
        }

        // Check if player is already on the lobby server
        if (player_sent.getCurrentServer().isPresent() &&
                player_sent.getCurrentServer().get().getServerInfo().getName().equals(servername)) {
            msg.send(player_source, MessageManager.SOURCE_ONLY, "<gold>Already connected to this server!</gold>");
            return Command.SINGLE_SUCCESS;
        }


        // Connect player to target server
        target_server.get().ping().thenAccept(ping -> {
            if (ping != null) {
                player_sent.createConnectionRequest(target_server.get()).fireAndForget();

                // Send message (send_message in config)
                msg.send(player_source, MessageManager.SOURCE_ONLY,
                        send_message.replace("%servername%", servername).replace("%playername%", player_sent.getUsername()));

            } else {
                msg.send(player_source, MessageManager.SOURCE_AND_CONSOLE_WARN, "<yellow>Server {} is offline!</yellow>", servername);
            }
        }).exceptionally(throwable -> {
            msg.send(player_source, MessageManager.SOURCE_AND_CONSOLE_WARN, "<yellow>Server {} is offline!</yellow>", servername);
            return null;
        });

        // The execution was successful
        return Command.SINGLE_SUCCESS;
    }
}