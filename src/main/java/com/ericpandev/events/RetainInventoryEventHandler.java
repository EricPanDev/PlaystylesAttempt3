package com.ericpandev.events;

import com.ericpandev.playstyle.PlaystyleManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemStack;
// import net.minecraft.server.network.ServerPlayerEntity;

public class RetainInventoryEventHandler {
    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            // Only apply if the old player's playstyle is "retain"
            if (PlaystyleManager.isRetain(oldPlayer)) {
                int size = oldPlayer.getInventory().size();
                // Capture a copy of the old inventory
                ItemStack[] oldInv = new ItemStack[size];
                for (int i = 0; i < size; i++) {
                    oldInv[i] = oldPlayer.getInventory().getStack(i).copy();
                }
                // Schedule a task to run one tick later to restore the inventory
                newPlayer.getServer().execute(() -> {
                    for (int i = 0; i < size; i++) {
                        newPlayer.getInventory().setStack(i, oldInv[i]);
                    }
                });
            }
        });
    }
}
