package com.ericpandev;

import com.ericpandev.commands.PlaystyleCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ericpandev.events.RetainInventoryEventHandler;
import com.ericpandev.playstyle.PlaystyleManager;

public class playstyles implements ModInitializer {
    public static final String MOD_ID = "playstyles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        LOGGER.info("Playstyles mod by @ericpandev for YSWS Minecraft hosted by Hack Club");

        PlaystyleCommand.register();
        RetainInventoryEventHandler.register();

        // Register tick event
        ServerTickEvents.START_SERVER_TICK.register(this::onTick);

        // Register the block use event to prevent crafting table interactions
        UseBlockCallback.EVENT.register(this::onPlayerUseBlock);
    }

    private void onTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter % 5 == 0) {

            // Loop through all players in the server
            for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (PlaystyleManager.isUndead(player)) {
                    World world = player.getWorld();
                    BlockPos pos = player.getBlockPos();

                    // Check if the player is in direct sunlight
                    if (world.isDay() && world.getLightLevel(pos) >= 15) {
                        // Set the player on fire for 5 seconds (50 ticks)
                        player.setOnFireFor(50);
                    }
                }
            }
        }
    }

    private ActionResult onPlayerUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        if (world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE) {
            if (PlaystyleManager.isSurvivalist(player)) {
                player.sendMessage(Text.literal("You are not allowed to use crafting tables!"), false);
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }
}
