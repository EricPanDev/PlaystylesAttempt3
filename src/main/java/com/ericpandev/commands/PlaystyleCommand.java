package com.ericpandev.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
// import net.minecraft.server.world.ServerWorld;
// import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
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
                            builder.suggest("normal");
                            builder.suggest("retain");
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
            case "normal":
                PlaystyleManager.setPlaystyle(player, "normal");
                source.sendFeedback(() -> Text.literal("Playstyle has been reset."), false);
                break;
            case "retain":
                PlaystyleManager.setPlaystyle(player, "retain");
                source.sendFeedback(() -> Text.literal("Playstyle set to retain!"), false);
                break;
            default:
                source.sendError(Text.literal("Invalid playstyle!"));
                return Command.SINGLE_SUCCESS;
        }

        // Force mobs to recheck their targets based on the new playstyle
        recheckMobsAI(player);

        return Command.SINGLE_SUCCESS;
    }

    private static void recheckMobsAI(ServerPlayerEntity player) {
        World world = player.getWorld(); // Get the world from the player
    
        // Get all mob entities within a certain range of the player
        world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(100), entity -> true).forEach(mob -> {
            // Reset the mob's target if it is currently targeting the player
            if (mob.getTarget() == player) {
                mob.setTarget(null);  // Reset the target
    
                // Trigger the mob's tick() method directly to recheck its AI
                mob.tick();  // This forces the mob to update its state and AI behavior
            }
        });
    }

}
