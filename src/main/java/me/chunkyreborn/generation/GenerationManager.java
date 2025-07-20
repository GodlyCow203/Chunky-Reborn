package me.chunkyreborn.generation;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import java.util.*;

public class GenerationManager {
    private static final Map<UUID, ChunkGenerationTask> tasks = new HashMap<>();
    private static final Map<UUID, ShapeType> shapeMap = new HashMap<>();
    private static final Map<UUID, GenerationSettings> playerSettings = new HashMap<>();
    private static final Map<Player, BossBar> bossBars = new HashMap<>();

    // Add this new map for dashboard IDs
    private static final Map<UUID, String> dashboardIds = new HashMap<>();

    // Existing methods ...

    public static void setTask(Player player, ChunkGenerationTask task) {
        tasks.put(player.getUniqueId(), task);
    }

    public static ChunkGenerationTask getTask(Player player) {
        return tasks.get(player.getUniqueId());
    }

    public static void removeTask(Player player) {
        tasks.remove(player.getUniqueId());
    }

    public static boolean hasTask(Player player) {
        return tasks.containsKey(player.getUniqueId());
    }

    public static void setShape(Player player, ShapeType shape) {
        shapeMap.put(player.getUniqueId(), shape);
    }

    public static ShapeType getShape(Player player) {
        return shapeMap.getOrDefault(player.getUniqueId(), ShapeType.SQUARE);
    }

    public static GenerationSettings getSettings(Player player) {
        return playerSettings.computeIfAbsent(player.getUniqueId(),
                uuid -> new GenerationSettings(player.getWorld()));
    }

    public static void removeSettings(Player player) {
        playerSettings.remove(player.getUniqueId());
    }

    public static void addBossBar(Player player, BossBar bossBar) {
        bossBars.put(player, bossBar);
        bossBar.addPlayer(player);
    }

    public static void removeBossBar(Player player) {
        BossBar bar = bossBars.remove(player);
        if (bar != null) {
            bar.removeAll();
        }
    }

    public static BossBar getBossBar(Player player) {
        return bossBars.get(player);
    }

    // New methods for dashboardId management:
    public static void setDashboardId(Player player, String dashboardId) {
        dashboardIds.put(player.getUniqueId(), dashboardId);
    }

    public static String getDashboardId(Player player) {
        return dashboardIds.get(player.getUniqueId());
    }

    public static void removeDashboardId(Player player) {
        dashboardIds.remove(player.getUniqueId());
    }
}
