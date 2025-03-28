package com.ericpandev;

import com.ericpandev.commands.PlaystyleCommand;  // Import PlaystyleCommand
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericpandev.events.RetainInventoryEventHandler;

public class playstyles implements ModInitializer {
    public static final String MOD_ID = "playstyles";

    // This logger is used to write text to the console and the log file.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        LOGGER.info("Playstyles mod by @ericpandev for YSWS Minecraft hosted by Hack Club");

        // Register the PlaystyleCommand
        PlaystyleCommand.register();
        RetainInventoryEventHandler.register();
    }
}
