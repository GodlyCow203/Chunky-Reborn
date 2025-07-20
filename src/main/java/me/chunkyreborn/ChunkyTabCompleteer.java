package me.chunkyreborn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkyTabCompleteer implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "world", "shape", "center", "radius", "worldborder", "spawn",
            "corners", "pattern", "selection", "silent", "quiet",
            "progress", "reload", "trim", "start", "pause", "resume", "stop",
            "smart", "help", "version", "about", "developer"
    );

    private static final List<String> SHAPES = Arrays.asList("square", "circle", "hexagon");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            // Suggest subcommands starting with what player typed
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        // Handle tab completion for subcommand arguments
        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "shape":
                if (args.length == 2) {
                    for (String shape : SHAPES) {
                        if (shape.startsWith(args[1].toLowerCase())) {
                            completions.add(shape);
                        }
                    }
                }
                break;

            case "world":
                if (args.length == 2) {
                    // Suggest loaded worlds
                    Bukkit.getWorlds().forEach(w -> {
                        if (w.getName().startsWith(args[1].toLowerCase())) {
                            completions.add(w.getName());
                        }
                    });
                }
                break;

            case "pattern":
                if (args.length == 2) {
                    // Suggest your predefined patterns here, example:
                    List<String> patterns = Arrays.asList("spiral", "grid", "random");
                    for (String pattern : patterns) {
                        if (pattern.startsWith(args[1].toLowerCase())) {
                            completions.add(pattern);
                        }
                    }
                }
                break;

            // You can add more detailed completions for other commands here if needed

            default:
                break;
        }

        return completions;
    }
}
