package org.lucerna.customServerTeleport;

import com.velocitypowered.api.command.CommandSource;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;


public class MessageManager {
    private final ComponentLogger logger;

    public final MiniMessage mm;

    public static final int SOURCE_ONLY = 1;
    public static final int SOURCE_AND_CONSOLE_INFO = 2;
    public static final int SOURCE_AND_CONSOLE_WARN = 3;
    public static final int SOURCE_AND_CONSOLE_ERROR = 4;


    public MessageManager(ComponentLogger logger) {
        this.logger = logger;
        this.mm = MiniMessage.miniMessage();
    }

    public String format(String template, String... args) {
        // Format string such that the "{}" are replaced with the arguments

        String result = template;

        for (Object arg : args) {
            result = result.replaceFirst("\\{}",
                    arg == null ? "null" : arg.toString());
        }

        return result;
    }

    public Component deserialize(String message) {
        return mm.deserialize(message);
    }

    public void send(CommandSource source, int option, String message, String... args) {
        // If is NOT blank
        if (!message.isBlank()) {

            // Format message (replace "{}")
            for (Object arg : args) {
                message = message.replaceFirst("\\{}",
                        arg == null ? "null" : arg.toString());
            }
            // Deserialize minimessage
            Component component = deserialize(message);

            // Only if the source is a player OR if the source IS the console but
            // the message WILL NOT be logged with the logger (a.k.a. SOURCE_ONLY)
            if ((source instanceof Player) || (option == SOURCE_ONLY)) {
                source.sendMessage(component);
            }

            // Send to console, somehow
            switch (option) {
                case SOURCE_ONLY:
                    break;
                case SOURCE_AND_CONSOLE_INFO:
                    logger.info(component);
                    break;
                case SOURCE_AND_CONSOLE_WARN:
                    logger.warn(component);
                    break;
                case SOURCE_AND_CONSOLE_ERROR:
                    logger.error(component);
                    break;
            }
        }
    }
}
