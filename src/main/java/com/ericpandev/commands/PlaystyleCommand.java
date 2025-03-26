package com.ericpandev.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class PlaystyleCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("playstyle")
                    .then(CommandManager.argument("style", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("difficult");
                            builder.suggest("peaceful");
                            // Add more styles here
                            return builder.buildFuture();
                        })
                        .executes(context -> executePlaystyleCommand(context, StringArgumentType.getString(context, "style")))
                    )
            );
        });
    }

    private static int executePlaystyleCommand(CommandContext<ServerCommandSource> context, String style) {
        ServerCommandSource source = context.getSource();

        switch (style.toLowerCase()) {
            case "difficult":
                // Send feedback for 'difficult'
                source.sendFeedback(() -> Text.literal("Playstyle set to difficult!"), false);
                break;
            case "peaceful":
                // Send feedback for 'peaceful'
                source.sendFeedback(() -> Text.literal("Playstyle set to peaceful!"), false);
                break;
            default:
                // Send error message for an invalid playstyle
                source.sendError(Text.literal("Invalid playstyle!"));
                return Command.SINGLE_SUCCESS;
        }

        return Command.SINGLE_SUCCESS;
    }
}
