package com.ericpandev.playstyle;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class ExperienceManager {
    private static final Map<UUID, Integer> xpCache = new HashMap<>();
    
    public static void storeXp(UUID uuid, int xp) {
        xpCache.put(uuid, xp);
    }
    
    public static int retrieveXp(UUID uuid) {
        return xpCache.getOrDefault(uuid, 0);
    }
    
    public static void clearXp(UUID uuid) {
        xpCache.remove(uuid);
    }
}
