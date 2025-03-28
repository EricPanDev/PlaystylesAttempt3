package com.ericpandev.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import com.ericpandev.playstyle.PlaystyleManager;

public class PlaystyleCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("playstyle")
                    .executes(context -> showAvailablePlaystyles(context)) // Show the list when no arguments
                    .then(CommandManager.argument("style", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("difficult");
                            builder.suggest("peaceful");
                            builder.suggest("normal");
                            builder.suggest("retain");
                            builder.suggest("glass");
                            builder.suggest("berserk");
                            builder.suggest("undead");
                            return builder.buildFuture();
                        })
                        .executes(context -> executePlaystyleCommand(context, StringArgumentType.getString(context, "style")))
                    )
            );
        });
    }

// Show the list of available playstyles
private static int showAvailablePlaystyles(CommandContext<ServerCommandSource> context) {
    ServerCommandSource source = context.getSource();
    ServerPlayerEntity player = source.getPlayer();

    if (player == null) {
        source.sendError(Text.literal("Only a player can set a playstyle!"));
        return Command.SINGLE_SUCCESS;
    }

    // Create a title with bold and underline formatting
    Text title = Text.literal("Available Playstyles:")
            .formatted(Formatting.BOLD, Formatting.UNDERLINE);

    // Build the clickable list for each playstyle without underlining
    Text playstylesList = Text.empty()
        .append("\n")
        .append(createClickablePlaystyle("Normal", "normal", "Revert to normal Minecraft"))
        .append("\n")
        .append(createClickablePlaystyle("Peaceful", "peaceful", "Mobs ignore you but you can't attack them"))
        .append("\n")
        .append(createClickablePlaystyle("Retain", "retain", "You do not lose your items upon death"))
        .append("\n")
        .append(createClickablePlaystyle("Difficult", "difficult", "You take 1.5x damage"))
        .append("\n")
        .append(createClickablePlaystyle("Glass", "glass", "You have 1 heart of health"))
        .append("\n")
        .append(createClickablePlaystyle("Berserk", "berserk", "You deal x2 damage but also receive x2 damage"))
        .append("\n")
        .append(createClickablePlaystyle("undead", "undead", "Mobs ignore you but you take damage from the sun"));

    // Combine title and list
    Text availablePlaystyles = title.copy().append(playstylesList);

    source.sendFeedback(() -> availablePlaystyles, false);
    return Command.SINGLE_SUCCESS;
}

// Create clickable playstyle text with a description
private static Text createClickablePlaystyle(String name, String style, String description) {
    // Ensure the clickable playstyle is not underlined by explicitly not applying that formatting
    return Text.literal(name)
        .styled(styleBuilder -> styleBuilder.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/playstyle " + style))
                .withColor(Formatting.GOLD)  // Playstyle name in gold
                .withBold(true)  // Bold playstyle name
                .withUnderline(false))  // Explicitly turn off underline
        .append(Text.literal(" - " + description)
            .styled(descStyle -> descStyle.withColor(Formatting.WHITE)  // Description in white
                .withBold(false)  // Unbold description
                .withUnderline(false))  // Ensure no underline for description
        );
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
            case "berserk":
                PlaystyleManager.setPlaystyle(player, "berserk");
                source.sendFeedback(() -> Text.literal("Playstyle set to berserk!"), false);
                break;
            case "undead":
                PlaystyleManager.setPlaystyle(player, "undead");
                source.sendFeedback(() -> Text.literal("Playstyle set to undead!"), false);
                break;
            case "glass":
                PlaystyleManager.setPlaystyle(player, "glass");
                EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (healthAttribute != null) {
                    healthAttribute.setBaseValue(2.0);
                    player.setHealth(2.0f);
                }
                source.sendFeedback(() -> Text.literal("Playstyle set to glass!"), false);
                break;
            default:
                source.sendError(Text.literal("Invalid playstyle!"));
                return Command.SINGLE_SUCCESS;
        }
        if (!style.equalsIgnoreCase("glass")) {
            EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            if (healthAttribute != null) {
                healthAttribute.setBaseValue(20.0);
            }
        }

        // Force mobs to recheck their targets based on the new playstyle
        recheckMobsAI(player);

        return Command.SINGLE_SUCCESS;
    }

    private static void recheckMobsAI(ServerPlayerEntity player) {
        World world = player.getWorld();
        // Get all mob entities within a certain range of the player
        world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(100), entity -> true).forEach(mob -> {
            if (mob.getTarget() == player) {
                mob.setTarget(null);  // Reset the target
                mob.tick();  // Force AI update
            }
        });
    }
}
