package com.ericpandev.playstyle;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaystyleManager {
    private static final Map<ServerPlayerEntity, String> playerPlaystyles = new ConcurrentHashMap<>();

    public static void setPlaystyle(ServerPlayerEntity player, String playstyle) {
        playerPlaystyles.put(player, playstyle);
    }

    public static boolean isPeaceful(ServerPlayerEntity player) {
        String style = playerPlaystyles.get(player);
        return style != null && style.equalsIgnoreCase("peaceful");
    }
}
