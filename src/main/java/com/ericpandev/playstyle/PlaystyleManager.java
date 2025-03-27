package com.ericpandev.playstyle;

import net.minecraft.entity.player.PlayerEntity;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaystyleManager {
    private static final Map<UUID, String> playerPlaystyles = new ConcurrentHashMap<>();

    public static void setPlaystyle(PlayerEntity player, String playstyle) {
        playerPlaystyles.put(player.getUuid(), playstyle);
    }

    public static String getPlaystyle(PlayerEntity player) {
        return playerPlaystyles.getOrDefault(player.getUuid(), "normal");
    }

    public static boolean isPeaceful(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("peaceful");
    }

    public static boolean isDifficult(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("difficult");
    }

    public static boolean isRetain(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("retain");
    }

    public static boolean isGlass(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("glass");
    }
}
