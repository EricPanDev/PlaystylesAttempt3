package com.ericpandev.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
                    .executes(context -> showAvailablePlaystyles(context))
                    .then(CommandManager.argument("style", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("difficult");
                            builder.suggest("peaceful");
                            builder.suggest("normal");
                            builder.suggest("retain");
                            builder.suggest("glass");
                            builder.suggest("berserk");
                            builder.suggest("undead");
                            builder.suggest("survivalist");
                            return builder.buildFuture();
                        })
                        .executes(context -> executePlaystyleCommand(context, StringArgumentType.getString(context, "style"), context.getSource().getPlayer()))
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                            .executes(context -> executePlaystyleCommand(context, StringArgumentType.getString(context, "style"), EntityArgumentType.getPlayer(context, "target"))))
                    )
            );
        });
    }

    private static int showAvailablePlaystyles(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendError(Text.literal("Only a player can set a playstyle!"));
            return Command.SINGLE_SUCCESS;
        }

        Text title = Text.literal("Available Playstyles:").formatted(Formatting.BOLD, Formatting.UNDERLINE);
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
            .append(createClickablePlaystyle("Undead", "undead", "Mobs ignore you but you take damage from the sun"))
            .append("\n")
            .append(createClickablePlaystyle("Survivalist", "survivalist", "Minecraft without the craft(ing table)"));

        source.sendFeedback(() -> title.copy().append(playstylesList), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Text createClickablePlaystyle(String name, String style, String description) {
        return Text.literal(name)
            .styled(styleBuilder -> styleBuilder.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/playstyle " + style))
                    .withColor(Formatting.GOLD)
                    .withBold(true)
                    .withUnderline(false))
            .append(Text.literal(" - " + description)
                .styled(descStyle -> descStyle.withColor(Formatting.WHITE).withBold(false).withUnderline(false)));
    }

    private static int executePlaystyleCommand(CommandContext<ServerCommandSource> context, String style, ServerPlayerEntity player) {
        if (player == null) {
            context.getSource().sendError(Text.literal("Only a player can set a playstyle!"));
            return Command.SINGLE_SUCCESS;
        }

        switch (style.toLowerCase()) {
            case "difficult":
            case "peaceful":
            case "normal":
            case "retain":
            case "berserk":
            case "undead":
            case "survivalist":
                PlaystyleManager.setPlaystyle(player, style);
                context.getSource().sendFeedback(() -> Text.literal("Playstyle set to " + style + "!"), false);
                break;
            case "glass":
                PlaystyleManager.setPlaystyle(player, "glass");
                EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (healthAttribute != null) {
                    healthAttribute.setBaseValue(2.0);
                    player.setHealth(2.0f);
                }
                context.getSource().sendFeedback(() -> Text.literal("Playstyle set to glass!"), false);
                break;
            default:
                context.getSource().sendError(Text.literal("Invalid playstyle!"));
                return Command.SINGLE_SUCCESS;
        }

        if (!style.equalsIgnoreCase("glass")) {
            EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            if (healthAttribute != null) {
                healthAttribute.setBaseValue(20.0);
            }
        }

        recheckMobsAI(player);
        return Command.SINGLE_SUCCESS;
    }

    private static void recheckMobsAI(ServerPlayerEntity player) {
        World world = player.getWorld();
        world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(100), entity -> true).forEach(mob -> {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
                mob.tick();
            }
        });
    }

    
}