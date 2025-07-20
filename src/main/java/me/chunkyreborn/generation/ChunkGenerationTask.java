package me.chunkyreborn.generation;

import me.chunkyreborn.ChunkyRebornPlugin;
import me.chunkyreborn.firebase.FirebaseHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChunkGenerationTask extends BukkitRunnable {
    private final ChunkyRebornPlugin plugin;
    private final World world;
    private final int centerX;
    private final int centerZ;
    private final int radiusChunks;
    private final Set<Biome> allowedBiomes;
    private final Player player;
    private final ShapeType shapeType;
    private final String dashboardId;

    private int currentX;
    private int currentZ;
    private final int total;
    private int progress = 0;
    private boolean paused = false;
    private static final Map<Player, BossBar> bossBars = new HashMap<>();

    public ChunkGenerationTask(ChunkyRebornPlugin plugin, World world, Location center, int radiusChunks, Set<Biome> allowedBiomes, Player player, String dashboardId) {
        this.plugin = plugin;
        this.world = world;
        this.centerX = center.getBlockX() >> 4;
        this.centerZ = center.getBlockZ() >> 4;
        this.radiusChunks = radiusChunks;
        this.allowedBiomes = allowedBiomes;
        this.player = player;
        this.shapeType = GenerationManager.getShape(player);
        this.dashboardId = dashboardId;

        this.currentX = -radiusChunks;
        this.currentZ = -radiusChunks;

        this.total = calculateTotalChunksInsideShape();
    }

    @Override
    public void run() {
        if (paused) return;

        int chunksPerRun = 1; // You can increase this for faster gen
        int generatedThisRun = 0;

        while (generatedThisRun < chunksPerRun) {
            if (currentZ > radiusChunks) {
                cancel();
                if (player.isOnline() && !plugin.getConfig().getBoolean("silent")) {
                    player.sendMessage("§aChunk generation completed!");
                }
                break;
            }

            if (isInsideShape(currentX, currentZ)) {
                int chunkX = centerX + currentX;
                int chunkZ = centerZ + currentZ;

                if (allowedBiomes.isEmpty() || allowedBiomes.contains(world.getBiome(chunkX << 4, 0, chunkZ << 4))) {
                    // 🔁 Mark as "generating"
                    FirebaseHelper.sendChunkUpdate(dashboardId, chunkX, chunkZ, "generating");

                    // Load/generate
                    world.getChunkAt(chunkX, chunkZ).load();
                    progress++;

                    // ✅ Mark as "done"
                    FirebaseHelper.sendChunkUpdate(dashboardId, chunkX, chunkZ, "done");
                }
            }

            currentX++;
            if (currentX > radiusChunks) {
                currentX = -radiusChunks;
                currentZ++;
            }

            generatedThisRun++;
        }
    }

    private int calculateTotalChunksInsideShape() {
        int count = 0;
        for (int x = -radiusChunks; x <= radiusChunks; x++) {
            for (int z = -radiusChunks; z <= radiusChunks; z++) {
                if (isInsideShape(x, z)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInsideShape(int x, int z) {
        switch (shapeType) {
            case CIRCLE:
                return (x * x + z * z) <= radiusChunks * radiusChunks;
            case HEXAGON:
                int absX = Math.abs(x);
                int absZ = Math.abs(z);
                return absZ <= radiusChunks && absX <= radiusChunks && (absX + absZ) <= radiusChunks;
            case SQUARE:
            default:
                return true;
        }
    }

    public int getTotalChunks() {
        return total;
    }

    public int getChunksGenerated() {
        return progress;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }
}
