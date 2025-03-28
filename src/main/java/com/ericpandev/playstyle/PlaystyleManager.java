package com.ericpandev.playstyle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaystyleManager {
    private static final Map<UUID, String> playerPlaystyles = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private static final File DATA_FILE;

    static {
        // Get the config directory from Fabric and resolve the playstyles.json file
        Path configDir = FabricLoader.getInstance().getConfigDir();
        DATA_FILE = configDir.resolve("playstyles.json").toFile();
        load();
    }

    public static void setPlaystyle(PlayerEntity player, String playstyle) {
        playerPlaystyles.put(player.getUuid(), playstyle);
        save();
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

    public static boolean isBerserk(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("berserk");
    }

    public static boolean isUndead(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("undead");
    }

    public static boolean isSurvivalist(PlayerEntity player) {
        String style = playerPlaystyles.get(player.getUuid());
        return style != null && style.equalsIgnoreCase("survivalist");
    }

    private static void load() {
        if (!DATA_FILE.exists()) {
            return;
        }
        try (Reader reader = new FileReader(DATA_FILE)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = GSON.fromJson(reader, type);
            if (map != null) {
                playerPlaystyles.clear();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    playerPlaystyles.put(UUID.fromString(entry.getKey()), entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void save() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<UUID, String> entry : playerPlaystyles.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue());
        }
        try (Writer writer = new FileWriter(DATA_FILE)) {
            GSON.toJson(map, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
