package com.ericpandev;

import com.ericpandev.commands.PlaystyleCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
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
    }

    private void onTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter % 5 == 0) {
            LOGGER.info("Running every 5 ticks");

            // Loop through all players in the server
            for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (PlaystyleManager.isUndead(player)) {
                    World world = player.getWorld();
                    BlockPos pos = player.getBlockPos();

                    // Check if the player is in direct sunlight
                    if (world.isDay() && world.getLightLevel(pos) >= 15) {
                        // Set the player on fire for 5 seconds (100 ticks)
                        player.setOnFireFor(50);
                    }
                }
            }
        }
    }
}
