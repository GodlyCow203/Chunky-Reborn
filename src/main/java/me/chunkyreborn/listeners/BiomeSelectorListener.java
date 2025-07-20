package me.chunkyreborn.listeners;

import me.chunkyreborn.ChunkyRebornPlugin;
import me.chunkyreborn.generation.GenerationManager;
import me.chunkyreborn.gui.BiomeSelectorGUI;
import me.chunkyreborn.generation.ChunkGenerationTask;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class BiomeSelectorListener implements Listener {

    private final ChunkyRebornPlugin plugin;


    public BiomeSelectorListener(ChunkyRebornPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBiomeSelect(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals("Select Biomes")) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("Start Generation")) {
            player.closeInventory();
            Set<Biome> selected = BiomeSelectorGUI.getSelectedBiomes(player);
            player.sendMessage("§aStarting generation with selected biomes: §e" + selected.size());

            // Generate dashboard ID and store it
            String dashboardId = UUID.randomUUID().toString();
            GenerationManager.setDashboardId(player, dashboardId);

            // Start task with dashboardId
            new ChunkGenerationTask(plugin, player.getWorld(), player.getLocation(), 10, selected, player, dashboardId)
                    .runTaskTimer(plugin, 0L, 2L);

            BiomeSelectorGUI.clear(player);
            return;
        }

        try {
            Biome biome = Biome.valueOf(name.toUpperCase().replace(" ", "_"));
            BiomeSelectorGUI.toggleBiomeSelection(player, biome);
            player.sendMessage("§7Toggled §e" + biome.name());
        } catch (IllegalArgumentException ignored) {
        }
    }
}
