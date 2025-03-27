package com.ericpandev.playstyle;

import net.minecraft.entity.player.PlayerInventory;
// import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    // Use a Map to store PlayerInventory for each player by UUID
    private static final Map<UUID, PlayerInventory> inventoryCache = new HashMap<>();

    // Store the player's inventory in the cache
    public static void storeInventory(UUID uuid, PlayerInventory inventory) {
        // Create a copy of the inventory and store it in the cache
        PlayerInventory inventoryCopy = copyInventory(inventory);
        inventoryCache.put(uuid, inventoryCopy);
    }

    // Retrieve the player's inventory from the cache
    public static PlayerInventory getInventory(UUID uuid) {
        return inventoryCache.get(uuid);
    }

    // Clear the player's inventory from the cache
    public static void clearInventory(UUID uuid) {
        inventoryCache.remove(uuid);
    }

    // Create a copy of the PlayerInventory
    private static PlayerInventory copyInventory(PlayerInventory inventory) {
        PlayerInventory copy = new PlayerInventory(inventory.player);

        // Copy the main inventory
        for (int i = 0; i < inventory.main.size(); i++) {
            copy.main.set(i, inventory.main.get(i).copy()); // Copy each ItemStack
        }

        // Copy the armor inventory
        for (int i = 0; i < inventory.armor.size(); i++) {
            copy.armor.set(i, inventory.armor.get(i).copy()); // Copy each ItemStack
        }

        // Copy the off-hand inventory
        if (inventory.offHand != null) {
            copy.offHand.set(0, inventory.offHand.get(0).copy()); // Copy offhand item
        }

        return copy;
    }
}
