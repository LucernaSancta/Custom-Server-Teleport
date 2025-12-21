// https://docs.papermc.io/velocity/dev/command-api/

package org.lucerna.customServerTeleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class CommandsEgg {

    public static BrigadierCommand createBrigadierCommand(
            final ProxyServer proxy,
            final String command,
            final String permission,
            final String servername
            ) {

        LiteralCommandNode<CommandSource> customCommand = BrigadierCommand.literalArgumentBuilder(command)
                .requires(source -> source.hasPermission(permission))
                .executes(context -> {
                    // Get the subject that executed the command
                    CommandSource source = context.getSource();

                    // If source IS NOT a player
                    if (!(source instanceof Player)) {
                        Component message = Component.text("This command can only be executed by a player!", NamedTextColor.RED);
                        source.sendMessage(message);
                    } else {
                        // TODO: Custom message from config
                        Component message = Component.text("Sending to " + servername, NamedTextColor.AQUA);
                        source.sendMessage(message);

                        Player player = (Player) source;

                        // Get the server object by name
                        proxy.getServer(servername).ifPresent(targetServer -> {
                            // Attempt to connect the player to the target server
                            player.createConnectionRequest(targetServer).connect().thenAccept(success -> {
                                // Check if NOT successful 
                                if (!success.isSuccessful()) {
                                    player.sendMessage(Component.text("Failed to connect to the server", NamedTextColor.RED));
                                }
                            // TODO: Custom messages from config
                            }).exceptionally(throwable -> {
                                player.sendMessage(Component.text("Error occurred while trying to send you to the server", NamedTextColor.RED));
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
        return new BrigadierCommand(customCommand);
    }
}