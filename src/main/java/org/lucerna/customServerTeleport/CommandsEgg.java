// https://docs.papermc.io/velocity/dev/command-api/

package org.lucerna.customServerTeleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class CommandsEgg {

    public static BrigadierCommand createBrigadierCommand(
            final ProxyServer proxy,
            final String command,
            final String permission,
            final String servername) {
        LiteralCommandNode<CommandSource> customCommand = BrigadierCommand.literalArgumentBuilder(command)
                .requires(source -> source.hasPermission(permission))
                .executes(context -> {
                    // Here you get the subject that executed the command
                    CommandSource source = context.getSource();

                    Component message = Component.text("Hello World "+servername, NamedTextColor.AQUA);
                    source.sendMessage(message);

                    // Returning Command.SINGLE_SUCCESS means that the execution was successful
                    // Returning BrigadierCommand.FORWARD will send the command to the server
                    return Command.SINGLE_SUCCESS;
                })
                // Build :)
                .build();

        // BrigadierCommand implements Command
        return new BrigadierCommand(customCommand);
    }
}