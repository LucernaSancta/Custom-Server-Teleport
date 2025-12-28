// https://docs.papermc.io/velocity/dev/command-api/

package org.lucerna.customServerTeleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

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
                } else {
                    Player player = (Player) source;
                    Optional<RegisteredServer> target_server = proxy.getServer(servername);


                    // Check if available
                    if (target_server.isEmpty()) {
                        msg.send(player, MessageManager.SOURCE_AND_CONSOLE_ERROR, "<red>Server {} is not available!</red>", servername);
                        return Command.SINGLE_SUCCESS;
                    }

                    // Check if player is already on the lobby server
                    if (player.getCurrentServer().isPresent() &&
                            player.getCurrentServer().get().getServerInfo().getName().equals(servername)) {
                        msg.send(player, MessageManager.SOURCE_ONLY, "<gold>You are already connected to this server!</gold>");
                        return Command.SINGLE_SUCCESS;
                    }


                    // Connect player to lobby server
                    target_server.get().ping().thenAccept(ping -> {
                        if (ping != null) {
                            player.createConnectionRequest(target_server.get()).fireAndForget();

                            // Send message (send_message in config)
                            msg.send(player, MessageManager.SOURCE_ONLY,
                                    send_message.replace("%servername%", servername).replace("%playername%", player.getUsername()));

                        } else {
                            msg.send(player, MessageManager.SOURCE_AND_CONSOLE_WARN, "<yellow>Server {} is offline!</yellow>", servername);
                        }
                    }).exceptionally(throwable -> {
                        msg.send(player, MessageManager.SOURCE_AND_CONSOLE_WARN, "<yellow>Server {} is offline!</yellow>", servername);
                        return null;
                    });
                }
                // The execution was successful
                return Command.SINGLE_SUCCESS;
            })
            // Build :)
            .build();

        // Return BrigadierCommand "egg"
        return new BrigadierCommand(rootNode);
    }
}