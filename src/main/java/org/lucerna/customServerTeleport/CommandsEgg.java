// https://docs.papermc.io/velocity/dev/command-api/

package org.lucerna.customServerTeleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;


public class CommandsEgg {

    public static BrigadierCommand createBrigadierCommand(
        final ProxyServer proxy,
        final MessageManager msg,
        final String command,
        final String permission,
        final String servername,
        final String send_message,
        final String enter_message
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

                    // Send initial message (send_message in config)
                    msg.send(player, MessageManager.SOURCE_ONLY,
                            send_message.replace("%servername%", servername).replace("%playername%", player.getUsername()));

                    // Get the server object by name
                    proxy.getServer(servername).ifPresent(targetServer -> {

                        // Attempt to connect the player to the target server
                        player.createConnectionRequest(targetServer).connect().thenAccept(success -> {

                            // Check if NOT successful
                            if (!success.isSuccessful()) {
                                msg.send(player, MessageManager.SOURCE_AND_CONSOLE_ERROR, "<red>Failed to connect to server {}</red>", servername);

                            } else {
                                // Send final message (enter_message in config)
                                msg.send(player, MessageManager.SOURCE_ONLY,
                                        enter_message.replace("%servername%", servername).replace("%playername%", player.getUsername()));
                            }

                        // TODO: Custom messages from config
                        }).exceptionally(throwable -> {
                            msg.send(source, MessageManager.SOURCE_ONLY, "<red>Error occurred while trying to send you to the server</red>");
                            return null;
                        });
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