package com.ericpandev.commands;

import com.mojang.brigadier.Command;
// import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import com.ericpandev.playstyle.PlaystyleManager;

public class PlaystyleCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("playstyle")
                    .then(CommandManager.argument("style", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("difficult");
                            builder.suggest("peaceful");
                            return builder.buildFuture();
                        })
                        .executes(context -> executePlaystyleCommand(context, StringArgumentType.getString(context, "style")))
                    )
            );
        });
    }

    private static int executePlaystyleCommand(CommandContext<ServerCommandSource> context, String style) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendError(Text.literal("Only a player can set a playstyle!"));
            return Command.SINGLE_SUCCESS;
        }

        switch (style.toLowerCase()) {
            case "difficult":
                PlaystyleManager.setPlaystyle(player, "difficult");
                source.sendFeedback(() -> Text.literal("Playstyle set to difficult!"), false);
                break;
            case "peaceful":
                PlaystyleManager.setPlaystyle(player, "peaceful");
                source.sendFeedback(() -> Text.literal("Playstyle set to peaceful!"), false);
                break;
            default:
                source.sendError(Text.literal("Invalid playstyle!"));
                return Command.SINGLE_SUCCESS;
        }

        return Command.SINGLE_SUCCESS;
    }
}
