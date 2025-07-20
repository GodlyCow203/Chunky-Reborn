package me.chunkyreborn.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BiomeSelectorGUI {

    private static final Map<UUID, Set<Biome>> selectedBiomes = new HashMap<>();
    private static final String GUI_TITLE = "Select Biomes";

    // Glass pane colors:
    private static final Material GREEN_PANE = Material.GREEN_STAINED_GLASS_PANE;
    private static final Material RED_PANE = Material.RED_STAINED_GLASS_PANE;
    private static final Material YELLOW_PANE = Material.YELLOW_STAINED_GLASS_PANE;
    private static final Material GRAY_PANE = Material.GRAY_STAINED_GLASS_PANE;

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);

        // Initialize selected biomes for this player if not present
        selectedBiomes.putIfAbsent(player.getUniqueId(), new HashSet<>());

        int slot = 0;
        for (Biome biome : Biome.values()) {
            if (slot >= 45) break; // Leave space for control buttons

            // Check if biome is selected (default: not selected)
            boolean selected = selectedBiomes.get(player.getUniqueId()).contains(biome);

            Material paneMaterial = selected ? GREEN_PANE : RED_PANE;
            ItemStack item = new ItemStack(paneMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName((selected ? "§a" : "§c") + biome.name());
            item.setItemMeta(meta);

            gui.setItem(slot++, item);
        }

        // Confirm button
        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aStart Generation");
        confirm.setItemMeta(confirmMeta);
        gui.setItem(53, confirm);

        player.openInventory(gui);
    }

    public static Set<Biome> getSelectedBiomes(Player player) {
        return selectedBiomes.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }

    // Toggle biome selection and update the GUI slot color accordingly
    public static void toggleBiomeSelection(Player player, Biome biome) {
        Set<Biome> set = selectedBiomes.get(player.getUniqueId());
        if (set.contains(biome)) {
            set.remove(biome);
        } else {
            set.add(biome);
        }

        // Update GUI for this player
        updateBiomeSlot(player, biome);
    }

    // Update a specific biome slot's color in the open inventory for a player (green/red)
    private static void updateBiomeSlot(Player player, Biome biome) {
        InventoryView view = player.getOpenInventory();
        if (view == null || !GUI_TITLE.equals(view.getTitle())) return;

        Inventory inv = view.getTopInventory();


        int slot = getBiomeSlot(biome);
        if (slot == -1) return;

        Set<Biome> selected = selectedBiomes.get(player.getUniqueId());
        boolean isSelected = selected != null && selected.contains(biome);

        Material paneMaterial = isSelected ? GREEN_PANE : RED_PANE;

        ItemStack item = new ItemStack(paneMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((isSelected ? "§a" : "§c") + biome.name());
        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    // Get slot of biome (assuming order Biome.values() and slot < 45)
    private static int getBiomeSlot(Biome biome) {
        Biome[] biomes = Biome.values();
        for (int i = 0; i < biomes.length && i < 45; i++) {
            if (biomes[i] == biome) return i;
        }
        return -1;
    }

    /**
     * Call this to update biome item to yellow (selected & currently generating)
     */
    public static void setBiomeGenerating(Player player, Biome biome) {
        updateBiomeColor(player, biome, YELLOW_PANE, "§e");
    }

    /**
     * Call this to update biome item to green (selected & not generating)
     */
    public static void setBiomeNotGenerating(Player player, Biome biome) {
        updateBiomeColor(player, biome, GREEN_PANE, "§a");
    }

    /**
     * Call this to update biome item to gray (unselected & generation in progress)
     */
    public static void setBiomeGray(Player player, Biome biome) {
        updateBiomeColor(player, biome, GRAY_PANE, "§7");
    }

    /**
     * Call this to update biome item to red (unselected & generation not in progress)
     */
    public static void setBiomeRed(Player player, Biome biome) {
        updateBiomeColor(player, biome, RED_PANE, "§c");
    }

    private static void updateBiomeColor(Player player, Biome biome, Material material, String colorCode) {
        InventoryView view = player.getOpenInventory();
        if (view == null || !GUI_TITLE.equals(view.getTitle())) return;

        Inventory inv = view.getTopInventory();


        int slot = getBiomeSlot(biome);
        if (slot == -1) return;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorCode + biome.name());
        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    public static void clear(Player player) {
        selectedBiomes.remove(player.getUniqueId());
    }
}
