package me.chunkyreborn.commands;

import me.chunkyreborn.ChunkyRebornPlugin;
import me.chunkyreborn.generation.ChunkGenerationTask;
import me.chunkyreborn.generation.GenerationManager;
import me.chunkyreborn.generation.GenerationSettings;
import me.chunkyreborn.generation.ShapeType;
import me.chunkyreborn.gui.BiomeSelectorGUI;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChunkyRebornCommand implements CommandExecutor {
    private final ChunkyRebornPlugin plugin;

    public ChunkyRebornCommand(ChunkyRebornPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                String dashboardId = UUID.randomUUID().toString();
                int radius = 5; // default radius
                if (args.length >= 2) {
                    try {
                        radius = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid radius: " + args[1]);
                        return true;
                    }
                }

                Set<Biome> allowedBiomes = new HashSet<>();
                if (args.length > 2) {
                    for (int i = 2; i < args.length; i++) {
                        try {
                            allowedBiomes.add(Biome.valueOf(args[i].toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.RED + "Invalid biome: " + args[i]);
                            return true;
                        }
                    }
                }

                World world = player.getWorld();
                player.sendMessage(ChatColor.YELLOW + "Starting generation in " + world.getName()
                        + " with radius " + radius + " and biomes: "
                        + (allowedBiomes.isEmpty() ? "All" : allowedBiomes.toString()));

                ChunkGenerationTask task = new ChunkGenerationTask(plugin, world, player.getLocation(), radius, allowedBiomes, player, dashboardId);

                task.runTaskTimer(plugin, 0L, 2L);

                GenerationManager.setTask(player, task);

                return true;
            }
            case "version" -> {
                String version = plugin.getDescription().getVersion();
                player.sendMessage(ChatColor.YELLOW + "ChunkyReborn version: " + ChatColor.WHITE + version);
                return true;
            }

            case "about" -> {
                player.sendMessage(ChatColor.GOLD + "ChunkyReborn: " + ChatColor.GRAY + "Advanced chunk pre-generation plugin with live tracking.");
                player.sendMessage(ChatColor.GRAY + "Supports radius, shapes, biome filtering, real-time dashboard & more.");
                return true;
            }

            case "help" -> {
                player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.GREEN + "Chunky Reborn Commands" + ChatColor.GOLD + " ===");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn start <radius> [biomes...] " + ChatColor.WHITE + "- Start generation");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn pause " + ChatColor.WHITE + "- Pause generation");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn resume " + ChatColor.WHITE + "- Resume generation");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn stop " + ChatColor.WHITE + "- Stop generation");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn shape <square|circle|hexagon> " + ChatColor.WHITE + "- Set generation shape");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn world <world> " + ChatColor.WHITE + "- Select the world");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn center [<x> <z>] " + ChatColor.WHITE + "- Set generation center");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn radius <radius> " + ChatColor.WHITE + "- Set generation radius");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn worldborder " + ChatColor.WHITE + "- Match center and radius to world border");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn spawn " + ChatColor.WHITE + "- Set center to world spawn");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn corners <x1> <z1> <x2> <z2> " + ChatColor.WHITE + "- Set selection by corners");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn pattern <pattern> " + ChatColor.WHITE + "- Set generation pattern");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn selection " + ChatColor.WHITE + "- Show current selection");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn silent " + ChatColor.WHITE + "- Toggle silent mode");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn quiet <interval> " + ChatColor.WHITE + "- Set quiet interval in seconds");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn progress " + ChatColor.WHITE + "- Show generation progress");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn reload " + ChatColor.WHITE + "- Reload plugin config");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn version " + ChatColor.WHITE + "- Check the Plugin version");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn help " + ChatColor.WHITE + "- Help Menu");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn about " + ChatColor.WHITE + "- About ChunkyReborn");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn trim " + ChatColor.WHITE + "- Delete chunks outside selection");
                player.sendMessage(ChatColor.YELLOW + "/chunkyreborn smart " + ChatColor.WHITE + "- Provides the best settings for your Server");
                player.sendMessage(ChatColor.GRAY + "Example: /chunkyreborn start 10 plains forest");
                return true;
            }
            case "developer" -> {
                player.sendMessage(ChatColor.AQUA + "ChunkyReborn by " + ChatColor.WHITE + "ADuckPlayingMC ( DC: _cow_0990");
                player.sendMessage(ChatColor.GRAY + "Inspired by Chunky, extended by me");
                player.sendMessage(ChatColor.GRAY + "Website: ( Dashboard coming soon )");
                return true;
            }


            case "world" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /chunky world <world>");
                    return true;
                }
                World world = Bukkit.getWorld(args[1]);
                if (world == null) {
                    player.sendMessage("World not found: " + args[1]);
                    return true;
                }
                GenerationSettings settings = GenerationManager.getSettings(player);
                settings.setWorld(world);
                player.sendMessage("Selected world set to: " + world.getName());
                return true;
            }

            case "center" -> {
                GenerationSettings settings = GenerationManager.getSettings(player);
                if (args.length == 1) {
                    // Use player's current location as center
                    settings.setCenterX(player.getLocation().getBlockX());
                    settings.setCenterZ(player.getLocation().getBlockZ());
                    player.sendMessage("Center set to your current position.");
                } else if (args.length == 3) {
                    try {
                        int x = Integer.parseInt(args[1]);
                        int z = Integer.parseInt(args[2]);
                        settings.setCenterX(x);
                        settings.setCenterZ(z);
                        player.sendMessage("Center set to (" + x + ", " + z + ").");
                    } catch (NumberFormatException e) {
                        player.sendMessage("Invalid coordinates.");
                    }
                } else {
                    player.sendMessage("Usage: /chunky center [<x> <z>]");
                }
                return true;
            }

            case "radius" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /chunky radius <radius>");
                    return true;
                }
                try {
                    int radius = Integer.parseInt(args[1]);
                    if (radius < 1 || radius > 1000) {
                        player.sendMessage("Radius must be between 1 and 1000.");
                        return true;
                    }
                    GenerationManager.getSettings(player).setRadius(radius);
                    player.sendMessage("Radius set to " + radius);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid radius.");
                }
                return true;
            }

            case "worldborder" -> {
                GenerationSettings settings = GenerationManager.getSettings(player);
                World world = settings.getWorld();
                if (world == null) {
                    player.sendMessage("No world selected.");
                    return true;
                }
                var border = world.getWorldBorder();
                int centerX = border.getCenter().getBlockX();
                int centerZ = border.getCenter().getBlockZ();
                int radius = (int) (border.getSize() / 2 / 16); // radius in chunks
                settings.setCenterX(centerX);
                settings.setCenterZ(centerZ);
                settings.setRadius(radius);
                player.sendMessage("Center and radius set to world border.");
                return true;
            }

            case "spawn" -> {
                GenerationSettings settings = GenerationManager.getSettings(player);
                World world = settings.getWorld();
                if (world == null) {
                    player.sendMessage("No world selected.");
                    return true;
                }
                var spawn = world.getSpawnLocation();
                settings.setCenterX(spawn.getBlockX());
                settings.setCenterZ(spawn.getBlockZ());
                player.sendMessage("Center set to spawn point of world.");
                return true;
            }

            case "corners" -> {
                if (args.length != 5) {
                    player.sendMessage("Usage: /chunky corners <x1> <z1> <x2> <z2>");
                    return true;
                }
                try {
                    int x1 = Integer.parseInt(args[1]);
                    int z1 = Integer.parseInt(args[2]);
                    int x2 = Integer.parseInt(args[3]);
                    int z2 = Integer.parseInt(args[4]);
                    int centerX = (x1 + x2) / 2;
                    int centerZ = (z1 + z2) / 2;
                    int radiusX = Math.abs(x1 - x2) / 2 / 16; // chunks radius in x
                    int radiusZ = Math.abs(z1 - z2) / 2 / 16; // chunks radius in z
                    int radius = Math.max(radiusX, radiusZ);
                    GenerationSettings settings = GenerationManager.getSettings(player);
                    settings.setCenterX(centerX);
                    settings.setCenterZ(centerZ);
                    settings.setRadius(radius);
                    player.sendMessage("Selection set from corners.");
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid coordinates.");
                }
                return true;
            }

            case "pattern" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /chunky pattern <pattern>");
                    return true;
                }
                GenerationManager.getSettings(player).setPattern(args[1]);
                player.sendMessage("Pattern set to " + args[1]);
                return true;
            }

            case "selection" -> {
                GenerationSettings settings = GenerationManager.getSettings(player);
                player.sendMessage("Current selection:");
                player.sendMessage("- World: " + (settings.getWorld() == null ? "None" : settings.getWorld().getName()));
                player.sendMessage("- Shape: " + settings.getShape());
                player.sendMessage("- Center: (" + settings.getCenterX() + ", " + settings.getCenterZ() + ")");
                player.sendMessage("- Radius: " + settings.getRadius());
                player.sendMessage("- Pattern: " + settings.getPattern());
                player.sendMessage("- Silent: " + (settings.isSilent() ? "Yes" : "No"));
                player.sendMessage("- Quiet interval: " + settings.getQuietInterval() + "s");
                return true;
            }

            case "silent" -> {
                GenerationSettings settings = GenerationManager.getSettings(player);
                settings.setSilent(!settings.isSilent());
                player.sendMessage("Silent mode " + (settings.isSilent() ? "enabled" : "disabled"));
                return true;
            }


            case "quiet" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /chunky quiet <interval>");
                    return true;
                }
                try {
                    int interval = Integer.parseInt(args[1]);
                    if (interval < 1) {
                        player.sendMessage("Interval must be positive.");
                        return true;
                    }
                    GenerationManager.getSettings(player).setQuietInterval(interval);
                    player.sendMessage("Quiet interval set to " + interval + " seconds.");
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid interval.");
                }
                return true;
            }

            case "reload" -> {
                plugin.reloadConfig();
                player.sendMessage("Configuration reloaded.");
                return true;
            }

            case "progress" -> {
                ChunkGenerationTask task = GenerationManager.getTask(player);
                if (task == null) {
                    player.sendMessage(ChatColor.RED + "No running generation task.");
                    return true;
                }
                int generated = task.getChunksGenerated();
                int total = task.getTotalChunks();
                player.sendMessage(ChatColor.YELLOW + "Chunk Generation Progress: " + generated + "/" + total + " chunks generated (" + (int) ((generated / (double) total) * 100) + "%).");
                return true;
            }


            case "trim" -> {
                // Implement async chunk unload outside current selection, careful with performance
                player.sendMessage("Trimming chunks outside selection... (not implemented yet)");
                return true;
            }

            case "pause" -> {
                ChunkGenerationTask task = GenerationManager.getTask(player);
                if (task != null) {
                    task.pause();
                    player.sendMessage(ChatColor.YELLOW + "Generation paused.");
                } else {
                    player.sendMessage(ChatColor.RED + "No running generation task found.");
                }
                return true;
            }
            case "resume" -> {
                ChunkGenerationTask task = GenerationManager.getTask(player);
                if (task != null) {
                    task.resume();
                    player.sendMessage(ChatColor.GREEN + "Generation resumed.");
                } else {
                    player.sendMessage(ChatColor.RED + "No generation task to resume.");
                }
                return true;
            }

            case "pbar" -> {
                ChunkGenerationTask task = GenerationManager.getTask(player);
                if (task == null) {
                    player.sendMessage(ChatColor.RED + "No running generation task.");
                    return true;
                }

                BossBar existingBar = (BossBar) GenerationManager.getBossBar(player);
                final org.bukkit.boss.BossBar bar;
                if (existingBar == null) {
                    bar = (org.bukkit.boss.BossBar) Bukkit.createBossBar("Generating Chunks...", BarColor.GREEN, BarStyle.SOLID);
                    GenerationManager.addBossBar(player, bar);
                } else {
                    bar = (org.bukkit.boss.BossBar) existingBar;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (task.isCancelled() || task.getChunksGenerated() >= task.getTotalChunks()) {
                            bar.setProgress(1.0);
                            bar.setTitle(ChatColor.GREEN + "Generation Complete!");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GenerationManager.removeBossBar(player);
                                }
                            }.runTaskLater(plugin, 40L); // Remove after 2 seconds
                            cancel();
                            return;
                        }

                        double progress = (double) task.getChunksGenerated() / task.getTotalChunks();
                        bar.setProgress(progress);
                        bar.setTitle(ChatColor.YELLOW + "Generating Chunks: " + (int) (progress * 100) + "%");
                    }
                }.runTaskTimer(plugin, 0L, 5L);

                player.sendMessage(ChatColor.GREEN + "BossBar progress display started.");
                return true;
            }
            case "smart" -> {
                long maxMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);
                int players = Bukkit.getOnlinePlayers().size();
                double tps = Bukkit.getTPS()[0];
                int cores = Runtime.getRuntime().availableProcessors();

                // Safety guards
                if (tps < 10) {
                    player.sendMessage(ChatColor.RED + "⚠ Server TPS too low (" + String.format("%.2f", tps) + "). Try again later.");
                    return true;
                }

                // Smart scoring heuristic
                double performanceScore = (maxMemoryMB / (double)(players == 0 ? 1 : players)) * (tps / 20.0);
                int radius;
                int delay;

                if (performanceScore > 8000) {
                    radius = 1000;
                    delay = 1;
                } else if (performanceScore > 4000) {
                    radius = 750;
                    delay = 3;
                } else if (performanceScore > 2000) {
                    radius = 500;
                    delay = 6;
                } else if (performanceScore > 1000) {
                    radius = 300;
                    delay = 10;
                } else {
                    radius = 150;
                    delay = 15;
                }

                String shape = (radius > 600) ? "circle" : "square";

                player.sendMessage(ChatColor.AQUA + "📊 Smart Generation Recommendation:");
                player.sendMessage(ChatColor.GRAY + "- Server RAM: " + maxMemoryMB + "MB");
                player.sendMessage(ChatColor.GRAY + "- Online Players: " + players);
                player.sendMessage(ChatColor.GRAY + "- TPS: " + String.format("%.2f", tps));
                player.sendMessage(ChatColor.GRAY + "- CPU Cores: " + cores);
                player.sendMessage("");
                player.sendMessage(ChatColor.GREEN + "✅ Suggested Settings:");
                player.sendMessage(ChatColor.YELLOW + "Shape: " + shape);
                player.sendMessage(ChatColor.YELLOW + "Radius: " + radius + " chunks");
                player.sendMessage(ChatColor.YELLOW + "Delay: " + delay + " ticks between chunks");
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "Tip: Use:");
                player.sendMessage(ChatColor.GRAY + " /chunkyreborn shape " + shape);
                player.sendMessage(ChatColor.GRAY + " /chunkyreborn radius " + radius);
                player.sendMessage(ChatColor.GRAY + " /chunkyreborn delay " + delay);

                return true;
            }


            case "shape" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.YELLOW + "Usage: /chunkyreborn shape <square|circle|hexagon>");
                    return true;
                }
                try {
                    ShapeType shape = ShapeType.valueOf(args[1].toUpperCase());
                    GenerationManager.setShape(player, shape);
                    player.sendMessage(ChatColor.GREEN + "Shape set to: " + shape.name());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "Invalid shape.");
                }
                return true;
            }

            case "stop" -> {
                ChunkGenerationTask task = GenerationManager.getTask(player);
                if (task != null) {
                    task.cancel();
                    GenerationManager.removeTask(player);
                    player.sendMessage(ChatColor.RED + "Generation stopped.");
                } else {
                    player.sendMessage(ChatColor.RED + "No generation task to stop.");
                }
                return true;
            }

            case "gui" -> {
                BiomeSelectorGUI.open(player);
                return true;
            }
            case "dashboard" -> {
                String dashboardId = UUID.randomUUID().toString().substring(0, 8);
                GenerationManager.setDashboardId(player, dashboardId); // Save per-player
                String url = "https://chunkyreborn.web.app/dashboard.html?id=" + dashboardId;
                player.sendMessage(ChatColor.GREEN + "Open your dashboard: " + ChatColor.UNDERLINE + url);
                return true;
            }



            default -> {
                sendHelp(player);
                return true;
            }
        }
    }


            private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.GREEN + "Chunky Reborn Commands" + ChatColor.GOLD + " ===");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn start <radius> [biomes...] " + ChatColor.WHITE + "- Start generation");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn pause " + ChatColor.WHITE + "- Pause generation");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn resume " + ChatColor.WHITE + "- Resume generation");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn stop " + ChatColor.WHITE + "- Stop generation");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn shape <square|circle|hexagon> " + ChatColor.WHITE + "- Set generation shape");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn world <world> " + ChatColor.WHITE + "- Select the world");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn center [<x> <z>] " + ChatColor.WHITE + "- Set generation center");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn radius <radius> " + ChatColor.WHITE + "- Set generation radius");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn worldborder " + ChatColor.WHITE + "- Match center and radius to world border");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn spawn " + ChatColor.WHITE + "- Set center to world spawn");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn corners <x1> <z1> <x2> <z2> " + ChatColor.WHITE + "- Set selection by corners");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn pattern <pattern> " + ChatColor.WHITE + "- Set generation pattern");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn selection " + ChatColor.WHITE + "- Show current selection");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn silent " + ChatColor.WHITE + "- Toggle silent mode");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn quiet <interval> " + ChatColor.WHITE + "- Set quiet interval in seconds");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn progress " + ChatColor.WHITE + "- Show generation progress");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn reload " + ChatColor.WHITE + "- Reload plugin config");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn version " + ChatColor.WHITE + "- Check the Plugin version");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn help " + ChatColor.WHITE + "- Help Menu");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn about " + ChatColor.WHITE + "- About ChunkyReborn");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn trim " + ChatColor.WHITE + "- Delete chunks outside selection");
        player.sendMessage(ChatColor.YELLOW + "/chunkyreborn smart " + ChatColor.WHITE + "- Provides the best settings for your Server");
        player.sendMessage(ChatColor.GRAY + "Example: /chunkyreborn start 10 plains forest");
    }

}